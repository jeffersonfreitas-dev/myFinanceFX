package model.service;


import java.util.List;

import model.dao.DAODashboard;
import model.dao.DAOFactory;
import model.dto.dashboard.ChartBillpayStatus;

public class DashboardService {
	
	private DAODashboard dao = DAOFactory.createDashboardDAO();
	
	
	public List<ChartBillpayStatus> grafStatusContaPagar(){
		List<ChartBillpayStatus> result = dao.billpayStatusTotal();
		return result;
	}

	public List<ChartBillpayStatus> grafStatusContaReceber(){
		List<ChartBillpayStatus> result = dao.receivableStatusTotal();
		return result;
	}
	
	
}
