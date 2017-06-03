package fr.ensimag.loupsgarous.dao;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 * Classe abstraite permettant de factoriser du code pour les DAO
 * basées sur JDBC
 */
public abstract class AbstractDataBaseDAO {

    protected final DataSource dataSource;
    
    protected AbstractDataBaseDAO(DataSource ds) {
        this.dataSource = ds;
    }

    protected Connection getConn() throws SQLException {
        return dataSource.getConnection();
    }
}
