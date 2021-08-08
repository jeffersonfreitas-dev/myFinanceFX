package gui.bankAccount;

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
import model.entities.BankAccount;
import model.entities.BankAgence;
import model.entities.Company;
import model.exceptions.RecordAlreadyRecordedException;
import model.exceptions.ValidationException;
import model.service.BankAccountService;
import model.service.BankAgenceService;
import model.service.CompanyService;
import utils.Alerts;
import utils.Constraints;
import utils.Utils;

public class BankAccountViewRegisterController implements Initializable{
	
	private BankAccount entity;
	public void setBankAccount(BankAccount entity) {
		this.entity = entity;
	}
	
	private BankAgenceService bankAgenceService;
	private CompanyService companyService;
	private BankAccountService service;
	public void setBankAccountService(BankAccountService service, BankAgenceService bankAgenceService, CompanyService companyService) {
		this.service = service;
		this.bankAgenceService = bankAgenceService;
		this.companyService = companyService;
	}

	@FXML
	private Button btnSave;
	@FXML
	public void onBtnSaveAction(ActionEvent event) {
		if(entity == null || service == null) {
			throw new IllegalStateException("Entidade e/ou Serviço não instanciado.");
		}
		try {
			BankAccount account = getFormData();
			account.setBalance(entity.getBalance());
			service.saveOrUpdate(account);
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

	@FXML
	private TextField txtId;
	@FXML
	private TextField txtCode;
	@FXML
	private TextField txtAccount;
	@FXML
	private ComboBox<BankAgence> cmbAgence;
	private void initializeComboBoxAgence() {
		Callback<ListView<BankAgence>, ListCell<BankAgence>> factory = lv -> new ListCell<BankAgence>() {
			@Override
			protected void updateItem(BankAgence item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getAgence() +" - "+item.getBank().getName());
			}
		};
		cmbAgence.setCellFactory(factory);
		cmbAgence.setButtonCell(factory.call(null));
	}	
	@FXML
	private ComboBox<Company> cmbCompany;
	private void initializeComboBoxCompany() {
		Callback<ListView<Company>, ListCell<Company>> factory = lv -> new ListCell<Company>() {
			@Override
			protected void updateItem(Company item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		cmbCompany.setCellFactory(factory);
		cmbCompany.setButtonCell(factory.call(null));
	}
	@FXML
	private Label lblErrorCode;
	@FXML
	private Label lblErrorAccount;
	@FXML
	private Label lblErrorCompany;
	@FXML
	private Label lblErrorAgence;
	
	private ObservableList<BankAgence> obsAgence;
	private ObservableList<Company> obsCompanies;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializationNodes();
	}

	
	private void initializationNodes() {
		btnCancel.getStyleClass().add("btn-danger");
		btnSave.getStyleClass().add("btn-success");
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtCode, 10);
		Constraints.setTextFieldMaxLength(txtAccount, 20);
		initializeComboBoxAgence();
		initializeComboBoxCompany();
	}
	
	
	private BankAccount getFormData() {
		BankAccount account = new BankAccount();
		ValidationException exception = new ValidationException("");
		account.setId(Utils.tryParseToInt(txtId.getText()));
		
		if(txtCode.getText() == null || txtCode.getText().trim().equals("")) {
			exception.setError("code", "O código da conta não pode ser vazio");
		}
		account.setCode(txtCode.getText());
		
		if(txtAccount.getText() == null || txtAccount.getText().trim().equals("")) {
			exception.setError("account", "O número da conta não pode ser vazio");
		}
		account.setAccount(txtAccount.getText());
		
		if(cmbCompany.getValue() == null) {
			exception.setError("company", "Selecione uma empresa");
		}
		account.setCompany(cmbCompany.getValue());

		if(cmbAgence.getValue() == null) {
			exception.setError("agence", "Selecione uma agencia bancária");
		}
		account.setBankAgence(cmbAgence.getValue());
		
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		return account;
	}	

	
	private void setErrorsMessage(Map<String, String> errors) {
		Set<String> keys = errors.keySet();
		lblErrorAccount.setText("");
		lblErrorCode.setText("");
		lblErrorCompany.setText("");
		lblErrorAgence.setText("");
		
		if(keys.contains("code")) {
			lblErrorCode.setText(errors.get("code"));
		}
		if(keys.contains("account")) {
			lblErrorAccount.setText(errors.get("account"));
		}
		if(keys.contains("company")) {
			lblErrorCompany.setText(errors.get("company"));
		}
		if(keys.contains("agence")) {
			lblErrorAgence.setText(errors.get("agence"));
		}
	}
	
	
	public void loadAssociateObjects() {
		if(bankAgenceService == null || companyService == null) {
			throw new IllegalStateException("O serviço não foi instanciado");
		}
		List<BankAgence> agences = bankAgenceService.findAll();
		List<Company> companies = companyService.findByAll();
		obsAgence = FXCollections.observableArrayList(agences);
		obsCompanies = FXCollections.observableArrayList(companies);
		cmbAgence.setItems(obsAgence);
		cmbCompany.setItems(obsCompanies);
	}


	public void updateFormData() {
		if(entity == null) {
			throw new IllegalStateException("Entidade não foi instanciada");
		}
		
		txtId.setText(String.valueOf(entity.getId()));
		txtCode.setText(entity.getCode());
		txtAccount.setText(entity.getAccount());
		
		if(entity.getBankAgence() == null) {
			cmbAgence.getSelectionModel().selectFirst();
		}else {
			cmbAgence.setValue(entity.getBankAgence());
		}
		
		if(entity.getCompany() == null) {
			cmbCompany.getSelectionModel().selectFirst();
		}else {
			cmbCompany.setValue(entity.getCompany());
		}
	}	
}
