package report.billpay;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import model.dao.DAOBankAccount;
import model.dao.DAOBillpay;
import model.dao.DAOFactory;
import model.dao.DAOPayment;
import model.entities.BankAccount;
import model.entities.Billpay;
import model.entities.Payment;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;

public class ReportBillpay extends JFrame{
	private static final long serialVersionUID = 1L;
	
	private DAOBillpay dao = DAOFactory.createBillpayDAO();
	private DAOPayment daoPayment = DAOFactory.createPaymentDAO();
	private DAOBankAccount daoBankAccount = DAOFactory.createBankAccountDAO();

	
	   public void showExtratoSimple(Integer id) throws JRException, ClassNotFoundException, SQLException {
		   
		   Billpay bill = dao.findById(id);
		   String reportSrcFile = "reports/ContaPagar_Historico.jrxml";
		   List<RelatorioHistoricoBillpayDTO> itens = new ArrayList<>();
		   RelatorioHistoricoBillpayDTO item = new RelatorioHistoricoBillpayDTO();
		   
		   item.setCode("");
		   item.setFechado(bill.getFechada());
		   item.setData_recebimento(null);
		   item.setData_pagamento(null);
		   item.setAccount("");
		   item.setType_account("");
		   item.setAgence("");
		   item.setNome_banco("");
		   item.setDate(new java.sql.Date(bill.getDate().getTime()));
		   item.setDue_date(new java.sql.Date(bill.getDueDate().getTime()));
		   item.setFornecedor(bill.getClifor().getName());
		   item.setFulfillment(bill.getFulfillment() == null ? 1 : bill.getFulfillment());
		   item.setHistoric(bill.getHistoric());
		   item.setName(bill.getAccountPlan().getName());
		   item.setPortion(bill.getPortion() == null ? 1 : bill.getPortion());
		   item.setStatus(bill.getStatus());
		   item.setValue(new BigDecimal(bill.getValue()));
		   
		   
		   if(bill.getStatus().equals("QUITADA") || bill.getStatus().equals("QUITADO")) {
			   Payment pay = daoPayment.findByBillpay(bill.getId());
			   item.setData_pagamento(new java.sql.Date(pay.getDate().getTime()));
			   BankAccount bankAcount = daoBankAccount.findById(pay.getBankAccount().getId());
			   item.setCode(bankAcount.getCode());
			   item.setAccount(bankAcount.getAccount());
			   item.setType_account(bankAcount.getType());
			   item.setAgence(bankAcount.getBankAgence().getAgence());
			   item.setNome_banco(bankAcount.getBankAgence().getBank().getName());
		   }
		   
		   itens.add(item);
		   
	       JasperReport jasperReport = JasperCompileManager.compileReport(reportSrcFile);
	       JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(itens);
	       JasperPrint print = JasperFillManager.fillReport(jasperReport, null, beanColDataSource);
	       JRViewer viewer = new JRViewer(print);
	       viewer.setOpaque(true);
	       viewer.setVisible(true);
	       this.add(viewer);
	       this.setSize(1000, 600);
	       this.setVisible(true);
	   }

}
