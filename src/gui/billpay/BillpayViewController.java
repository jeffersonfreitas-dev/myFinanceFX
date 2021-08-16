package gui.billpay;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import org.kordamp.bootstrapfx.BootstrapFX;

import database.exceptions.DatabaseException;
import gui.payment.PaymentViewRegisterController;
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
import model.entities.Billpay;
import model.entities.Payment;
import model.service.AccountPlanService;
import model.service.BankAccountService;
import model.service.BillpayService;
import model.service.CliforService;
import model.service.CompanyService;
import model.service.PaymentService;
import utils.Alerts;
import utils.Utils;

public class BillpayViewController implements Initializable{
	
	private BillpayService service;
	public void setBillpayService(BillpayService service) {
		this.service = service;
	}
	
	@FXML
	private Button btnNew;
	@FXML
	public void onBtnNewAction(ActionEvent event) {
		Stage stage = new Stage();
		Billpay entity = new Billpay();
		loadModalView("/gui/billpay/BillpayViewRegister.fxml", 600.0, 585.0, entity, "Cadastro de contas a pagar", stage, (BillpayViewRegisterController controller) -> {
				controller.setBillpay(entity);
				controller.setBillpayService(new BillpayService());
				controller.setCliforService(new CliforService());
				controller.setAccountPlanService(new AccountPlanService());
				controller.setCompanyService(new CompanyService());
				controller.updateFormData();
				controller.loadAssociateObjects();					
			});
	}


	@FXML
	private TableView<Billpay> tblView;
	@FXML
	private TableColumn<Billpay, Integer> tblColumnInvoice;
	@FXML
	private TableColumn<Billpay, Date> tblColumnDate;
	@FXML
	private TableColumn<Billpay, Date> tblColumnDueDate;
	@FXML
	private TableColumn<Billpay, String> tblColumnHistoric;
	@FXML
	private TableColumn<Billpay, String> tblColumnProvider;
	@FXML
	private TableColumn<Billpay, Double> tblColumnValue;
	@FXML
	private TableColumn<Billpay, String> tblColumnStatus;
	@FXML
	private TableColumn<Billpay, Billpay> tblColumnEDIT;
	@FXML
	private TableColumn<Billpay, Billpay> tblColumnDELETE;
	@FXML
	private TableColumn<Billpay, Billpay> tblColumnPAY;
	
	private ObservableList<Billpay> obsList = null;

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializationNodes();
	}


	private void initializationNodes() {
	
		btnNew.getStyleClass().add("btn-primary");
		tblColumnInvoice.setCellValueFactory(new PropertyValueFactory<>("invoice"));
		tblColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
		Utils.formatTableColumnDate(tblColumnDate, "dd/MM/yyyy");
		tblColumnDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
		Utils.formatTableColumnDate(tblColumnDueDate, "dd/MM/yyyy");
		tblColumnHistoric.setCellValueFactory(new PropertyValueFactory<>("historic"));
		tblColumnValue.setCellValueFactory(new PropertyValueFactory<>("value"));
		Utils.formatTableColumnDouble(tblColumnValue, 2);
		tblColumnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
		tblColumnProvider.setCellValueFactory(v -> {	String s = v.getValue().getClifor().getName();
		return new ReadOnlyStringWrapper(s);
		});
	}


	public void updateTableView() {
		if(service == null) {
			throw new IllegalStateException("O serviço não foi instanciado");
		}
		List<Billpay> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tblView.setItems(obsList);
		initializationNodes();
		initRemoveButtons();
		initEditButtons();
		initPaymentButtons();
	}
	
	
	private void initEditButtons() {
		tblColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tblColumnEDIT.setCellFactory(param -> new TableCell<Billpay, Billpay>() {
			private final Button button = new Button();
			@Override
			protected void updateItem(Billpay entity, boolean empty) {
				button.setGraphic(new ImageView("/assets/icons/edit16.png"));
				button.setStyle(" -fx-background-color:transparent;");
				button.setCursor(Cursor.HAND);
				super.updateItem(entity, empty);
				if(entity == null || entity.getStatus().equals("P")) {
					setGraphic(null);
					return;
				}
				
				setGraphic(button);
				button.setOnAction( e -> {
					Stage stage = new Stage();
					loadModalView("/gui/billpay/BillpayViewRegister.fxml", 600.0, 585.0, entity, "Alteração de contas a pagar", stage, (BillpayViewRegisterController controller) -> {
						controller.setBillpay(entity);
						controller.setBillpayService(new BillpayService());
						controller.setCliforService(new CliforService());
						controller.setAccountPlanService(new AccountPlanService());
						controller.setCompanyService(new CompanyService());
						controller.updateFormData();
						controller.loadAssociateObjects();					
					});
				});
			}
		});
	}
	
	
	private void initPaymentButtons() {
		tblColumnPAY.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tblColumnPAY.setCellFactory(param -> new TableCell<Billpay, Billpay>() {
			private final Button button = new Button();
			
			@Override
			protected void updateItem(Billpay entity, boolean empty) {
				button.setGraphic(new ImageView("/assets/icons/payment16.png"));
				button.setStyle(" -fx-background-color:transparent;");
				button.setCursor(Cursor.HAND);
				super.updateItem(entity, empty);
				if(entity == null || entity.getStatus().equals("P")) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(e -> {
					Stage stage = new Stage();
					loadModalView("/gui/payment/PaymentViewRegister.fxml", 600.0, 270.0, entity, "Pagamento de contas", stage, (PaymentViewRegisterController controller) -> {
						controller.setBillpayService(new BillpayService());
						controller.setAccountService(new BankAccountService());
						controller.setService(new PaymentService());
						controller.setPayment(new Payment());
						controller.loadAssociateObjects();
						controller.setBillpay(entity);					
					});
				});
			}
		});
	}


	private void initRemoveButtons() {
		tblColumnDELETE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tblColumnDELETE.setCellFactory(param -> new TableCell<Billpay, Billpay>() {
			private final Button button = new Button();
			
			@Override
			protected void updateItem(Billpay entity, boolean empty) {
				button.setGraphic(new ImageView("/assets/icons/trash16.png"));
				button.setStyle(" -fx-background-color:transparent;");
				button.setCursor(Cursor.HAND);
				super.updateItem(entity, empty);
				if(entity == null || entity.getStatus().equals("P")) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(e -> removeEntity(entity));
			}
		});
	}
	
	
	private void removeEntity(Billpay entity) {
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
	
	
	private synchronized <T> void loadModalView(String path, Double width, Double height, Billpay entity, String title, Stage stage, Consumer<T> initialization) {
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
