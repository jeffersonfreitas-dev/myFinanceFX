package gui.moviment;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import org.kordamp.bootstrapfx.BootstrapFX;

import database.exceptions.DatabaseException;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
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
import model.entities.Moviment;
import model.service.MovimentService;
import utils.Alerts;
import utils.Utils;

public class MovimentViewController implements Initializable{
	
	private MovimentService service;
	public void setMovimentService(MovimentService service) {
		this.service = service;
	}
	
	@FXML
	private Button btnNew;
	@FXML
	public void onBtnNewAction() {

		Stage stage = new Stage();
		Moviment entity = new Moviment();
		loadModalView("/gui/moviment/MovimentViewRegister.fxml", 600.0, 250.0, entity, "Cadastro de movimentação", stage, (MovimentViewRegisterController controller) -> {
			controller.setService(new MovimentService());
			controller.setMoviment(entity);				
			});
	}


	@FXML
	private TableView<Moviment> tblView;
	@FXML
	private TableColumn<Moviment, Integer> tblColumnCode;
	@FXML
	private TableColumn<Moviment, Date> tblColumnDateBeginner;
	@FXML
	private TableColumn<Moviment, Date> tblColumnDateFinish;
	@FXML
	private TableColumn<Moviment, Double> tblColumnValueFinish;
	@FXML
	private TableColumn<Moviment, Double> tblColumnBalance;
	@FXML
	private TableColumn<Moviment, Double> tblColumnAplicacao;
	@FXML
	private TableColumn<Moviment, Double> tblColumnResgate;
	@FXML
	private TableColumn<Moviment, Double> tblColumnPoupanca;
	@FXML
	private TableColumn<Moviment, Double> tblColumnValueBeginner;
	@FXML
	private TableColumn<Moviment, String> tblColumnName;
	@FXML
	private TableColumn<Moviment, String> tblColumnCompany;
	@FXML
	private TableColumn<Moviment, Moviment> tblColumnDELETE;
	@FXML
	private TableColumn<Moviment, Moviment> tblColumnCLOSE;
	@FXML
	private TableColumn<Moviment, String> tblColumnStatus;
	
	private ObservableList<Moviment> obsList = null;

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializationNodes();
	}


	private void initializationNodes() {
		btnNew.getStyleClass().add("btn-primary");		
		tblColumnCode.setCellValueFactory(new PropertyValueFactory<>("id"));
		tblColumnDateBeginner.setCellValueFactory(new PropertyValueFactory<>("dateBeginner"));
		Utils.formatTableColumnDate(tblColumnDateBeginner, "dd/MM/yyyy");
		tblColumnDateFinish.setCellValueFactory(new PropertyValueFactory<>("dateFinish"));
		Utils.formatTableColumnDate(tblColumnDateFinish, "dd/MM/yyyy");
		tblColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tblColumnBalance.setCellValueFactory(new PropertyValueFactory<>("balanceMoviment"));
		Utils.formatTableColumnDouble(tblColumnValueBeginner, 2);
		tblColumnValueFinish.setCellValueFactory(new PropertyValueFactory<>("valueFinish"));
		Utils.formatTableColumnDouble(tblColumnValueFinish, 2);
		tblColumnValueBeginner.setCellValueFactory(new PropertyValueFactory<>("valueBeginner"));
		Utils.formatTableColumnDouble(tblColumnValueBeginner, 2);
		tblColumnPoupanca.setCellValueFactory(new PropertyValueFactory<>("valuePoupanca"));
		Utils.formatTableColumnDouble(tblColumnPoupanca, 2);
		tblColumnAplicacao.setCellValueFactory(new PropertyValueFactory<>("valueAplicacao"));
		Utils.formatTableColumnDouble(tblColumnAplicacao, 2);
		tblColumnResgate.setCellValueFactory(new PropertyValueFactory<>("valueResgate"));
		Utils.formatTableColumnDouble(tblColumnResgate, 2);
		tblColumnStatus.setCellValueFactory(v -> {
			String result = "";
			result = v.getValue().isClosed() ? "FECHADO" : "ABERTO";
			return new ReadOnlyStringWrapper(result);
		});
	}


	public void updateTableView() {
		if(service == null) {
			throw new IllegalStateException("O serviço não foi instanciado");
		}
		List<Moviment> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tblView.setItems(obsList);
		initializationNodes();
		initClosingButtons();
		initRemoveButtons();
	}
	

	private void initClosingButtons() {
		tblColumnCLOSE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tblColumnCLOSE.setCellFactory(param -> new TableCell<Moviment, Moviment>() {
			private final Button button = new Button();
			
			@Override
			protected void updateItem(Moviment entity, boolean empty) {
				button.setGraphic(new ImageView("/assets/icons/payment16.png"));
				button.setStyle(" -fx-background-color:transparent;");
				button.setCursor(Cursor.HAND);
				super.updateItem(entity, empty);
				if(entity == null || entity.isClosed()) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(e -> closeMoviment(entity));
			}
		});
	}
	
	
	private void closeMoviment(Moviment entity) {
		Optional<ButtonType> opt = Alerts.showConfirmation("Confirmação", "Você tem certeza que deseja fechar este movimento?");
		if(opt.get() == ButtonType.OK) {
			if(service == null) {
				throw new IllegalStateException("Serviço não instanciado");
			}
			try {
				service.closeMoviment(entity);
				updateTableView();
			} catch (DatabaseException e) {
				e.printStackTrace();
				Alerts.showAlert("Erro ao fechar o movimento", null, e.getMessage(), AlertType.ERROR);
			}			
			
		}
	}
	
	private void initRemoveButtons() {
		tblColumnDELETE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tblColumnDELETE.setCellFactory(param -> new TableCell<Moviment, Moviment>() {
			private final Button button = new Button();
			
			@Override
			protected void updateItem(Moviment entity, boolean empty) {
				button.setGraphic(new ImageView("/assets/icons/trash16.png"));
				button.setStyle(" -fx-background-color:transparent;");
				button.setCursor(Cursor.HAND);
				super.updateItem(entity, empty);
				if(entity == null || entity.isClosed()) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(e -> removeEntity(entity));
			}
		});
	}
	
	
	private void removeEntity(Moviment entity) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Você tem certeza que deseja remover este item?");
		
		if(result.get() == ButtonType.OK) {
			if(service == null) {
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
	
	
	private synchronized <T> void loadModalView(String path, Double width, Double height, Moviment entity, String title, Stage stage, Consumer<T> initialization) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
			Window window = btnNew.getScene().getWindow();
			Pane pane = loader.load();	

			Scene scene = new Scene(pane);
			scene.getStylesheets().addAll(BootstrapFX.bootstrapFXStylesheet()); 
			stage.setTitle(title);
			stage.setScene(scene);
			stage.setResizable(false);
			stage.initOwner(window);
			stage.initModality(Modality.WINDOW_MODAL);
			stage.setHeight(height);
			stage.setWidth(width);
			
			
			T controller = loader.getController();
			initialization.accept(controller);
			
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
