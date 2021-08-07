package gui.bank;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.kordamp.bootstrapfx.BootstrapFX;

import database.exceptions.DatabaseException;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import model.entities.Bank;
import model.service.BankService;
import utils.Alerts;

public class BankViewController implements Initializable{
	
	private BankService service;
	
	public void setBankService(BankService service) {
		this.service = service;
	}
	

	@FXML
	private Button btnNew;
	@FXML
	public void onBtnNewAction() {
		Stage stage = new Stage();
		Bank entity = new Bank();
		loadModalView(entity, "Cadastro de bancos", stage);
	}
	
	@FXML
	private TableView<Bank> tblView;
	@FXML
	private TableColumn<Bank, Integer> tblColumnId;
	@FXML
	private TableColumn<Bank, String> tblColumnCode;
	@FXML
	private TableColumn<Bank, String> tblColumnName;
	@FXML
	private TableColumn<Bank, Bank> tblColumnEDIT;
	@FXML
	private TableColumn<Bank, Bank> tblColumnDELETE;
	
	private ObservableList<Bank> obsList;
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initialization();
	}
	
	
	public void updateTableView() {
		if(service == null) {
			throw new IllegalArgumentException("Serviço não instanciado");
		}
		
		List<Bank> list = service.findAll();
		obsList = FXCollections.observableList(list);
		tblView.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}


	private void initialization() {
		tblColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tblColumnCode.setCellValueFactory(new PropertyValueFactory<>("code"));
		tblColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		btnNew.getStyleClass().add("btn-primary");
	}
	
	
	private void initEditButtons() {
		tblColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tblColumnEDIT.setCellFactory(param -> new TableCell<Bank, Bank>() {
			private final Button button = new Button();
			@Override
			protected void updateItem(Bank entity, boolean empty) {
				button.setGraphic(new ImageView("/assets/icons/edit16.png"));
				button.setStyle(" -fx-background-color:transparent;");
				button.setCursor(Cursor.HAND);
				super.updateItem(entity, empty);
				if(entity == null) {
					setGraphic(null);
					return;
				}
				
				setGraphic(button);
				button.setOnAction( e -> {
					Stage stage = new Stage();
					loadModalView(entity, "Alteração de banco", stage);
				});
			}
		});
	}
	
	
	private void initRemoveButtons() {
		tblColumnDELETE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tblColumnDELETE.setCellFactory(param -> new TableCell<Bank, Bank>() {
			private final Button button = new Button();
			
			@Override
			protected void updateItem(Bank entity, boolean empty) {
				button.setGraphic(new ImageView("/assets/icons/trash16.png"));
				button.setStyle(" -fx-background-color:transparent;");
				button.setCursor(Cursor.HAND);
				super.updateItem(entity, empty);
				if(entity == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(e -> removeEntity(entity));
			}
		});
	}
	
	
	private void removeEntity(Bank bank) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Você tem certeza que deseja remover este item?");
		
		if(result.get() == ButtonType.OK) {
			if(service == null) {
				throw new IllegalStateException("Serviço não instanciado");
			}
			try {
				service.remove(bank);
				updateTableView();
			} catch (DatabaseException e) {
				e.printStackTrace();
				Alerts.showAlert("Erro ao remover registro", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
	
	
	private synchronized <T> void loadModalView(Bank entity, String title, Stage stage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/bank/BankViewRegister.fxml"));
			Window window = btnNew.getScene().getWindow();
			Pane pane = loader.load();	

			Scene scene = new Scene(pane);
			scene.getStylesheets().addAll(BootstrapFX.bootstrapFXStylesheet()); 
			stage.setTitle(title);
			stage.setScene(scene);
			stage.setResizable(false);
			stage.initOwner(window);
			stage.initModality(Modality.WINDOW_MODAL);
			stage.setHeight(270.0);
			stage.setWidth(600.0);
			
			BankViewRegisterController controller = loader.getController();
			controller.setBank(entity);
			controller.setBankService(new BankService());
			controller.updateFormData();
			
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					updateTableView();
				}
			});
			
			stage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("Erro", "Erro ao abrir a janela", e.getMessage(), AlertType.ERROR);
		}
	}
	
}
