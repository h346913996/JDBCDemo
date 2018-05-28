package service;

import dao.DataAccessObjects;
import dao.impl.OracleDAO;

public class DaoWebService {
	public static DataAccessObjects getDao(){
		return new OracleDAO();
	}
}
