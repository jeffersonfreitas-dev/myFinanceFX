package model.dto.dashboard;

import java.io.Serializable;

public class ChartBillpayStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	private String status;
	private Double valor;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

}
