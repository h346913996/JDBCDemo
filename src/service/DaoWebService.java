package service;

import dao.DataAccessObjects;
import dao.impl.PostgreSqlDAO;

public class DaoWebService {
	public static DataAccessObjects getDao(){
		return new PostgreSqlDAO();
	}
}
