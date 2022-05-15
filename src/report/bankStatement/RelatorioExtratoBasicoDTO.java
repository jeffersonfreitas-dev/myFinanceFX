package report.bankStatement;

import java.math.BigDecimal;
import java.util.Date;

public class RelatorioExtratoBasicoDTO {

	private Date date;
//	private String Historic;
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
}
