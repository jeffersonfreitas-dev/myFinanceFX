package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.Database;
import database.exceptions.DatabaseException;
import model.dao.DAOBankAgence;
import model.entities.Bank;
import model.entities.BankAgence;

public class DAOBankAgenceImpl implements DAOBankAgence{
	
	private Connection conn = null;
	
	public DAOBankAgenceImpl(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(BankAgence entity) {
		PreparedStatement stmt = null;
		String sql = "INSERT INTO bank_agence (agence, dv, id_bank) VALUES (?, ?, ?)";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, entity.getAgence());
			stmt.setString(2, entity.getDv());
			stmt.setInt(3, entity.getBank().getId());
			
			int result = stmt.executeUpdate();
			
			if(result < 1) {
				throw new DatabaseException("Nenhuma linha afetada na operação de inserção");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando insert agencia -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
		}
		
	}

	@Override
	public void update(BankAgence entity) {
		PreparedStatement stmt = null;
		String sql = "UPDATE bank_agence SET agence = ?, dv = ?, id_bank = ? WHERE id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, entity.getAgence());
			stmt.setString(2, entity.getDv());
			stmt.setInt(3, entity.getBank().getId());
			stmt.setInt(4, entity.getId());
			
			int result = stmt.executeUpdate();
			
			if(result < 1) {
				throw new DatabaseException("Nenhuma linha afetada na operação de alteração");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando update agencia -> " + e.getMessage());			
		}finally {
			Database.closeStatement(stmt);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement stmt = null;
		String sql = "DELETE FROM bank_agence WHERE id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			
			int result = stmt.executeUpdate();
			
			if(result < 1) {
				throw new DatabaseException("Nenhuma linha afetada na operação de exclusão");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando deleteById agencia -> " + e.getMessage());			
		}finally {
			Database.closeStatement(stmt);
		}
		
	}

	@Override
	public BankAgence findById(Integer id) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT s.*, b.* FROM bank_agence s INNER JOIN bank b ON s.id_bank = b.id WHERE id = ?";
		
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				Bank bank = instanceBank(rs);
				BankAgence agence = instanceBankAgence(rs, bank);
				return agence;
			}			
			return null;
		}catch(SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando findById agencia -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}

	
	@Override
	public List<BankAgence> findAllOrderByAgence() {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT s.*, b.* FROM bank_agence s INNER JOIN bank b ON s.id_bank = b.id ORDER BY agence";
		try {
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			List<BankAgence> list = new ArrayList<>();
			Map<Integer, Bank> banks = new HashMap<>();
			
			while(rs.next()) {
				
				Bank bank = banks.get(rs.getInt("id_bank"));
				if(bank == null) {
					bank = instanceBank(rs);
					banks.put(rs.getInt("id_bank"), bank);
				}
				
				BankAgence agence = instanceBankAgence(rs, bank);
				list.add(agence);
			}
			return list;
		}catch(SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando findAllOrderByAgence agencia -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}

	
	private BankAgence instanceBankAgence(ResultSet rs, Bank bank) throws SQLException{
		BankAgence agence = new BankAgence();
		agence.setId(rs.getInt("id"));
		agence.setAgence(rs.getString("agence"));
		agence.setDv(rs.getString("dv"));
		agence.setBank(bank);
		return null;
	}

	private Bank instanceBank(ResultSet rs) throws SQLException{
		Bank bank = new Bank();
		bank.setId(rs.getInt("id_bank"));
		bank.setCode(rs.getString("code"));
		bank.setName(rs.getString("name"));
		return bank;
	}
}
