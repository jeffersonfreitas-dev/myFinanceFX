package gui.main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import org.kordamp.bootstrapfx.BootstrapFX;

import application.Main;
import gui.bank.BankViewController;
import gui.bank.BankViewRegisterController;
import gui.billpay.BillpayViewController;
import gui.moviment.MovimentViewController;
import gui.receivable.ReceivableViewController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.entities.Bank;
import model.service.BankService;
import model.service.BillpayService;
import model.service.MovimentService;
import model.service.ReceivableService;
import utils.Alerts;

public class MainViewController implements Initializable{
	
	@FXML
	private Button novoRegistro;
	@FXML
	public void onNovoRegistroAction(ActionEvent event) {
		loadModalView("/gui/bank/BankViewRegister.fxml", "Cadastro de bancos", 270.0, 600.0, (BankViewRegisterController controller) -> {
			controller.setBank(new Bank());
			controller.setBankService(new BankService());
		});
	}
	
	@FXML
	private Label nome;
	@FXML
	private Label versao;
	@FXML
	private Label nomeTela;
	@FXML
	private Label nomeUsuario;
	
	@FXML
	private MenuBar mnuMain;

	
	@FXML
	private MenuItem mnuItenAbout;
	@FXML
	public void onMnuItemAboutAction(ActionEvent event) {
//		FXMLLoader loader = getLoaderView("/gui/about/AboutView.fxml");
//		Window parentScene = mnuMain.getScene().getWindow();
//		loadModalView(loader, "Sobre o sistema", parentScene, 310.0, 460.0, x -> {});
	}
	
	@FXML
	private Hyperlink linkBank;
	@FXML
	public void onlinkBankAction() {
		loadView("/gui/bank/BankView.fxml",  (BankViewController controller) -> {
			controller.setBankService(new BankService());
			controller.updateTableView();
		}, "");
	}
	
	@FXML
	private MenuItem mnuItemBankAgence;
	@FXML
	public void onMnuItemBankAgenceAction() {
//		FXMLLoader loader = getLoaderView("/gui/bankAgence/BankAgenceView.fxml");
//		Window parentScene = mnuMain.getScene().getWindow();
//		loadModalView(loader, "Lista de agencias bancárias", parentScene, 600.0, 800.0, (BankAgenceViewController controller) -> {
//			controller.setBankAgenceService(new BankAgenceService());
//			controller.updateTableView();
//		});
	}
	
	@FXML
	private MenuItem mnuItemCompany;
	@FXML
	public void onMnuItemCompanyAction() {
//		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/company/CompanyView.fxml"));
//		Window parentScene = mnuMain.getScene().getWindow();
//		loadModalView(loader, "Lista de empresas", parentScene, 600.0, 800.0, (CompanyViewController controller) -> {
//			controller.setCompanyService(new CompanyService());
//			controller.updateTableView();
//		});
	}
	
	@FXML
	private MenuItem mnuItemAccountPlan;
	@FXML
	public void onMnuItemAccountPlanAction() {
//		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/accountPlan/AccountPlanView.fxml"));
//		Window parentScene = mnuMain.getScene().getWindow();
//		loadModalView(loader, "Lista de planos de conta", parentScene, 600.0, 800.0, (AccountPlanViewController controller) -> {
//			controller.setAccountPlanService(new AccountPlanService());
//			controller.updateTableView();
//		});
	}
	
	@FXML
	private MenuItem mnuItemBankAccount;
	@FXML
	private void onMnuItemBankAccountAction() {
//		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/bankAccount/BankAccountView.fxml"));
//		Window parentScene = mnuMain.getScene().getWindow();
//		loadModalView(loader, "Lista de contas bancárias", parentScene, 600.0, 800.0, (BankAccountViewController controller) -> {
//			controller.setBankAccountService(new BankAccountService());
//			controller.updateTableView();
//		});
	}

	@FXML
	private MenuItem mnuItemClifor; 
	@FXML
	public void onMnuItemCliforAction() {
//		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/clifor/CliforView.fxml"));
//		Window parentScene = mnuMain.getScene().getWindow();
//		loadModalView(loader, "Lista de clientes e fornecedores", parentScene, 600.0, 800.0, (CliforViewController controller) -> {
//			controller.setCliforService(new CliforService());
//			controller.updateTableView();
//		});
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
	private MenuItem mnuItemBillpay;
	@FXML
	private void onMnuItemBillpayAction() {
		loadView("/gui/billpay/BillpayView.fxml", (BillpayViewController controller) -> {
			controller.setBillpayService(new BillpayService());
			controller.updateTableView();
		}, "");
	}

	@FXML
	private MenuItem mnuItemMoviment;
	@FXML
	private void onMnuItemMovimentAction() {
		loadView("/gui/moviment/MovimentView.fxml", (MovimentViewController controller) -> {
			controller.setMovimentService(new MovimentService());
			controller.updateTableView();
		}, "../moviment/Moviment.css");
	}
	
	
	@FXML
	private MenuItem mnuItemReceivable;
	@FXML
	private void onMnuItemReceivableAction() {
		loadView("/gui/receivable/ReceivableView.fxml", (ReceivableViewController controller) -> {
			controller.setReceivableService(new ReceivableService());
			controller.updateTableView();
		}, "");
	}
	
	private synchronized <T> void loadModalView(String path, String title, double heigth, double width, Consumer<T> initialization) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
			Window window = mnuMain.getScene().getWindow();
			Pane pane = loader.load();	
			Stage stage = new Stage();
			stage.setTitle(title);
			Scene scene = new Scene(pane);
			scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet()); 
			stage.setScene(scene);
			stage.setResizable(false);
			stage.initOwner(window);
			stage.initModality(Modality.WINDOW_MODAL);
			stage.setHeight(heigth);
			stage.setWidth(width);
			
			T controller = loader.getController();
			initialization.accept(controller);
			
			stage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("Erro", "Erro ao abrir a janela", e.getMessage(), AlertType.ERROR);
		}
	}


	private FXMLLoader getLoaderView(String absolutePath) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(absolutePath));
		return loader;
	}
	
	
	private synchronized <T> void loadView(String absolutePath, Consumer<T> consumer, String cssPath) {
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
			novoRegistro.setVisible(true);
			nomeTela.setText("Lista de bancos");
			T controller = loader.getController();
			consumer.accept(controller);
		}catch(IOException e) {
			e.printStackTrace();
			Alerts.showAlert("Erro", "Erro ao abrir a janela", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		nome.getStyleClass().setAll("h1");
		novoRegistro.setVisible(false);
		novoRegistro.getStyleClass().add("btn-primary");
		
		nomeTela.setText("DASHBOARD");
		nomeTela.getStyleClass().addAll("h3", "strong", "text-primary");
		nomeUsuario.getStyleClass().addAll("h5", "strong");
	}

}
