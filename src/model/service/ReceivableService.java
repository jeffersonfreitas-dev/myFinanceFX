package model.service;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import model.dao.DAOFactory;
import model.dao.DAOReceivable;
import model.entities.Receivable;
import model.exceptions.RecordAlreadyRecordedException;

public class ReceivableService {
	
	private DAOReceivable dao = DAOFactory.createReceivableDAO();
	
	public Receivable findById(Integer id) {
		return dao.findById(id);
	}

	public List<Receivable> findAll() {
		return dao.findAllOrderByDueDate();
	}

	public void remove(Receivable entity) {
		dao.deleteById(entity.getId());
		
	}

	public void saveOrUpdate(Receivable entity) {
		Receivable receivable = dao.findByInvoiceAndCompanyId(entity.getInvoice(), entity.getCompany().getId());
		
		if(receivable != null && !entity.equals(receivable)) {
			throw new RecordAlreadyRecordedException("Já existe uma conta a receber com esta nota para esta empresa cadastrada.");
		}
		
		if(entity.getId() == null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(entity.getDueDate());
			cal.add(Calendar.MONTH, -1);
			
			if(entity.getParcelas() == null && entity.getFulfillment() == null) {
				entity.setFulfillment(1);
			}else {
				entity.setFulfillment(entity.getParcelas());
			}
			
			for(int i = 0; i < entity.getFulfillment(); i++) {
				Receivable receb = new Receivable();
				receb.setAccountPlan(entity.getAccountPlan());
				receb.setClifor(entity.getClifor());
				receb.setCompany(entity.getCompany());
				receb.setDate(entity.getDate());
				receb.setFulfillment(entity.getFulfillment());
				receb.setHistoric(entity.getHistoric());
				receb.setInvoice(UUID.randomUUID().toString());
				receb.setValue(entity.getValue() / entity.getFulfillment());
				receb.setPortion(i+1);
				receb.setStatus("RECEBER");
				cal.add(Calendar.MONTH, 1);
				receb.setDueDate(cal.getTime());
				dao.insert(receb);
			}
			
		}else {
			dao.update(entity);
			
		}
		
	}

	public void payment(Receivable entity) {
		entity.setStatus("PAGO");
		dao.update(entity);
	}

	public List<Receivable> filtro(String status) {
		return dao.filtro(status);
	}

}
