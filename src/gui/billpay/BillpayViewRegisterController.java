package gui.billpay;

import java.net.URL;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

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
import model.entities.AccountPlan;
import model.entities.Billpay;
import model.entities.Clifor;
import model.entities.Company;
import model.exceptions.RecordAlreadyRecordedException;
import model.exceptions.ValidationException;
import model.service.AccountPlanService;
import model.service.BillpayService;
import model.service.CliforService;
import model.service.CompanyService;
import utils.Alerts;
import utils.Constraints;
import utils.Utils;

public class BillpayViewRegisterController implements Initializable{
	
	private BillpayService service;
	public void setBillpayService(BillpayService billpayService) {
		this.service = billpayService;
	}

	private CompanyService companyService;
	public void setCompanyService(CompanyService companyService) {
		this.companyService = companyService;
	}

	private CliforService cliforService;
	public void setCliforService(CliforService cliforService) {
		this.cliforService = cliforService;
	}

	private AccountPlanService accountService;
	public void setAccountPlanService(AccountPlanService accountPlanService) {
		this.accountService = accountPlanService;
	}

	private Billpay entity;
	public void setBillpay(Billpay billpay) {
		this.entity = billpay;
	}	

	@FXML
	private DatePicker pkEmission;
	@FXML
	private DatePicker pkDueDate;
	@FXML
	private TextField txtValue;
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtInvoice;
	@FXML
	private TextField txtPortion;
	@FXML
	private TextField txtFulfillment;
	@FXML
	private TextField txtQtdParcelas;
	@FXML
	private TextArea txtHistoric;
	
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
	private ComboBox<Clifor> cmbClifor;
	private void initializeComboBoxClifor() {
		Callback<ListView<Clifor>, ListCell<Clifor>> factory = lv -> new ListCell<Clifor>() {
			@Override
			protected void updateItem(Clifor item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		cmbClifor.setCellFactory(factory);
		cmbClifor.setButtonCell(factory.call(null));
	}
	
	@FXML
	private ComboBox<AccountPlan> cmbAccount;
	private void initializeComboBoxAccountPlan() {
		Callback<ListView<AccountPlan>, ListCell<AccountPlan>> factory = lv -> new ListCell<AccountPlan>() {
			@Override
			protected void updateItem(AccountPlan item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		cmbAccount.setCellFactory(factory);
		cmbAccount.setButtonCell(factory.call(null));
	}
	@FXML
	private Label lblErrorParcelamento;
	@FXML
	private Label lblErrorEmission;
	@FXML
	private Label lblErrorDueDate;
	@FXML
	private Label lblErrorValue;
	@FXML
	private Label lblErrorHistoric;
	@FXML
	private Label lblErrorCompany;
	@FXML
	private Label lblErrorClifor;
	@FXML
	private Label lblErrorAccountPlan;
	
	private ObservableList<Company> obsCompany;
	private ObservableList<Clifor> obsClifor;
	private ObservableList<AccountPlan> obsAccount;
	@FXML
	private Button btnSave;
	@FXML
	public void onBtnSaveAction(ActionEvent event) {
		if(entity == null || service == null) {
			throw new IllegalStateException("Entidade e/ou Serviço não instanciado.");
		}
		try {
			Billpay entity = getFormDate();
			entity.setFechada(this.entity.getFechada());
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
		} catch (Exception e) {
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
		Utils.formatDatePicker(pkDueDate, "dd/MM/yyyy");
		Utils.formatDatePicker(pkEmission, "dd/MM/yyyy");
		initializeComboBoxAccountPlan();
		initializeComboBoxCompany();
		initializeComboBoxClifor();
		pkDueDate.setPromptText("DD/MM/AAAA");
		pkEmission.setPromptText("DD/MM/AAAA");
		txtValue.setPromptText("0,00");
	}


	
	public void updateFormData() {
		if(entity == null) {
			throw new IllegalStateException("Entidade não instanciada");
		}
		
		txtHistoric.setText(entity.getHistoric());
		txtValue.setText(String.format("%.2f", entity.getValue()));
		txtId.setText(String.valueOf(entity.getId()));
		txtInvoice.setText(entity.getInvoice());
		txtPortion.setText(String.valueOf(entity.getPortion()));
		txtFulfillment.setText(String.valueOf(entity.getFulfillment()));
		
		if(entity.getDueDate() != null) {
			pkDueDate.setValue(convertToLocalDateViaInstant(entity.getDueDate()));
		}
		
		if(entity.getDate() != null) {
			pkEmission.setValue(convertToLocalDateViaInstant(entity.getDate()));
		}
		
		if(entity.getAccountPlan() == null) {
			cmbAccount.getSelectionModel().selectFirst();
		}else {
			cmbAccount.setValue(entity.getAccountPlan());
		}
		
		if(entity.getClifor() == null) {
			cmbClifor.getSelectionModel().selectFirst();
		}else {
			cmbClifor.setValue(entity.getClifor());
		}
		
		if(entity.getCompany() == null) {
			cmbCompany.getSelectionModel().selectFirst();
		}else {
			cmbCompany.setValue(entity.getCompany());
		}
	}
	
	
	public void loadAssociateObjects() {
		if(companyService == null || cliforService == null || accountService == null) {
			throw new IllegalStateException("Serviço não iniciado");
		}
		
		List<Company> companies = companyService.findByAll();
		List<Clifor> clifors = cliforService.findAllByTipo(true);
		List<AccountPlan> accounts = accountService.findAllByType(false);
		obsAccount = FXCollections.observableArrayList(accounts);
		obsClifor = FXCollections.observableArrayList(clifors);
		obsCompany = FXCollections.observableArrayList(companies);
		cmbAccount.setItems(obsAccount);
		cmbClifor.setItems(obsClifor);
		cmbCompany.setItems(obsCompany);
	}
	
	
	private Billpay getFormDate() throws ParseException {
		Billpay bill = new Billpay();
		ValidationException exception = new ValidationException("");
		bill.setStatus(entity.getStatus());
		bill.setId(Utils.tryParseToInt(txtId.getText()));
		bill.setInvoice(txtInvoice.getText());
		bill.setPortion(Utils.tryParseToInt(txtPortion.getText()));
		bill.setFulfillment(Utils.tryParseToInt(txtFulfillment.getText()));
		bill.setParcelas(Utils.tryParseToInt(txtQtdParcelas.getText()));
		
		if(pkEmission.getValue() == null) {
			exception.setError("emission", "Informe uma data de emissão válida");
		}else {
			bill.setDate(Date.from(pkEmission.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
		}

		if(pkDueDate.getValue() == null) {
			exception.setError("dueDate", "Informe um vencimento válido");
		}else {
			bill.setDueDate(Date.from(pkDueDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
		}
		
		if(txtValue.getText() == null || txtValue.getText().trim().equals("")) {
			exception.setError("value", "Informe um valor para a conta");
		}else {
			bill.setValue(Double.parseDouble(txtValue.getText().replace(",", ".")));
		}

		if(txtHistoric.getText() == null || txtHistoric.getText().trim().equals("")) {
			exception.setError("historic", "Informe um histórico para a conta");
		}
		bill.setHistoric(txtHistoric.getText());
		
		if(cmbCompany.getValue() == null) {
			exception.setError("company", "Selecione uma empresa");
		}
		bill.setCompany(cmbCompany.getValue());
		
		if(cmbClifor.getValue() == null) {
			exception.setError("clifor", "Selecione um fornecedor");
		}
		bill.setClifor(cmbClifor.getValue());
		
		if(cmbAccount.getValue() == null) {
			exception.setError("accountPlan", "Selecione um plano de conta");
		}
		bill.setAccountPlan(cmbAccount.getValue());
		
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		
		return bill;
	}	
	
	
	private void setErrorsMessage(Map<String, String> errors) {
		Set<String> keys = errors.keySet();
		lblErrorAccountPlan.setText("");
		lblErrorClifor.setText("");
		lblErrorCompany.setText("");
		lblErrorDueDate.setText("");
		lblErrorEmission.setText("");
		lblErrorHistoric.setText("");
		lblErrorParcelamento.setText("");
		lblErrorValue.setText("");
		
		if(keys.contains("accountPlan")) {
			lblErrorAccountPlan.setText(errors.get("accountPlan"));
		}
		if(keys.contains("clifor")) {
			lblErrorClifor.setText(errors.get("clifor"));
		}
		if(keys.contains("company")) {
			lblErrorCompany.setText(errors.get("company"));
		}
		if(keys.contains("dueDate")) {
			lblErrorDueDate.setText(errors.get("dueDate"));
		}
		if(keys.contains("emission")) {
			lblErrorEmission.setText(errors.get("emission"));
		}
		if(keys.contains("historic")) {
			lblErrorHistoric.setText(errors.get("historic"));
		}
		if(keys.contains("invoice")) {
			lblErrorParcelamento.setText(errors.get("parcelamento"));
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
