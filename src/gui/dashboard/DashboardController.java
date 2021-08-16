package gui.dashboard;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.stage.Stage;

public class DashboardController implements Initializable{

	@FXML
	private PieChart pie;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Stage stage = new Stage();

		Scene scene = new Scene(new Group());
        stage.setTitle("Imported Fruits");
        stage.setWidth(500);
        stage.setHeight(500);
        
		 ObservableList<PieChart.Data> pieChartData =
	                FXCollections.observableArrayList(
	                new PieChart.Data("Laranjas", 25),
	                new PieChart.Data("Uvas", 10),
	                new PieChart.Data("Peras", 22),
	                new PieChart.Data("Maçãs", 43));
	        pie = new PieChart(pieChartData);
	        pie.setTitle("Imported Fruits");	
	        

	        ((Group) scene.getRoot()).getChildren().add(pie);
	        stage.setScene(scene);
	        stage.show();	 		
		
		
	}
	
}
