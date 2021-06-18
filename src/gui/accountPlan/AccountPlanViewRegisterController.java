package gui.accountPlan;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import database.exceptions.DatabaseException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.entities.AccountPlan;
import model.exceptions.RecordAlreadyRecordedException;
import model.exceptions.ValidationException;
import model.service.AccountPlanService;
import utils.Alerts;
import utils.Constraints;
import utils.Utils;

public class AccountPlanViewRegisterController implements Initializable{

	private AccountPlan entity;
	public void setAccountPlan(AccountPlan entity) {
		this.entity = entity;
	}
	
	private AccountPlanService service;
	public void setAccountPlanService(AccountPlanService service) {
		this.service = service;
	}
	@FXML
	private Label lblErrorName;
	@FXML
	private Button btnSave;
	@FXML
	public void onBtnSaveAction(ActionEvent event) {
		try {
			AccountPlan account = getFormData();
			service.saveOrUpdate(account);
			Stage stage = Utils.getCurrentStage(event);
			stage.setTitle("Lista de planos de conta");
			loadView("/gui/accountPlan/AccountPlanView.fxml", stage.getScene());
		} catch (RecordAlreadyRecordedException e) {
			Alerts.showAlert("Registro já cadastrado", null, e.getMessage(), AlertType.INFORMATION);
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
		stage.setTitle("Lista de planos de conta");
		loadView("/gui/accountPlan/AccountPlanView.fxml", stage.getScene());
	}

	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private RadioButton rdioCredit;
	@FXML
	private RadioButton rdioDebito;
	@FXML
	private ToggleGroup rdioGroup;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializationNodes();
	}

	private void initializationNodes() {
		rdioGroup = new ToggleGroup();
		rdioCredit.setToggleGroup(rdioGroup);
		rdioDebito.setToggleGroup(rdioGroup);
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 60);
		btnSave.setGraphic(new ImageView("/assets/icons/save16.png"));
		btnCancel.setGraphic(new ImageView("/assets/icons/cancel16.png"));
	}

	
	private void loadView(String pathView, Scene scene) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(pathView));
			VBox box = loader.load();
			
			AccountPlanViewController controller = loader.getController();
			controller.setAccountPlanService(new AccountPlanService());
			controller.updateTableView();
			
			VBox mainBox = (VBox) scene.getRoot();
			mainBox.getChildren().clear();
			mainBox.getChildren().addAll(box.getChildren());
		}catch(IOException e) {
			e.printStackTrace();
			Alerts.showAlert("Erro", "Erro ao carregar a janela", e.getMessage(), AlertType.ERROR);
		}
	}	
	
	
	private AccountPlan getFormData() {
		AccountPlan account = new AccountPlan();
		ValidationException exception = new ValidationException("");
		account.setId(Utils.tryParseToInt(txtId.getText()));
		
		if(txtName == null || txtName.getText().trim().equals("")) {
			exception.setError("name", "O campo nome não pode ser vazio");
		}
		account.setName(txtName.getText());
		
		if(rdioCredit.isSelected()) {
			account.setCredit(true);
		}else {
			account.setCredit(false);
		}
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		return account;
	}
	
	
	private void setErrorsMessage(Map<String, String> errors) {
		Set<String> keys = errors.keySet();
		lblErrorName.setText("");
		
		if(keys.contains("name")) {
			lblErrorName.setText(errors.get("name"));
		}
	}

	public void updateFormData() {
		if(entity == null) {
			throw new IllegalStateException("A entidade não foi instanciada");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		
		if(entity.isCredit()) {
			rdioCredit.setSelected(true);
		}else {
			rdioDebito.setSelected(true);
		}
	}
}
