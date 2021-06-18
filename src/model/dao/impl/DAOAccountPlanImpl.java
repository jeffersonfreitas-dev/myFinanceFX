package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.Database;
import database.exceptions.DatabaseException;
import model.dao.DAOAccountPlan;
import model.entities.AccountPlan;

public class DAOAccountPlanImpl implements DAOAccountPlan{
	
	private Connection conn;
	
	public DAOAccountPlanImpl(Connection conn) {
		this.conn = conn;
	}

	
	@Override
	public void insert(AccountPlan entity) {
		PreparedStatement stmt = null;
		String sql = "INSERT INTO account_plan (name, credit) VALUES (upper(?), ?)";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, entity.getName());
			stmt.setBoolean(2, entity.isCredit());
			int result = stmt.executeUpdate();
			
			if(result  < 1) {
				throw new DatabaseException("Falha ao salvar o registro");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando insert plano de conta -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
		}
		
	}

	
	@Override
	public void update(AccountPlan entity) {
		PreparedStatement stmt = null;
		String sql = "UPDATE account_plan SET name = upper(?), credit = ? WHERE id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, entity.getName());
			stmt.setBoolean(2, entity.isCredit());
			stmt.setInt(3, entity.getId());
			int result = stmt.executeUpdate();
			
			if(result < 1) {
				throw new DatabaseException("Falha ao atualizar o registro");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando update plano de conta -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
		}
	}

	
	@Override
	public void deleteById(Integer id) {
		PreparedStatement stmt = null;
		String sql = "DELETE FROM account_plan WHERE id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			int result = stmt.executeUpdate();
			
			if(result < 1) {
				throw new DatabaseException("Falha ao excluir o registro");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando delete plano de conta -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
		}
		
	}

	
	@Override
	public AccountPlan findById(Integer id) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM account_plan WHERE id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				AccountPlan ap = new AccountPlan(rs.getInt("id"), rs.getString("name"), rs.getBoolean("credit"));
				return ap;
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando findId plano de conta -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}

	
	@Override
	public List<AccountPlan> findAllOrderByName() {
		List<AccountPlan> list = new ArrayList<AccountPlan>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM account_plan ORDER BY name";	
		try {
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			while(rs.next()) {
				AccountPlan ap = new AccountPlan(rs.getInt("id"), rs.getString("name"), rs.getBoolean("credit"));
				list.add(ap);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando findId plano de conta -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}

	
	@Override
	public AccountPlan findByNameAndCredit(String name, boolean credit) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM account_plan WHERE upper(name) = upper(?) AND credit = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, name);
			stmt.setBoolean(2, credit);
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				AccountPlan ap = new AccountPlan(rs.getInt("id"), rs.getString("name"), rs.getBoolean("credit"));
				return ap;
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando findname plano de conta -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}

}
