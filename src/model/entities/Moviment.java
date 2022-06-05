package model.entities;

import java.io.Serializable;
import java.util.Date;

public class Moviment implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Date dateBeginner;
	private String name;
	private Date dateFinish;
	private Double valueBeginner;
	private Double valueFinish;
	private Double valuePoupanca;
	private Double balanceMoviment;
	private boolean closed;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDateBeginner() {
		return dateBeginner;
	}

	public void setDateBeginner(Date dateBeginner) {
		this.dateBeginner = dateBeginner;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDateFinish() {
		return dateFinish;
	}

	public void setDateFinish(Date dateFinish) {
		this.dateFinish = dateFinish;
	}

	public Double getValueBeginner() {
		return valueBeginner;
	}

	public void setValueBeginner(Double valueBeginner) {
		this.valueBeginner = valueBeginner;
	}

	public Double getValueFinish() {
		return valueFinish;
	}

	public void setValueFinish(Double valueFinish) {
		this.valueFinish = valueFinish;
	}

	public Double getBalanceMoviment() {
		return balanceMoviment;
	}

	public void setBalanceMoviment(Double balanceMoviment) {
		this.balanceMoviment = balanceMoviment;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public Double getValuePoupanca() {
		return valuePoupanca;
	}

	public void setValuePoupanca(Double valuePoupanca) {
		this.valuePoupanca = valuePoupanca;
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
		Moviment other = (Moviment) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Moviment() {
		super();
	}

}
