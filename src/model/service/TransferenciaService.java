package model.service;

import java.util.List;

import javafx.scene.control.Alert.AlertType;
import model.dao.DAOFactory;
import model.dao.DAOMoviment;
import model.dao.DAOTransferencia;
import model.entities.Moviment;
import model.entities.Transferencia;
import utils.Alerts;

public class TransferenciaService {
	
	private DAOTransferencia dao = DAOFactory.createTransferenciaDAO();
	private BankStatementService statementService = new BankStatementService();
	private MovimentService movimentService = new MovimentService();
	private DAOMoviment daoMoviment = DAOFactory.createMovimentDAO();

	
	public Transferencia findById(Integer id) {
		return dao.findById(id);
	}

	public List<Transferencia> findAll() {
		return dao.findAllOrderDate();
	}

	public void remove(Transferencia entity) {
		try {
			boolean movimentOpen = movimentService.movimentOpen();
			boolean dateInMoviment = movimentService.dateInMoviment(entity.getDate());
			if(movimentOpen && dateInMoviment) {
				statementService.deleteByTransferenciaId(entity.getId());
				dao.deleteById(entity.getId());
				
				Moviment moviment = movimentService.findByAllOpenMoviment().get(0);
				
				if(entity.getDestinationAccount().getType().equals("APLICAÇÃO")) {
					moviment.setValueAplicacao(moviment.getValueAplicacao() - entity.getValue());
				}
				if(entity.getOriginAccount().getType().equals("APLICAÇÃO")) {
					moviment.setValueResgate(moviment.getValueResgate() - entity.getValue());
				}
				daoMoviment.update(moviment);

			}
		} catch (Exception e) {
			e.printStackTrace();
			Alerts.showAlert("Erro ao deletar", "Não foi possível deletar o registro", e.getMessage(), AlertType.ERROR);
		}		
		
		
	}

	public void saveOrUpdate(Transferencia entity) {
		if(entity.getId() == null) {
			entity.setClose(false);
			
			try {
				boolean movimentOpen = movimentService.movimentOpen();
				boolean dateInMoviment = movimentService.dateInMoviment(entity.getDate());
				if(movimentOpen && dateInMoviment) {
					Integer id = dao.insert(entity);
					entity = dao.findById(id);
					statementService.createBankStatementByTransferencia(entity);

					Moviment moviment = movimentService.findByAllOpenMoviment().get(0);
					
					if(entity.getDestinationAccount().getType().equals("APLICAÇÃO")) {
						moviment.setValueAplicacao(moviment.getValueAplicacao() + entity.getValue());
					}
					if(entity.getOriginAccount().getType().equals("APLICAÇÃO")) {
						moviment.setValueResgate(moviment.getValueResgate() + entity.getValue());
					}
					daoMoviment.update(moviment);

				}
			} catch (Exception e) {
				e.printStackTrace();
				Alerts.showAlert("Erro ao salvar", "Transfência não realizada", e.getMessage(), AlertType.ERROR);
			}			
		}else {
//			dao.update(entity);
		}
		
	}

}
