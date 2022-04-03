package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.Database;
import database.exceptions.DatabaseException;
import model.dao.DAOCompany;
import model.entities.Company;

public class DAOCompanyImpl implements DAOCompany{
	
	private Connection conn;
	
	public DAOCompanyImpl(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Company entity) {
		PreparedStatement stmt = null;
		String sql = "INSERT INTO company (name) VALUES (upper(?))";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, entity.getName());
			int result = stmt.executeUpdate();
			
			if(result < 1) {
				throw new DatabaseException("Falha ao salvar o registro. Nenhuma linha afetada");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao salvar o registro");
		}finally {
			Database.closeStatement(stmt);
		}
	}

	
	@Override
	public void update(Company entity) {
		PreparedStatement stmt = null;
		String sql = "UPDATE company SET name = upper(?) WHERE id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, entity.getName());
			stmt.setInt(2, entity.getId());
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
		String sql = "DELETE FROM company WHERE id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao deletar o registro");
		}finally {
			Database.closeStatement(stmt);
		}
		
	}

	
	@Override
	public Company findById(Integer id) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM company WHERE id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				Company company = new Company(rs.getInt("id"), rs.getString("name"));
				return company;
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
	public List<Company> findAllOrderByName() {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM company order by name";
		List<Company> list = new ArrayList<Company>();
		try {
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			while (rs.next()) {
				Company company = new Company(rs.getInt("id"), rs.getString("name"));
				list.add(company);
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

	
	@Override
	public Company findByName(String name) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM company WHERE name = upper(?)";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, name);
			rs = stmt.executeQuery();
			if(rs.next()) {
				Company company = new Company(rs.getInt("id"), rs.getString("name"));
				return company;
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao procurar o registro no banco de dados com o nome " + name);
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}

}
