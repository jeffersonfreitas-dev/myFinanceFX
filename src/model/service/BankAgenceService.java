package model.service;

import java.util.List;

import model.dao.DAOBankAgence;
import model.dao.DAOFactory;
import model.entities.BankAgence;
import model.exceptions.RecordAlreadyRecordedException;

public class BankAgenceService {
	
	private DAOBankAgence dao = DAOFactory.createBankAgenceDAO();
	
	public List<BankAgence> findAll(){
		return dao.findAllOrderByAgence();
	}

	public void remove(BankAgence entity) {
		dao.deleteById(entity.getId());
		
	}

	public void saveOrUpdate(BankAgence entity) {
		BankAgence agence = dao.findByAgenceAndBankId(entity.getAgence(), entity.getBank().getId());
		
		if(agence != null && !agence.equals(entity)) {
			throw new RecordAlreadyRecordedException("Agencia e banco já cadastrado!");
		}
		
		if(entity.getId() == null) {
			dao.insert(entity);
		}else {
			dao.update(entity);
		}
	}

}
