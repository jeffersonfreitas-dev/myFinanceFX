package model.dao;

import java.time.LocalDate;
import java.util.List;

import model.entities.Billpay;

public interface DAOBillpay {
	
	void insert (Billpay entity);
	void update (Billpay entity);
	void deleteById (Integer id);
	Billpay findById(Integer id);
	List<Billpay> findAllOrderByDueDate();
	Billpay findByInvoiceAndCompanyId(String invoice, Integer id_company);
	List<Billpay> filtro(String status, String nome, String combobox, LocalDate inicio, LocalDate fim);
	List<Billpay> findAllByAccountPlanId(Integer idAccountPlan);
	List<Billpay> findAllByCliforId(Integer id);

}
