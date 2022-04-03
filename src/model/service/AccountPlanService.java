package model.service;

import java.util.List;

import model.dao.DAOAccountPlan;
import model.dao.DAOFactory;
import model.entities.AccountPlan;
import model.exceptions.RecordAlreadyRecordedException;

public class AccountPlanService {
	
	private DAOAccountPlan dao = DAOFactory.createAccountPlanDAO();

	public List<AccountPlan> findAll() {
		return dao.findAllOrderByName();
	}
	
	public List<AccountPlan> findAllPagination(Integer limit){
		return dao.findAllPagination(limit);
	}

	public void remove(AccountPlan entity) {
		dao.deleteById(entity.getId());
		
	}

	public void saveOrUpdate(AccountPlan account) {
		AccountPlan ap = dao.findByNameAndCredit(account.getName(), account.isCredit());
		
		if(ap != null && !ap.equals(account)) {
			throw new RecordAlreadyRecordedException("Já existe uma conta com este nome e tipo cadastrado");
		}
		
		if(account.getId() == null) {
			dao.insert(account);
		}else {
			dao.update(account);
		}
		
	}

}
