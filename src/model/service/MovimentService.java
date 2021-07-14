package model.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import model.dao.DAOFactory;
import model.dao.DAOMoviment;
import model.entities.Moviment;

public class MovimentService {
	
	private DAOMoviment dao = DAOFactory.createMovimentDAO();
	
	
	public List<Moviment> findAll(){
		return dao.findAllOrderByDateBeginner();
	}


	public void save(Moviment moviment) {
		
//		Moviment isRecorded = dao.findByActiveTrueA(bank.getCode(), bank.getName());
//		
//		if(isRecorded != null && !bank.equals(isRecorded)) {
//			throw new RecordAlreadyRecordedException("Registro já cadastrado no banco de dados!");
//		}
		moviment.setClosed(false);
		moviment.setValueBeginner(0.0);
		moviment.setBalanceMoviment(0.0);
		moviment.setValueFinish(0.0);
		
		LocalDate d = moviment.getDateBeginner().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		
		moviment.setName(d.getYear() +"/"+d.getMonthValue());
		dao.insert(moviment);
	}


	public void closeMoviment(Moviment entity) {
		
	}


//	public void remove(Bank bank) {
//		dao.deleteById(bank.getId());
//	}

}
