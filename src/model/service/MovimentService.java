package model.service;

import java.util.List;

import model.dao.DAOFactory;
import model.dao.DAOMoviment;
import model.entities.Moviment;

public class MovimentService {
	
	private DAOMoviment dao = DAOFactory.createMovimentDAO();
	
	
	public List<Moviment> findAll(){
		return dao.findAllOrderByDateBeginner();
	}


//	public void saveOrUpdate(Bank bank) {
//		
//		Bank isRecorded = dao.findByCodeOrName(bank.getCode(), bank.getName());
//		
//		if(isRecorded != null && !bank.equals(isRecorded)) {
//			throw new RecordAlreadyRecordedException("Registro já cadastrado no banco de dados!");
//		}
//		
//		if(bank.getId() == null) {
//			dao.insert(bank);
//		}else if (bank.getId() != null && bank.equals(isRecorded)){
//			dao.update(bank);
//		}
//	}
//
//
//	public void remove(Bank bank) {
//		dao.deleteById(bank.getId());
//	}

}
