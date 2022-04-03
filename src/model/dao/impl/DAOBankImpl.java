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
		String sql = "INSERT INTO bank (code, name) VALUES (upper(?), upper(?))";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, entity.getCode());
			stmt.setString(2, entity.getName());
			int result = stmt.executeUpdate();
			
			if(result < 1) {
				throw new DatabaseException("Falha ao salvar o registro. Nenhuma linha afetada");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao inserir o registro no banco de dados");
		}finally {
			Database.closeStatement(stmt);
		}
	}

	
	@Override
	public void update(Bank entity) {
		PreparedStatement stmt = null;
		String sql = "UPDATE bank SET code = upper(?), name = upper(?) WHERE id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, entity.getCode());
			stmt.setString(2, entity.getName());
			stmt.setInt(3, entity.getId());
			int result = stmt.executeUpdate();
			
			if(result < 1) {
				throw new DatabaseException("Falha ao atualizar o registro. Nenhuma linha afetada");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao atualizar o registro");
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
				throw new DatabaseException("Falha ao deletar o registro. Nenhuma linha afetada");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao deletar o registro");
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
			throw new DatabaseException("Ocorreu um erro ao procurar o registro no banco de dados com o código nº " + id);
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
			throw new DatabaseException("Ocorreu um erro ao listar os registros cadastrados no banco de dados");
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
		String sql = "SELECT * FROM bank WHERE upper(code) = upper(?) OR upper(name) = upper(?)";
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
			throw new DatabaseException("Ocorreu um erro ao procurar o registro cadastrado no banco de dados com o código nº " + code + " e nome " + name);
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}

}
