package model.service;

import java.util.List;

import model.dao.DAOFactory;
import model.dao.DAOPayment;
import model.entities.Billpay;
import model.entities.Payment;

public class PaymentService {
	
	private DAOPayment dao = DAOFactory.createPaymentDAO();

	public List<Payment> findAll() {
		return dao.findAllOrderByDate();
	}

	public void remove(Payment entity) {
		dao.deleteById(entity.getId());
		
	}

	public void save(Payment entity, BillpayService billService) {
		Billpay bill = billService.findById(entity.getBillpay().getId());
		bill.setStatus("PAGO");
		billService.saveOrUpdate(bill);
		dao.insert(entity);
	}

}
