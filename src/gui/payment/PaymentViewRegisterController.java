package gui.payment;

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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import model.entities.BankAccount;
import model.entities.Billpay;
import model.entities.Payment;
import model.exceptions.ValidationException;
import model.service.BankAccountService;
import model.service.BillpayService;
import model.service.PaymentService;
import utils.Alerts;
import utils.Constraints;
import utils.Utils;

public class PaymentViewRegisterController implements Initializable{
	
	private Billpay billpay;
	public void setBillpay(Billpay billpay) {
		this.billpay = billpay;
	}
	
	private PaymentService service;
	public void setService(PaymentService service) {
		this.service = service;
	}
	
	private BankAccountService accountService;
	public void setAccountService(BankAccountService accountService) {
		this.accountService = accountService;
	}
	
	private BillpayService billService;
	public void setBillpayService(BillpayService billService) {
		this.billService = billService;
	}
	
	private Payment entity;
	public void setPayment(Payment entity) {
		this.entity = entity;
	}
	
	@FXML
	private TextField txtId;
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
	private Button btnPayment;
	@FXML
	public void onBtnPaymentAction(ActionEvent event) {
		if(entity == null || service == null) {
			throw new IllegalStateException("Serviço e/ou Entidade não instanciado");
		}
		
		try {
			Payment payment = getFormDate();
			service.save(payment, billService);
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
	

	private Payment getFormDate() {
		Payment pay = new Payment();
		if(billpay == null) {
			throw new IllegalStateException("Entidade conta a pagar não instanciada");
		}
		pay.setBillpay(billpay);
		ValidationException exception = new ValidationException("");
		pay.setId(Utils.tryParseToInt(txtId.getText()));
		
		if(cmbAccount.getValue() == null) {
			exception.setError("account", "Informe a conta bancária para pagamento");
		}
		pay.setBankAccount(cmbAccount.getValue());
		
		if(pkDate.getValue() == null) {
			exception.setError("date", "Informe a data de pagamento");
		}else {
			pay.setDate(Date.from(pkDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
		}
		
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		return pay;
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
		if(service == null || billService == null) {
			throw new IllegalStateException("Serviço não instanciado");
		}
		
		List<BankAccount> accounts = accountService.findAll();
		obsBankAccount = FXCollections.observableArrayList(accounts);
		cmbAccount.setItems(obsBankAccount);
	}


	private void initializationNodes() {
		btnPayment.setGraphic(new ImageView("/assets/icons/payment16.png"));
		btnCancel.setGraphic(new ImageView("/assets/icons/cancel16.png"));
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
			lblErroAccount.setText(errors.get("date"));
		}
	}

}
