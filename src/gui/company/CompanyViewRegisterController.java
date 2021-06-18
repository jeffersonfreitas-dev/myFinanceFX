package gui.company;

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
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.entities.Company;
import model.exceptions.RecordAlreadyRecordedException;
import model.exceptions.ValidationException;
import model.service.CompanyService;
import utils.Alerts;
import utils.Constraints;
import utils.Utils;

public class CompanyViewRegisterController implements Initializable{
	
	private Company entity;
	public void setCompany (Company entity) {
		this.entity = entity;
	}
	
	private CompanyService service;
	public void setCompanyService(CompanyService service) {
		this.service = service;
	}
	
	@FXML
	private Label lblErrorName;
	@FXML
	private Button btnSave;
	@FXML
	public void onBtnSaveAction(ActionEvent event) {
		if(service == null) {
			throw new IllegalStateException("O serviço não foi instanciado");
		}
		
		try {
			Company company = getFormData();
			service.saveOrUpdate(company);
			Stage stage = Utils.getCurrentStage(event);
			stage.setTitle("Lista de empresas");
			loadView("/gui/company/CompanyView.fxml", stage.getScene());
		} catch (RecordAlreadyRecordedException e) {
			Alerts.showAlert("Registro já cadastrado", null, e.getMessage(), AlertType.INFORMATION);
		} catch (ValidationException e) {
			e.printStackTrace();
			setMessagesError(e.getErrors());
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
		stage.setTitle("Lista de empresas");
		loadView("/gui/company/CompanyView.fxml", stage.getScene());
	}

	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializationNodes();
	}


	private void initializationNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 60);
		btnSave.setGraphic(new ImageView("/assets/icons/save16.png"));
		btnCancel.setGraphic(new ImageView("/assets/icons/cancel16.png"));
	}
	
	
	private void loadView(String pathView, Scene scene) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(pathView));
			VBox box = loader.load();
			
			VBox mainBox = (VBox) scene.getRoot();
			mainBox.getChildren().clear();
			mainBox.getChildren().addAll(box.getChildren());
			
			CompanyViewController controller = loader.getController();
			controller.setCompanyService(new CompanyService());
			controller.updateTableView();
		}catch(IOException e) {
			e.printStackTrace();
			Alerts.showAlert("Erro", "Erro ao carregar a janela", e.getMessage(), AlertType.ERROR);
		}
	}	

	
	private Company getFormData() {
		Company c = new Company();
		ValidationException exception = new ValidationException("");
		c.setId(Utils.tryParseToInt(txtId.getText()));
		
		if(txtName == null || txtName.getText().trim().equals("")) {
			exception.setError("name", "O campo nome não pode ser vazio");
		}
		c.setName(txtName.getText());
		
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		return c;
	}
	
	
	private void setMessagesError(Map<String, String> errors) {
		Set<String> keys = errors.keySet();
		
		if(keys.contains("name")) {
			lblErrorName.setText(errors.get("name"));
		}
		
	}


	public void updateFormData() {
		if(entity == null) {
			throw new IllegalStateException("Entidade não foi instanciada");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}
}
