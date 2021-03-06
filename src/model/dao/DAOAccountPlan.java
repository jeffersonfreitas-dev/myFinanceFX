package model.dao;

import java.util.List;

import model.entities.AccountPlan;

public interface DAOAccountPlan {
	
	void insert(AccountPlan entity);
	void update (AccountPlan entity);
	void deleteById (Integer id);
	AccountPlan findById(Integer id);
	List<AccountPlan> findAllOrderByName();
	List<AccountPlan> findAllByType(Boolean credit);
	List<AccountPlan> findAllPagination(Integer limit);
	AccountPlan findByNameAndCredit(String name, boolean credit);


}
