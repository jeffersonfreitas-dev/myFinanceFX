package model.dao;

import java.util.Date;
import java.util.List;

import model.entities.BankAccount;
import model.entities.BankStatement;

public interface DAOBankStatement {
	
	void insert (BankStatement entity);
	void update (BankStatement entity);
	void deleteById (Integer id);
	BankStatement findById(Integer id);
	List<BankStatement> findAllOrderByDateAndBankAccount();
	List<BankStatement> findAllByAccountAndMoviment(BankAccount bankAccount, Date dateBeginner, Date dateFinish);
	void deleteTransferenciaById(Integer id);
	Integer hasMovimentByDate(Date dateBeginner, Date dateFinish);
	void deleteByDateInitialAndFinal(Date dateBeginner, Date dateFinish);
	BankStatement findByPayment(Integer payment);
	void deletePaymentById(Integer id);

}
