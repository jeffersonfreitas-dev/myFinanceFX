package model.dao;

import java.util.List;

import model.entities.Payment;

public interface DAOPayment {
	
	Integer insert (Payment entity);
	void update (Payment entity);
	void deleteById (Integer id);
	Payment findById(Integer id);
	List<Payment> findAllOrderByDate();
	Payment findByBillpay(Integer id);

}
