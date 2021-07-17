package model.entities;

import java.io.Serializable;
import java.util.Date;

public class BankStatement implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Date date;
	private boolean credit;
	private Double value;
	private String historic;
	private BankAccount bankAccount;
	private Payment payment;
	private Receivement receivement;
	private boolean initialValue;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

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

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public String getHistoric() {
		return historic;
	}

	public void setHistoric(String historic) {
		this.historic = historic;
	}

	public BankAccount getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public Receivement getReceivement() {
		return receivement;
	}

	public void setReceivement(Receivement receivement) {
		this.receivement = receivement;
	}

	public boolean isInitialValue() {
		return initialValue;
	}

	public void setInitialValue(boolean initialValue) {
		this.initialValue = initialValue;
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
		BankStatement other = (BankStatement) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public BankStatement() {

	}

}
