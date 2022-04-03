package model.service;

import java.util.List;

import model.dao.DAOBank;
import model.dao.DAOFactory;
import model.entities.Bank;
import model.exceptions.RecordAlreadyRecordedException;

public class BankService {
	
	private DAOBank dao = DAOFactory.createBankDAO();
	
	
	public List<Bank> findAll(){
		return dao.findAllOrderByName();
	}


	public void saveOrUpdate(Bank bank) {
		
		Bank isRecorded = dao.findByCodeOrName(bank.getCode(), bank.getName());
		
		if(isRecorded != null && !bank.equals(isRecorded)) {
			throw new RecordAlreadyRecordedException("Já existe um banco com este código e nome cadastrado!");
		}
		
		if(bank.getId() == null) {
			dao.insert(bank);
		}else {
			dao.update(bank);
		}
	}

	public void remove(Bank bank) {
		dao.deleteById(bank.getId());
	}

}
