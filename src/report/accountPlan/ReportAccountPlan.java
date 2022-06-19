package report.accountPlan;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import model.dao.DAOAccountPlan;
import model.dao.DAOBankAccount;
import model.dao.DAOBillpay;
import model.dao.DAOFactory;
import model.dao.DAOPayment;
import model.dao.DAOReceivable;
import model.dao.DAOReceivement;
import model.entities.AccountPlan;
import model.entities.BankAccount;
import model.entities.Billpay;
import model.entities.Payment;
import model.entities.Receivable;
import model.entities.Receivement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;

public class ReportAccountPlan extends JFrame{
	private static final long serialVersionUID = 1L;
	
	private DAOAccountPlan dao = DAOFactory.createAccountPlanDAO();
	private DAOBillpay daoBill = DAOFactory.createBillpayDAO();
	private DAOReceivable daoReceivable = DAOFactory.createReceivableDAO();
	private DAOBankAccount daoBankAccount = DAOFactory.createBankAccountDAO();
	private DAOPayment daoPayment = DAOFactory.createPaymentDAO();
	private DAOReceivement daoReceivament = DAOFactory.createReceivementDAO();
	
	   public void showExtratoSimple(Integer id) throws JRException, ClassNotFoundException, SQLException {
		   
		   AccountPlan account = dao.findById(id);
		   String reportSrcFile = "";
		   List<RelatorioHistoricoAccontPlanDTO> itens = new ArrayList<RelatorioHistoricoAccontPlanDTO>();
		   
		  
		   if(account.isCredit()) {
			   
			   reportSrcFile = "reports/PlanoContaCredito_Historico.jrxml";
			   List<Receivable> bills = daoReceivable.findAllByAccountPlanId(id);
			   for(Receivable e : bills) {
				   RelatorioHistoricoAccontPlanDTO item = new RelatorioHistoricoAccontPlanDTO();
				   item.setCode("");
				   item.setData_recebimento(null);
				   item.setDate(new java.sql.Date(e.getDate().getTime()));
				   item.setDue_date(new java.sql.Date(e.getDueDate().getTime()));
				   item.setFornecedor(e.getClifor().getName());
				   item.setFulfillment(e.getFulfillment() == null ? 1 : e.getFulfillment());
				   item.setHistoric(e.getHistoric());
				   item.setName(e.getAccountPlan().getName());
				   item.setPortion(e.getPortion() == null ? 1 : e.getPortion());
				   item.setStatus(e.getStatus());
				   item.setValue(new BigDecimal(e.getValue()));
				   itens.add(item);
				   
				   if(e.getStatus().equals("RECEBIDA") || e.getStatus().equals("RECEBIDO")) {
					   Receivement pay = daoReceivament.findByReceivable(e.getId());
					   item.setData_recebimento(new java.sql.Date(pay.getDate().getTime()));
					   BankAccount bankAcount = daoBankAccount.findById(pay.getBankAccount().getId());
					   item.setCode(bankAcount.getCode());
				   }
			   }			   
			   
			   
		   }else {
			   reportSrcFile = "reports/PlanoContaDebito_Historico.jrxml";
			   List<Billpay> bills = daoBill.findAllByAccountPlanId(id);
			   for(Billpay e : bills) {
				   RelatorioHistoricoAccontPlanDTO item = new RelatorioHistoricoAccontPlanDTO();
				   item.setCode("");
				   item.setData_pagamento(null);
				   item.setDate(new java.sql.Date(e.getDate().getTime()));
				   item.setDue_date(new java.sql.Date(e.getDueDate().getTime()));
				   item.setFornecedor(e.getClifor().getName());
				   item.setFulfillment(e.getFulfillment() == null ? 1 : e.getFulfillment());
				   item.setHistoric(e.getHistoric());
				   item.setName(e.getAccountPlan().getName());
				   item.setPortion(e.getPortion() == null ? 1 : e.getPortion());
				   item.setStatus(e.getStatus());
				   item.setValue(new BigDecimal(e.getValue()));
				   itens.add(item);
				   
				   if(e.getStatus().equals("QUITADA")) {
					   Payment pay = daoPayment.findByBillpay(e.getId());
					   item.setData_pagamento(new java.sql.Date(pay.getDate().getTime()));
					   BankAccount bankAcount = daoBankAccount.findById(pay.getBankAccount().getId());
					   item.setCode(bankAcount.getCode());
				   }
			   }
		   }
		   
		   
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
	   
	   

//	   public void showExtratoHistorico(BankStatement extrato) {
//		   
//		   try {
//			String reportSrcFile = "reports/Extrato_Historico.jrxml";
//			   JasperReport jasperReport = JasperCompileManager.compileReport(reportSrcFile);
//			   
//			   List<RelatorioExtratoBasicoDTO> itens = new ArrayList<RelatorioExtratoBasicoDTO>();
//			   
//			   RelatorioExtratoBasicoDTO item = new RelatorioExtratoBasicoDTO();
//			   item.setDate(new java.sql.Date(extrato.getDate().getTime()));
////			   item.setHistoric(extrato.getHistoric());
//			   item.setCredit(extrato.isCredit());
//			   
//			   if(extrato.getPayment() != null) {
//				   Payment pay = daoPayment.findById(extrato.getPayment().getId());
//				   item.setValue(new BigDecimal(extrato.getValue()));
//				   item.setHistoric(pay.getBillpay().getHistoric());
//			   }
//			   itens.add(item);
//			   
//			   JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(itens);
//			   JasperPrint print = JasperFillManager.fillReport(jasperReport, null, beanColDataSource);
//			   JRViewer viewer = new JRViewer(print);
//			   viewer.setOpaque(true);
//			   viewer.setVisible(true);
//			   this.add(viewer);
//			   this.setSize(1000, 600);
//			   this.setVisible(true);
//		} catch (JRException e) {
//			e.printStackTrace();
//		}
//	}
}
