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
import utils.DefaultMessages;

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
		String sql = "DELETE FROM account_plan WHERE id = ?";
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
	public AccountPlan findById(Integer id) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM account_plan WHERE id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				AccountPlan ap = setAccount(rs);
				return ap;
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(DefaultMessages.getMsgErroFindby() +  ". Código nº " + id);
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
				AccountPlan ap = setAccount(rs);
				list.add(ap);
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
				AccountPlan ap = setAccount(rs);
				return ap;
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			String tipo = credit ? "crédito" : "débito";
			throw new DatabaseException(DefaultMessages.getMsgErroFindby() + ". Nome " + name + " do tipo " + tipo);
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}
	
	
	@Override
	public List<AccountPlan> findAllPagination(Integer limit) {
		List<AccountPlan> list = new ArrayList<AccountPlan>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM account_plan ORDER BY name limit ?";	
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, limit);
			rs = stmt.executeQuery();
			while(rs.next()) {
				AccountPlan ap = setAccount(rs);
				list.add(ap);
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
	public List<AccountPlan> findAllByType(Boolean credit) {
		List<AccountPlan> list = new ArrayList<AccountPlan>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM account_plan WHERE credit = ? order by name";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setBoolean(1, credit);
			rs = stmt.executeQuery();
			while (rs.next()) {
				AccountPlan ap = setAccount(rs);
				list.add(ap);
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
	
	
	
	private AccountPlan setAccount(ResultSet rs) throws SQLException {
		return new AccountPlan(rs.getInt("id"), rs.getString("name"), rs.getBoolean("credit"));
	}


}
