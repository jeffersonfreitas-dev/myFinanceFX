package model.service;

import java.util.List;

import model.dao.DAOBankStatement;
import model.dao.DAOFactory;
import model.entities.BankStatement;
import model.entities.Payment;

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
		ext.setHistoric("Pagamento realizado referente conta nº " + payment.getBillpay().getInvoice() + " no(a) " + payment.getBankAccount().getBankAgence().getBank().getName());
		ext.setPayment(payment);
		ext.setReceivement(null);
		ext.setValue(payment.getBillpay().getValue());
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
