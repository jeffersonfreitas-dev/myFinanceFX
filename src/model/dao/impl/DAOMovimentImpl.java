package model.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.Database;
import database.exceptions.DatabaseException;
import model.dao.DAOMoviment;
import model.entities.Moviment;

public class DAOMovimentImpl implements DAOMoviment{
	
	private Connection conn;
	
	public DAOMovimentImpl(Connection conn) {
		this.conn = conn;
	}

	
	@Override
	public void insert(Moviment entity) {
		PreparedStatement stmt = null;
		String sql = "INSERT INTO moviment (date_beginner, name, date_finish, value_beginner, balance_moviment, value_finish, closed) VALUES (?, upper(?), ?, ?, ?, ?, ?)";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setDate(1, new java.sql.Date(entity.getDateBeginner().getTime()));
			stmt.setString(2, entity.getName());
			stmt.setDate(3, new java.sql.Date(entity.getDateFinish().getTime()));
			stmt.setDouble(4, entity.getValueBeginner());
			stmt.setDouble(5, entity.getBalanceMoviment());
			stmt.setDouble(6, entity.getValueFinish());
			stmt.setBoolean(7, entity.isClosed());
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
	public void update(Moviment entity) {
		PreparedStatement stmt = null;
		String sql = "UPDATE moviment SET date_beginner = ?, name = upper(?), date_finish = ?, value_beginner = ?, balance_moviment = ?, value_finish = ?, closed = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setDate(1, new java.sql.Date(entity.getDateBeginner().getTime()));
			stmt.setString(2, entity.getName());
			stmt.setDate(3, new java.sql.Date(entity.getDateFinish().getTime()));
			stmt.setDouble(4, entity.getValueBeginner());
			stmt.setDouble(5, entity.getBalanceMoviment());
			stmt.setDouble(6, entity.getValueFinish());
			stmt.setBoolean(7, entity.isClosed());
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
		String sql = "DELETE FROM moviment WHERE id = ?";
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
	public Moviment findById(Integer id) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM moviment WHERE id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				Moviment Moviment = instantiateMoviment(rs); 
				return Moviment;
			}
			return null;

		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Erro ao executar: findById do registro -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}

	
	@Override
	public List<Moviment> findAllOrderByDateBeginner() {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM moviment";
		try {
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			
			List<Moviment> list = new ArrayList<>();
			
			while(rs.next()) {
				Moviment moviment = instantiateMoviment(rs); 
				list.add(moviment);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Erro ao executar comando: findAllOrderByNane do registro -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}


	private Moviment instantiateMoviment(ResultSet rs) throws SQLException{
		Moviment moviment = new Moviment();
		moviment.setBalanceMoviment(rs.getDouble("balance_moviment"));
		moviment.setClosed(rs.getBoolean("closed"));
		moviment.setDateBeginner(new Date(rs.getDate("date_beginner").getTime()));
		moviment.setDateFinish(new Date(rs.getDate("date_finish").getTime()));
		moviment.setId(rs.getInt("id"));
		moviment.setName(rs.getString("name"));
		moviment.setValueBeginner(rs.getDouble("value_beginner"));
		moviment.setValueFinish(rs.getDouble("value_finish"));
		return moviment;
	}

}
