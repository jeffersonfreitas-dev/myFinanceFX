package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.Database;
import database.exceptions.DatabaseException;
import model.dao.DAOClifor;
import model.entities.Clifor;

public class DAOCliforImpl implements DAOClifor{
	
	private Connection conn;
	
	public DAOCliforImpl(Connection conn) {
		this.conn = conn;
	}

	
	@Override
	public void insert(Clifor entity) {
		PreparedStatement stmt = null;
		String sql = "INSERT INTO clifor (name, provider) VALUES (upper(?), ?)";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, entity.getName());
			stmt.setBoolean(2, entity.isProvider());
			int result = stmt.executeUpdate();
			
			if(result  < 1) {
				throw new DatabaseException("Falha ao salvar o registro");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando insert clifor -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
		}
		
	}

	
	@Override
	public void update(Clifor entity) {
		PreparedStatement stmt = null;
		String sql = "UPDATE clifor SET name = upper(?), provider = ? WHERE id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, entity.getName());
			stmt.setBoolean(2, entity.isProvider());
			stmt.setInt(3, entity.getId());
			int result = stmt.executeUpdate();
			
			if(result < 1) {
				throw new DatabaseException("Falha ao atualizar o registro");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando update clifor -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
		}
	}

	
	@Override
	public void deleteById(Integer id) {
		PreparedStatement stmt = null;
		String sql = "DELETE FROM clifor WHERE id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			int result = stmt.executeUpdate();
			
			if(result < 1) {
				throw new DatabaseException("Falha ao excluir o registro");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando delete clifor -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
		}
		
	}

	
	@Override
	public Clifor findById(Integer id) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM clifor WHERE id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				Clifor clifor = new Clifor(rs.getInt("id"), rs.getString("name"), rs.getBoolean("provider"));
				return clifor;
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando findId clifor -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}

	
	@Override
	public List<Clifor> findAllOrderByName() {
		List<Clifor> list = new ArrayList<Clifor>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM clifor ORDER BY name";	
		try {
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			while(rs.next()) {
				Clifor clifor = new Clifor(rs.getInt("id"), rs.getString("name"), rs.getBoolean("provider"));
				list.add(clifor);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando findId clifor -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}

	
	@Override
	public Clifor findByNameAndProvider(String name, boolean provider) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM clifor WHERE upper(name) = upper(?) AND provider = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, name);
			stmt.setBoolean(2, provider);
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				Clifor clifor = new Clifor(rs.getInt("id"), rs.getString("name"), rs.getBoolean("provider"));
				return clifor;
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando findname clifor -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}

}
