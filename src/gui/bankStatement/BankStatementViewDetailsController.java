package gui.bankStatement;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.entities.BankAccount;
import model.entities.BankStatement;
import model.entities.Billpay;
import model.entities.Payment;
import model.entities.Receivable;
import model.entities.Receivement;
import model.entities.Transferencia;
import model.service.BankAccountService;
import model.service.BillpayService;
import model.service.PaymentService;
import model.service.ReceivableService;
import model.service.ReceivementService;
import model.service.TransferenciaService;
import utils.Utils;

public class BankStatementViewDetailsController implements Initializable{
	
	private PaymentService payService = new PaymentService();
	private BillpayService billService = new BillpayService();
	private ReceivementService receivService = new ReceivementService();
	private ReceivableService recService = new ReceivableService();
	private TransferenciaService transfService = new TransferenciaService();
	private BankAccountService accountService = new BankAccountService();
	
	private BankStatement bankStatement;
	public void setBankStatement(BankStatement bankStatement) {
		this.bankStatement = bankStatement;
	}

	private Payment payment;
	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	private Receivement receivement;
	public void setReceivement(Receivement receivement) {
		this.receivement = receivement;
	}
	
	private Transferencia transferencia;
	public void setTransferencia(Transferencia transferencia) {
		this.transferencia = transferencia;
	}
	

	@FXML
	private Button btnClose;
	@FXML
	public void onBtnCloseAction(ActionEvent event) {
		Stage stage = Utils.getCurrentStage(event);
		stage.close();
	}
	
	@FXML
	private TextArea text;
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializationNodes();
	}

	private void initializationNodes() {
		btnClose.setGraphic(new ImageView("/assets/icons/cancel16.png"));

	}
	
	public void setTextInArea() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		StringBuilder sb = new StringBuilder("***** DETALHAMENTO DO MOVIMENTO DE EXTRATO *****\n\n");
		
		
		if(payment.getId() > 0) {
			Payment pay = payService.findById(payment.getId());
			Billpay bill = billService.findById(pay.getBillpay().getId());
			sb.append("Data Pgto: " + sdf.format(pay.getDate()) + "\n");
			sb.append("Fornecedor: " + bill.getClifor().getName() + "\n");
			sb.append("Plano de Conta: " + bill.getAccountPlan().getName() + "\n");
			sb.append("Parcelas: " + bill.getPortion() + "/" + bill.getFulfillment() + "\n");
			sb.append("Valor: " + bill.getValue() + "\n\n");
			sb.append("Histórico: " + bill.getHistoric() + "\n");
		}else if (receivement.getId() > 0){
			Receivement receiv = receivService.findById(receivement.getId());
			Receivable rec = recService.findById(receiv.getReceivable().getId());
			sb.append("Data Rec: " + sdf.format(receiv.getDate()) + "\n");
			sb.append("Cliente: " + rec.getClifor().getName() + "\n");
			sb.append("Plano de Conta: " + rec.getAccountPlan().getName() + "\n");
			sb.append("Parcelas: " + rec.getPortion() + "/" + rec.getFulfillment() + "\n");
			sb.append("Valor: " + rec.getValue() + "\n\n");
			sb.append("Histórico: " + rec.getHistoric() + "\n");
		}else if(transferencia.getId() > 0) {
			Transferencia transf = transfService.findById(transferencia.getId());
			BankAccount origin = accountService.findById(transf.getOriginAccount().getId());
			BankAccount destino = accountService.findById(transf.getDestinationAccount().getId());
			
			sb.append("Data: " + sdf.format(transf.getDate()) + "\n");
			sb.append("Origem: " + origin.getCode() + "/ " + origin.getAccount() + " - " + origin.getBankAgence().getBank().getName() +"\n");
			sb.append("Destino: " + destino.getCode() + "/ " + destino.getAccount() + " - " + destino.getBankAgence().getBank().getName() +"\n");
			sb.append("Valor: " + transf.getValue() + "\n\n");
			sb.append("Histórico: " + transf.getObservation() + "\n");
			
		}else {
			sb.append("Data: " + sdf.format(bankStatement.getDate()) + "\n");
			sb.append("Valor: " + bankStatement.getValue() + "\n\n");
			sb.append("Histórico: " + bankStatement.getHistoric() + "\n");
		}

		text.setText(sb.toString());
	}

}
