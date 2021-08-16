package model.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import model.dao.DAOBankAccount;
import model.dao.DAOFactory;
import model.dao.DAOMoviment;
import model.entities.BankAccount;
import model.entities.BankStatement;
import model.entities.Moviment;
import model.exceptions.RecordAlreadyRecordedException;
import model.exceptions.ValidationException;
import utils.MyUtils;

public class MovimentService {
	
	private DAOMoviment dao = DAOFactory.createMovimentDAO();
	private DAOBankAccount daoAccount = DAOFactory.createBankAccountDAO();
	
	private BankStatementService statementService = new BankStatementService();

	
	public List<Moviment> findAll(){
		return dao.findAllOrderByDateBeginner();
	}


	public void saveOrUpdate(Moviment moviment) {
		if(moviment == null) {
			throw new ValidationException("O objeto movimento está nulo");
		}
		
		verificarSeTemMovimentoAberto();
		
		List<BankAccount> accounts = daoAccount.findAllOrderByAccount();
		Double totalBalance = accounts.stream().map( a -> a.getBalance()).reduce(0.0, Double::sum);
		
		
		if(MyUtils.dateInitialBeforeFinish(moviment.getDateBeginner(), moviment.getDateFinish())) {
			
			moviment.setClosed(false);
			moviment.setValueBeginner(totalBalance);
			moviment.setBalanceMoviment(0.0);
			moviment.setValueFinish(0.0);
			
			LocalDate d = moviment.getDateBeginner().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			
			moviment.setName(d.getYear() +"/"+d.getMonthValue());
			Integer id = dao.insert(moviment);
			dao.findById(id);
			createMovimentInitialInBankStatmentent(moviment, accounts);
		}else {
			throw new IllegalStateException("Data final menor que data inicial");
		}
		
	}


	private void verificarSeTemMovimentoAberto() {
		List<Moviment> isRecorded = dao.findByAllOpenMoviment();
		
		if(!isRecorded.isEmpty()) {
			throw new RecordAlreadyRecordedException("Já existe um movimento ativo.");
		}
	}


	private void createMovimentInitialInBankStatmentent(Moviment moviment, List<BankAccount> accounts) {
		statementService.createBankStatementByMoviment(moviment, accounts);
		
	}


	public void closeMoviment(Moviment moviment) {
		
		List<BankAccount> accounts = daoAccount.findAllOrderByAccount();
		
		for(BankAccount b : accounts) {
			List<BankStatement> exts = statementService.findAllByAccountAndMoviment(b, moviment.getDateBeginner(), moviment.getDateFinish());
			Double total = 0.0;
			for (BankStatement s : exts) {
				if(s.isInitialValue()) {
					s.setBalance(s.getValue());
					total = s.getValue();
				}else {
					if(s.isCredit()) {
						s.setBalance(total + s.getValue());
						total = total + s.getValue();
					}else {
						s.setBalance(total - s.getValue());
						total = total - s.getValue();
					}
				}
			}
			b.setBalance(total);
			daoAccount.update(b);
		}
		moviment.setValueFinish(accounts.stream().map(a -> a.getBalance()).reduce(0.0, (a, b) -> a+b));
		moviment.setBalanceMoviment(moviment.getValueFinish() - moviment.getValueBeginner());
		moviment.setClosed(true);
		dao.update(moviment);
	}


	public void remove(Moviment entity) {
		
	}
}
