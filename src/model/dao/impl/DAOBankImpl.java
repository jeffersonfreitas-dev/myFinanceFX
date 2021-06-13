package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.Database;
import database.exceptions.DatabaseException;
import model.dao.DAOBank;
import model.entities.Bank;

public class DAOBankImpl implements DAOBank{
	
	private Connection conn;
	
	public DAOBankImpl(Connection conn) {
		this.conn = conn;
	}

	
	@Override
	public void insert(Bank entity) {
		PreparedStatement stmt = null;
		String sql = "INSERT INTO bank (code, name) VALUES (?, ?)";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, entity.getCode());
			stmt.setString(2, entity.getName());
			int result = stmt.executeUpdate();
			
			if(result < 1) {
				throw new DatabaseException("Nenhuma linha afetada na operação de salvar");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao salvar o registro -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
		}
	}

	
	@Override
	public void update(Bank entity) {
		PreparedStatement stmt = null;
		String sql = "UPDATE bank SET code = ?, name = ? WHERE id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, entity.getCode());
			stmt.setString(2, entity.getName());
			stmt.setInt(3, entity.getId());
			int result = stmt.executeUpdate();
			
			if(result < 1) {
				throw new DatabaseException("Nenhuma linha afetada na operação de atualizar");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao atualizar o registro -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
		}
	}

	
	@Override
	public void deleteById(Integer id) {
		PreparedStatement stmt = null;
		String sql = "DELETE FROM bank WHERE id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			int result = stmt.executeUpdate();
			
			if(result < 1) {
				throw new DatabaseException("Nenhuma linha afetada na operação de exclusão");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao deletar o registro -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
		}
	}

	
	@Override
	public Bank findById(Integer id) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM bank WHERE id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				Bank bank = instantiateBank(rs); 
				return bank;
			}
			return null;

		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Erro ao executar: findById do banco -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}

	
	@Override
	public List<Bank> findAllOrderByName() {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM bank ORDER BY name";
		try {
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			
			List<Bank> list = new ArrayList<>();
			
			while(rs.next()) {
				Bank bank = instantiateBank(rs); 
				list.add(bank);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Erro ao executar comando: findAllOrderByNane em banco -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}


	private Bank instantiateBank(ResultSet rs) throws SQLException{
		Bank bank = new Bank();
		bank.setId(rs.getInt("id"));
		bank.setCode(rs.getString("code"));
		bank.setName(rs.getString("name"));
		return bank;
	}


	@Override
	public Bank findByCodeOrName(String code, String name) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM bank WHERE code = ? OR name = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, code);
			stmt.setString(2, name);
			rs = stmt.executeQuery();
			Bank bank = null;
			if(rs.next()) {
				bank = instantiateBank(rs); 
			}
			return bank;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Erro ao executar comando: alreadyRecordedByCodeOrName em banco -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}

}
