package gui.clifor;

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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.entities.Clifor;
import model.exceptions.RecordAlreadyRecordedException;
import model.exceptions.ValidationException;
import model.service.CliforService;
import utils.Alerts;
import utils.Constraints;
import utils.Utils;

public class CliforViewRegisterController implements Initializable{

	private Clifor entity;
	public void setClifor(Clifor entity) {
		this.entity = entity;
	}
	
	private CliforService service;
	public void setCliforService(CliforService service) {
		this.service = service;
	}
	@FXML
	private Label lblErrorName;
	@FXML
	private Button btnSave;
	@FXML
	public void onBtnSaveAction(ActionEvent event) {
		if(entity == null || service == null) {
			throw new IllegalStateException("Entidade e/ou Serviço não instanciado.");
		}
		try {
			Clifor clifor = getFormData();
			service.saveOrUpdate(clifor);
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
	private TextField txtName;
	@FXML
	private RadioButton rdioCustomer;
	@FXML
	private RadioButton rdioProvider;
	@FXML
	private ToggleGroup rdioGroup;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializationNodes();
	}

	private void initializationNodes() {
		rdioGroup = new ToggleGroup();
		rdioCustomer.setToggleGroup(rdioGroup);
		rdioProvider.setToggleGroup(rdioGroup);
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 60);
		btnCancel.getStyleClass().add("btn-danger");
		btnSave.getStyleClass().add("btn-success");
	}
	
	
	private Clifor getFormData() {
		Clifor clifor = new Clifor();
		ValidationException exception = new ValidationException("");
		clifor.setId(Utils.tryParseToInt(txtId.getText()));
		
		if(txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.setError("name", "O campo nome não pode ser vazio");
		}
		clifor.setName(txtName.getText());
		
		if(rdioProvider.isSelected()) {
			clifor.setProvider(true);
		}else {
			clifor.setProvider(false);
		}
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		return clifor;
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
		
		if(entity.isProvider()) {
			rdioProvider.setSelected(true);
		}else {
			rdioCustomer.setSelected(true);
		}
	}
}
