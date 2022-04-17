package gui.clifor;

import static org.mockito.Mockito.doNothing;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.kordamp.bootstrapfx.BootstrapFX;

import database.exceptions.DatabaseException;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import model.entities.Clifor;
import model.service.CliforService;
import utils.Alerts;

public class CliforViewController implements Initializable{
	
	private CliforService service;
	public void setCliforService(CliforService service) {
		this.service = service;
	}
	
	private Boolean tipo = true;
	private String nome = "";
	
	@FXML
	private Button btnNew;
	@FXML
	public void onBtnNewAction(ActionEvent event) {
		Stage stage = new Stage();
		Clifor entity = new Clifor();
		loadModalView(entity, "Cadastro de Clifor", stage);
	}

	@FXML
	private TableView<Clifor> tblView;
	@FXML
	private TableColumn<Clifor, Integer> tblColumnId;
	@FXML
	private TableColumn<Clifor, String> tblColumnProvider;
	@FXML
	private TableColumn<Clifor, String> tblColumnName;
	@FXML
	private TableColumn<Clifor, Clifor> tblColumnEDIT;
	@FXML
	private TableColumn<Clifor, Clifor> tblColumnDELETE;
	@FXML
	private RadioButton rdioClientes;
	@FXML
	private RadioButton rdioFornecedores;
	@FXML
	private ToggleGroup rdioGroup;
	@FXML
	private TextField filtroNome;
	
	
	private ObservableList<Clifor> obsList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializationNodes();
		
	}


	private void initializationNodes() {
		btnNew.getStyleClass().add("btn-primary");
		rdioClientes.setToggleGroup(rdioGroup);
		rdioFornecedores.setToggleGroup(rdioGroup);
		tblColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tblColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tblColumnProvider.setCellValueFactory( v -> {
			return new ReadOnlyStringWrapper(v.getValue().isProvider() ? "Fornecedor" : "Cliente");
		});
		txtNomeFiltroChange();
	}
	
	
	public void rdioButtonFiltroClick() {
		RadioButton rb = (RadioButton) rdioGroup.getSelectedToggle();
		if(rb.getText().equalsIgnoreCase("Clientes")) {
			this.tipo = false;
		}else {
			this.tipo = true;
		}
		updateTableFiltro(this.tipo, this.nome);
	}
	
	public void txtNomeFiltroChange() {
		filtroNome.textProperty().addListener((observable, oldValue, newValue) -> {
			this.nome = newValue;
			updateTableFiltro(this.tipo, this.nome);
		});
	}



	private void updateTableFiltro(Boolean fornecedor, String nome) {
		if(service == null) {
			throw new IllegalStateException("O serviço não foi instanciado");
		}
		
		List<Clifor> list = service.filtro(fornecedor, nome);
		obsList = FXCollections.observableArrayList(list);
		tblView.setItems(obsList);
	}


	public void updateTableView() {
		if(service == null) {
			throw new IllegalStateException("O serviço não foi instanciado");
		}
		
		List<Clifor> list = service.findAllByTipo(tipo);
		rdioFornecedores.setSelected(true);
		obsList = FXCollections.observableArrayList(list);
		tblView.setItems(obsList);
		this.nome = "";
		filtroNome.setText(nome);
		initEditButtons();
		initRemoveButtons();
	}

	
	
	private void initEditButtons() {
		tblColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tblColumnEDIT.setCellFactory(param -> new TableCell<Clifor, Clifor>() {
			private final Button button = new Button();

			@Override
			protected void updateItem(Clifor entity, boolean empty) {
				button.setGraphic(new ImageView("/assets/icons/edit16.png"));
				button.setStyle(" -fx-background-color:transparent;");
				button.setCursor(Cursor.HAND);
				super.updateItem(entity, empty);
				if (entity == null) {
					setGraphic(null);
					return;
				}

				setGraphic(button);
				button.setOnAction(e -> {
					Stage stage = new Stage();
					loadModalView(entity, "Alteração de Clifor", stage);
				});
			}
		});
	}


	private void initRemoveButtons() {
		tblColumnDELETE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tblColumnDELETE.setCellFactory(param -> new TableCell<Clifor, Clifor>() {
			private final Button button = new Button();

			@Override
			protected void updateItem(Clifor entity, boolean empty) {
				button.setGraphic(new ImageView("/assets/icons/trash16.png"));
				button.setStyle(" -fx-background-color:transparent;");
				button.setCursor(Cursor.HAND);				
				super.updateItem(entity, empty);
				if (entity == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(e -> removeEntity(entity));
			}
		});
	}

	private void removeEntity(Clifor entity) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação",
				"Você tem certeza que deseja remover este item?");

		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Serviço não instanciado");
			}
			try {
				service.remove(entity);
				updateTableView();
			} catch (DatabaseException e) {
				e.printStackTrace();
				Alerts.showAlert("Erro ao remover registro", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}


	private synchronized <T> void loadModalView(Clifor entity, String title, Stage stage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/clifor/CliforViewRegister.fxml"));
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
			
			CliforViewRegisterController controller = loader.getController();
			controller.setClifor(entity);
			controller.setCliforService(new CliforService());
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
