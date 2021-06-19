package model.dao;

import database.Database;
import model.dao.impl.DAOAccountPlanImpl;
import model.dao.impl.DAOBankAccountImpl;
import model.dao.impl.DAOBankAgenceImpl;
import model.dao.impl.DAOBankImpl;
import model.dao.impl.DAOCliforImpl;
import model.dao.impl.DAOCompanyImpl;

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

}
