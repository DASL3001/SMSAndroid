package manager.background.settings;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {

    private static DBConnection dbc;
    
    private String jdbcUrl;
    private String jdbcId;
    private String jdbcPwd;
    private Logger log;

    public DBConnection() {
    }

    public DBConnection(String jdbcUrl, String jdbcId, String jdbcPwd, Logger log) {
        this.jdbcUrl = jdbcUrl;
        this.jdbcId = jdbcId;
        this.jdbcPwd = jdbcPwd;
        this.log = log;
    }
    
    public synchronized static DBConnection InstanceOf(){
        if(dbc == null){
            dbc = new DBConnection();
        }
        return dbc;
    }

    public String getJdbcId() {
        return jdbcId;
    }

    public void setJdbcId(String jdbcId) {
        this.jdbcId = jdbcId;
    }

    public String getJdbcPwd() {
        return jdbcPwd;
    }

    public void setJdbcPwd(String jdbcPwd) {
        this.jdbcPwd = jdbcPwd;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public synchronized Connection openConnection() {
        try {
            // Register the jdbc driver we wish to use.
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(jdbcUrl + "?user=" + jdbcId + "&password=" + jdbcPwd + "&noAccessToProcedureBodies=true");

        } catch (Exception e) {
            log.log(Level.WARNING, "{0} openConnection(): Fatal Error, Unable to open remote connection!", e.getMessage());
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            log.warning(sw.toString());
            return null;
        }
    }

    public synchronized boolean closeConnection(Connection con) {
        try {
            // Register the jdbc driver we wish to use.
            Class.forName("com.mysql.jdbc.Driver");
            con.close();

            return true;
        } catch (Exception e) {
            log.log(Level.WARNING, "{0} closeConnection(): Error, Unable to close remote connection!", e.getMessage());
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            log.warning(sw.toString());
            return false;
        }
    }
}
