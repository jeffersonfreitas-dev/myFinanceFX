package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.Database;
import database.exceptions.DatabaseException;
import model.dao.DAOBankAccount;
import model.entities.Bank;
import model.entities.BankAccount;
import model.entities.BankAgence;
import model.entities.Company;

public class DAOBankAccountImpl implements DAOBankAccount{
	
	private Connection conn;
	public DAOBankAccountImpl (Connection conn) {
		this.conn = conn;
	}
	

	@Override
	public void insert(BankAccount entity) {
		PreparedStatement stmt = null;
		String sql = "INSERT INTO bank_account (code, account, id_bank_agence, id_company, balance) VALUES (upper(?), upper(?), ?, ?, ?)";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, entity.getCode());
			stmt.setString(2, entity.getAccount());
			stmt.setInt(3, entity.getBankAgence().getId());
			stmt.setInt(4, entity.getCompany().getId());
			stmt.setDouble(5, entity.getBalance());
			int result = stmt.executeUpdate();
			
			if(result < 1) {
				throw new DatabaseException("Falha ao salvar o registro");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando insert conta bancária -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
		}
	}

	
	@Override
	public void update(BankAccount entity) {
		PreparedStatement stmt = null;
		String sql = "UPDATE bank_account SET code = upper(?), account = upper(?), id_bank_agence = ?, id_company = ?, balance = ? WHERE id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, entity.getCode());
			stmt.setString(2, entity.getAccount());
			stmt.setInt(3, entity.getBankAgence().getId());
			stmt.setInt(4, entity.getCompany().getId());
			stmt.setDouble(5, entity.getBalance());
			stmt.setInt(6, entity.getId());
			int result = stmt.executeUpdate();
			
			if(result < 1) {
				throw new DatabaseException("Falha ao atualizar o registro");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando update conta bancária -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
		}		
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement stmt = null;
		String sql = "DELETE FROM bank_account WHERE id = ?";		
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			int result = stmt.executeUpdate();
			
			if(result < 1) {
				throw new DatabaseException("Falha ao deletar o registro");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando delete conta bancária -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
		}	
	}

	@Override
	public BankAccount findById(Integer id) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT acc.*, a.id as cod_agence, a.agence, a.dv, a.id_bank, c.id as cod_comp, c.name as name_comp, b.id as cod_bank, b.code as code_bank, b.name as name_bank FROM bank_account acc INNER JOIN bank_agence a ON acc.id_bank_agence = a.id INNER JOIN"
				+ " company c ON acc.id_company = c.id INNER JOIN bank b ON a.id_bank = b.id WHERE acc.id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			if(rs.next()) {
				BankAccount account = getBankAccount(rs);
				return account;
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando findById conta bancária -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}


	@Override
	public List<BankAccount> findAllOrderByAccount() {
		List<BankAccount> list = new ArrayList<BankAccount>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT acc.*, a.id as cod_agence, a.agence, a.dv, a.id_bank, c.id as cod_comp, c.name as name_comp, b.id as cod_bank, b.code as code_bank, b.name as name_bank FROM bank_account acc INNER JOIN bank_agence a ON acc.id_bank_agence = a.id INNER JOIN "
				+ " company c ON acc.id_company = c.id INNER JOIN bank b ON a.id_bank = b.id";	
		try {
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			while(rs.next()) {
				BankAccount account = getBankAccount(rs);
				list.add(account);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando findAll conta bancária -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}

	
	@Override
	public BankAccount findByAccountAndCompanyId(String account, Integer id_company) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT acc.*, a.id as cod_agence, a.agence, a.dv, a.id_bank, c.id as cod_comp, c.name as name_comp, b.id as cod_bank, b.code as code_bank, b.name as name_bank FROM bank_account acc INNER JOIN bank_agence a ON acc.id_bank_agence = a.id INNER JOIN"
				+ " company c ON acc.id_company = c.id INNER JOIN bank b ON a.id_bank = b.id WHERE account = upper(?) AND id_company = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, account);
			stmt.setInt(2, id_company);
			rs = stmt.executeQuery();
			if(rs.next()) {
				BankAccount acc = getBankAccount(rs);
				return acc;
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando findById conta bancária -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}
	

	private BankAccount getBankAccount(ResultSet rs) throws SQLException {
		BankAccount account = new BankAccount();
		account.setId(rs.getInt("id"));
		account.setAccount(rs.getString("account"));
		account.setCode(rs.getString("code"));
		account.setBankAgence(getBankAgence(rs));
		account.setCompany(getCompany(rs));
		account.setBalance(rs.getDouble("balance"));
		return account;
	}


	private Company getCompany(ResultSet rs) throws SQLException {
		return new Company(rs.getInt("cod_comp"), rs.getString("name_comp"));
	}


	private BankAgence getBankAgence(ResultSet rs) throws SQLException {
		BankAgence ag = new BankAgence();
		ag.setAgence(rs.getString("agence"));
		ag.setDv(rs.getString("dv"));
		ag.setId(rs.getInt("cod_agence"));
		ag.setBank(getBank(rs));
		return ag;
	}


	private Bank getBank(ResultSet rs) throws SQLException {
		return new Bank(rs.getInt("cod_bank"), rs.getString("code_bank"), rs.getString("name_bank"));
	}	

}
