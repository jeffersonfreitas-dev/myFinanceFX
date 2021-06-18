package model.dao;

import java.util.List;

import model.entities.BankAgence;

public interface DAOBankAgence {
	
	void insert (BankAgence entity);
	void update (BankAgence entity);
	void deleteById (Integer id);
	BankAgence findById(Integer id);
	List<BankAgence> findAllOrderByAgence();
	BankAgence findByAgenceAndBankId(String agence, Integer id_bank);

}
