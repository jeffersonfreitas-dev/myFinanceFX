package model.entities;

import java.io.Serializable;
import java.util.Date;

public class Receivement implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private BankAccount bankAccount;
	private Receivable receivable;
	private Date date;

	public Receivement() {

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public BankAccount getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}

	public Receivable getReceivable() {
		return receivable;
	}

	public void setReceivable(Receivable receivable) {
		this.receivable = receivable;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
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
		Receivement other = (Receivement) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
