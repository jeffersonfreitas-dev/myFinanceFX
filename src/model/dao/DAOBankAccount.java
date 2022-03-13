package model.dao;

import java.util.List;

import model.entities.BankAccount;

public interface DAOBankAccount {
	
	void insert (BankAccount entity);
	void update (BankAccount entity);
	void deleteById (Integer id);
	BankAccount findById(Integer id);
	List<BankAccount> findAllOrderByAccount();
	BankAccount findByAccountAndCompanyId(String account, Integer id_company);
	BankAccount findByNome(String code);

}
