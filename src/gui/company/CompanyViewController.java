package gui.company;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import database.exceptions.DatabaseException;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.entities.Company;
import model.service.CompanyService;
import utils.Alerts;
import utils.Utils;

public class CompanyViewController implements Initializable{
	
	private CompanyService service;
	public void setCompanyService(CompanyService service) {
		this.service = service;
	}


	@FXML
	private Button btnNew;
	@FXML
	public void onBtnNewAction(ActionEvent event) {
		Stage stage = Utils.getCurrentStage(event);
		stage.setTitle("Cadastro de empresa");
		Company company = new Company();
		loadView(company, "/gui/company/CompanyViewRegister.fxml", stage.getScene());
	}


	@FXML
	private TableView<Company> tblCompanies;
	@FXML
	private TableColumn<Company, Integer> columnId;
	@FXML
	private TableColumn<Company, String> columnName;
	@FXML
	private TableColumn<Company, Company> columnEDIT;
	@FXML
	private TableColumn<Company, Company> columnDELETE;
	
	private ObservableList<Company> obsList;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializationNodes();
		
	}

	
	private void loadView(Company company, String pathView, Scene scene) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(pathView));
			VBox box = loader.load();
			
			CompanyViewRegisterController controller = loader.getController();
			controller.setCompany(company);
			controller.setCompanyService(new CompanyService());
			controller.updateFormData();
			
			VBox mainBox = (VBox) scene.getRoot();
			mainBox.getChildren().clear();
			mainBox.getChildren().addAll(box.getChildren());
		}catch(IOException e) {
			e.printStackTrace();
			Alerts.showAlert("Erro", "Erro ao carregar a janela", e.getMessage(), AlertType.ERROR);
		}
		
	}	

	private void initializationNodes() {
		columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		btnNew.setGraphic(new ImageView("/assets/icons/new16.png"));
		initEditButtons();
		initRemoveButtons();
	}


	public void updateTableView() {
		if(service == null) {
			throw new IllegalArgumentException("Serviço não instanciado");
		}
		List<Company> list = service.findByAll();
		obsList = FXCollections.observableArrayList(list);
		tblCompanies.setItems(obsList);
	}
	
	
	private void initEditButtons() {
		columnEDIT.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue()));
		columnEDIT.setCellFactory(p -> new TableCell<Company, Company>() {
			private final Button button = new Button();
			@Override
			protected void updateItem(Company entity, boolean empty) {
				button.setGraphic(new ImageView("/assets/icons/edit16.png"));
				super.updateItem(entity, empty);
				if(entity == null) {
					setGraphic(null);
					return;
				}
				
				setGraphic(button);
				button.setOnAction( e -> {
					Stage stage = Utils.getCurrentStage(e);
					stage.setTitle("Alteração de empresa");
					loadView(entity, "/gui/company/CompanyViewRegister.fxml", Utils.getCurrentScene(e));
				});
			}
		});
	}
	
	
	private void initRemoveButtons() {
		columnDELETE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		columnDELETE.setCellFactory(param -> new TableCell<Company, Company>() {
			private final Button button = new Button();
			
			@Override
			protected void updateItem(Company entity, boolean empty) {
				button.setGraphic(new ImageView("/assets/icons/trash16.png"));
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

}
