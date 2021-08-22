package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.Database;
import database.exceptions.DatabaseException;
import model.dao.DAODashboard;
import model.dto.dashboard.ChartBillpayStatus;

public class DAODashboardImpl implements DAODashboard {
	
	private Connection conn;

	public DAODashboardImpl(Connection connection) {
		this.conn = connection;
	}

	@Override
	public List<ChartBillpayStatus> billpayStatusTotal() {
		List<ChartBillpayStatus> list = new ArrayList<ChartBillpayStatus>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select sum(value) as valor, status from billpay group by status order by status";
		try {
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			while(rs.next()) {
				ChartBillpayStatus c = new ChartBillpayStatus();
				c.setStatus(rs.getString("status"));
				c.setValor(rs.getDouble("valor"));
				list.add(c);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao gerar a consulta -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}

	@Override
	public List<ChartBillpayStatus> receivableStatusTotal() {
		List<ChartBillpayStatus> list = new ArrayList<ChartBillpayStatus>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select sum(value) as valor, status from receivable group by status order by status";
		try {
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			while(rs.next()) {
				ChartBillpayStatus c = new ChartBillpayStatus();
				c.setStatus(rs.getString("status"));
				c.setValor(rs.getDouble("valor"));
				list.add(c);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao gerar a consulta -> " + e.getMessage());
		}finally {
			Database.closeStatement(stmt);
			Database.closeResultSet(rs);
		}
	}

}
