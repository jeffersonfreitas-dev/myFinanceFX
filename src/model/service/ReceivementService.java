package model.service;

import java.util.Date;
import java.util.List;

import database.exceptions.DatabaseException;
import javafx.scene.control.Alert.AlertType;
import model.dao.DAOBankAccount;
import model.dao.DAOFactory;
import model.dao.DAOMoviment;
import model.dao.DAOReceivement;
import model.entities.BankAccount;
import model.entities.Moviment;
import model.entities.Receivable;
import model.entities.Receivement;
import utils.Alerts;

public class ReceivementService {
	
	private BankStatementService statementService = new BankStatementService();
	private DAOReceivement dao = DAOFactory.createReceivementDAO();
	private DAOBankAccount daoAccount = DAOFactory.createBankAccountDAO();
	private DAOMoviment daoMoviment = DAOFactory.createMovimentDAO();

	public List<Receivement> findAll() {
		return dao.findAllOrderByDate();
	}

	public void remove(Receivement entity) {
		dao.deleteById(entity.getId());
		
	}

	public void save(Receivement entity, ReceivableService receivableService) {
		try {
			if(movimentOpen() && dateInMoviment(entity.getDate())) {
				Receivable receb = receivableService.findById(entity.getReceivable().getId());
				receb.setStatus("R");
				receivableService.saveOrUpdate(receb);
				Integer id = dao.insert(entity);
				Receivement receivement = dao.findById(id);
				BankAccount account = daoAccount.findById(receivement.getBankAccount().getId());
				receivement.setBankAccount(account);
				receivement.setReceivable(receb);
				statementService.createBankStatementByReceivement(receivement);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Alerts.showAlert("Erro ao salvar", "Recebimento não realizado", e.getMessage(), AlertType.ERROR);
		}		
	}
	
	
	private boolean dateInMoviment(Date date) {
		List<Moviment> moviments = daoMoviment.findByAllOpenMoviment();
		if(moviments.isEmpty()) {
			throw new DatabaseException("Não existe nenhum movimento aberto");
		}
		
		Moviment moviment = moviments.get(0);
		
		boolean isBeforeFinish = date.before(moviment.getDateFinish());
		boolean isAfterBeginner = date.after(moviment.getDateBeginner());
		
		if(isAfterBeginner && isBeforeFinish){
			return true;
		}else {
			throw new DatabaseException("A data do recebimento está fora do periodo do movimento aberto");
		}
	}

	
	private boolean movimentOpen() {
		List<Moviment> moviments = daoMoviment.findByAllOpenMoviment();
		
		if(moviments.isEmpty()) {
			throw new DatabaseException("Não existe nenhum movimento aberto");
		}
		return true;
	}

}
