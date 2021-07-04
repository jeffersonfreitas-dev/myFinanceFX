package gui.billpay;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/billpay/BillpayViewRegister.fxml"));
		Window parent = btnNew.getScene().getWindow();
		Billpay billpay = new Billpay();
		loadViewModal(billpay, loader , parent,"Cadastro de conta a pagar", 600.0, 800.0, (BillpayViewRegisterController controller) -> {
			controller.setBillpayService(new BillpayService());
			controller.setCompanyService(new CompanyService());
			controller.setCliforService(new CliforService());
			controller.setAccountPlanService(new AccountPlanService());
			controller.setBillpay(billpay);
			controller.loadAssociateObjects();
			controller.updateFormData();
			controller.setConfigPortiont(billpay.getId());			
		});
	}


	@FXML
	private Button btnClose;
	@FXML
	public void onBtnCloseAction(ActionEvent event) {
		Stage stage = Utils.getCurrentStage(event);
		loadView(stage);
	}


	@FXML
	private TableView<Billpay> tblBillpay;
	@FXML
	private TableColumn<Billpay, Integer> columnInvoice;
	@FXML
	private TableColumn<Billpay, Date> columnDate;
	@FXML
	private TableColumn<Billpay, Date> columnDueDate;
	@FXML
	private TableColumn<Billpay, String> columnHistoric;
	@FXML
	private TableColumn<Billpay, Double> columnValue;
	@FXML
	private TableColumn<Billpay, Integer> columnPortion;
	@FXML
	private TableColumn<Billpay, String> columnStatus;
	@FXML
	private TableColumn<Billpay, Billpay> columnEDIT;
	@FXML
	private TableColumn<Billpay, Billpay> columnREMOVE;
	@FXML
	private TableColumn<Billpay, Billpay> columnPAY;
	@FXML
	private BarChart<Billpay, Billpay> chartBillStatus;
	@FXML
	private BarChart<Billpay, Billpay> chartBillXReceive;
	@FXML
	private PieChart chartBillToCompany;
	
	private ObservableList<Billpay> obsList = null;

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializationNodes();
	}


	private void initializationNodes() {
	
		btnNew.setGraphic(new ImageView("/assets/icons/new16.png"));
		btnClose.setGraphic(new ImageView("/assets/icons/cancel16.png"));
		columnInvoice.setCellValueFactory(new PropertyValueFactory<>("invoice"));
		columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
		Utils.formatTableColumnDate(columnDate, "dd/MM/yyyy");
		columnDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
		Utils.formatTableColumnDate(columnDueDate, "dd/MM/yyyy");
		columnHistoric.setCellValueFactory(new PropertyValueFactory<>("historic"));
		columnValue.setCellValueFactory(new PropertyValueFactory<>("value"));
		Utils.formatTableColumnDouble(columnValue, 2);
		columnPortion.setCellValueFactory(new PropertyValueFactory<>("portion"));
		columnStatus.setCellValueFactory(v -> {	String s = v.getValue().getStatus(); Utils.formatTableColumnStatus(columnStatus, s);
			return new ReadOnlyStringWrapper(s);
		});
		
		initRemoveButtons();
		initEditButtons();
		initPaymentButtons();
	}


	public void updateTableView() {
		if(service == null) {
			throw new IllegalStateException("O serviço não foi instanciado");
		}
		List<Billpay> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tblBillpay.setItems(obsList);
		initializationNodes();
	}
	
	
	private synchronized <T> void loadViewModal(Billpay billpay, FXMLLoader loader, Window parent, String title, double height, double width,
			Consumer<T> initialization) {
		try {
			Pane pane = loader.load();
			Stage stage = new Stage();
			stage.setTitle(title);
			stage.setScene(new Scene(pane));
			stage.setResizable(false);
			stage.initOwner(parent);
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
	
	
	private void loadView(Stage stage) {
		try {
			VBox mainBox =  (VBox) ((ScrollPane) stage.getScene().getRoot()).getContent();
			Node mnu = mainBox.getChildren().get(0);
			mainBox.getChildren().clear();
			mainBox.getChildren().add(mnu);
		} catch (Exception e) {
			e.printStackTrace();
			Alerts.showAlert("Erro", "Erro ao abrir a janela", e.getMessage(), AlertType.ERROR);
		}
	}
	
	
	private void initEditButtons() {
		columnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		columnEDIT.setCellFactory(param -> new TableCell<Billpay, Billpay>() {
			private final Button button = new Button();
			@Override
			protected void updateItem(Billpay entity, boolean empty) {
				button.setGraphic(new ImageView("/assets/icons/edit16.png"));
				super.updateItem(entity, empty);
				if(entity == null || entity.getStatus().equals("PAGO")) {
					setGraphic(null);
					return;
				}
				
				setGraphic(button);
				button.setOnAction( e -> {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/billpay/BillpayViewRegister.fxml"));
					Window parent = btnNew.getScene().getWindow();
					loadViewModal(entity, loader , parent,"Cadastro de conta a pagar", 600.0, 800.0, (BillpayViewRegisterController controller) -> {
						controller.setBillpayService(new BillpayService());
						controller.setCompanyService(new CompanyService());
						controller.setCliforService(new CliforService());
						controller.setAccountPlanService(new AccountPlanService());
						controller.setBillpay(entity);
						controller.loadAssociateObjects();
						controller.updateFormData();
						controller.setConfigPortiont(entity.getId());							
					});
				});
			}
		});
	}
	
	
	private void initPaymentButtons() {
		columnPAY.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		columnPAY.setCellFactory(param -> new TableCell<Billpay, Billpay>() {
			private final Button button = new Button();
			
			@Override
			protected void updateItem(Billpay entity, boolean empty) {
				button.setGraphic(new ImageView("/assets/icons/payment16.png"));
				super.updateItem(entity, empty);
				if(entity == null || entity.getStatus().equals("PAGO")) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(e -> {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/payment/PaymentViewRegister.fxml"));
					Window window = button.getScene().getWindow();
					loadViewModal(entity, loader, window, "Pagamento de conta", 245.0, 500.0, (PaymentViewRegisterController controller) -> {
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
		columnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		columnREMOVE.setCellFactory(param -> new TableCell<Billpay, Billpay>() {
			private final Button button = new Button();
			
			@Override
			protected void updateItem(Billpay entity, boolean empty) {
				button.setGraphic(new ImageView("/assets/icons/trash16.png"));
				super.updateItem(entity, empty);
				if(entity == null || entity.getStatus().equals("PAGO")) {
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
	
//	private void paymentBill(Billpay entity) {
//		Optional<ButtonType> result = Alerts.showConfirmation("Pagamento de conta", "Deseja quitar esta conta a pagar?");
//		
//		if(result.get() == ButtonType.OK) {
//			if(service == null) {
//				throw new IllegalStateException("Serviço não instanciado");
//			}
//			
//			try {
//				service.payment(entity);
//				updateTableView();
//			} catch (DatabaseException e) {
//				e.printStackTrace();
//				Alerts.showAlert("Erro ao remover registro", null, e.getMessage(), AlertType.ERROR);
//			}
//		}
//	}

}
