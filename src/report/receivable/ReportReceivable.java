package report.receivable;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import model.dao.DAOBankAccount;
import model.dao.DAOFactory;
import model.dao.DAOReceivable;
import model.dao.DAOReceivement;
import model.entities.BankAccount;
import model.entities.Receivable;
import model.entities.Receivement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;

public class ReportReceivable extends JFrame{
	private static final long serialVersionUID = 1L;
	
	private DAOReceivable dao = DAOFactory.createReceivableDAO();
	private DAOReceivement daoReceivement = DAOFactory.createReceivementDAO();
	private DAOBankAccount daoBankAccount = DAOFactory.createBankAccountDAO();

	
	   public void showExtratoSimple(Integer id) throws JRException, ClassNotFoundException, SQLException {
		   
		   Receivable rec = dao.findById(id);
		   String reportSrcFile = "reports/ContaReceber_Historico.jrxml";
		   List<RelatorioHistoricoReceivableDTO> itens = new ArrayList<>();
		   RelatorioHistoricoReceivableDTO item = new RelatorioHistoricoReceivableDTO();
		   
		   item.setCode("");
		   item.setFechado(rec.getFechada());
		   item.setData_recebimento(null);
		   item.setData_pagamento(null);
		   item.setAccount("");
		   item.setType_account("");
		   item.setAgence("");
		   item.setNome_banco("");
		   item.setDate(new java.sql.Date(rec.getDate().getTime()));
		   item.setDue_date(new java.sql.Date(rec.getDueDate().getTime()));
		   item.setFornecedor(rec.getClifor().getName());
		   item.setFulfillment(rec.getFulfillment() == null ? 1 : rec.getFulfillment());
		   item.setHistoric(rec.getHistoric());
		   item.setName(rec.getAccountPlan().getName());
		   item.setPortion(rec.getPortion() == null ? 1 : rec.getPortion());
		   item.setStatus(rec.getStatus());
		   item.setValue(new BigDecimal(rec.getValue()));
		   
		   
		   if(rec.getStatus().equals("RECEBIDA") || rec.getStatus().equals("RECEBIDO")) {
			   Receivement pay = daoReceivement.findByReceivable(rec.getId());
			   item.setData_recebimento(new java.sql.Date(pay.getDate().getTime()));
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
