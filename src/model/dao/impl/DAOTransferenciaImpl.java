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
import model.dao.DAOTransferencia;
import model.entities.BankAccount;
import model.entities.Transferencia;

public class DAOTransferenciaImpl implements DAOTransferencia{
	
	private Connection conn;
	public DAOTransferenciaImpl(Connection conn) {
		this.conn = conn;
	}
	
	

	@Override
	public Integer insert(Transferencia entity) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "INSERT INTO transferencia (date, observation, value, id_account_origin, id_account_destination, closed) VALUES "
				+ "(?, upper(?), ?, ?, ?, ?)";
		try {
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setDate(1, new java.sql.Date(entity.getDate().getTime()));
			stmt.setString(2, entity.getObservation());
			stmt.setDouble(3, entity.getValue());
			stmt.setInt(4, entity.getOriginAccount().getId());
			stmt.setInt(5, entity.getDestinationAccount().getId());
			stmt.setBoolean(6, entity.isClose());
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
			throw new DatabaseException("Ocorreu um erro ao executar o comando insert -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
		}
	}

	
	@Override
	public void update(Transferencia entity) {
		PreparedStatement stmt = null;
		String sql = "UPDATE transferencia SET date = ?, observation = upper(?), value = ?, id_account_origin = ?, id_account_destination = ?,"
				+ " closed = ? WHERE id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setDate(1, new java.sql.Date(entity.getDate().getTime()));
			stmt.setString(2, entity.getObservation());
			stmt.setDouble(3, entity.getValue());
			stmt.setInt(4, entity.getOriginAccount().getId());
			stmt.setInt(5, entity.getDestinationAccount().getId());
			stmt.setBoolean(6, entity.isClose());
			int result = stmt.executeUpdate();
			if(result < 1) {
				throw new DatabaseException("Falha ao atualizar o registro");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando update -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
		}
	}

	
	@Override
	public void deleteById(Integer id) {
		PreparedStatement stmt = null;
		String sql = "DELETE FROM transferencia WHERE id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			int result = stmt.executeUpdate();
			if(result < 1) {
				throw new DatabaseException("Falha ao deletar o registro");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando delete -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
		}
	}

	
	@Override
	public Transferencia findById(Integer id) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT t.*, o.id as cod_origem, o.type as type_origem, o.code as code_origem, d.id as cod_destination, d.type as type_destino, d.code as code_destination FROM transferencia t INNER JOIN bank_account o ON t.id_account_origin = o.id "
				+ "INNER JOIN bank_account d ON t.id_account_destination = d.id WHERE t.id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				Transferencia transf = getTransferencia(rs);
				return transf;
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando findById-> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}


	@Override
	public List<Transferencia> findAllOrderDate() {
		List<Transferencia> list = new ArrayList<Transferencia>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT t.*, o.id as cod_origem, o.code as code_origem, o.type as type_origem, d.id as cod_destination, d.code as code_destination, d.type as type_destino FROM transferencia t INNER JOIN bank_account o ON t.id_account_origin = o.id "
				+ "INNER JOIN bank_account d ON t.id_account_destination = d.id order by t.date";
		try {
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				Transferencia transf = getTransferencia(rs);
				list.add(transf);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando findAll -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}


	
	private Transferencia getTransferencia(ResultSet rs) throws SQLException {
		Transferencia transf = new Transferencia();
		transf.setId(rs.getInt("id"));
		transf.setDate(new java.util.Date(rs.getDate("date").getTime()));
		transf.setValue(rs.getDouble("value"));
		transf.setObservation(rs.getString("observation"));
		transf.setOriginAccount(getOriginAccount(rs));
		transf.setDestinationAccount(getDestinationAccount(rs));
		return transf;
	}


	private BankAccount getOriginAccount(ResultSet rs) throws SQLException {
		BankAccount account = new BankAccount();
		account.setId(rs.getInt("cod_origem"));
		account.setCode(rs.getString("code_origem"));
		account.setType(rs.getString("type_origem"));
		return account;
	}

	private BankAccount getDestinationAccount(ResultSet rs) throws SQLException {
		BankAccount account = new BankAccount();
		account.setId(rs.getInt("cod_destination"));
		account.setCode(rs.getString("code_destination"));
		account.setType(rs.getString("type_destino"));
		return account;
	}

}
