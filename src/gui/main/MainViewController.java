package gui.main;

import java.io.IOException;
import java.util.function.Consumer;

import application.Main;
import gui.accountPlan.AccountPlanViewController;
import gui.bank.BankViewController;
import gui.bankAccount.BankAccountViewController;
import gui.bankAgence.BankAgenceViewController;
import gui.billpay.BillpayViewController;
import gui.clifor.CliforViewController;
import gui.company.CompanyViewController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.service.AccountPlanService;
import model.service.BankAccountService;
import model.service.BankAgenceService;
import model.service.BankService;
import model.service.BillpayService;
import model.service.CliforService;
import model.service.CompanyService;
import utils.Alerts;

public class MainViewController {
	
	@FXML
	private MenuBar mnuMain;
	
	@FXML
	private MenuItem mnuItenAbout;
	@FXML
	public void onMnuItemAboutAction(ActionEvent event) {
		FXMLLoader loader = getLoaderView("/gui/about/AboutView.fxml");
		Window parentScene = mnuMain.getScene().getWindow();
		loadModalView(loader, "Sobre o sistema", parentScene, 310.0, 460.0, x -> {});
	}
	
	@FXML
	private MenuItem mnuItemBank;
	@FXML
	public void onMnuItemBankAction() {
		FXMLLoader loader = getLoaderView("/gui/bank/BankView.fxml");
		Window parentScene = mnuMain.getScene().getWindow();
		loadModalView(loader, "Lista de bancos", parentScene, 600.0, 800.0, (BankViewController controller) -> {
			controller.setBankService(new BankService());
			controller.updateTableView();
		});
	}
	
	@FXML
	private MenuItem mnuItemBankAgence;
	@FXML
	public void onMnuItemBankAgenceAction() {
		FXMLLoader loader = getLoaderView("/gui/bankAgence/BankAgenceView.fxml");
		Window parentScene = mnuMain.getScene().getWindow();
		loadModalView(loader, "Lista de agencias bancárias", parentScene, 600.0, 800.0, (BankAgenceViewController controller) -> {
			controller.setBankAgenceService(new BankAgenceService());
			controller.updateTableView();
		});
	}
	
	@FXML
	private MenuItem mnuItemCompany;
	@FXML
	public void onMnuItemCompanyAction() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/company/CompanyView.fxml"));
		Window parentScene = mnuMain.getScene().getWindow();
		loadModalView(loader, "Lista de empresas", parentScene, 600.0, 800.0, (CompanyViewController controller) -> {
			controller.setCompanyService(new CompanyService());
			controller.updateTableView();
		});
	}
	
	@FXML
	private MenuItem mnuItemAccountPlan;
	@FXML
	public void onMnuItemAccountPlanAction() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/accountPlan/AccountPlanView.fxml"));
		Window parentScene = mnuMain.getScene().getWindow();
		loadModalView(loader, "Lista de planos de conta", parentScene, 600.0, 800.0, (AccountPlanViewController controller) -> {
			controller.setAccountPlanService(new AccountPlanService());
			controller.updateTableView();
		});
	}
	
	@FXML
	private MenuItem mnuItemBankAccount;
	@FXML
	private void onMnuItemBankAccountAction() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/bankAccount/BankAccountView.fxml"));
		Window parentScene = mnuMain.getScene().getWindow();
		loadModalView(loader, "Lista de contas bancárias", parentScene, 600.0, 800.0, (BankAccountViewController controller) -> {
			controller.setBankAccountService(new BankAccountService());
			controller.updateTableView();
		});
	}

	@FXML
	private MenuItem mnuItemClifor; 
	@FXML
	public void onMnuItemCliforAction() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/clifor/CliforView.fxml"));
		Window parentScene = mnuMain.getScene().getWindow();
		loadModalView(loader, "Lista de clientes e fornecedores", parentScene, 600.0, 800.0, (CliforViewController controller) -> {
			controller.setCliforService(new CliforService());
			controller.updateTableView();
		});
	}
	
	
	@FXML
	private MenuItem mnuItemBillpay;
	@FXML
	private void onMnuItemBillpayAction() {
		loadView("/gui/billpay/BillpayView.fxml", (BillpayViewController controller) -> {
			controller.setBillpayService(new BillpayService());
			controller.updateTableView();
		});
	}
	
	private synchronized <T> void loadModalView(FXMLLoader loader, String title, Window parentScene, double heigth, double width, 
			Consumer<T> initialization) {
		try {
			Pane pane = loader.load();	
			Stage stage = new Stage();
			stage.setTitle(title);
			stage.setScene(new Scene(pane));
			stage.setResizable(false);
			stage.initOwner(parentScene);
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
	
	
	private synchronized <T> void loadView(String absolutePath, Consumer<T> consumer) {
		try {
			FXMLLoader loader = getLoaderView(absolutePath);
			VBox box = loader.load();
			Scene mainScene = Main.getMainScene();
			
			VBox mainBox = (VBox)  ((ScrollPane) mainScene.getRoot()).getContent();
			Node mainMenu = mainBox.getChildren().get(0);
			mainBox.getChildren().clear();
			mainBox.getChildren().add(mainMenu);
			mainBox.getChildren().addAll(box.getChildren());
			mainBox.prefWidthProperty().bind(Main.getMainScene().widthProperty().multiply(1));
			T controller = loader.getController();
			consumer.accept(controller);
		}catch(IOException e) {
			e.printStackTrace();
			Alerts.showAlert("Erro", "Erro ao abrir a janela", e.getMessage(), AlertType.ERROR);
		}
	}

}
