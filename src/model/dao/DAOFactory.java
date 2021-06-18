package model.dao;

import database.Database;
import model.dao.impl.DAOBankAgenceImpl;
import model.dao.impl.DAOBankImpl;
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

}
