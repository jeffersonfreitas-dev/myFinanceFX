package model.dao;

import database.Database;
import model.dao.impl.DAOAccountPlanImpl;
import model.dao.impl.DAOBankAccountImpl;
import model.dao.impl.DAOBankAgenceImpl;
import model.dao.impl.DAOBankImpl;
import model.dao.impl.DAOBankStatementImpl;
import model.dao.impl.DAOBillpayImpl;
import model.dao.impl.DAOCliforImpl;
import model.dao.impl.DAOCompanyImpl;
import model.dao.impl.DAODashboardImpl;
import model.dao.impl.DAOMovimentImpl;
import model.dao.impl.DAOPaymentImpl;
import model.dao.impl.DAOReceivableImpl;
import model.dao.impl.DAOReceivementImpl;
import model.dao.impl.DAOTransferenciaImpl;

public class DAOFactory {
	
	public static DAOBank createBankDAO() {
		return new DAOBankImpl(Database.getConnection());
	}
	
	public static DAOBankAgence createBankAgenceDAO() {
		return new DAOBankAgenceImpl(Database.getConnection());
	}

	public static DAOCompany createCompanyDAO() {
		return new DAOCompanyImpl(Database.getConnection());
	}

	public static DAOAccountPlan createAccountPlanDAO() {
		return new DAOAccountPlanImpl(Database.getConnection());
	}

	public static DAOClifor createCliforDAO() {
		return new DAOCliforImpl(Database.getConnection());
	}

	public static DAOBankAccount createBankAccountDAO() {
		return new DAOBankAccountImpl(Database.getConnection());
	}

	public static DAOBillpay createBillpayDAO() {
		return new DAOBillpayImpl(Database.getConnection());
	}

	public static DAOPayment createPaymentDAO() {
		return new DAOPaymentImpl(Database.getConnection());
	}

	public static DAOReceivable createReceivableDAO() {
		return new DAOReceivableImpl(Database.getConnection());
	}

	public static DAOReceivement createReceivementDAO() {
		return new DAOReceivementImpl(Database.getConnection());
	}

	public static DAOBankStatement createBankStatementDAO() {
		return new DAOBankStatementImpl(Database.getConnection());
	}
	
	public static DAOMoviment createMovimentDAO() {
		return new DAOMovimentImpl(Database.getConnection());
	}

	public static DAODashboard createDashboardDAO() {
		return new DAODashboardImpl(Database.getConnection());
	}

	public static DAOTransferencia createTransferenciaDAO() {
		return new DAOTransferenciaImpl(Database.getConnection());
	}

}
