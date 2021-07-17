package model.service;

import java.util.Date;
import java.util.List;

import database.exceptions.DatabaseException;
import javafx.scene.control.Alert.AlertType;
import model.dao.DAOBankAccount;
import model.dao.DAOFactory;
import model.dao.DAOMoviment;
import model.dao.DAOPayment;
import model.entities.BankAccount;
import model.entities.Billpay;
import model.entities.Moviment;
import model.entities.Payment;
import utils.Alerts;


public class PaymentService {
	
	private BankStatementService statementService = new BankStatementService();

	private DAOPayment dao = DAOFactory.createPaymentDAO();
	private DAOBankAccount daoAccount = DAOFactory.createBankAccountDAO();
	private DAOMoviment daoMoviment = DAOFactory.createMovimentDAO();

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
			if(movimentOpen() && dateInMoviment(entity.getDate())) {
				Billpay bill = billService.findById(entity.getBillpay().getId());
				bill.setStatus("PAGO");
				billService.saveOrUpdate(bill);
				Integer idPayment = dao.insert(entity);
				Payment payment = dao.findById(idPayment);
				BankAccount account = daoAccount.findById(payment.getBankAccount().getId());
				payment.setBankAccount(account);
				payment.setBillpay(bill);
				statementService.createBankStatementByPayment(payment);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Alerts.showAlert("Erro ao salvar", "Pagamento não realizado", e.getMessage(), AlertType.ERROR);
		}
	}

	private boolean dateInMoviment(Date date) {
		List<Moviment> moviments = daoMoviment.findByAllOpenMoviment();
		if(moviments.isEmpty()) {
			throw new DatabaseException("Não existe nenhum movimento aberto");
		}
		
		Moviment moviment = moviments.get(0);
		
		boolean isBeforeFinish = date.before(moviment.getDateFinish());
		boolean isAfterBeginner = date.after(moviment.getDateBeginner());
		
		if(isAfterBeginner && isBeforeFinish){
			return true;
		}else {
			throw new DatabaseException("A data do pagamento está fora do periodo do movimento aberto");
		}
	}

	private boolean movimentOpen() {
		List<Moviment> moviments = daoMoviment.findByAllOpenMoviment();
		
		if(moviments.isEmpty()) {
			throw new DatabaseException("Não existe nenhum movimento aberto");
		}
		return true;
	}

}
