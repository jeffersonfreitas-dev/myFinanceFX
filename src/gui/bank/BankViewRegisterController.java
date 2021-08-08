package gui.bank;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import database.exceptions.DatabaseException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.entities.Bank;
import model.exceptions.RecordAlreadyRecordedException;
import model.exceptions.ValidationException;
import model.service.BankService;
import utils.Alerts;
import utils.Constraints;
import utils.Utils;

public class BankViewRegisterController implements Initializable{
	
	private Bank entity;
	public void setBank(Bank bank) {
		this.entity = bank;
	}
	
	private BankService service;
	public void setBankService(BankService service) {
		this.service = service;
	}
	
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtCode;
	@FXML
	private TextField txtName;
	
	@FXML
	private Label lblErrorCode;
	@FXML
	private Label lblErrorName;
	
	@FXML
	private Button btnCancel;
	@FXML
	public void onBtnCancelAction(ActionEvent event) {
		Stage stage = Utils.getCurrentStage(event);
		stage.close();
	}
	
	
	@FXML
	private Button btnSave;
	@FXML
	public void onBtnSaveAction(ActionEvent event) {
		if(entity == null || service == null) {
			throw new IllegalStateException("Entidade e/ou Serviço não instanciado.");
		}
		try {
			Bank bank = getFormData();
			service.saveOrUpdate(bank);
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
	
	private void setErrorsMessage(Map<String, String> errors) {
		Set<String> keys = errors.keySet();
		
		lblErrorCode.setText("");
		lblErrorName.setText("");
		
		if(keys.contains("code")) {
			lblErrorCode.setText(errors.get("code"));
		}
		if(keys.contains("name")) {
			lblErrorName.setText(errors.get("name"));
		}
		
	}

	private Bank getFormData() {
		Bank bank = new Bank();
		ValidationException exception = new ValidationException("");
		bank.setId(Utils.tryParseToInt(txtId.getText()));
		
		if(txtCode.getText() == null || txtCode.getText().trim().equals("")) {
			exception.setError("code", "O campo código do banco não pode ser vazio.");
		}
		bank.setCode(txtCode.getText());
		
		if(txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.setError("name", "O campo nome não pode ser vazio.");
		}
		bank.setName(txtName.getText());
		
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		return bank;
	}


	public void updateFormData() {
		if(entity == null) {
			throw new IllegalStateException("Entidade não foi instanciada");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtCode.setText(entity.getCode());
		txtName.setText(entity.getName());
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializationNodes();
		
	}

	private void initializationNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtCode, 10);
		Constraints.setTextFieldMaxLength(txtName, 60);
		btnCancel.getStyleClass().add("btn-danger");
		btnSave.getStyleClass().add("btn-success");
	}

}
