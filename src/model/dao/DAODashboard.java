package model.dao;

import java.util.List;

import model.dto.dashboard.ChartBillpayStatus;

public interface DAODashboard {

	List<ChartBillpayStatus> billpayStatusTotal();

	List<ChartBillpayStatus> receivableStatusTotal();

}
