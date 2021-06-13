package model.entities;

import java.io.Serializable;

public class BankAgence implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String agence;
	private String dv;
	private Bank bank;

	public BankAgence() {
	}

	public BankAgence(Integer id, String agence, String dv, Bank bank) {
		this.id = id;
		this.agence = agence;
		this.dv = dv;
		this.bank = bank;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAgence() {
		return agence;
	}

	public void setAgence(String agence) {
		this.agence = agence;
	}

	public String getDv() {
		return dv;
	}

	public void setDv(String dv) {
		this.dv = dv;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
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
		BankAgence other = (BankAgence) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
