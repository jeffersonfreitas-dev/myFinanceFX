package report.bankStatement;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import model.dao.DAOFactory;
import model.dao.DAOPayment;
import model.entities.BankStatement;
import model.entities.Payment;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;

public class ReportBankStatement extends JFrame{
	private static final long serialVersionUID = 1L;
	
	private DAOPayment daoPayment = DAOFactory.createPaymentDAO();
	
	   public void showExtratoSimple(List<BankStatement> extratos) throws JRException, ClassNotFoundException, SQLException {
		   
	        String reportSrcFile = "reports/Extrato_Simples.jrxml";
	        JasperReport jasperReport = JasperCompileManager.compileReport(reportSrcFile);
	        
	        List<RelatorioExtratoBasicoDTO> itens = new ArrayList<RelatorioExtratoBasicoDTO>();
	        
	        for(BankStatement e : extratos) {
	        	RelatorioExtratoBasicoDTO item = new RelatorioExtratoBasicoDTO();
	        	item.setDate(new java.sql.Date(e.getDate().getTime()));
	        	item.setHistoric(e.getHistoric());
	        	item.setCredit(e.isCredit());
	        	item.setValue(new BigDecimal(e.getValue()));
	        	itens.add(item);
	        }
	        
	        JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(itens);
	        JasperPrint print = JasperFillManager.fillReport(jasperReport, null, beanColDataSource);
	        JRViewer viewer = new JRViewer(print);
	        viewer.setOpaque(true);
	        viewer.setVisible(true);
	        this.add(viewer);
	        this.setSize(1000, 600);
	        this.setVisible(true);
	    }
	   
	   

	   public void showExtratoHistorico(BankStatement extrato) {
		   
		   try {
			String reportSrcFile = "reports/Extrato_Historico.jrxml";
			   JasperReport jasperReport = JasperCompileManager.compileReport(reportSrcFile);
			   
			   List<RelatorioExtratoBasicoDTO> itens = new ArrayList<RelatorioExtratoBasicoDTO>();
			   
			   RelatorioExtratoBasicoDTO item = new RelatorioExtratoBasicoDTO();
			   item.setDate(new java.sql.Date(extrato.getDate().getTime()));
//			   item.setHistoric(extrato.getHistoric());
			   item.setCredit(extrato.isCredit());
			   
			   if(extrato.getPayment() != null) {
				   Payment pay = daoPayment.findById(extrato.getPayment().getId());
				   item.setValue(new BigDecimal(extrato.getValue()));
				   item.setHistoric(pay.getBillpay().getHistoric());
			   }
			   itens.add(item);
			   
			   JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(itens);
			   JasperPrint print = JasperFillManager.fillReport(jasperReport, null, beanColDataSource);
			   JRViewer viewer = new JRViewer(print);
			   viewer.setOpaque(true);
			   viewer.setVisible(true);
			   this.add(viewer);
			   this.setSize(1000, 600);
			   this.setVisible(true);
		} catch (JRException e) {
			e.printStackTrace();
		}
	}
}
