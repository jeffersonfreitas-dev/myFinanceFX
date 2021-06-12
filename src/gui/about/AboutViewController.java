package gui.about;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import utils.Utils;

public class AboutViewController {
	
	@FXML
	private Button btnClose;
	
	@FXML
	public void onBtnCloseAction(ActionEvent event) {
		Utils.getCurrentStage(event).close();
	}

}
