package gui.company;

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
import javafx.event.ActionEvent;
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
import model.entities.Company;
import model.service.CompanyService;
import utils.Alerts;

public class CompanyViewController implements Initializable{
	
	private CompanyService service;
	public void setCompanyService(CompanyService service) {
		this.service = service;
	}


	@FXML
	private Button btnNew;
	@FXML
	public void onBtnNewAction(ActionEvent event) {
		List<Company> list = service.findByAll();
		if(list.isEmpty()) {
			Stage stage = new Stage();
			Company entity = new Company();
			loadModalView(entity, "Cadastro de empresas", stage);
		}else {
			Alerts.showAlert("Aviso", "Aviso de implementação", "Só é permitido cadastrar uma empresa. \nCadastro de mais empresa não foi implementada!", AlertType.INFORMATION);
		}
	}


	@FXML
	private TableView<Company> tblView;
	@FXML
	private TableColumn<Company, Integer> tblColumnId;
	@FXML
	private TableColumn<Company, String> tblColumnName;
	@FXML
	private TableColumn<Company, Company> tblColumnEDIT;
	@FXML
	private TableColumn<Company, Company> tblColumnDELETE;
	
	private ObservableList<Company> obsList;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializationNodes();
	}	

	private void initializationNodes() {
		tblColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tblColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		btnNew.getStyleClass().add("btn-primary");

	}


	public void updateTableView() {
		if(service == null) {
			throw new IllegalArgumentException("Serviço não instanciado");
		}
		List<Company> list = service.findByAll();
		obsList = FXCollections.observableArrayList(list);
		tblView.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}
	
	
	private void initEditButtons() {
		tblColumnEDIT.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue()));
		tblColumnEDIT.setCellFactory(p -> new TableCell<Company, Company>() {
			private final Button button = new Button();
			@Override
			protected void updateItem(Company entity, boolean empty) {
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
		tblColumnDELETE.setCellFactory(param -> new TableCell<Company, Company>() {
			private final Button button = new Button();
			
			@Override
			protected void updateItem(Company entity, boolean empty) {
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
	
	
	private void removeEntity(Company company) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Você tem certeza que deseja remover este item?");
		
		if(result.get() == ButtonType.OK) {
			if(service == null) {
				throw new IllegalStateException("Serviço não instanciado");
			}
			try {
				service.remove(company);
				updateTableView();
			} catch (DatabaseException e) {
				e.printStackTrace();
				Alerts.showAlert("Erro ao remover registro", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
	
	
	private synchronized <T> void loadModalView(Company entity, String title, Stage stage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/company/CompanyViewRegister.fxml"));
			Window window = btnNew.getScene().getWindow();
			Pane pane = loader.load();	

			Scene scene = new Scene(pane);
			scene.getStylesheets().addAll(BootstrapFX.bootstrapFXStylesheet()); 
			stage.setTitle(title);
			stage.setScene(scene);
			stage.setResizable(false);
			stage.initOwner(window);
			stage.initModality(Modality.WINDOW_MODAL);
			stage.setHeight(225.0);
			stage.setWidth(600.0);
			
			CompanyViewRegisterController controller = loader.getController();
			controller.setCompany(entity);
			controller.setCompanyService(new CompanyService());
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
