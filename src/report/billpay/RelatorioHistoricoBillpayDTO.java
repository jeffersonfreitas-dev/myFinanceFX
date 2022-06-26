package report.billpay;

import java.math.BigDecimal;
import java.sql.Date;

public class RelatorioHistoricoBillpayDTO {

	private String name;
	private String historic;
	private Date date;
	private Date due_date;
	private BigDecimal value;
	private Integer portion;
	private Integer fulfillment;
	private String status;
	private String fornecedor;
	private Date data_pagamento;
	private Date data_recebimento;
	private String code;
	private Boolean fechado;
	private String account;
	private String type_account;
	private String agence;
	private String nome_banco;
	private String nome_empresa;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Date getDue_date() {
		return due_date;
	}

	public void setDue_date(Date due_date) {
		this.due_date = due_date;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
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

	public String getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(String fornecedor) {
		this.fornecedor = fornecedor;
	}

	public Date getData_pagamento() {
		return data_pagamento;
	}

	public void setData_pagamento(Date data_pagamento) {
		this.data_pagamento = data_pagamento;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getData_recebimento() {
		return data_recebimento;
	}

	public void setData_recebimento(Date data_recebimento) {
		this.data_recebimento = data_recebimento;
	}

	public boolean isFechado() {
		return fechado;
	}

	public void setFechado(boolean fechado) {
		this.fechado = fechado;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getType_account() {
		return type_account;
	}

	public void setType_account(String type_account) {
		this.type_account = type_account;
	}

	public String getAgence() {
		return agence;
	}

	public void setAgence(String agence) {
		this.agence = agence;
	}

	public String getNome_banco() {
		return nome_banco;
	}

	public void setNome_banco(String nome_banco) {
		this.nome_banco = nome_banco;
	}

	public Boolean getFechado() {
		return fechado;
	}

	public void setFechado(Boolean fechado) {
		this.fechado = fechado;
	}

	public String getNome_empresa() {
		return nome_empresa;
	}

	public void setNome_empresa(String nome_empresa) {
		this.nome_empresa = nome_empresa;
	}

}
