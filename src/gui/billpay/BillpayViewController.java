package gui.billpay;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
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
	
	private String status = "PAGAR";
	private String nome = "";
	private String valorCombobox = "Histórico";
	private LocalDate dtaInicio;
	private LocalDate dtaFinal;
	
	@FXML
	private Button btnNew;
	@FXML
	public void onBtnNewAction(ActionEvent event) {
		Stage stage = new Stage();
		Billpay entity = new Billpay();
		loadModalView("/gui/billpay/BillpayViewRegister.fxml", 650.0, 580.0, entity, "Cadastro de contas a pagar", stage, (BillpayViewRegisterController controller) -> {
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
	private TableColumn<Billpay, Billpay> tblColumnEDIT;
	@FXML
	private TableColumn<Billpay, Billpay> tblColumnDELETE;
	@FXML
	private TableColumn<Billpay, Billpay> tblColumnPAY;
	@FXML
	private RadioButton rdioPagas;
	@FXML
	private RadioButton rdioVencidas;
	@FXML
	private RadioButton rdioPagar;
	@FXML
	private ToggleGroup rdioGroup;
	@FXML
	private TextField filtroNome;
	@FXML
	private ComboBox<String> combobox = new ComboBox<String>();
	@FXML
	private DatePicker dataInicio;
	@FXML
	private DatePicker dataFinal;
	@FXML
	private Button irDataFiltro;
	@FXML
	public void onBtnIrDataFiltroClick() {
		if(dataInicio != null) {
			this.dtaInicio = dataInicio.getValue();
		}
		
		if(dataFinal != null) {
			this.dtaFinal = dataFinal.getValue();
		}
		updateTableFiltro(this.status, this.nome, this.valorCombobox, this.dtaInicio, this.dtaFinal);
		
	}
	
	private ObservableList<Billpay> obsList = null;

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializationNodes();
	}


	private void initializationNodes() {
		rdioPagas.setToggleGroup(rdioGroup);
		rdioPagar.setToggleGroup(rdioGroup);
		rdioVencidas.setToggleGroup(rdioGroup);
		filtroNome.setText("");
		btnNew.getStyleClass().add("btn-primary");
		tblColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
		Utils.formatTableColumnDate(tblColumnDate, "dd/MM/yyyy");
		tblColumnDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
		Utils.formatTableColumnDate(tblColumnDueDate, "dd/MM/yyyy");
		tblColumnHistoric.setCellValueFactory(new PropertyValueFactory<>("historic"));
		tblColumnValue.setCellValueFactory(new PropertyValueFactory<>("value"));
		Utils.formatTableColumnDouble(tblColumnValue, 2);
		tblColumnProvider.setCellValueFactory(v -> {	String s = v.getValue().getClifor().getName();
		return new ReadOnlyStringWrapper(s);
		});
		Utils.formatDatePicker(dataInicio, "dd/MM/yyyy");
		Utils.formatDatePicker(dataFinal, "dd/MM/yyyy");
		dataInicio.setPromptText("Data Inicial");
		dataFinal.setPromptText("Data Final");
		
		combobox.setItems(FXCollections.observableArrayList("Histórico", "Fornecedor", "Vencimento"));
		combobox.setValue(valorCombobox);
		
		setarPlaceHolder(valorCombobox);
		
		txtNomeFiltroChange();
		rdioPagar.setSelected(true);

	}
	
	
	private void setarPlaceHolder(String valor) {
		if(valor.equals("Histórico")) {
			filtroNome.setPromptText("Digite o texto para pesquisar no histórico");
		}else if(valor.equals("Fornecedor")) {
			filtroNome.setPromptText("Digite o nome do fornecedor para pesquisa");
		}else {
			filtroNome.setPromptText("Data inicial/final sem pontuação, no formato DDMMAAAA");
		}
		
	}


	public void txtNomeFiltroChange() {
		filtroNome.textProperty().addListener((observable, oldValue, newValue) -> {
			this.nome = newValue;
			updateTableFiltro(this.status, this.nome, this.valorCombobox, this.dtaInicio, this.dtaFinal);
		});
	}
	
	public void rdioButtonFiltroClick() {
		RadioButton rb = (RadioButton) rdioGroup.getSelectedToggle();
		if(rb.getText().equalsIgnoreCase("Quitadas")) {
			this.status = "QUITADA";
		}else if(rb.getText().equalsIgnoreCase("Vencidas")){
			this.status = "VENCIDA";
		}else {
			this.status = "PAGAR";
		}
		updateTableFiltro(this.status, this.nome, this.valorCombobox, this.dtaInicio, this.dtaFinal);
	}
	
	
	private void updateTableFiltro(String status, String nome, String combobox, LocalDate inicio, LocalDate fim) {
		if(service == null) {
			throw new IllegalStateException("O serviço não foi instanciado");
		}
		
		List<Billpay> list = service.filtro(status, nome, combobox, inicio, fim);
		obsList = FXCollections.observableArrayList(list);
		tblView.setItems(obsList);
	}
	
	
	public void getComboBoxValue() {
		this.valorCombobox = combobox.getValue();
		setarPlaceHolder(valorCombobox);
	}

	

	public void updateTableView() {
		if(service == null) {
			throw new IllegalStateException("O serviço não foi instanciado");
		}
		List<Billpay> list = service.filtro(this.status, this.nome, this.valorCombobox, this.dtaInicio, this.dtaFinal);
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
					loadModalView("/gui/billpay/BillpayViewRegister.fxml", 650.0, 580.0, entity, "Alteração de contas a pagar", stage, (BillpayViewRegisterController controller) -> {
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
					loadModalView("/gui/payment/PaymentViewRegister.fxml", 650.0, 270.0, entity, "Pagamento de contas", stage, (PaymentViewRegisterController controller) -> {
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
