package model.dao;

import java.util.List;

import model.entities.Bank;

public interface DAOBank {
	
	void insert (Bank entity);
	void update (Bank entity);
	void deleteById (Integer id);
	Bank findById(Integer id);
	List<Bank> findAllOrderByName();
	Bank findByCodeOrName(String code, String name);

}
