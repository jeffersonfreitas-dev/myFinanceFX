package model.dao;

import database.Database;
import model.dao.impl.DAOBankAgenceImpl;
import model.dao.impl.DAOBankImpl;

public class DAOFactory {
	
	public static DAOBank createBankDAO() {
		return new DAOBankImpl(Database.getConnection());
	}
	
	public static DAOBankAgence createBankAgenceDAO() {
		return new DAOBankAgenceImpl(Database.getConnection());
	}


}
