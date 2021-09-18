package model.service;

import java.util.List;

import model.dao.DAOBankAccount;
import model.dao.DAOFactory;
import model.entities.BankAccount;
import model.exceptions.RecordAlreadyRecordedException;

public class BankAccountService {
	
	private DAOBankAccount dao = DAOFactory.createBankAccountDAO();
	
	public List<BankAccount> findAll(){
		return dao.findAllOrderByAccount();
	}

	public void remove(BankAccount entity) {
		dao.deleteById(entity.getId());
		
	}

	public void saveOrUpdate(BankAccount entity) {
		BankAccount account = dao.findByAccountAndCompanyId(entity.getAccount(), entity.getCompany().getId());
		
		if(account != null && !account.equals(entity)) {
			throw new RecordAlreadyRecordedException("Número de conta já cadastrada para esta empresa!");
		}
		
		if(entity.getId() == null) {
			entity.setBalance(0.0);
			dao.insert(entity);
		}else {
			dao.update(entity);
		}
	}

	public BankAccount findById(Integer id) {
		return dao.findById(id);
	}

}
