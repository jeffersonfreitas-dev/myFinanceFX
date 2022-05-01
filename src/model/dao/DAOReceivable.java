package model.dao;

import java.util.List;

import model.entities.Receivable;

public interface DAOReceivable {
	
	void insert (Receivable entity);
	void update (Receivable entity);
	void deleteById (Integer id);
	Receivable findById(Integer id);
	List<Receivable> findAllOrderByDueDate();
	Receivable findByInvoiceAndCompanyId(String invoice, Integer id_company);
	List<Receivable> filtro(String status);

}
