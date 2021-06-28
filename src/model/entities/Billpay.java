package model.entities;

import java.io.Serializable;
import java.util.Date;

public class Billpay implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String invoice;
	private String historic;
	private Date date;
	private Date dueDate;
	private Double value;
	private Integer portion;
	private Integer fulfillment;
	private String status;
	private Clifor clifor;
	private Company company;
	private AccountPlan accountPlan;

	public Clifor getClifor() {
		return clifor;
	}

	public void setClifor(Clifor clifor) {
		this.clifor = clifor;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public AccountPlan getAccountPlan() {
		return accountPlan;
	}

	public void setAccountPlan(AccountPlan accountPlan) {
		this.accountPlan = accountPlan;
	}

	public Billpay() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getInvoice() {
		return invoice;
	}

	public void setInvoice(String invoice) {
		this.invoice = invoice;
	}

	public String getHistoric() {
		return historic;
	}

	public void setHistoric(String historic) {
		this.historic = historic;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Integer getPortion() {
		return portion;
	}

	public void setPortion(Integer portion) {
		this.portion = portion;
	}

	public Integer getFulfillment() {
		return fulfillment;
	}

	public void setFulfillment(Integer fulfillment) {
		this.fulfillment = fulfillment;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Billpay other = (Billpay) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
