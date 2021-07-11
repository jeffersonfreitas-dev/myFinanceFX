package model.dao;

import java.util.List;

import model.entities.Moviment;

public interface DAOMoviment {
	
	void insert (Moviment entity);
	void update (Moviment entity);
	void deleteById (Integer id);
	Moviment findById(Integer id);
	List<Moviment> findAllOrderByDateBeginner();

}
