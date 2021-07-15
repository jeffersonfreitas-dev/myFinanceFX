package model.service;

import java.util.List;

import javafx.scene.control.Alert.AlertType;
import model.dao.DAOBankAccount;
import model.dao.DAOBillpay;
import model.dao.DAOFactory;
import model.dao.DAOPayment;
import model.entities.BankAccount;
import model.entities.Billpay;
import model.entities.Payment;
import utils.Alerts;

public class PaymentService {
	
	private BankStatementService statementService = new BankStatementService();

	private DAOPayment dao = DAOFactory.createPaymentDAO();
	private DAOBankAccount daoAccount = DAOFactory.createBankAccountDAO();

	public List<Payment> findAll() {
		return dao.findAllOrderByDate();
	}

	public void remove(Payment entity) {
		dao.deleteById(entity.getId());
		
	}
	
	public Payment findById(Integer id) {
		if(id == null) {
			throw new IllegalStateException("Id nulo do pagamento, não é possível localizar o registro");
		}
		return dao.findById(id);
	}

	public void save(Payment entity, BillpayService billService) {
		try {
			Billpay bill = billService.findById(entity.getBillpay().getId());
			bill.setStatus("PAGO");
			billService.saveOrUpdate(bill);
			Integer idPayment = dao.insert(entity);
			Payment payment = dao.findById(idPayment);
			BankAccount account = daoAccount.findById(payment.getBankAccount().getId());
			payment.setBankAccount(account);
			payment.setBillpay(bill);
			statementService.createBankStatementByPayment(payment);
		} catch (Exception e) {
			e.printStackTrace();
			Alerts.showAlert("Erro ao salvar", "Erro ao salvar o movimento no Extrato", e.getMessage(), AlertType.ERROR);
		}
	}

}
