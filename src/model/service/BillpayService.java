package model.service;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import model.dao.DAOBillpay;
import model.dao.DAOFactory;
import model.entities.Billpay;
import model.exceptions.RecordAlreadyRecordedException;

public class BillpayService {
	
	private DAOBillpay dao = DAOFactory.createBillpayDAO();
	
	public Billpay findById(Integer id) {
		return dao.findById(id);
	}

	public List<Billpay> findAll() {
		return dao.findAllOrderByDueDate();
	}

	public void remove(Billpay entity) {
		dao.deleteById(entity.getId());
		
	}

	public void saveOrUpdate(Billpay entity) {
		Billpay billpay = dao.findByInvoiceAndCompanyId(entity.getInvoice(), entity.getCompany().getId());
		
		if(billpay != null && !entity.equals(billpay)) {
			throw new RecordAlreadyRecordedException("Já existe uma conta a pagar com esta nota para esta empresa cadastrada.");
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
				Billpay bill = new Billpay();
				bill.setAccountPlan(entity.getAccountPlan());
				bill.setClifor(entity.getClifor());
				bill.setCompany(entity.getCompany());
				bill.setDate(entity.getDate());
				bill.setFulfillment(entity.getFulfillment());
				bill.setHistoric(entity.getHistoric());
				bill.setInvoice(UUID.randomUUID().toString());
				bill.setValue(entity.getValue() / entity.getFulfillment());
				bill.setPortion(i+1);
				bill.setStatus("PAGAR");
				cal.add(Calendar.MONTH, 1);
				bill.setDueDate(cal.getTime());
				bill.setFechada(false);
				dao.insert(bill);
			}
			
		}else {
			dao.update(entity);
		}
		
	}

	public void payment(Billpay entity) {
		entity.setStatus("PAGO");
		dao.update(entity);
	}

	public List<Billpay> filtro(String status, String nome, String combobox, LocalDate inicio, LocalDate fim) {
		return dao.filtro(status, nome, combobox, inicio, fim);
	}


}
