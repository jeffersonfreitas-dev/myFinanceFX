package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import database.Database;
import database.exceptions.DatabaseException;
import model.dao.DAOPayment;
import model.entities.Bank;
import model.entities.BankAccount;
import model.entities.BankAgence;
import model.entities.Billpay;
import model.entities.Clifor;
import model.entities.Payment;

public class DAOPaymentImpl implements DAOPayment{
	
	private Connection conn;
	public DAOPaymentImpl(Connection conn) {
		this.conn = conn;
	}

	
	@Override
	public Integer insert(Payment entity) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "INSERT INTO payment (date, id_bank_account, id_billpay) VALUES (?, ?, ?)";
		try {
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setDate(1, new java.sql.Date(entity.getDate().getTime()));
			stmt.setInt(2, entity.getBankAccount().getId());
			stmt.setInt(3, entity.getBillpay().getId());
			
			int result = stmt.executeUpdate();
			if(result < 1) {
				throw new DatabaseException("Falha ao salvar o registro");
			}
			
			rs = stmt.getGeneratedKeys();
			
			if(rs.next()) {
				return rs.getInt(1);
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando insert do pagamento -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}

	
	@Override
	public void update(Payment entity) {
		PreparedStatement stmt = null;
		String sql = "UPDATE payment SET date = ?, id_bank_account =?, id_billpay = ? WHERE id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setDate(1, new java.sql.Date(entity.getDate().getTime()));
			stmt.setInt(2, entity.getBankAccount().getId());
			stmt.setInt(3, entity.getBillpay().getId());
			int result = stmt.executeUpdate();
			if(result < 1) {
				throw new DatabaseException("Falha ao atualizar o registro");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando update pagamento -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
		}
	}

	
	@Override
	public void deleteById(Integer id) {
		PreparedStatement stmt = null;
		String sql = "DELETE FROM payment WHERE id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			int result = stmt.executeUpdate();
			if(result < 1) {
				throw new DatabaseException("Falha ao deletar o registro");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando delete pagamento -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
		}
	}

	
	@Override
	public Payment findById(Integer id) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT p.*, \r\n"
				+ "	b.id as cod_bill, \r\n"
				+ "	b.historic as historicbill,\r\n"
				+ "	b.date as datebill,\r\n"
				+ "	b.due_date as due_datebill,\r\n"
				+ "	b.portion as portionbill,\r\n"
				+ "	b.fulfillment as fulfillmentbill,\r\n"
				+ "	a.id as cod_account,\r\n"
				+ "	a.account,\r\n"
				+ "	a.type,\r\n"
				+ "	aa.id as idagence,\r\n"
				+ "	aa.agence,\r\n"
				+ "	bb.id as idbank,\r\n"
				+ "	bb.name as namebank,\r\n"
				+ "	ac.id as idaccountplan,\r\n"
				+ "	ac.name as nameaccountplan,\r\n"
				+ "	c.id as idclifor,\r\n"
				+ "	c.name as nameclifor\r\n"
				+ "	FROM payment p INNER JOIN billpay b ON p.id_billpay = b.id \r\n"
				+ "	INNER JOIN bank_account a ON p.id_bank_account = a.id\r\n"
				+ "	INNER JOIN bank_agence aa ON a.id_bank_agence = aa.id\r\n"
				+ "	INNER JOIN clifor c ON b.id_clifor = c.id\r\n"
				+ "	INNER JOIN account_plan ac on b.id_account_plan = ac.id\r\n"
				+ "	INNER JOIN bank bb ON aa.id_bank = bb.id\r\n"
				+ "	WHERE p.id = ?;";
		
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				Payment pay = getEntity(rs);
				return pay;
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando findById pagamento -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}


	@Override
	public List<Payment> findAllOrderByDate() {
		List<Payment> list = new ArrayList<Payment>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT p.*, b.id as cod_bill, a.id as cod_account FROM payment p INNER JOIN billpay b ON p.id_billpay = b.id "
				+ "INNER JOIN bank_account a ON p.id_bank_account = a.id WHERE id = ? ORDER BY date";
		try {
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				Payment pay = getEntity(rs);
				list.add(pay);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando findAllOrderByDueDate pagamento -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}

	
	private Payment getEntity(ResultSet rs) throws SQLException {
		Payment pay = new Payment();
		pay.setBankAccount(getAccount(rs));
		pay.setBillpay(getBillpay(rs));
		pay.setDate(new java.util.Date(rs.getDate("date").getTime()));
		pay.setId(rs.getInt("id"));
		return pay;
	}


	private BankAccount getAccount(ResultSet rs) throws SQLException {
		BankAccount account = new BankAccount();
		account.setId(rs.getInt("cod_account"));
		account.setAccount(rs.getString("account"));
		account.setType(rs.getString("type"));
		account.setBankAgence(getBankAgence(rs));
		return account;
	}


	private BankAgence getBankAgence(ResultSet rs) throws SQLException {
		BankAgence agence = new BankAgence();
		agence.setId(rs.getInt("idagence"));
		agence.setAgence(rs.getString("agence"));
		agence.setBank(getBank(rs));
		return agence;
	}


	private Bank getBank(ResultSet rs) throws SQLException {
		Bank bank = new Bank();
		bank.setId(rs.getInt("idbank"));
		bank.setName(rs.getString("namebank"));
		return bank;
	}


	private Billpay getBillpay(ResultSet rs) throws SQLException {
		Billpay bill = new Billpay();
		bill.setId(rs.getInt("cod_bill"));
		bill.setHistoric(rs.getString("historicbill"));
		bill.setDate(rs.getDate("datebill"));
		bill.setDueDate(rs.getDate("due_datebill"));
		bill.setPortion(rs.getInt("portionbill"));
		bill.setFulfillment(rs.getInt("fulfillmentbill"));
		bill.setClifor(getClifor(rs));
		return bill;
	}


	private Clifor getClifor(ResultSet rs) throws SQLException {
		Clifor clifor = new Clifor();
		clifor.setId(rs.getInt("idclifor"));
		clifor.setName(rs.getString("nameclifor"));
		return clifor;
	}

}
