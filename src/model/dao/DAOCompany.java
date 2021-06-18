package model.dao;

import java.util.List;

import model.entities.Company;

public interface DAOCompany {
	
	void insert(Company entity);
	void update (Company entity);
	void deleteById (Integer id);
	Company findById(Integer id);
	List<Company> findAllOrderByName();
	Company findByName(String name);

}
