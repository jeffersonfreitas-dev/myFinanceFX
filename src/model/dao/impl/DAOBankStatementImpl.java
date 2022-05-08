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
import model.entities.Transferencia;
import utils.DefaultMessages;

public class DAOBankStatementImpl implements DAOBankStatement{
	
	private Connection conn;
	
	public DAOBankStatementImpl(Connection conn) {
		this.conn = conn;
	}

	
	@Override
	public void insert(BankStatement entity) {
		PreparedStatement stmt = null;
		String sql = "INSERT INTO bank_statement (date, credit, value, historic, id_payment, id_bank_account, id_receivement, initial_value, id_transferencia) VALUES"
				+ "(?, ?, ?, upper(?), ?, ?, ?, ?, ?)";
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
			if(entity.getTransferencia() == null) {
				stmt.setNull(9, Types.INTEGER);
			}else {
				stmt.setInt(9, entity.getTransferencia().getId());
			}
			int result = stmt.executeUpdate();
			
			if(result  < 1) {
				throw new DatabaseException(DefaultMessages.getMsgErroSalvar() + ". Nenhuma linha afetada");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(DefaultMessages.getMsgErroSalvar());
		}finally {
			Database.closeStatement(stmt);
		}
		
	}

	
	@Override
	public void update(BankStatement entity) {
		PreparedStatement stmt = null;
		String sql = "UPDATE bank_statement SET id = ?, date = ?, credit = ?, value = ?, historic = upper(?), id_payment = ?, id_bank_account = ? "
				+ "id_receivement = ?, initial_value= ?, id_transferencia = ?";
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
			stmt.setInt(10, entity.getTransferencia().getId());
			int result = stmt.executeUpdate();
			if(result < 1) {
				throw new DatabaseException(DefaultMessages.getMsgErroAtualizar() + ". Nenhuma linha afetada");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(DefaultMessages.getMsgErroAtualizar());
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
				throw new DatabaseException(DefaultMessages.getMsgErroDeletar() + ". Nenhuma linha afetada");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(DefaultMessages.getMsgErroDeletar());
		}finally {
			Database.closeStatement(stmt);
		}
	}

	
	@Override
	public BankStatement findById(Integer id) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT x.*, p.id as cod_payment, c.id as cod_account, r.id as cod_receivement, t.id as cod_transferencia FROM bank_statement x INNER JOIN bank_account "
				+ "ON x.id_bank_account = c.id LEFT JOIN payment p ON x.id_payment = p.id LEFT JOIN receivement r ON x.id_receivement = r.id transferencia t ON x.id_transferencia = t.id"
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
			throw new DatabaseException(DefaultMessages.getMsgErroFindby() + ". Código nº " + id);
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
		String sql = "SELECT x.*, p.id as cod_payment, c.id as cod_account, c.account, c.code, r.id as cod_receivement, t.id as cod_transferencia FROM bank_statement x INNER JOIN bank_account c "
				+ "ON x.id_bank_account = c.id LEFT JOIN payment p ON x.id_payment = p.id LEFT JOIN receivement r ON x.id_receivement = r.id LEFT JOIN transferencia t ON x.id_transferencia = t.id "
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
			throw new DatabaseException(DefaultMessages.getMsgErroFindall());
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
		entity.setTransferencia(getTransferencia(rs));
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

	private Transferencia getTransferencia(ResultSet rs) throws SQLException {
		if(rs.findColumn("cod_transferencia") > 0) {
			Transferencia trans = new Transferencia();
			trans.setId(rs.getInt("id_transferencia"));
			return trans;
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
		String sql = "SELECT x.*, p.id as cod_payment, c.id as cod_account, c.account, c.code, r.id as cod_receivement, t.id as cod_transferencia FROM bank_statement x INNER JOIN bank_account c "
				+ "ON x.id_bank_account = c.id LEFT JOIN payment p ON x.id_payment = p.id LEFT JOIN receivement r ON x.id_receivement = r.id LEFT JOIN transferencia t ON x.id_transferencia = t.id "
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
			throw new DatabaseException(DefaultMessages.getMsgErroFindall());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}
	
	
	@Override
	public void deleteTransferenciaById(Integer id) {
		PreparedStatement stmt = null;
		String sql = "DELETE FROM bank_statement WHERE id_transferencia = ? ";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			Integer result = stmt.executeUpdate();
			if(result < 1) {
				throw new DatabaseException(DefaultMessages.getMsgErroDeletar() + ". Nenhuma linha afetada");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(DefaultMessages.getMsgErroDeletar());
		}finally {
			Database.closeStatement(stmt);
		}
	}
	
	

	@Override
	public Integer hasMovimentByDate(Date dateBeginner, Date dateFinish) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT COUNT(id) as reg FROM bank_statement WHERE date BETWEEN ? AND ? and initial_value = false";
		
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setDate(1, new java.sql.Date(dateBeginner.getTime()));
			stmt.setDate(2, new java.sql.Date(dateFinish.getTime()));
			rs = stmt.executeQuery();
			if(rs.next()) {
				return rs.getInt("reg");
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(DefaultMessages.getMsgErroFindby() + ". Data inicial " + dateBeginner + " - " + dateFinish);
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}


	@Override
	public void deleteByDateInitialAndFinal(Date dateBeginner, Date dateFinish) {
		PreparedStatement stmt = null;
		String sql = "DELETE FROM bank_statement WHERE date BETWEEN ? AND ?";
		
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setDate(1, new java.sql.Date(dateBeginner.getTime()));
			stmt.setDate(2, new java.sql.Date(dateFinish.getTime()));
			Integer result = stmt.executeUpdate();
			if(result < 1) {
				throw new DatabaseException(DefaultMessages.getMsgErroDeletar() + ". Nenhuma linha afetada");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(DefaultMessages.getMsgErroFindby() + ". Data inicial " + dateBeginner + " - " + dateFinish);
		}finally {
			Database.closeStatement(stmt);
		}
		
	}
}
