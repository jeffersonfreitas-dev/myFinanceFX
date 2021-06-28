package model.service;

import java.util.List;

import javafx.collections.ObservableList;
import model.dao.DAOBillpay;
import model.dao.DAOFactory;
import model.entities.Billpay;
import model.exceptions.RecordAlreadyRecordedException;

public class BillpayService {
	
	private DAOBillpay dao = DAOFactory.createBillpayDAO();
	


	public List<Billpay> findAll() {
		return dao.findAllOrderByDueDate();
	}

	public void remove(Billpay entity) {
		dao.deleteById(entity.getId());
		
	}

	public void saveOrUpdate(Billpay entity) {
		Billpay Billpay = dao.findByInvoiceAndCompanyId(entity.getInvoice(), entity.getCompany().getId());
		
		if(Billpay != null && !Billpay.equals(entity)) {
			throw new RecordAlreadyRecordedException("Já existe uma conta a pagar com esta nota para esta empresa cadastrada.");
		}
		
		if(entity.getId() == null) {
			entity.setStatus("ABERTO");
			entity.setPortion(1);
			dao.insert(entity);
		}else {
			dao.update(entity);
		}
		
	}

}
