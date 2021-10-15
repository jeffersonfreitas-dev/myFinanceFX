package gui.transferencia;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import model.entities.BankAccount;
import model.entities.Transferencia;
import model.exceptions.RecordAlreadyRecordedException;
import model.exceptions.ValidationException;
import model.service.BankAccountService;
import model.service.TransferenciaService;
import utils.Alerts;
import utils.Constraints;
import utils.Utils;

public class TransferenciaViewRegisterController implements Initializable{
	
	private TransferenciaService service;
	public void setTransferenciaService(TransferenciaService transferenciaService) {
		this.service = transferenciaService;
	}

	private BankAccountService bankAccountService;
	public void setBankAccountService(BankAccountService bankAccountService) {
		this.bankAccountService = bankAccountService;
	}

	private Transferencia entity;
	public void setTransferencia(Transferencia transferencia) {
		this.entity = transferencia;
	}	

	@FXML
	private TextField txtId;
	@FXML
	private DatePicker pkDate;
	@FXML
	private TextField txtValue;
	@FXML
	private TextArea txtObservation;
	
	@FXML
	private ComboBox<BankAccount> cmbBankAccountOrigin;
	private void initializeComboBoxBankAccount() {
		Callback<ListView<BankAccount>, ListCell<BankAccount>> factory = lv -> new ListCell<BankAccount>() {
			@Override
			protected void updateItem(BankAccount item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getCode() + " / " + item.getAccount() + " / " + item.getBankAgence().getBank().getName());
			}
		};
		cmbBankAccountOrigin.setCellFactory(factory);
		cmbBankAccountOrigin.setButtonCell(factory.call(null));
	}
	
	
	@FXML
	private ComboBox<BankAccount> cmbBankAccountDestination;
	private void initializeComboBoxBankAccountDestination() {
		Callback<ListView<BankAccount>, ListCell<BankAccount>> factory = lv -> new ListCell<BankAccount>() {
			@Override
			protected void updateItem(BankAccount item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getCode() + " / " + item.getAccount() + " / " + item.getBankAgence().getBank().getName());
			}
		};
		cmbBankAccountDestination.setCellFactory(factory);
		cmbBankAccountDestination.setButtonCell(factory.call(null));
	}
	
	
	@FXML
	private Label lblErrorDate;
	@FXML
	private Label lblErrorValue;
	@FXML
	private Label lblErrorObservation;
	@FXML
	private Label lblErrorBankAccountOrigin;
	@FXML
	private Label lblErrorBankAccountDestination;
	private ObservableList<BankAccount> obsOrigin;
	private ObservableList<BankAccount> obsDestination;
	@FXML
	private Button btnSave;
	@FXML
	public void onBtnSaveAction(ActionEvent event) {
		if(entity == null || service == null) {
			throw new IllegalStateException("Entidade e/ou Serviço não instanciado.");
		}
		try {
			Transferencia entity = getFormDate();
			service.saveOrUpdate(entity);
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
		initializationNodes();
	}

	private void initializationNodes() {
		btnCancel.getStyleClass().add("btn-danger");
		btnSave.getStyleClass().add("btn-success");	
		Constraints.setTextFieldDouble(txtValue);
		Constraints.setTextFieldInteger(txtId);
		Utils.formatDatePicker(pkDate, "dd/MM/yyyy");
		initializeComboBoxBankAccount();
		initializeComboBoxBankAccountDestination();
		pkDate.setPromptText("DD/MM/AAAA");
		txtValue.setPromptText("0,00");
	}


	
	public void updateFormData() {
		if(entity == null) {
			throw new IllegalStateException("Entidade não instanciada");
		}
		
		txtId.setText(String.valueOf(entity.getId()));
		txtObservation.setText(entity.getObservation());
		txtValue.setText(String.format("%.2f", entity.getValue()));
		
		if(entity.getDate() != null) {
			pkDate.setValue(convertToLocalDateViaInstant(entity.getDate()));
		}
		
		if(entity.getOriginAccount() == null) {
			cmbBankAccountOrigin.getSelectionModel().selectFirst();
		}else {
			cmbBankAccountOrigin.setValue(entity.getOriginAccount());
		}

		if(entity.getDestinationAccount() == null) {
			cmbBankAccountDestination.getSelectionModel().selectFirst();
		}else {
			cmbBankAccountDestination.setValue(entity.getDestinationAccount());
		}
	}
	
	
	public void loadAssociateObjects() {
		if(bankAccountService == null) {
			throw new IllegalStateException("Serviço não iniciado");
		}
		
		List<BankAccount> bankOrigin = bankAccountService.findAll();
		List<BankAccount> bankDestinatiion = bankAccountService.findAll();
		obsDestination = FXCollections.observableArrayList(bankDestinatiion);
		obsOrigin = FXCollections.observableArrayList(bankOrigin);
		cmbBankAccountDestination.setItems(obsDestination);
		cmbBankAccountOrigin.setItems(obsOrigin);
	}
	
	
	private Transferencia getFormDate() {
		Transferencia transf = new Transferencia();
		ValidationException exception = new ValidationException("");
		transf.setId(Utils.tryParseToInt(txtId.getText()));
		
		if(pkDate.getValue() == null) {
			exception.setError("date", "Informe uma data válida");
		}else {
			transf.setDate(Date.from(pkDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
		}
		
		if(txtValue.getText() == null || txtValue.getText().trim().equals("")) {
			exception.setError("value", "Informe um valor");
		}else {
			transf.setValue(Double.parseDouble(txtValue.getText().replace(",", ".")));
		}

		if(txtObservation.getText() == null || txtObservation.getText().trim().equals("")) {
			exception.setError("observation", "Informe uma observação para a transferência");
		}
		transf.setObservation(txtObservation.getText());
		
		if(cmbBankAccountOrigin.getValue() == null) {
			exception.setError("origin", "Selecione uma conta de origin");
		}
		transf.setOriginAccount(cmbBankAccountOrigin.getValue());

		if(cmbBankAccountDestination.getValue() == null) {
			exception.setError("destination", "Selecione uma conta de destino");
		}
		transf.setDestinationAccount(cmbBankAccountDestination.getValue());
		
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		
		return transf;
	}	
	
	
	private void setErrorsMessage(Map<String, String> errors) {
		Set<String> keys = errors.keySet();
		lblErrorBankAccountDestination.setText("");
		lblErrorBankAccountOrigin.setText("");
		lblErrorDate.setText("");
		lblErrorObservation.setText("");
		lblErrorValue.setText("");
		
		if(keys.contains("origin")) {
			lblErrorBankAccountOrigin.setText(errors.get("origin"));
		}
		if(keys.contains("destination")) {
			lblErrorBankAccountDestination.setText(errors.get("destination"));
		}
		if(keys.contains("date")) {
			lblErrorDate.setText(errors.get("date"));
		}
		if(keys.contains("observation")) {
			lblErrorObservation.setText(errors.get("observation"));
		}
		if(keys.contains("value")) {
			lblErrorValue.setText(errors.get("value"));
		}
	}
	
	private LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
	    return dateToConvert.toInstant()
	      .atZone(ZoneId.systemDefault())
	      .toLocalDate();
	}
}
