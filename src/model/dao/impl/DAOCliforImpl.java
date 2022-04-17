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
import utils.DefaultMessages;

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
		String sql = "DELETE FROM clifor WHERE id = ?";
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
			throw new DatabaseException(DefaultMessages.getMsgErroFindby());
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
			throw new DatabaseException(DefaultMessages.getMsgErroFindall());
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
			String tipo = provider ? "fornecedor" : "cliente";
			throw new DatabaseException(DefaultMessages.getMsgErroFindby() + ". Nome " + name + " e tipo " + tipo);
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}


	@Override
	public List<Clifor> findAllByTipo(Boolean fornecedor) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM clifor WHERE provider = ? order by name";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setBoolean(1, fornecedor);
			rs = stmt.executeQuery();
			
			List<Clifor> result = new ArrayList<>();
			while(rs.next()) {
				Clifor clifor = new Clifor(rs.getInt("id"), rs.getString("name"), rs.getBoolean("provider"));
				result.add(clifor);
			}
			return result;			
		} catch (Exception e) {
			e.printStackTrace();
			String tipo = fornecedor ? "fornecedor" : "cliente";
			throw new DatabaseException(DefaultMessages.getMsgErroFindby() + ". Tipo " + tipo);
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}


	@Override
	public List<Clifor> filtro(Boolean fornecedor, String nome) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		StringBuilder sb = new StringBuilder("SELECT * FROM clifor WHERE provider = ?");
		if(nome != "") {
			sb.append(" and upper(name) like upper(?)");
		}
		sb.append(" order by name");
		
		String sql = sb.toString();
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setBoolean(1, fornecedor);
			
			if(nome != "") {
				stmt.setString(2, "%"+nome+"%");
			}
			
			rs = stmt.executeQuery();
			
			List<Clifor> result = new ArrayList<>();
			while(rs.next()) {
				Clifor clifor = new Clifor(rs.getInt("id"), rs.getString("name"), rs.getBoolean("provider"));
				result.add(clifor);
			}
			return result;			
		} catch (Exception e) {
			e.printStackTrace();
			String tipo = fornecedor ? "fornecedor" : "cliente";
			throw new DatabaseException(DefaultMessages.getMsgErroFindby() + ". Tipo " + tipo);
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}

}
