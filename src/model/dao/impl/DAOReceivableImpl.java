package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.Database;
import database.exceptions.DatabaseException;
import model.dao.DAOReceivable;
import model.entities.AccountPlan;
import model.entities.Clifor;
import model.entities.Company;
import model.entities.Receivable;
import utils.DefaultMessages;

public class DAOReceivableImpl implements DAOReceivable{
	
	private Connection conn;
	public DAOReceivableImpl(Connection conn) {
		this.conn = conn;
	}

	
	@Override
	public void insert(Receivable entity) {
		PreparedStatement stmt = null;
		String sql = "INSERT INTO receivable (invoice, historic, date, due_date, value, portion, fulfillment, status, id_clifor, id_company, id_account_plan) VALUES "
				+ "(upper(?), upper(?), ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, entity.getInvoice());
			stmt.setString(2, entity.getHistoric());
			stmt.setDate(3, new java.sql.Date(entity.getDate().getTime()));
			stmt.setDate(4, new java.sql.Date(entity.getDueDate().getTime()));
			stmt.setDouble(5, entity.getValue());
			stmt.setInt(6, entity.getPortion());
			stmt.setInt(7, entity.getFulfillment());
			stmt.setString(8, entity.getStatus());
			stmt.setInt(9, entity.getClifor().getId());
			stmt.setInt(10, entity.getCompany().getId());
			stmt.setInt(11, entity.getAccountPlan().getId());
			int result = stmt.executeUpdate();
			if(result < 1) {
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
	public void update(Receivable entity) {
		PreparedStatement stmt = null;
		String sql = "UPDATE receivable SET invoice = upper(?), historic = upper(?), date = ?, due_date = ?, value = ?, portion =?, fulfillment = ?,"
				+ " status = ?, id_clifor = ?, id_company = ?, id_account_plan = ? WHERE id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, entity.getInvoice());
			stmt.setString(2, entity.getHistoric());
			stmt.setDate(3, new java.sql.Date(entity.getDate().getTime()));
			stmt.setDate(4, new java.sql.Date(entity.getDueDate().getTime()));
			stmt.setDouble(5, entity.getValue());
			stmt.setInt(6, entity.getPortion());
			stmt.setInt(7, entity.getFulfillment());
			stmt.setString(8, entity.getStatus());
			stmt.setInt(9, entity.getClifor().getId());
			stmt.setInt(10, entity.getCompany().getId());
			stmt.setInt(11, entity.getAccountPlan().getId());
			stmt.setInt(12, entity.getId());
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
		String sql = "DELETE FROM receivable WHERE id = ?";
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
	public Receivable findById(Integer id) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT b.*, c.id as cod_clifor, c.name as name_clifor, e.id as cod_company, e.name as name_company, p.id as cod_account, "
				+ "p.credit, p.name FROM receivable b INNER JOIN clifor c ON b.id_clifor = c.id "
				+ "INNER JOIN company e ON b.id_company = e.id INNER JOIN account_plan p ON b.id_account_plan = p.id WHERE b.id = ?";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				Receivable bill = getReceivable(rs);
				return bill;
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(DefaultMessages.getMsgErroFindby() + ". Código nº " + id);
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}


	@Override
	public List<Receivable> findAllOrderByDueDate() {
		List<Receivable> list = new ArrayList<Receivable>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT b.*, c.id as cod_clifor, c.name as name_clifor, e.id as cod_company, e.name as name_company, p.id as cod_account, "
				+ "p.credit, p.name FROM receivable b INNER JOIN clifor c ON b.id_clifor = c.id "
				+ "INNER JOIN company e ON b.id_company = e.id INNER JOIN account_plan p ON b.id_account_plan = p.id ORDER BY due_date";
		try {
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				Receivable bill = getReceivable(rs);
				list.add(bill);
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
	public List<Receivable> findAllByAccountPlanId(Integer idAccountPlan) {
		List<Receivable> list = new ArrayList<Receivable>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "		select\r\n"
				+ "				a.name,\r\n"
				+ "				a.id as cod_account,\r\n"
				+ "				a.credit,\r\n"
				+ "				b.id,\r\n"
				+ "				b.historic,\r\n"
				+ "				b.date,\r\n"
				+ "				b.due_date,\r\n"
				+ "				b.value,\r\n"
				+ "				b.portion,\r\n"
				+ "				b.invoice,\r\n"
				+ "				b.fulfillment,\r\n"
				+ "				b.status,\r\n"
				+ "				c.id as cod_clifor,\r\n"
				+ "				c.name as name_clifor,\r\n"
				+ "				cc.id as cod_company,\r\n"
				+ "				cc.name as name_company\r\n"
				+ "				from account_plan a\r\n"
				+ "				inner join receivable b on b.id_account_plan = a.id\r\n"
				+ "				inner join company cc on b.id_company = cc.id\r\n"
				+ "				inner join clifor c on b.id_clifor = c.id\r\n"
				+ "				where a.id = ?\r\n"
				+ "				order by b.due_date";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, idAccountPlan);
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				Receivable bill = getReceivable(rs);
				list.add(bill);
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
	public List<Receivable> findAllByCliforId(Integer idClifor) {
		List<Receivable> list = new ArrayList<Receivable>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "		select\r\n"
				+ "				a.name,\r\n"
				+ "				a.id as cod_account,\r\n"
				+ "				a.credit,\r\n"
				+ "				b.id,\r\n"
				+ "				b.historic,\r\n"
				+ "				b.date,\r\n"
				+ "				b.due_date,\r\n"
				+ "				b.value,\r\n"
				+ "				b.portion,\r\n"
				+ "				b.invoice,\r\n"
				+ "				b.fulfillment,\r\n"
				+ "				b.status,\r\n"
				+ "				c.id as cod_clifor,\r\n"
				+ "				c.name as name_clifor,\r\n"
				+ "				cc.id as cod_company,\r\n"
				+ "				cc.name as name_company\r\n"
				+ "				from account_plan a\r\n"
				+ "				inner join receivable b on b.id_account_plan = a.id\r\n"
				+ "				inner join company cc on b.id_company = cc.id\r\n"
				+ "				inner join clifor c on b.id_clifor = c.id\r\n"
				+ "				where c.id = ?\r\n"
				+ "				order by b.due_date";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, idClifor);
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				Receivable bill = getReceivable(rs);
				list.add(bill);
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
	public Receivable findByInvoiceAndCompanyId(String invoice, Integer id_company) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT b.*, c.id as cod_clifor, c.name as name_clifor, e.id as cod_company, e.name as name_company, p.id as cod_account, "
				+ "p.credit, p.name FROM receivable b INNER JOIN clifor c ON b.id_clifor = c.id INNER JOIN company e ON b.id_company = e.id "
				+ "INNER JOIN account_plan p ON b.id_account_plan = p.id WHERE upper(invoice) = upper(?) AND id_company = ? ";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, invoice);
			stmt.setInt(2, id_company);
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				Receivable bill = getReceivable(rs);
				return bill;
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(DefaultMessages.getMsgErroFindby() + ". Nota nº " + invoice + " e empresa " + id_company);
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}
	
	
	@Override
	public List<Receivable> filtro(String status) {
		List<Receivable> list = new ArrayList<Receivable>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT b.*, c.id as cod_clifor, c.name as name_clifor, e.id as cod_company, e.name as name_company, p.id as cod_account, "
				+ "p.credit, p.name FROM receivable b INNER JOIN clifor c ON b.id_clifor = c.id INNER JOIN company e ON b.id_company = e.id "
				+ "INNER JOIN account_plan p ON b.id_account_plan = p.id WHERE b.status = ? ";
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, status);
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				Receivable receb = getReceivable(rs);
				list.add(receb);
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

	
	private Receivable getReceivable(ResultSet rs) throws SQLException {
		Receivable bill = new Receivable();
		bill.setId(rs.getInt("id"));
		bill.setAccountPlan(getAccountPlan(rs));
		bill.setClifor(getClifor(rs));
		bill.setCompany(getCompany(rs));
		bill.setDate(new java.util.Date(rs.getDate("date").getTime()));
		bill.setDueDate(new java.util.Date(rs.getDate("due_date").getTime()));
		bill.setFulfillment(rs.getInt("fulfillment"));
		bill.setHistoric(rs.getString("historic"));
		bill.setInvoice(rs.getString("invoice"));
		bill.setPortion(rs.getInt("portion"));
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

}
