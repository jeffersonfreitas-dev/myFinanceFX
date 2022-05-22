package gui.bankStatement;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import org.kordamp.bootstrapfx.BootstrapFX;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.entities.BankAccount;
import model.entities.BankStatement;
import model.entities.Moviment;
import model.service.BankAccountService;
import model.service.BankStatementService;
import model.service.MovimentService;
import net.sf.jasperreports.engine.JRException;
import report.bankStatement.ReportBankStatement;
import utils.Alerts;
import utils.Utils;

public class BankStatementViewController implements Initializable{
	
	private BankStatementService service;
	public void setBankStatementService(BankStatementService service) {
		this.service = service;
	}
	
	private List<BankStatement> extratos = new ArrayList<BankStatement>();
	
	@FXML
	private Button btnNew;
	@FXML
	public void onBtnNewAction(ActionEvent event) {
		loadModalView("/gui/bankStatement/BankStatementViewChooseAccount.fxml", "Escolha a conta para exibir o extrato", 270.0, 600.0, (BankStatementViewChooseAccountController controller) ->{
			controller.setBankStatementService(new BankStatementService());
			controller.setBankAccountService(new BankAccountService());
			controller.setMovimentService(new MovimentService());
			controller.loadAssociateObjects();
		});
	}


	@FXML
	private Button btnImprimir;
	@FXML
	public void onBtnImprimirAction(ActionEvent event) {
        try {
          new ReportBankStatement().showExtratoSimple(this.extratos);
      } catch (ClassNotFoundException | JRException | SQLException e1) {
          e1.printStackTrace();
      }
	}
	
	@FXML
	private TableView<BankStatement> tblView;
	@FXML
	private TableColumn<BankStatement, Date> columnDate;
	@FXML
	private TableColumn<BankStatement, String> columnHistoric;
	@FXML
	private TableColumn<BankStatement, String> columnCredit;
	@FXML
	private TableColumn<BankStatement, Double> columnValue;
	@FXML
	private TableColumn<BankStatement, Double> columnBalance;
	
	private ObservableList<BankStatement> obsList = null;

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializationNodes();
	}


	private void initializationNodes() {
	
		btnNew.getStyleClass().add("btn-info");
		btnImprimir.getStyleClass().add("btn-warning");
		columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
		Utils.formatTableColumnDate(columnDate, "dd/MM/yyyy");
		columnHistoric.setCellValueFactory(new PropertyValueFactory<>("historic"));
		columnValue.setCellValueFactory(new PropertyValueFactory<>("value"));
		Utils.formatTableColumnDouble(columnValue, 2);
		columnBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
		Utils.formatTableColumnDouble(columnBalance, 2);
		columnCredit.setCellValueFactory(v -> {
			String result = "";
			result = v.getValue().isCredit() ? "C" : "D";
			return new ReadOnlyStringWrapper(result);
		});
		
		
		//METODO DOIS CLICKES ROW TABELA
		tblView.setRowFactory( tv -> {
		    TableRow<BankStatement> row = new TableRow<>();
		    row.setOnMouseClicked(event -> {
		        if (event.getClickCount() == 2 && (! row.isEmpty())) {
		        	BankStatement entity = row.getItem();
		            
	            	loadModalView("/gui/bankStatement/BankStatementViewDetails.fxml", "Historico de movimentação", 350.0, 590.0, (BankStatementViewDetailsController controller) -> {
	            		controller.setBankStatement(entity);
	            		controller.setPayment(entity.getPayment());
	            		controller.setReceivement(entity.getReceivement());
	            		controller.setTransferencia(entity.getTransferencia());
	            		controller.setTextInArea();
	            	});

		        }
		    });
		    return row ;
		});
	}
	
	
	public void updateTableView(BankAccount bankAccount, Moviment moviment) {
		if(service == null) {
			throw new IllegalStateException("O serviço não foi instanciado");
		}
		
		Double total = 0.0;
		
		extratos = service.findAllByAccountAndMoviment(bankAccount, moviment.getDateBeginner(), moviment.getDateFinish());
		for(BankStatement s : extratos) {
			
			if(s.isInitialValue()) {
				s.setBalance(s.getValue());
				total = s.getValue();
			}else {
				if(s.isCredit()) {
					s.setBalance(total + s.getValue());
					total = total + s.getValue();
				}else {
					s.setBalance(total - s.getValue());
					total = total - s.getValue();
				}
			}
			
		}
		obsList = FXCollections.observableArrayList(extratos);
		tblView.setItems(obsList);
		initializationNodes();
	}
	
	
	private synchronized <T> void loadModalView(String path, String title, double heigth, double width, Consumer<T> initialization) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
			Pane pane = loader.load();	
			Window window = btnNew.getScene().getWindow();
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
	
	
}
