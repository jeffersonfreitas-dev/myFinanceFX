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
import model.dao.DAOReceivement;
import model.entities.BankAccount;
import model.entities.Receivable;
import model.entities.Receivement;

public class DAOReceivementImpl implements DAOReceivement{
	
	private Connection conn;
	public DAOReceivementImpl(Connection conn) {
		this.conn = conn;
	}

	
	@Override
	public Integer insert(Receivement entity) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "INSERT INTO receivement (date, id_bank_account, id_receivable) VALUES (?, ?, ?)";
		try {
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setDate(1, new java.sql.Date(entity.getDate().getTime()));
			stmt.setInt(2, entity.getBankAccount().getId());
			stmt.setInt(3, entity.getReceivable().getId());
			int result = stmt.executeUpdate();
			if(result < 1) {
				throw new DatabaseException("Falha ao salvar o registro");
			}
			
			rs = stmt.getGeneratedKeys();
			
			if(rs.next()) {
				return rs.getInt("id");
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando insert do pagamento -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
		}
	}

	
	@Override
	public void update(Receivement entity) {
		PreparedStatement stmt = null;
		String sql = "UPDATE receivement SET date = ?, id_bank_account =?, id_receivable = ? WHERE id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setDate(1, new java.sql.Date(entity.getDate().getTime()));
			stmt.setInt(2, entity.getBankAccount().getId());
			stmt.setInt(3, entity.getReceivable().getId());
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
		String sql = "DELETE FROM receivement WHERE id = ?";
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
	public Receivement findById(Integer id) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT p.*, b.id as cod_bill, a.id as cod_account FROM receivement p INNER JOIN receivable b ON p.id_receivable = b.id "
				+ "INNER JOIN bank_account a ON p.id_bank_account = a.id WHERE p.id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				Receivement pay = getEntity(rs);
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
	public List<Receivement> findAllOrderByDate() {
		List<Receivement> list = new ArrayList<Receivement>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT p.*, b.id as cod_bill, a.id as cod_account FROM receivement p INNER JOIN receivable b ON p.id_receivable = b.id "
				+ "INNER JOIN bank_account a ON p.id_bank_account = a.id WHERE p.id = ? ORDER BY p.date";
		try {
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				Receivement pay = getEntity(rs);
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
	
	
	@Override
	public Receivement findByReceivable(Integer receivable) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT p.*, \r\n"
				+ "	b.id as cod_bill, \r\n"
				+ "	a.id as cod_account\r\n"
				+ "	FROM receivement p INNER JOIN receivable b ON p.id_receivable = b.id \r\n"
				+ "	INNER JOIN bank_account a ON p.id_bank_account = a.id\r\n"
				+ "	WHERE b.id = ?";
		
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, receivable);
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				Receivement pay = getEntity(rs);
				return pay;
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando findById recebimento -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}

	
	private Receivement getEntity(ResultSet rs) throws SQLException {
		Receivement pay = new Receivement();
		pay.setBankAccount(getAccount(rs));
		pay.setReceivable(getReceivable(rs));
		pay.setDate(new java.util.Date(rs.getDate("date").getTime()));
		pay.setId(rs.getInt("id"));
		return pay;
	}


	private BankAccount getAccount(ResultSet rs) throws SQLException {
		BankAccount account = new BankAccount();
		account.setId(rs.getInt("cod_account"));
		return account;
	}


	private Receivable getReceivable(ResultSet rs) throws SQLException {
		Receivable bill = new Receivable();
		bill.setId(rs.getInt("cod_bill"));
		return bill;
	}




}
