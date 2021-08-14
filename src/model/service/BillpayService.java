package model.service;

import java.util.Calendar;
import java.util.List;

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
		
		entity.setFulfillment(1);
		
		if(entity.getId() == null) {
			Calendar cal = Calendar.getInstance();
			for(int i = 0; i < entity.getFulfillment(); i++) {
				cal.setTime(entity.getDueDate());
				Billpay bill = new Billpay();
				bill.setAccountPlan(entity.getAccountPlan());
				bill.setClifor(entity.getClifor());
				bill.setCompany(entity.getCompany());
				bill.setDate(entity.getDate());
				bill.setFulfillment(entity.getFulfillment());
				bill.setHistoric(entity.getHistoric());
				bill.setInvoice(entity.getInvoice() + "/"+(i+1));
				bill.setValue(entity.getValue() / entity.getFulfillment());
				bill.setPortion(i+1);
				bill.setStatus("ABERTO");
				cal.add(Calendar.MONTH, i);
				bill.setDueDate(cal.getTime());
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

}
