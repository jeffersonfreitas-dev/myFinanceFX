package gui.dashboard;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.dto.dashboard.ChartBillpayStatus;
import model.service.DashboardService;

public class DashboardController implements Initializable{

	private DashboardService service;
	public void setDashboardService (DashboardService service) {
		this.service = service;
	}
	
	
	ObservableList<PieChart.Data> pieChartData;
	

	@FXML
	private PieChart pie;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
//		Stage stage = new Stage();
//
//		Scene scene = new Scene(new Group());
//        stage.setTitle("Imported Fruits");
//        stage.setWidth(500);
//        stage.setHeight(500);
//        updateView();
//        
//	
//	        
//
//	        ((Group) scene.getRoot()).getChildren().add(pie);
//	        stage.setScene(scene);
//	        stage.show();	 		
		
		
	}

	public VBox updateView(VBox box) {
		
		List<ChartBillpayStatus> charts = service.grafStatusContaPagar();
		
		
		
		HBox hBox = (HBox) box.getChildren().get(0);
		PieChart chart = (PieChart) hBox.getChildren().get(0);
		 pieChartData =
	                FXCollections.observableArrayList(
	                new PieChart.Data("ABERTO", charts.get(0).getValor()),
	                new PieChart.Data("PAGO", charts.get(1).getValor()));
		 chart.setTitle("Status de conta a pagar");
		 chart.setData(pieChartData);
		return box;
	}
	
}
