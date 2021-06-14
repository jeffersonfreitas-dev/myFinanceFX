package gui.bankAgence;

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.entities.Bank;
import model.entities.BankAgence;
import model.exceptions.ValidationException;
import model.service.BankAgenceService;
import utils.Alerts;
import utils.Constraints;
import utils.Utils;

public class BankAgenceViewRegisterController implements Initializable{
	
	private BankAgence entity;
	public void setBankAgence(BankAgence entity) {
		this.entity = entity;
	}
	
	private BankAgenceService service;
	public void setBankAgenceService(BankAgenceService service) {
		this.service = service;
	}
	
	@FXML
	private Label lblErrorAgence;
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtAgence;
	@FXML
	private TextField txtDV;
	@FXML
	private ComboBox<Bank> cmbBank;
	@FXML
	private Button btnSave;
	@FXML
	private void onBtnSaveAction(ActionEvent event) {
		if(service == null || entity == null) {
			throw new IllegalStateException("Entidade e/ou Servi�o n�o instanciado.");
		}
		try {
			Stage stage = Utils.getCurrentStage(event);
			BankAgence agence = getFormData();
			service.saveOrUpdate(agence);
			stage.setTitle("Lista de agencias banc�rias");
			loadView("/gui/bankAgence/BankAgenceView.fxml", stage.getScene());
		} catch (ValidationException e) {
			e.printStackTrace();
			setErrorMessages(e.getErrors());
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
		stage.setTitle("Lista de agencias banc�rias");
		loadView("/gui/bankAgence/BankAgenceView.fxml", stage.getScene());
	}
	

	private void loadView(String absolutPath, Scene scene) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absolutPath));
			VBox box = loader.load();
			
			BankAgenceViewController controller = loader.getController();
			controller.setBankAgenceService(new BankAgenceService());
			controller.updateTableView();
			
			VBox mainBox = (VBox) scene.getRoot();
			mainBox.getChildren().clear();
			mainBox.getChildren().addAll(box);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeNodes();
	}


	private void initializeNodes() {
		btnSave.setGraphic(new ImageView("/assets/icons/save16.png"));
		btnCancel.setGraphic(new ImageView("/assets/icons/cancel16.png"));
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtDV, 4);
		Constraints.setTextFieldMaxLength(txtDV, 1);
	}
	
	
	private BankAgence getFormData() {
		BankAgence agence = new BankAgence();
		ValidationException exception = new ValidationException("");
		agence.setId(Utils.tryParseToInt(txtId.getText()));
		
		if(txtAgence.getText() == null || txtAgence.getText().trim().equals("")) {
			exception.setError("agence", "O campo agencia n�o pode ser vazio.");
		}
		agence.setAgence(txtAgence.getText());
		
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		return agence;
	}
	
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> keys = errors.keySet();
		lblErrorAgence.setText("");
		
		if(keys.contains("agence")) {
			lblErrorAgence.setText(errors.get("agence"));
		}
	}


	public void updateFormData() {
		if(entity == null) {
			throw new IllegalStateException("Entidade n�o foi instanciada");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtAgence.setText(entity.getAgence());
		txtDV.setText(entity.getDv());
	}

}
