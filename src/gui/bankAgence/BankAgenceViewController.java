package gui.bankAgence;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import database.exceptions.DatabaseException;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.entities.Bank;
import model.entities.BankAgence;
import model.service.BankAgenceService;
import utils.Alerts;
import utils.Utils;

public class BankAgenceViewController implements Initializable {

	private BankAgenceService service;

	public void setBankAgenceService(BankAgenceService service) {
		this.service = service;
	}

	@FXML
	private Button btnNew;
	@FXML
	private TableColumn<BankAgence, Integer> tblColumnId;
	@FXML
	private TableColumn<BankAgence, String> tblColumnAgence;
	@FXML
	private TableColumn<BankAgence, String> tblColumnDV;
	@FXML
	private TableColumn<BankAgence, String> tblColumnBank;
	@FXML
	private TableView<BankAgence> tblView;
	@FXML
	private TableColumn<BankAgence, BankAgence> tblColumnEDIT;
	@FXML
	private TableColumn<BankAgence, BankAgence> tblColumnREMOVE;

	private ObservableList<BankAgence> obsList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializationNodes();
	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalArgumentException("Serviço não instanciado");
		}
		List<BankAgence> list = service.findAll();
		obsList = FXCollections.observableList(list);
		tblView.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void initializationNodes() {
		btnNew.setGraphic(new ImageView("/assets/icons/new16.png"));
		tblColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tblColumnAgence.setCellValueFactory(new PropertyValueFactory<>("agence"));
		tblColumnDV.setCellValueFactory(new PropertyValueFactory<>("dv"));
		tblColumnBank.setCellValueFactory(d -> {
			BankAgence agence = d.getValue();
			Bank bank = agence.getBank();
			String result = bank.getName();
			return new ReadOnlyStringWrapper(result);
		});
	}

	private void initEditButtons() {
		tblColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tblColumnEDIT.setCellFactory(param -> new TableCell<BankAgence, BankAgence>() {
			private final Button button = new Button();

			@Override
			protected void updateItem(BankAgence entity, boolean empty) {
				button.setGraphic(new ImageView("/assets/icons/edit16.png"));
				super.updateItem(entity, empty);
				if (entity == null) {
					setGraphic(null);
					return;
				}

				setGraphic(button);
				button.setOnAction(e -> {
					Stage stage = Utils.getCurrentStage(e);
					stage.setTitle("Alteração da agencia");
//					loadView(entity, "/gui/bank/BankAgenceViewRegister.fxml", Utils.getCurrentScene(e));
				});
			}
		});
	}

	private void initRemoveButtons() {
		tblColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tblColumnREMOVE.setCellFactory(param -> new TableCell<BankAgence, BankAgence>() {
			private final Button button = new Button();

			@Override
			protected void updateItem(BankAgence entity, boolean empty) {
				button.setGraphic(new ImageView("/assets/icons/trash16.png"));
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

	private void removeEntity(BankAgence entity) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação",
				"Você tem certeza que deseja remover este item?");

		if (result.get() == ButtonType.OK) {
			if (service == null) {
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
}
