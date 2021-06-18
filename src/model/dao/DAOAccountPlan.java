package model.dao;

import java.util.List;

import model.entities.AccountPlan;

public interface DAOAccountPlan {
	
	void insert(AccountPlan entity);
	void update (AccountPlan entity);
	void deleteById (Integer id);
	AccountPlan findById(Integer id);
	List<AccountPlan> findAllOrderByName();
	AccountPlan findByNameAndCredit(String name, boolean credit);

}
