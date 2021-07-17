package model.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import model.dao.DAOBankAccount;
import model.dao.DAOFactory;
import model.dao.DAOMoviment;
import model.entities.BankAccount;
import model.entities.Moviment;
import model.exceptions.RecordAlreadyRecordedException;
import utils.MyUtils;

public class MovimentService {
	
	private DAOMoviment dao = DAOFactory.createMovimentDAO();
	private DAOBankAccount daoAccount = DAOFactory.createBankAccountDAO();
	
	private BankStatementService statementService = new BankStatementService();

	
	public List<Moviment> findAll(){
		return dao.findAllOrderByDateBeginner();
	}


	public void save(Moviment moviment) {
		
		List<Moviment> isRecorded = dao.findByAllOpenMoviment();
		
		if(!isRecorded.isEmpty()) {
			throw new RecordAlreadyRecordedException("Já existe um movimento ativo.");
		}
		
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
			Moviment movv = dao.findById(id);
			createMovimentInitialInBankStatmentent(moviment, accounts);
		}else {
			throw new IllegalStateException("Data final menor que data inicial");
		}
		
	}


	private void createMovimentInitialInBankStatmentent(Moviment moviment, List<BankAccount> accounts) {
		statementService.createBankStatementByMoviment(moviment, accounts);
		
	}


	public void closeMoviment(Moviment entity) {
		
	}
}
