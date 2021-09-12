package model.entities;

import java.io.Serializable;
import java.util.Date;

public class Transferencia implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String observation;
	private boolean close;
	private Date date;
	private BankAccount originAccount;
	private BankAccount destinationAccount;
	private Double value;

	public Transferencia() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}



	public String getObservation() {
		return observation;
	}

	public void setObservation(String observation) {
		this.observation = observation;
	}

	public boolean isClose() {
		return close;
	}

	public void setClose(boolean close) {
		this.close = close;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public BankAccount getOriginAccount() {
		return originAccount;
	}

	public void setOriginAccount(BankAccount originAccount) {
		this.originAccount = originAccount;
	}

	public BankAccount getDestinationAccount() {
		return destinationAccount;
	}

	public void setDestinationAccount(BankAccount destinationAccount) {
		this.destinationAccount = destinationAccount;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		if(value == null || value < 0) {
			value = 0.0;
		}
		this.value = value;
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
		Transferencia other = (Transferencia) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
