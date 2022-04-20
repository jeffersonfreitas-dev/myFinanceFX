package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.Database;
import database.exceptions.DatabaseException;
import model.dao.DAOBillpay;
import model.entities.AccountPlan;
import model.entities.Billpay;
import model.entities.Clifor;
import model.entities.Company;

public class DAOBillpayImpl implements DAOBillpay{
	
	private Connection conn;
	public DAOBillpayImpl(Connection conn) {
		this.conn = conn;
	}

	
	@Override
	public void insert(Billpay entity) {
		PreparedStatement stmt = null;
		String sql = "INSERT INTO billpay (invoice, historic, date, due_date, value, status, id_clifor, id_company, id_account_plan) VALUES "
				+ "(upper(?), upper(?), ?, ?, ?, ?, ?, ?, ?)";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, entity.getInvoice());
			stmt.setString(2, entity.getHistoric());
			stmt.setDate(3, new java.sql.Date(entity.getDate().getTime()));
			stmt.setDate(4, new java.sql.Date(entity.getDueDate().getTime()));
			stmt.setDouble(5, entity.getValue());
			stmt.setString(6, entity.getStatus());
			stmt.setInt(7, entity.getClifor().getId());
			stmt.setInt(8, entity.getCompany().getId());
			stmt.setInt(9, entity.getAccountPlan().getId());
			int result = stmt.executeUpdate();
			if(result < 1) {
				throw new DatabaseException("Falha ao salvar o registro");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando insert conta a pagar -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
		}
	}

	
	@Override
	public void update(Billpay entity) {
		PreparedStatement stmt = null;
		String sql = "UPDATE billpay SET invoice = upper(?), historic = upper(?), date = ?, due_date = ?, value = ?,"
				+ " status = ?, id_clifor = ?, id_company = ?, id_account_plan = ? WHERE id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, entity.getInvoice());
			stmt.setString(2, entity.getHistoric());
			stmt.setDate(3, new java.sql.Date(entity.getDate().getTime()));
			stmt.setDate(4, new java.sql.Date(entity.getDueDate().getTime()));
			stmt.setDouble(5, entity.getValue());
			stmt.setString(6, entity.getStatus());
			stmt.setInt(7, entity.getClifor().getId());
			stmt.setInt(8, entity.getCompany().getId());
			stmt.setInt(9, entity.getAccountPlan().getId());
			stmt.setInt(10, entity.getId());
			int result = stmt.executeUpdate();
			if(result < 1) {
				throw new DatabaseException("Falha ao atualizar o registro");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando update conta a pagar -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
		}
	}

	
	@Override
	public void deleteById(Integer id) {
		PreparedStatement stmt = null;
		String sql = "DELETE FROM billpay WHERE id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			int result = stmt.executeUpdate();
			if(result < 1) {
				throw new DatabaseException("Falha ao deletar o registro");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando delete conta a pagar -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
		}
	}

	
	@Override
	public Billpay findById(Integer id) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT b.*, c.id as cod_clifor, c.name as name_clifor, e.id as cod_company, e.name as name_company, p.id as cod_account, "
				+ "p.credit, p.name FROM billpay b INNER JOIN clifor c ON b.id_clifor = c.id "
				+ "INNER JOIN company e ON b.id_company = e.id INNER JOIN account_plan p ON b.id_account_plan = p.id WHERE b.id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				Billpay bill = getBillpay(rs);
				return bill;
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando findById conta a pagar -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}


	@Override
	public List<Billpay> findAllOrderByDueDate() {
		List<Billpay> list = new ArrayList<Billpay>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT b.*, c.id as cod_clifor, c.name as name_clifor, e.id as cod_company, e.name as name_company, p.id as cod_account, "
				+ "p.credit, p.name FROM billpay b INNER JOIN clifor c ON b.id_clifor = c.id "
				+ "INNER JOIN company e ON b.id_company = e.id INNER JOIN account_plan p ON b.id_account_plan = p.id ORDER BY due_date";
		try {
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				Billpay bill = getBillpay(rs);
				list.add(bill);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando findAllOrderByDueDate conta a pagar -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}

	@Override
	public Billpay findByInvoiceAndCompanyId(String invoice, Integer id_company) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT b.*, c.id as cod_clifor, c.name as name_clifor, e.id as cod_company, e.name as name_company, p.id as cod_account, "
				+ "p.credit, p.name FROM billpay b INNER JOIN clifor c ON b.id_clifor = c.id INNER JOIN company e ON b.id_company = e.id "
				+ "INNER JOIN account_plan p ON b.id_account_plan = p.id WHERE upper(invoice) = upper(?) AND id_company = ? ";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, invoice);
			stmt.setInt(2, id_company);
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				Billpay bill = getBillpay(rs);
				return bill;
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando findByInvoiceAndCompanyId conta a pagar -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}

	
	private Billpay getBillpay(ResultSet rs) throws SQLException {
		Billpay bill = new Billpay();
		bill.setId(rs.getInt("id"));
		bill.setAccountPlan(getAccountPlan(rs));
		bill.setClifor(getClifor(rs));
		bill.setCompany(getCompany(rs));
		bill.setDate(new java.util.Date(rs.getDate("date").getTime()));
		bill.setDueDate(new java.util.Date(rs.getDate("due_date").getTime()));
		bill.setHistoric(rs.getString("historic"));
		bill.setInvoice(rs.getString("invoice"));
		bill.setStatus(rs.getString("status"));
		bill.setValue(rs.getDouble("value"));
		return bill;
	}


	private AccountPlan getAccountPlan(ResultSet rs) throws SQLException {
		AccountPlan account = new AccountPlan();
		account.setId(rs.getInt("cod_account"));
		account.setCredit(rs.getBoolean("credit"));
		account.setName(rs.getString("name"));
		return account;
	}


	private Clifor getClifor(ResultSet rs) throws SQLException {
		Clifor clifor = new Clifor();
		clifor.setId(rs.getInt("cod_clifor"));
		clifor.setName(rs.getString("name_clifor"));
		return clifor;
	}


	private Company getCompany(ResultSet rs) throws SQLException {
		Company company = new Company();
		company.setId(rs.getInt("cod_company"));
		company.setName(rs.getString("name_company"));
		return company;
	}


	@Override
	public List<Billpay> filtro(String status, String nome, String combobox) {
		List<Billpay> list = new ArrayList<Billpay>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder("SELECT b.*, c.id as cod_clifor, c.name as name_clifor, e.id as cod_company, e.name as name_company, p.id as cod_account,p.credit, p.name FROM billpay b ");
		sb.append("INNER JOIN clifor c ON b.id_clifor = c.id INNER JOIN company e ON b.id_company = e.id INNER JOIN account_plan p ON b.id_account_plan = p.id where b.status = ? ");
		if(nome != "") {
			if(combobox.equals("Histórico")) {
				sb.append("and upper(b.historic) like upper(?) ");
			}
		}
		sb.append("ORDER BY due_date");
		
		try {
			stmt = conn.prepareStatement(sb.toString());
			stmt.setString(1, status);
			
			if(nome != "") {
				stmt.setString(2, "%"+nome+"%");
			}
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				Billpay bill = getBillpay(rs);
				list.add(bill);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao executar o comando findAllOrderByDueDate conta a pagar -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}
}
