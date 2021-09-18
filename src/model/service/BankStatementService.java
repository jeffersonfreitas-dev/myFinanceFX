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
import model.entities.Transferencia;

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
		ext.setHistoric("Pagamento da conta nº " + payment.getBillpay().getInvoice() + " - " + payment.getBillpay().getClifor().getName());
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
		ext.setHistoric("Recebimento da conta nº " + receivement.getReceivable().getInvoice() + " - " + receivement.getReceivable().getClifor().getName());
		ext.setPayment(null);
		ext.setReceivement(receivement);
		ext.setValue(receivement.getReceivable().getValue());
		ext.setInitialValue(false);
		dao.insert(ext);
	}


	public void createBankStatementByTransferencia(Transferencia entity) {
		try {
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if(entity == null) {
			throw new IllegalStateException("Entidade Transferência está nulo");
		}
		
		//Origem
		BankStatement ext = new BankStatement();
		ext.setBankAccount(entity.getOriginAccount());
		ext.setCredit(false);
		ext.setDate(entity.getDate());
		ext.setHistoric("Transferência para a conta nº " + entity.getDestinationAccount().getCode());
		ext.setPayment(null);
		ext.setReceivement(null);
		ext.setTransferencia(entity);
		ext.setValue(entity.getValue());
		ext.setInitialValue(false);
		dao.insert(ext);

		
		//Destino
		BankStatement ext2 = new BankStatement();
		ext2.setBankAccount(entity.getDestinationAccount());
		ext2.setCredit(true);
		ext2.setDate(entity.getDate());
		ext2.setHistoric("Transferência recebida da conta nº " + entity.getOriginAccount().getCode());
		ext2.setPayment(null);
		ext2.setReceivement(null);
		ext2.setTransferencia(entity);
		ext2.setValue(entity.getValue());
		ext2.setInitialValue(false);
		dao.insert(ext2);
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
