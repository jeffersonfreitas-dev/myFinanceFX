package report.bankStatement;

import java.math.BigDecimal;
import java.util.Date;

public class RelatorioExtratoHistoricoDTO {

	private Date date;
	private String Historic;
	private boolean credit;
	private BigDecimal value;
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getHistoric() {
		return Historic;
	}
	public void setHistoric(String historic) {
		Historic = historic;
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
	
	

}
