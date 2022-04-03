package model.service;

import java.util.List;

import model.dao.DAOCompany;
import model.dao.DAOFactory;
import model.entities.Company;
import model.exceptions.RecordAlreadyRecordedException;

public class CompanyService {
	
	private DAOCompany dao = DAOFactory.createCompanyDAO();

	public List<Company> findByAll() {
		return dao.findAllOrderByName();
	}

	public void saveOrUpdate(Company company) {
		Company result = dao.findByName(company.getName());
		
		if(result != null && !result.equals(company)) {
			throw new RecordAlreadyRecordedException("Já existe uma empresa cadastrada com este nome");
		}
		
		if(company.getId() == null) {
			dao.insert(company);
		}else {
			dao.update(company);
		}
		
	}

	public void remove(Company company) {
		dao.deleteById(company.getId());
		
	}

}
