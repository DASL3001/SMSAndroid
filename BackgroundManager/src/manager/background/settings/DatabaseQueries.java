package manager.background.settings;

import java.sql.Connection;
import java.util.logging.Logger;


public class DatabaseQueries extends CommonConstants {

    private Connection con;
    private Logger log;

    // Basic Constructor
    public DatabaseQueries() {
    }

    public DatabaseQueries(Connection con, Logger appsLog) {
        this.con = con;
        this.log = appsLog;
    }

    public void load(Logger appsLog) {
        log = appsLog;
    }

    public Connection getCon() {
        return con;
    }

    public void setCon(Connection con) {
        this.con = con;
    }
    // Selects:
    public String prepVdsCustomerList() {
        // TODO : write content
        return "";
       
    }
}
