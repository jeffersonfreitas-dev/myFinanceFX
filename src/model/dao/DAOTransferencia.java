package model.dao;

import java.util.List;

import model.entities.Transferencia;

public interface DAOTransferencia {
	
	Integer insert (Transferencia entity);
	void update (Transferencia entity);
	void deleteById (Integer id);
	Transferencia findById(Integer id);
	List<Transferencia> findAllOrderDate();

}
