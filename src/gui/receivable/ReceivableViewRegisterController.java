package gui.receivable;

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
import model.entities.AccountPlan;
import model.entities.Clifor;
import model.entities.Company;
import model.entities.Receivable;
import model.exceptions.RecordAlreadyRecordedException;
import model.exceptions.ValidationException;
import model.service.AccountPlanService;
import model.service.CliforService;
import model.service.CompanyService;
import model.service.ReceivableService;
import utils.Alerts;
import utils.Constraints;
import utils.Utils;

public class ReceivableViewRegisterController implements Initializable{
	
	private ReceivableService service;
	public void setReceivableService(ReceivableService receivableService) {
		this.service = receivableService;
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

	private Receivable entity;
	public void setReceivable(Receivable receivable) {
		this.entity = receivable;
	}	
	
	
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtInvoice;
	@FXML
	private DatePicker pkEmission;
	@FXML
	private DatePicker pkDueDate;
	@FXML
	private TextField txtValue;
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
	private Label lblErrorInvoice;
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
	@FXML
	private Button btnSave;
	@FXML
	public void onBtnSaveAction(ActionEvent event) {
		if(entity == null || service == null) {
			throw new IllegalStateException("Entidade e/ou Servi�o n�o instanciado.");
		}
		try {
			Receivable entity = getFormDate();
			service.saveOrUpdate(entity);
			onBtnCancelAction(event);
			Stage stage = Utils.getCurrentStage(event);
			stage.getOnCloseRequest().handle(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
			stage.close();
			
		} catch (RecordAlreadyRecordedException e) {
			Alerts.showAlert("Registro j� cadastrado", null, e.getMessage(), AlertType.INFORMATION);
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
	
	
	private ObservableList<Company> obsCompany;
	private ObservableList<Clifor> obsClifor;
	private ObservableList<AccountPlan> obsAccount;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializationNodes();
	}

	private void initializationNodes() {
		btnCancel.getStyleClass().add("btn-danger");
		btnSave.getStyleClass().add("btn-success");	
		Constraints.setTextFieldDouble(txtValue);
		Constraints.setTextFieldMaxLength(txtInvoice, 20);
		Constraints.setTextFieldInteger(txtId);
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
			throw new IllegalStateException("Entidade n�o instanciada");
		}
		
		txtId.setText(String.valueOf(entity.getId()));
		txtInvoice.setText(entity.getInvoice());
		txtHistoric.setText(entity.getHistoric());
		txtValue.setText(String.format("%.2f", entity.getValue()));
		
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
			throw new IllegalStateException("Servi�o n�o iniciado");
		}
		
		List<Company> companies = companyService.findByAll();
		List<Clifor> clifors = cliforService.findAll();
		List<AccountPlan> accounts = accountService.findAll();
		obsAccount = FXCollections.observableArrayList(accounts);
		obsClifor = FXCollections.observableArrayList(clifors);
		obsCompany = FXCollections.observableArrayList(companies);
		cmbAccount.setItems(obsAccount);
		cmbClifor.setItems(obsClifor);
		cmbCompany.setItems(obsCompany);
	}
	
	
	private Receivable getFormDate() {
		Receivable recep = new Receivable();
		ValidationException exception = new ValidationException("");
		recep.setId(Utils.tryParseToInt(txtId.getText()));
		recep.setStatus(entity.getStatus());
		
		if(txtInvoice.getText() == null || txtInvoice.getText().trim().equals("")) {
			exception.setError("invoice", "Informe a nota fiscal");
		}
		recep.setInvoice(txtInvoice.getText());
		
		if(pkEmission.getValue() == null) {
			exception.setError("emission", "Informe uma data v�lida");
		}else {
			recep.setDate(Date.from(pkEmission.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
		}
		
		if(pkDueDate.getValue() == null) {
			exception.setError("dueDate", "Informe um vencimento v�lido");
		}else {
			recep.setDueDate(Date.from(pkDueDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
			
			if(recep.getDueDate().before(recep.getDate())) {
				exception.setError("dueDate", "Vencimento menor que a emiss�o");
			}
		}
		
		if(txtValue.getText() == null || txtValue.getText().trim().equals("")) {
			exception.setError("value", "Informe um valor para a conta");
		}else {
			recep.setValue(Double.parseDouble(txtValue.getText().replace(",", ".")));
		}

		if(txtHistoric.getText() == null || txtHistoric.getText().trim().equals("")) {
			exception.setError("historic", "Informe um hist�rico para a conta");
		}
		recep.setHistoric(txtHistoric.getText());
		
		if(cmbCompany.getValue() == null) {
			exception.setError("company", "Selecione uma empresa");
		}
		recep.setCompany(cmbCompany.getValue());
		
		if(cmbClifor.getValue() == null) {
			exception.setError("clifor", "Selecione um cliente");
		}
		recep.setClifor(cmbClifor.getValue());
		
		if(cmbAccount.getValue() == null) {
			exception.setError("accountPlan", "Selecione um plano de conta");
		}
		recep.setAccountPlan(cmbAccount.getValue());
		
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		
		return recep;
	}	
	
	
	private void setErrorsMessage(Map<String, String> errors) {
		Set<String> keys = errors.keySet();
		lblErrorAccountPlan.setText("");
		lblErrorClifor.setText("");
		lblErrorCompany.setText("");
		lblErrorDueDate.setText("");
		lblErrorEmission.setText("");
		lblErrorHistoric.setText("");
		lblErrorInvoice.setText("");
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
			lblErrorInvoice.setText(errors.get("invoice"));
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
