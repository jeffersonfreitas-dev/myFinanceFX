package model.dao;

import java.util.List;

import model.entities.Clifor;

public interface DAOClifor {
	
	void insert(Clifor entity);
	void update (Clifor entity);
	void deleteById (Integer id);
	Clifor findById(Integer id);
	List<Clifor> findAllOrderByName();
	Clifor findByNameAndProvider(String name, boolean provider);
	List<Clifor> findAllByTipo(Boolean fornecedor);
	List<Clifor> filtro(Boolean fornecedor, String nome);

}
