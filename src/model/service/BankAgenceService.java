package model.service;

import java.util.List;

import model.dao.DAOBankAgence;
import model.dao.DAOFactory;
import model.entities.BankAgence;

public class BankAgenceService {
	
	private DAOBankAgence dao = DAOFactory.createBankAgenceDAO();
	
	public List<BankAgence> findAll(){
		return dao.findAllOrderByAgence();
	}

	public void remove(BankAgence entity) {
		dao.deleteById(entity.getId());
		
	}

}
