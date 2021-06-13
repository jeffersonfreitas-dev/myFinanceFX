package model.dao;

import database.Database;
import model.dao.impl.DAOBankImpl;

public class DAOFactory {
	
	public static DAOBank createBankDAO() {
		return new DAOBankImpl(Database.getConnection());
	}

}
