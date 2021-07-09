package model.dao;

import java.util.List;

import model.entities.BankStatement;

public interface DAOBankStatement {
	
	void insert (BankStatement entity);
	void update (BankStatement entity);
	void deleteById (Integer id);
	BankStatement findById(Integer id);
	List<BankStatement> findAllOrderByDateAndBankAccount();

}
