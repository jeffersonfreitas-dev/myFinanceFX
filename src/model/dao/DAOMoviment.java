package model.dao;

import java.util.List;

import model.entities.Moviment;

public interface DAOMoviment {
	
	Integer insert (Moviment entity);
	Integer update (Moviment entity);
	Integer deleteById (Integer id);
	Moviment findById(Integer id);
	List<Moviment> findAllOrderByDateBeginner();
	List<Moviment> findByAllOpenMoviment();

}
