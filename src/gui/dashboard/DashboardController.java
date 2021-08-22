package gui.dashboard;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
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
	ObservableList<PieChart.Data> pieChartData2;
	

	@FXML
	private PieChart pie;

	@FXML
	private PieChart pieRec;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	public VBox updateView(VBox box) {
		HBox hBox = (HBox) box.getChildren().get(0);
		pieChartStatusContaPagar(hBox);
		pieChartStatusContaReceber(hBox);
		return box;
	}

	
	
	
	private void pieChartStatusContaPagar(HBox hBox) {
		List<ChartBillpayStatus> charts = service.grafStatusContaPagar();
		PieChart chart = (PieChart) hBox.getChildren().get(0);
		pieChartData = FXCollections.observableArrayList(
	                new PieChart.Data("ABERTO", charts.get(0).getValor()),
	                new PieChart.Data("PAGO", charts.get(1).getValor())
	                );
		chart.setTitle("STATUS CONTAS A PAGAR");
		chart.setLegendVisible(false);
		chart.setData(pieChartData);
		applyCustomColorSequence(pieChartData, "red", "green");
		applyPercentData(pieChartData);
	}

	private void pieChartStatusContaReceber(HBox hBox) {
		List<ChartBillpayStatus> charts = service.grafStatusContaReceber();
		PieChart chart = (PieChart) hBox.getChildren().get(1);
		pieChartData2 = FXCollections.observableArrayList(
				new PieChart.Data("ABERTO", charts.get(0).getValor()),
				new PieChart.Data("RECEBIDO", charts.get(1).getValor())
				);
		chart.setTitle("STATUS CONTAS A RECEBER");
		chart.setLegendVisible(false);
		chart.setData(pieChartData2);
		applyCustomColorSequence(pieChartData2, "orange", "blue");
//		applyPercentData(pieChartData2);
	}
	
	
	  private void applyCustomColorSequence(ObservableList<PieChart.Data> pieChartData, String... pieColors) {
		    int i = 0;
		    for (PieChart.Data data : pieChartData) {
		      data.getNode().setStyle("-fx-pie-color: " + pieColors[i % pieColors.length] + ";");
		      i++;
		    }
		  }	

	  private void applyPercentData(ObservableList<PieChart.Data> pieChartData) {
		  Double total = pieChartData.stream().map(s -> s.getPieValue()).reduce(0.0, (x,y) -> x + y);
		  for (PieChart.Data data : pieChartData) {
			  data.nameProperty().bind(Bindings.concat(data.getName(), " ", data.pieValueProperty().divide(total).multiply(100), "%"));
		  }
	  }	
	
}
