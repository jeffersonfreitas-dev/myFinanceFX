package model.service;

import java.util.List;

import javafx.scene.control.Alert.AlertType;
import model.dao.DAOFactory;
import model.dao.DAOMoviment;
import model.dao.DAOTransferencia;
import model.entities.BankAccount;
import model.entities.Billpay;
import model.entities.Payment;
import model.entities.Transferencia;
import utils.Alerts;

public class TransferenciaService {
	
	private DAOTransferencia dao = DAOFactory.createTransferenciaDAO();
	private BankStatementService statementService = new BankStatementService();
	private MovimentService movimentService = new MovimentService();

	
	public Transferencia findById(Integer id) {
		return dao.findById(id);
	}

	public List<Transferencia> findAll() {
		return dao.findAllOrderDate();
	}

	public void remove(Transferencia entity) {
		dao.deleteById(entity.getId());
		
	}

	public void saveOrUpdate(Transferencia entity) {
		if(entity.getId() == null) {
			entity.setClose(false);
			
			try {
				boolean movimentOpen = movimentService.movimentOpen();
				boolean dateInMoviment = movimentService.dateInMoviment(entity.getDate());
				if(movimentOpen && dateInMoviment) {
					statementService.createBankStatementByTransferencia(entity);
				}
				dao.insert(entity);
			} catch (Exception e) {
				e.printStackTrace();
				Alerts.showAlert("Erro ao salvar", "Transfência não realizada", e.getMessage(), AlertType.ERROR);
			}			
		}else {
//			dao.update(entity);
		}
		
	}

}
