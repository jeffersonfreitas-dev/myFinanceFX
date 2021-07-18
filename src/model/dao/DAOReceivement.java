package model.dao;

import java.util.List;

import model.entities.Receivement;

public interface DAOReceivement {
	
	Integer insert (Receivement entity);
	void update (Receivement entity);
	void deleteById (Integer id);
	Receivement findById(Integer id);
	List<Receivement> findAllOrderByDate();

}
