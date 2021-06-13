package model.service;

import java.util.List;

import model.dao.DAOBank;
import model.dao.DAOFactory;
import model.entities.Bank;

public class BankService {
	
	private DAOBank dao = DAOFactory.createBankDAO();
	
	
	public List<Bank> findAll(){
		return dao.findAllOrderByName();
	}


	public void saveOrUpdate(Bank bank) {
		//TODO: verificar se existe o registro já cadastrado
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
