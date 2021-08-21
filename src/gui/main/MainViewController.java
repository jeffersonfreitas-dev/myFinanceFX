package gui.main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.accountPlan.AccountPlanViewController;
import gui.bank.BankViewController;
import gui.bankAccount.BankAccountViewController;
import gui.bankAgence.BankAgenceViewController;
import gui.billpay.BillpayViewController;
import gui.clifor.CliforViewController;
import gui.company.CompanyViewController;
import gui.dashboard.DashboardController;
import gui.moviment.MovimentViewController;
import gui.receivable.ReceivableViewController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import model.service.AccountPlanService;
import model.service.BankAccountService;
import model.service.BankAgenceService;
import model.service.BankService;
import model.service.BillpayService;
import model.service.CliforService;
import model.service.CompanyService;
import model.service.DashboardService;
import model.service.MovimentService;
import model.service.ReceivableService;
import utils.Alerts;

public class MainViewController implements Initializable{
	
	@FXML
	private Label versao;
	@FXML
	private Label nomeTela;
	@FXML
	private Label nomeUsuario;
	
	@FXML
	private MenuBar mnuMain;

	
	@FXML
	private Hyperlink linkAbout;
	@FXML
	public void onlinkAboutAction(ActionEvent event) {
		loadView("/gui/about/AboutView.fxml",  "Sobre o sistema", x -> {}, "");
	}
	
	@FXML
	private Hyperlink linkDashboard;
	@FXML
	public void onlinkDashboard() {
		loadViewteste("/gui/dashboard/Dashboard.fxml",  "DASHBOARD");
	}

	@FXML
	private Hyperlink linkBank;
	@FXML
	public void onlinkBankAction() {
		loadView("/gui/bank/BankView.fxml",  "Lista de bancos", (BankViewController controller) -> {
			controller.setBankService(new BankService());
			controller.updateTableView();
		}, "");
	}

	@FXML
	private Hyperlink linkAccountPlan;
	@FXML
	public void onlinkAccountPlanAction() {
		loadView("/gui/accountPlan/AccountPlanView.fxml", "Lista de planos de conta", (AccountPlanViewController controller) -> {
			controller.setAccountPlanService(new AccountPlanService());
			controller.updateTableView();
		}, "");
	}
	
	@FXML
	private Hyperlink linkBankAgence;
	@FXML
	public void onlinkBankAgenceAction() {
		loadView("/gui/bankAgence/BankAgenceView.fxml", "Lista de agencias bancárias", (BankAgenceViewController controller) -> {
			controller.setBankAgenceService(new BankAgenceService());
			controller.updateTableView();
		}, "");
	}
	
	@FXML
	private Hyperlink linkCompany;
	@FXML
	public void onlinkCompanyAction() {
		loadView("/gui/company/CompanyView.fxml", "Lista de empresas", (CompanyViewController controller) -> {
			controller.setCompanyService(new CompanyService());
			controller.updateTableView();
		}, "");
	}
	
//	@FXML
//	private MenuItem mnuItemAccountPlan;
//	@FXML
//	public void onMnuItemAccountPlanAction() {
//		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/accountPlan/AccountPlanView.fxml"));
//		Window parentScene = mnuMain.getScene().getWindow();
//		loadModalView(loader, "Lista de planos de conta", parentScene, 600.0, 800.0, (AccountPlanViewController controller) -> {
//			controller.setAccountPlanService(new AccountPlanService());
//			controller.updateTableView();
//		});
//	}
	
	@FXML
	private Hyperlink linkBankAccount;
	@FXML
	public void onlinkBankAccountAction() {
		loadView("/gui/bankAccount/BankAccountView.fxml", "Lista de contas bancárias", (BankAccountViewController controller) -> {
			controller.setBankAccountService(new BankAccountService());
			controller.updateTableView();
		}, "");
	}

	@FXML
	private Hyperlink linkClifor; 
	@FXML
	public void onlinkCliforAction() {
		loadView("/gui/clifor/CliforView.fxml", "Lista de clientes/fornecedores", (CliforViewController controller) -> {
			controller.setCliforService(new CliforService());
			controller.updateTableView();
		}, "");
	}
	
	
	@FXML
	private MenuItem mnuItemBankStatement;
	@FXML
	private void onMnuItemBankStatementAction() {
//		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/bankStatement/BankStatementViewChooseAccount.fxml"));
//		Window window = mnuMain.getScene().getWindow();
//		loadModalView(loader, "Escolha a conta para exibir o extrato", window, 230.0, 500.0, (BankStatementViewChooseAccountController controller) ->{
//			controller.setBankStatementService(new BankStatementService());
//			controller.setBankAccountService(new BankAccountService());
//			controller.setMovimentService(new MovimentService());
//			controller.loadAssociateObjects();
//		});
	}
	
	
	@FXML
	private Hyperlink linkBillpay;
	@FXML
	public void onlinkBillpayAction() {
		loadView("/gui/billpay/BillpayView.fxml", "Lista de contas a pagar", (BillpayViewController controller) -> {
			controller.setBillpayService(new BillpayService());
			controller.updateTableView();
		}, "");
	}

	@FXML
	private Hyperlink linkMoviment;
	@FXML
	private void onlinkMovimentAction() {
		loadView("/gui/moviment/MovimentView.fxml", "Lista de movimentações", (MovimentViewController controller) -> {
			controller.setMovimentService(new MovimentService());
			controller.updateTableView();
		}, "");
	}
	
	
	@FXML
	private Hyperlink linkReceivable;
	@FXML
	private void onlinkReceivableAction() {
		loadView("/gui/receivable/ReceivableView.fxml", "Lista de contas a receber", (ReceivableViewController controller) -> {
			controller.setReceivableService(new ReceivableService());
			controller.updateTableView();
		}, "");
	}
	
//	private synchronized <T> void loadModalView(String path, String title, double heigth, double width, Consumer<T> initialization) {
//		try {
//			FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
//			Window window = mnuMain.getScene().getWindow();
//			Pane pane = loader.load();	
//			Stage stage = new Stage();
//			stage.setTitle(title);
//			Scene scene = new Scene(pane);
//			scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet()); 
//			stage.setScene(scene);
//			stage.setResizable(false);
//			stage.initOwner(window);
//			stage.initModality(Modality.WINDOW_MODAL);
//			stage.setHeight(heigth);
//			stage.setWidth(width);
//			
//			T controller = loader.getController();
//			initialization.accept(controller);
//			
////			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
////				@Override
////				public void handle(WindowEvent event) {
////					updateTableView();
////				}
////			});
//			
//			stage.showAndWait();
//		} catch (IOException e) {
//			e.printStackTrace();
//			Alerts.showAlert("Erro", "Erro ao abrir a janela", e.getMessage(), AlertType.ERROR);
//		}
//	}


	private FXMLLoader getLoaderView(String absolutePath) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(absolutePath));
		return loader;
	}
	
	
	private synchronized <T> void loadView(String absolutePath, String tela ,Consumer<T> consumer, String cssPath) {
		try {
			FXMLLoader loader = getLoaderView(absolutePath);
			VBox box = loader.load();
			BorderPane mainScene = (BorderPane) Main.getMainScene().getRoot();
			BorderPane secound = (BorderPane) mainScene.getCenter();
			Node top = secound.getTop();
			Node bottom = secound.getBottom();
			secound.getChildren().clear();
			secound.setTop(top);
			secound.setBottom(bottom);
			secound.setCenter(box);
			nomeTela.setText(tela);
			T controller = loader.getController();
			consumer.accept(controller);
		}catch(IOException e) {
			e.printStackTrace();
			Alerts.showAlert("Erro", "Erro ao abrir a janela", e.getMessage(), AlertType.ERROR);
		}
	}

	
	//////////TESTEE TEM QUE APAGAR
	private synchronized <T> void loadViewteste(String absolutePath, String tela) {
		try {
			FXMLLoader loader = getLoaderView(absolutePath);
			VBox box = loader.load();
			BorderPane mainScene = (BorderPane) Main.getMainScene().getRoot();
			BorderPane secound = (BorderPane) mainScene.getCenter();
			Node top = secound.getTop();
			Node bottom = secound.getBottom();
			DashboardController controller = loader.getController();
			controller.setDashboardService(new DashboardService());
			VBox box2 = controller.updateView(box);
			secound.getChildren().clear();
			secound.setTop(top);
			secound.setBottom(bottom);
			secound.setCenter(box2);
			nomeTela.setText(tela);
		}catch(IOException e) {
			e.printStackTrace();
			Alerts.showAlert("Erro", "Erro ao abrir a janela", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		nomeTela.setText("");
		nomeTela.getStyleClass().addAll("h3", "strong", "text-primary");
		nomeUsuario.getStyleClass().addAll("h5", "strong");
	}

}
