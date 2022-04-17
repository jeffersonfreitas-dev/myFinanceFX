package model.service;

import java.util.List;

import model.dao.DAOClifor;
import model.dao.DAOFactory;
import model.entities.Clifor;
import model.exceptions.RecordAlreadyRecordedException;

public class CliforService {
	
	private DAOClifor dao = DAOFactory.createCliforDAO();

	public List<Clifor> findAll() {
		return dao.findAllOrderByName();
	}

	public void remove(Clifor entity) {
		dao.deleteById(entity.getId());
		
	}

	public void saveOrUpdate(Clifor entity) {
		Clifor clifor = dao.findByNameAndProvider(entity.getName(), entity.isProvider());
		
		if(clifor != null && !clifor.equals(entity)) {
			throw new RecordAlreadyRecordedException("Já existe um cliente ou fornecedor com este nome");
		}
		
		if(entity.getId() == null) {
			dao.insert(entity);
		}else {
			dao.update(entity);
		}
		
	}

	public List<Clifor> findAllByTipo(Boolean fornecedor) {
		return dao.findAllByTipo(fornecedor);
	}

}
