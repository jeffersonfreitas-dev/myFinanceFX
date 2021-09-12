package gui.transferencia;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.entities.Transferencia;
import model.service.BankStatementService;
import utils.Utils;

public class TransferenciaViewController implements Initializable{
	
//	private TransferenciaService service;
//	public void setBankStatementService(BankStatementService service) {
//		this.service = service;
//	}
	
	@FXML
	private Button btnNew;
	@FXML
	public void onBtnNewAction(ActionEvent event) {
//		loadModalView("/gui/bankStatement/BankStatementViewChooseAccount.fxml", "Escolha a conta para exibir o extrato", 270.0, 600.0, (BankStatementViewChooseAccountController controller) ->{
//			controller.setBankStatementService(new BankStatementService());
//			controller.setBankAccountService(new BankAccountService());
//			controller.setMovimentService(new MovimentService());
//			controller.loadAssociateObjects();
//		});
	}



	@FXML
	private TableView<Transferencia> tblView;
	@FXML
	private TableColumn<Transferencia, Date> columnDate;
	@FXML
	private TableColumn<Transferencia, String> columnObservation;
	@FXML
	private TableColumn<Transferencia, String> columnOrigin;
	@FXML
	private TableColumn<Transferencia, String> columnDestination;
	@FXML
	private TableColumn<Transferencia, Double> columnValue;
	
	private ObservableList<Transferencia> obsList = null;

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializationNodes();
	}


	private void initializationNodes() {
	
		btnNew.getStyleClass().add("btn-primary");
		columnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
		Utils.formatTableColumnDate(columnDate, "dd/MM/yyyy");
		columnObservation.setCellValueFactory(new PropertyValueFactory<>("observation"));
		columnValue.setCellValueFactory(new PropertyValueFactory<>("value"));
		Utils.formatTableColumnDouble(columnValue, 2);
		columnOrigin.setCellValueFactory(v -> {
			String result = "";
			result = v.getValue().getOriginAccount().getCode();
			return new ReadOnlyStringWrapper(result);
		});
		columnDestination.setCellValueFactory(v -> {
			String result = "";
			result = v.getValue().getDestinationAccount().getCode();
			return new ReadOnlyStringWrapper(result);
		});
	}

	
//	public void updateTableView(BankAccount bankAccount, Moviment moviment) {
//		if(service == null) {
//			throw new IllegalStateException("O serviço não foi instanciado");
//		}
//		
//		Double total = 0.0;
//		
//		List<BankStatement> list = service.findAllByAccountAndMoviment(bankAccount, moviment.getDateBeginner(), moviment.getDateFinish());
//		for(BankStatement s : list) {
//			
//			if(s.isInitialValue()) {
//				s.setBalance(s.getValue());
//				total = s.getValue();
//			}else {
//				if(s.isCredit()) {
//					s.setBalance(total + s.getValue());
//					total = total + s.getValue();
//				}else {
//					s.setBalance(total - s.getValue());
//					total = total - s.getValue();
//				}
//			}
//			
//		}
//		obsList = FXCollections.observableArrayList(list);
//		tblView.setItems(obsList);
//		initializationNodes();
//	}
	
	
//	private synchronized <T> void loadModalView(String path, String title, double heigth, double width, Consumer<T> initialization) {
//		try {
//			FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
//			Pane pane = loader.load();	
//			Window window = btnNew.getScene().getWindow();
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
//			stage.showAndWait();
//		} catch (IOException e) {
//			e.printStackTrace();
//			Alerts.showAlert("Erro", "Erro ao abrir a janela", e.getMessage(), AlertType.ERROR);
//		}
//	}
	
	
}
