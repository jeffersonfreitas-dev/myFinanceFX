package gui.accountPlan;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.kordamp.bootstrapfx.BootstrapFX;

import database.exceptions.DatabaseException;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.Pagination;
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
import model.entities.AccountPlan;
import model.service.AccountPlanService;
import utils.Alerts;

public class AccountPlanViewController implements Initializable {

	private AccountPlanService service;

	public void setAccountPlanService(AccountPlanService accountPlanService) {
		this.service = accountPlanService;
	}

	@FXML
	private Button btnNew;

	@FXML
	public void onBtnNewAction(ActionEvent event) {
		Stage stage = new Stage();
		AccountPlan entity = new AccountPlan();
		loadModalView(entity, "Cadastro de plano de conta", stage);
	}

	@FXML
	private TableView<AccountPlan> tblView;
	@FXML
	private TableColumn<AccountPlan, Integer> tblColumnId;
	@FXML
	private TableColumn<AccountPlan, String> tblColumnCredit;
	@FXML
	private TableColumn<AccountPlan, String> tblColumnName;
	@FXML
	private TableColumn<AccountPlan, AccountPlan> tblColumnEDIT;
	@FXML
	private TableColumn<AccountPlan, AccountPlan> tblColumnDELETE;
	@FXML
	private Pagination pagination = new Pagination();

	private ObservableList<AccountPlan> obsList;
	
    private ObservableList<AccountPlan> masterData = FXCollections.observableArrayList();
    
    List<AccountPlan> list = new ArrayList<AccountPlan>();
    
    private int dataSize;
    private int rowsPerPage = 4;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializationNodes();

	}

	private void initializationNodes() {
		tblColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tblColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tblColumnCredit.setCellValueFactory(v -> {
			return new ReadOnlyStringWrapper(v.getValue().isCredit() ? "CRÉDITO" : "DÉBITO");
		});
		btnNew.getStyleClass().add("btn-primary");
		
		
        dataSize = masterData.size();

        pagination.currentPageIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                changeTableView(newValue.intValue(), rowsPerPage);
            }

        });
        
        int totalPage = (int) (Math.ceil(dataSize * 1.0 / rowsPerPage));
        pagination.setPageCount(totalPage);
        pagination.setCurrentPageIndex(0);
        changeTableView(0, rowsPerPage); 
		
	}

	
	
    private void changeTableView(int index, int limit) {

        int fromIndex = index * limit;
        int toIndex = Math.min(fromIndex + limit, dataSize);
        
//        List<AccountPlan> list = service.findAll();

        List<AccountPlan> subListObs = list.subList(fromIndex, toIndex);
        ObservableList<AccountPlan> tmpObsToSetTableVal = FXCollections.observableArrayList();

        tblView.getItems().clear();
        tblView.setItems(null);

        for (AccountPlan t : subListObs) {
            tmpObsToSetTableVal.add(t);
        }

        tblView.setItems(tmpObsToSetTableVal);

    }	
	
	
	
	
	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("O serviço não foi instanciado");
		}

		list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
//		createPage(0, rowsPerPage, dataSize, list);
		tblView.setItems(obsList);
//        Pagination pagination = new Pagination((dataSize / rowsPerPage + 1), 0);
//        pagination.setPageFactory(this::createPage);
		initEditButtons();
		initRemoveButtons();
	}

//	private Node createPage(int pageIndex) {
//
//		int fromIndex = pageIndex * rowsPerPage;
//		int toIndex = Math.min(fromIndex + rowsPerPage, dataSize);
//		tblView.setItems(FXCollections.observableArrayList(list.subList(fromIndex, toIndex)));
//		return new BorderPane(tblView);
//	}

	private void initEditButtons() {
		tblColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tblColumnEDIT.setCellFactory(param -> new TableCell<AccountPlan, AccountPlan>() {
			private final Button button = new Button();

			@Override
			protected void updateItem(AccountPlan entity, boolean empty) {
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
					loadModalView(entity, "Alteração de plano de conta", stage);
				});
			}
		});
	}

	private void initRemoveButtons() {
		tblColumnDELETE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tblColumnDELETE.setCellFactory(param -> new TableCell<AccountPlan, AccountPlan>() {
			private final Button button = new Button();

			@Override
			protected void updateItem(AccountPlan entity, boolean empty) {
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

	private void removeEntity(AccountPlan entity) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação",
				"Você tem certeza que deseja remover este item?");

		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Serviço não instanciado");
			}
			try {
				service.remove(entity);
//				updateTableView();
			} catch (DatabaseException e) {
				e.printStackTrace();
				Alerts.showAlert("Erro ao remover registro", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

	private synchronized <T> void loadModalView(AccountPlan entity, String title, Stage stage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/accountPlan/AccountPlanViewRegister.fxml"));
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

			AccountPlanViewRegisterController controller = loader.getController();
			controller.setAccountPlan(entity);
			controller.setAccountPlanService(new AccountPlanService());
			controller.updateFormData();

			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
//					updateTableView();
				}
			});

			stage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("Erro", "Erro ao abrir a janela", e.getMessage(), AlertType.ERROR);
		}
	}
}
