package report.bankStatement;

import java.math.BigDecimal;
import java.util.Date;

public class RelatorioExtratoBasicoDTO {

	private Date date;
	private String account;
	private String agence;
	private String nomebank;
	private boolean credit;
	private BigDecimal value;
	private String historic;
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public boolean isCredit() {
		return credit;
	}
	public void setCredit(boolean credit) {
		this.credit = credit;
	}
	public BigDecimal getValue() {
		return value;
	}
	public void setValue(BigDecimal value) {
		this.value = value;
	}
	public String getHistoric() {
		return historic;
	}
	public void setHistoric(String historic) {
		this.historic = historic;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getAgence() {
		return agence;
	}
	public void setAgence(String agence) {
		this.agence = agence;
	}
	public String getNomebank() {
		return nomebank;
	}
	public void setNomebank(String nomebank) {
		this.nomebank = nomebank;
	}

	
}
