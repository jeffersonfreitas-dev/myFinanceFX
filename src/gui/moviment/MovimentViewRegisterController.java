package gui.moviment;

import java.net.URL;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import database.exceptions.DatabaseException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.entities.Moviment;
import model.exceptions.RecordAlreadyRecordedException;
import model.exceptions.ValidationException;
import model.service.MovimentService;
import utils.Alerts;
import utils.Utils;

public class MovimentViewRegisterController implements Initializable{
	
	private Moviment entity;
	public void setMoviment(Moviment moviment) {
		this.entity = moviment;
	}
	
	private MovimentService service;
	public void setService(MovimentService service) {
		this.service = service;
	}
	
	@FXML
	private TextField txtId;
	@FXML
	private DatePicker pkDate;
	@FXML
	private DatePicker pkDateFim;
	@FXML
	private Label lblErroAccount;
	@FXML
	private Button btnSave;
	@FXML
	public void onBtnSaveAction(ActionEvent event) {
		if(entity == null || service == null) {
			throw new IllegalStateException("Serviço e/ou Entidade não instanciado");
		}
		
		try {
			Moviment mov = getFormDate();
			service.save(mov);
			Stage stage = Utils.getCurrentStage(event);
			stage.getOnCloseRequest().handle(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
			stage.close();
		} catch (ValidationException e) {
			e.printStackTrace();
			setErrorsMessage(e.getErrors());
		} catch (IllegalStateException e) {
			e.printStackTrace();
			Alerts.showAlert("Erro ao salvar o registro", null, e.getMessage(), AlertType.ERROR);
		} catch (DatabaseException e) {
			e.printStackTrace();
			Alerts.showAlert("Erro ao salvar o registro", null, e.getMessage(), AlertType.ERROR);
		} catch (RecordAlreadyRecordedException e) {
			e.printStackTrace();
			Alerts.showAlert("Erro ao salvar o registro", null, e.getMessage(), AlertType.ERROR);
		}
	}
	

	private Moviment getFormDate() {
		Moviment mov = new Moviment();
		if(entity == null) {
			throw new IllegalStateException("Entidade não instanciada");
		}
		ValidationException exception = new ValidationException("");
		mov.setId(Utils.tryParseToInt(txtId.getText()));
		
		if(pkDate.getValue() == null) {
			exception.setError("date", "Informe a data inicial");
		}else {
			mov.setDateBeginner(Date.from(pkDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
		}
		
		if(pkDateFim.getValue() == null) {
			exception.setError("dateFim", "Informe a data final");
		}else {
			mov.setDateFinish(Date.from(pkDateFim.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
		}
		
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		return mov;
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
		btnSave.setGraphic(new ImageView("/assets/icons/save16.png"));
		btnCancel.setGraphic(new ImageView("/assets/icons/cancel16.png"));
		Utils.formatDatePicker(pkDate, "dd/MM/yyyy");
		pkDate.setPromptText("DD/MM/AAAA");
		Utils.formatDatePicker(pkDateFim, "dd/MM/yyyy");
		pkDateFim.setPromptText("DD/MM/AAAA");

	}
	
	
	private void setErrorsMessage(Map<String, String> errors) {
		Set<String> keys = errors.keySet();
		
		lblErroAccount.setText("");
		
		if(keys.contains("date")) {
			lblErroAccount.setText(errors.get("date"));
		}
		if(keys.contains("dateFim")) {
			lblErroAccount.setText(errors.get("dateFim"));
		}
	}

}
