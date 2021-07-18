package model.service;

import java.util.Date;
import java.util.List;

import model.dao.DAOBankStatement;
import model.dao.DAOFactory;
import model.entities.BankAccount;
import model.entities.BankStatement;
import model.entities.Moviment;
import model.entities.Payment;
import model.entities.Receivement;

public class BankStatementService {
	
	private DAOBankStatement dao = DAOFactory.createBankStatementDAO();

	public List<BankStatement> findAllOrderByDateAndBankAccount(){
		return dao.findAllOrderByDateAndBankAccount();
	}


	public void createBankStatementByPayment(Payment payment) throws Exception{
		if(payment == null) {
			throw new IllegalStateException("Entidade Pagamento está nulo");
		}
		BankStatement ext = new BankStatement();
		ext.setBankAccount(payment.getBankAccount());
		ext.setCredit(false);
		ext.setDate(payment.getDate());
		ext.setHistoric("Pagamento realizado referente conta nº " + payment.getBillpay().getInvoice());
		ext.setPayment(payment);
		ext.setReceivement(null);
		ext.setValue(payment.getBillpay().getValue());
		ext.setInitialValue(false);
		dao.insert(ext);
	}


	public void createBankStatementByMoviment(Moviment moviment, List<BankAccount> accounts) {
		if(moviment == null || accounts.size() < 1) {
			throw new IllegalStateException("Entidade Movimento ou Contas bancárias estão nulos");
		}
		
		for(BankAccount acc : accounts) {
			createRegisterByAccounts(acc, moviment);
		}
		
		
	}


	private void createRegisterByAccounts(BankAccount acc, Moviment moviment) {
		BankStatement ext = new BankStatement();
		ext.setBankAccount(acc);
		ext.setCredit(true);
		ext.setInitialValue(true);
		ext.setDate(moviment.getDateBeginner());
		ext.setHistoric("Saldo inicial do movimento nº " + moviment.getName());
		ext.setPayment(null);
		ext.setReceivement(null);
		ext.setValue(acc.getBalance());	
		dao.insert(ext);
	}


	public List<BankStatement> findAllByAccountAndMoviment(BankAccount bankAccount, Date dateBeginner,
			Date dateFinish) {
		return dao.findAllByAccountAndMoviment(bankAccount, dateBeginner, dateFinish);
	}


	public void createBankStatementByReceivement(Receivement receivement) {
		if(receivement == null) {
			throw new IllegalStateException("Entidade Recebimento está nulo");
		}
		BankStatement ext = new BankStatement();
		ext.setBankAccount(receivement.getBankAccount());
		ext.setCredit(true);
		ext.setDate(receivement.getDate());
		ext.setHistoric("Recebimento realizado referente conta nº " + receivement.getReceivable().getInvoice());
		ext.setPayment(null);
		ext.setReceivement(receivement);
		ext.setValue(receivement.getReceivable().getValue());
		ext.setInitialValue(false);
		dao.insert(ext);
	}


//	public void saveOrUpdate(Bank bank) {
//		
//		Bank isRecorded = dao.findByCodeOrName(bank.getCode(), bank.getName());
//		
//		if(isRecorded != null && !bank.equals(isRecorded)) {
//			throw new RecordAlreadyRecordedException("Registro já cadastrado no banco de dados!");
//		}
//		
//		if(bank.getId() == null) {
//			dao.insert(bank);
//		}else if (bank.getId() != null && bank.equals(isRecorded)){
//			dao.update(bank);
//		}
//	}
//
//
//	public void remove(Bank bank) {
//		dao.deleteById(bank.getId());
//	}

}
