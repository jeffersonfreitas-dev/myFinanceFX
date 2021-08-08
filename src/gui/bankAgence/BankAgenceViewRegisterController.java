package gui.bankAgence;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import database.exceptions.DatabaseException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import model.entities.Bank;
import model.entities.BankAgence;
import model.exceptions.RecordAlreadyRecordedException;
import model.exceptions.ValidationException;
import model.service.BankAgenceService;
import model.service.BankService;
import utils.Alerts;
import utils.Constraints;
import utils.Utils;

public class BankAgenceViewRegisterController implements Initializable{
	
	private BankAgence entity;
	public void setBankAgence(BankAgence entity) {
		this.entity = entity;
	}
	
	private BankAgenceService service;
	private BankService bankService;
	public void setBankAgenceServices(BankAgenceService service, BankService bankService) {
		this.service = service;
		this.bankService = bankService;
	}
	
	ObservableList<Bank> obsList;
	
	@FXML
	private Label lblErrorAgence;
	@FXML
	private Label lblErrorBank;
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtAgence;
	@FXML
	private ComboBox<Bank> cmbBank;
	private void initializeComboBoxBank() {
		Callback<ListView<Bank>, ListCell<Bank>> factory = lv -> new ListCell<Bank>() {
			@Override
			protected void updateItem(Bank item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		cmbBank.setCellFactory(factory);
		cmbBank.setButtonCell(factory.call(null));
	}
	
	@FXML
	private Button btnSave;
	@FXML
	private void onBtnSaveAction(ActionEvent event) {
		if(entity == null || service == null) {
			throw new IllegalStateException("Entidade e/ou Serviço não instanciado.");
		}
		try {
			BankAgence agence = getFormData();
			service.saveOrUpdate(agence);
			onBtnCancelAction(event);
			Stage stage = Utils.getCurrentStage(event);
			stage.getOnCloseRequest().handle(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
			stage.close();
			
		} catch (RecordAlreadyRecordedException e) {
			Alerts.showAlert("Registro já cadastrado", null, e.getMessage(), AlertType.INFORMATION);
		} catch (IllegalStateException e) {
			e.printStackTrace();
			Alerts.showAlert("Erro ao salvar o registro", null, e.getMessage(), AlertType.ERROR);
		} catch (ValidationException e) {
			e.printStackTrace();
			setErrorsMessage(e.getErrors());
		} catch (DatabaseException e) {
			e.printStackTrace();
			Alerts.showAlert("Erro ao salvar o registro", null, e.getMessage(), AlertType.ERROR);
		}
	}

	@FXML
	private Button btnCancel;
	@FXML
	public void onBtnCancelAction(ActionEvent event) {
		Stage stage = Utils.getCurrentStage(event);
		stage.close();
	}
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeNodes();
	}


	private void initializeNodes() {
		btnCancel.getStyleClass().add("btn-danger");
		btnSave.getStyleClass().add("btn-success");
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtAgence, 30);
		initializeComboBoxBank();
	}
	
	
	private BankAgence getFormData() {
		BankAgence agence = new BankAgence();
		ValidationException exception = new ValidationException("");
		agence.setId(Utils.tryParseToInt(txtId.getText()));
		
		if(txtAgence.getText() == null || txtAgence.getText().trim().equals("")) {
			exception.setError("agence", "O campo agencia não pode ser vazio.");
		}
		agence.setAgence(txtAgence.getText());
		
		if(cmbBank.getValue() == null) {
			exception.setError("bank", "Selecione um banco");
		}
		
		agence.setBank(cmbBank.getValue());
		
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		return agence;
	}
	
	
	private void setErrorsMessage(Map<String, String> errors) {
		Set<String> keys = errors.keySet();
		lblErrorAgence.setText("");
		lblErrorBank.setText("");
		if(keys.contains("agence")) {
			lblErrorAgence.setText(errors.get("agence"));
		}
		if(keys.contains("bank")) {
			lblErrorBank.setText(errors.get("bank"));
		}
	}


	public void updateFormData() {
		if(entity == null) {
			throw new IllegalStateException("Entidade não foi instanciada");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtAgence.setText(entity.getAgence());
		
		if(entity.getBank() == null) {
			cmbBank.getSelectionModel().selectFirst();
		}else {
			cmbBank.setValue(entity.getBank());
		}
	}
	
	public void loadAssociateObjects() {
		if(bankService == null) {
			throw new IllegalStateException("O serviço do banco não foi instanciado");
		}
		List<Bank> banks = bankService.findAll();
		obsList = FXCollections.observableArrayList(banks);
		cmbBank.setItems(obsList);
	}
}
