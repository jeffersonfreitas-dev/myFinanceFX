package gui.receivement;

import java.net.URL;
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
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import model.entities.BankAccount;
import model.entities.Receivable;
import model.entities.Receivement;
import model.exceptions.ValidationException;
import model.service.BankAccountService;
import model.service.ReceivableService;
import model.service.ReceivementService;
import utils.Alerts;
import utils.Utils;

public class ReceivementViewRegisterController implements Initializable{
	
	private Receivable receivable;
	public void setReceivable(Receivable receivable) {
		this.receivable = receivable;
	}
	
	private ReceivementService service;
	public void setService(ReceivementService service) {
		this.service = service;
	}
	
	private BankAccountService accountService;
	public void setAccountService(BankAccountService accountService) {
		this.accountService = accountService;
	}
	
	private ReceivableService recebService;
	public void setReceivableService(ReceivableService recebService) {
		this.recebService = recebService;
	}
	
	private Receivement entity;
	public void setReceivement(Receivement entity) {
		this.entity = entity;
	}
	
	@FXML
	private TextArea text;
	
	@FXML
	private DatePicker pkDate;
	@FXML
	private ComboBox<BankAccount> cmbAccount;
	private void initializeComboBoxAccount() {
		Callback<ListView<BankAccount>, ListCell<BankAccount>> factory = lv -> new ListCell<BankAccount>() {
			@Override
			protected void updateItem(BankAccount item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getAccount() +" - "+ item.getBankAgence().getBank().getName());
			}
		};
		cmbAccount.setCellFactory(factory);
		cmbAccount.setButtonCell(factory.call(null));
	}
	
	private ObservableList<BankAccount> obsBankAccount;
	
	@FXML
	private Label lblErroAccount;
	@FXML
	private Label lblErroData;
	@FXML
	private Button btnReceivement;
	@FXML
	public void onBtnReceivementAction(ActionEvent event) {
		if(entity == null || service == null) {
			throw new IllegalStateException("Serviço e/ou Entidade não instanciado");
		}
		
		try {
			Receivement receivement = getFormDate();
			service.save(receivement, recebService);
			Stage stage = Utils.getCurrentStage(event);
			stage.getOnCloseRequest().handle(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
			stage.close();
		} catch (ValidationException e) {
			e.printStackTrace();
			setErrorsMessage(e.getErrors());
		} catch (DatabaseException e) {
			e.printStackTrace();
			Alerts.showAlert("Erro ao salvar o registro", null, e.getMessage(), AlertType.ERROR);
		}
	}
	

	private Receivement getFormDate() {
		Receivement receivement = new Receivement();
		if(receivable == null) {
			throw new IllegalStateException("Entidade conta a pagar não instanciada");
		}
		receivement.setReceivable(receivable);
		ValidationException exception = new ValidationException("");
		
		if(cmbAccount.getValue() == null) {
			exception.setError("account", "Informe a conta bancária para recebimento");
		}
		receivement.setBankAccount(cmbAccount.getValue());
		
		if(pkDate.getValue() == null) {
			exception.setError("date", "Informe a data de recebimento");
		}else {
			receivement.setDate(Date.from(pkDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
		}
		
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		return receivement;
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
	
	
	public void loadAssociateObjects() {
		if(service == null || recebService == null) {
			throw new IllegalStateException("Serviço não instanciado");
		}
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("Cliente: " + receivable.getClifor().getName() + "\n");
		sb.append("Plano de Conta: " + receivable.getAccountPlan().getName() + "\n");
		sb.append("Parcelas: " + receivable.getPortion() + "/" + receivable.getFulfillment() + "\n");
		sb.append("Histórico: " + receivable.getHistoric() + "\n");
		text.setText(sb.toString());
		
		List<BankAccount> accounts = accountService.findAll();
		obsBankAccount = FXCollections.observableArrayList(accounts);
		cmbAccount.setItems(obsBankAccount);
	}


	private void initializationNodes() {
		btnCancel.getStyleClass().add("btn-danger");
		btnReceivement.getStyleClass().add("btn-success");
		Utils.formatDatePicker(pkDate, "dd/MM/yyyy");
		pkDate.setPromptText("DD/MM/AAAA");
		initializeComboBoxAccount();
	}
	
	private void setErrorsMessage(Map<String, String> errors) {
		Set<String> keys = errors.keySet();
		
		lblErroAccount.setText("");
		
		if(keys.contains("account")) {
			lblErroAccount.setText(errors.get("account"));
		}
		
		if(keys.contains("date")) {
			lblErroData.setText(errors.get("date"));
		}
	}

}
