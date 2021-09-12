package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import database.Database;
import database.exceptions.DatabaseException;
import model.dao.DAOBankStatement;
import model.entities.BankAccount;
import model.entities.BankStatement;
import model.entities.Payment;
import model.entities.Receivement;

public class DAOBankStatementImpl implements DAOBankStatement{
	
	private Connection conn;
	
	public DAOBankStatementImpl(Connection conn) {
		this.conn = conn;
	}

	
	@Override
	public void insert(BankStatement entity) {
		PreparedStatement stmt = null;
		String sql = "INSERT INTO bank_statement (date, credit, value, historic, id_payment, id_bank_account, id_receivement, initial_value) VALUES"
				+ "(?, ?, ?, upper(?), ?, ?, ?, ?)";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setDate(1, new java.sql.Date(entity.getDate().getTime()));
			stmt.setBoolean(2, entity.isCredit());
			stmt.setDouble(3, entity.getValue());
			stmt.setString(4, entity.getHistoric());
			if(entity.getPayment() == null) {
				stmt.setNull(5, Types.INTEGER);
			}else {
				stmt.setInt(5, entity.getPayment().getId());
			}
			stmt.setInt(6, entity.getBankAccount().getId());
			if(entity.getReceivement() == null) {
				stmt.setNull(7, Types.INTEGER);
			}else {
				stmt.setInt(7, entity.getReceivement().getId());
			}
			stmt.setBoolean(8, entity.isInitialValue());
			int result = stmt.executeUpdate();
			
			if(result  < 1) {
				throw new DatabaseException("Falha ao salvar o registro");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando insert extrato -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
		}
		
	}

	
	@Override
	public void update(BankStatement entity) {
		PreparedStatement stmt = null;
		String sql = "UPDATE bank_statement SET id = ?, date = ?, credit = ?, value = ?, historic = upper(?), id_payment = ?, id_bank_account = ? "
				+ "id_receivement = ?, initial_value= ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, entity.getId());
			stmt.setDate(2, new java.sql.Date(entity.getDate().getTime()));
			stmt.setBoolean(3, entity.isCredit());
			stmt.setDouble(4, entity.getValue());
			stmt.setString(5, entity.getHistoric());
			stmt.setInt(6, entity.getPayment().getId());
			stmt.setInt(7, entity.getBankAccount().getId());
			stmt.setInt(8, entity.getReceivement().getId());
			stmt.setBoolean(9, entity.isInitialValue());
			int result = stmt.executeUpdate();
			if(result < 1) {
				throw new DatabaseException("Falha ao salvar o registro");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando update extrato -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
		}
	}

	
	@Override
	public void deleteById(Integer id) {
		PreparedStatement stmt = null;
		String sql = "DELETE FROM bank_statement WHERE id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			int result = stmt.executeUpdate();
			if(result < 1) {
				throw new DatabaseException("Falha ao deletar o registro");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando delete extrato -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
		}
	}

	
	@Override
	public BankStatement findById(Integer id) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT x.*, p.id as cod_payment, c.id as cod_account, r.id as cod_receivement FROM bank_statement x INNER JOIN bank_account "
				+ "ON x.id_bank_account = c.id LEFT JOIN payment p ON x.id_payment = p.id LEFT JOIN receivement r ON x.id_receivement = r.id "
				+ "WHERE id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				BankStatement entity = getBankStatement(rs);
				return entity;
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando delete extrato -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}


	@Override
	public List<BankStatement> findAllOrderByDateAndBankAccount() {
		List<BankStatement> list = new ArrayList<BankStatement>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT x.*, p.id as cod_payment, c.id as cod_account, c.account, c.code, r.id as cod_receivement FROM bank_statement x INNER JOIN bank_account c "
				+ "ON x.id_bank_account = c.id LEFT JOIN payment p ON x.id_payment = p.id LEFT JOIN receivement r ON x.id_receivement = r.id "
				+ "ORDER BY x.date, c.account";
		try {
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				BankStatement entity = getBankStatement(rs);
				list.add(entity);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando findAll extrato -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}	
	
	
	private BankStatement getBankStatement(ResultSet rs) throws SQLException {
		BankStatement entity = new BankStatement();
		entity.setBankAccount(getBankAccount(rs));
		entity.setCredit(rs.getBoolean("credit"));
		entity.setDate(new Date(rs.getDate("date").getTime()));
		entity.setHistoric(rs.getString("historic"));
		entity.setId(rs.getInt("id"));
		entity.setPayment(getPayment(rs));
		entity.setReceivement(getReceivement(rs));
		entity.setValue(rs.getDouble("value"));
		entity.setInitialValue(rs.getBoolean("initial_value"));
		return entity;
	}


	private Receivement getReceivement(ResultSet rs) throws SQLException {
		if(rs.findColumn("cod_receivement") > 0) {
			Receivement rec = new Receivement();
			rec.setId(rs.getInt("id_receivement"));
			return rec;
		}
		return null;
	}


	private Payment getPayment(ResultSet rs) throws SQLException {
		if(rs.findColumn("cod_payment") > 0) {
			Payment pay = new Payment();
			pay.setId(rs.getInt("id_payment"));
			return pay;
		}
		return null;
	}


	private BankAccount getBankAccount(ResultSet rs) throws SQLException {
		BankAccount account = new BankAccount();
		account.setId(rs.getInt("cod_account"));
		account.setCode(rs.getString("code"));
		return account;
	}


	@Override
	public List<BankStatement> findAllByAccountAndMoviment(BankAccount bankAccount, Date dateBeginner, Date dateFinish) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT x.*, p.id as cod_payment, c.id as cod_account, c.account, c.code, r.id as cod_receivement FROM bank_statement x INNER JOIN bank_account c "
				+ "ON x.id_bank_account = c.id LEFT JOIN payment p ON x.id_payment = p.id LEFT JOIN receivement r ON x.id_receivement = r.id "
				+ "WHERE c.id = ? and x.date between ? and ? ORDER BY x.date, c.account";
		try {
			List<BankStatement> list = new ArrayList<>();
			
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, bankAccount.getId());
			stmt.setDate(2, new java.sql.Date(dateBeginner.getTime()));
			stmt.setDate(3, new java.sql.Date(dateFinish.getTime()));
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				BankStatement item = getBankStatement(rs);
				list.add(item);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}

}
