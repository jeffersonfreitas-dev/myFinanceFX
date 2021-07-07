package model.service;

import java.util.List;

import model.dao.DAOFactory;
import model.dao.DAOReceivement;
import model.entities.Receivable;
import model.entities.Receivement;

public class ReceivementService {
	
	private DAOReceivement dao = DAOFactory.createReceivementDAO();

	public List<Receivement> findAll() {
		return dao.findAllOrderByDate();
	}

	public void remove(Receivement entity) {
		dao.deleteById(entity.getId());
		
	}

	public void save(Receivement entity, ReceivableService receivableService) {
		Receivable receb = receivableService.findById(entity.getReceivable().getId());
		receb.setStatus("PAGO");
		receivableService.saveOrUpdate(receb);
		dao.insert(entity);
	}

}
