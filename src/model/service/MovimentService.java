package model.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import model.dao.DAOFactory;
import model.dao.DAOMoviment;
import model.entities.Moviment;
import model.exceptions.RecordAlreadyRecordedException;
import utils.MyUtils;

public class MovimentService {
	
	private DAOMoviment dao = DAOFactory.createMovimentDAO();
	
	
	public List<Moviment> findAll(){
		return dao.findAllOrderByDateBeginner();
	}


	public void save(Moviment moviment) {
		
		List<Moviment> isRecorded = dao.findByAllOpenMoviment();
		
		if(!isRecorded.isEmpty()) {
			throw new RecordAlreadyRecordedException("Já existe um movimento ativo.");
		}
		
		
		if(MyUtils.dateInitialBeforeFinish(moviment.getDateBeginner(), moviment.getDateFinish())) {
			
			moviment.setClosed(false);
			moviment.setValueBeginner(0.0);
			moviment.setBalanceMoviment(0.0);
			moviment.setValueFinish(0.0);
			
			LocalDate d = moviment.getDateBeginner().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			
			moviment.setName(d.getYear() +"/"+d.getMonthValue());
			dao.insert(moviment);
		}else {
			throw new IllegalStateException("Data final menor que data inicial");
		}
		
	}


	public void closeMoviment(Moviment entity) {
		
	}


//	public void remove(Bank bank) {
//		dao.deleteById(bank.getId());
//	}

}
