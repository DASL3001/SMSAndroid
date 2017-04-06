package manager.background.settings;

import java.util.logging.Logger;


public class ManagerSettings {

    private static ManagerSettings settings;

    private static final Config cfg = Config.getInstance();

    // Load flags:
    public boolean parametersLoaded;
    private static String cfgFileName;
    public boolean cfgLoaded;

    public String logName = "";

    // Parameter fields:
    public String[] siteList;
    public String modeName;
    public int mode;
    public boolean isAutoRestart;

    // Cfg fields:
    public int maxThreads;
    public long maxRunTimeInMinutes;
    public int pollModeMaxConnectionReuse;
    public int pollModeMaxDbConnections;
    public int pollModeMaxThreads;
    public long pollModeMaxThreadRuntime;
    public long pollModeActiveWaitTime;
    public long pollModeInactiveWaitTime;
    public int pollModeMaxRecordsAtOnce;
    public int webServicePort;
    public String commsInterfacePath;

    // Either or. If in config file it overides the arguments.
    public String jdbcUrl;
    public String jdbcId;
    public String jdbcPwd;

    static synchronized public ManagerSettings getInstance() {
        if (settings == null) {
            settings = new ManagerSettings();
        }
        return settings;
    }
    public long mapPollModeActiveWaitTime;
    public long mapPollModeInactiveWaitTime;
    
    public int mapPollModeMaxConnectionReuse;
    public int mapPollModeMaxDbConnections;
    public int mapPollModeMaxThreads;
    public int mapPollModeMaxThreadRuntime;
    
    public String mapJdbcUrl;
    public String mapJdbcId;
    public String mapJdbcPwd;
    public int siteMapDataMigRunHour;
    public boolean ovrMapSiteDataMigrate = false;
    public String[] mapSyncTableList;
    public int mapBaySyncModeActiveWaitTime;
    public int mapBaySyncModeInactiveWaitTime;
    public int mapBaySyncModeMaxThreads;
    public int mapBaySyncModeMaxThreadRuntime;
    public int mapBaySyncModeMaxSRDbConnections;
    public int mapBaySyncModeMaxMapDbConnections;
    public int mapBaySyncModeMaxConnectionReuse;
    public int mapBaySyncModeMaxInsertsForQuery = 1000;
 
    
    private ManagerSettings() {
        cfgLoaded = false;
        parametersLoaded = false;

        cfgFileName = "cfg/baymanager.conf";

        // Defaults:
        maxThreads = 16;
        maxRunTimeInMinutes = 60;
        pollModeMaxConnectionReuse = 100;
        pollModeMaxDbConnections = 16;
        pollModeMaxThreads = 16;
        pollModeMaxThreadRuntime = 60000;
        pollModeActiveWaitTime = 2000;
        pollModeInactiveWaitTime = 10000;
        pollModeMaxRecordsAtOnce = 1000;
        webServicePort = 80;
     

        // Map sync Defaults
        mapPollModeActiveWaitTime = 2000;
        mapPollModeInactiveWaitTime = 10000;
        mapPollModeMaxConnectionReuse = 100;
        mapPollModeMaxDbConnections = 16;
        mapPollModeMaxThreads = 16;
        mapPollModeMaxThreadRuntime = 60000;
        siteMapDataMigRunHour = 3;

        isAutoRestart = true;
    }

    public final void loadCfgSettings(Logger log) throws Exception {
        // Configure:
        log.info("Loading configuration settings..");

        // Setup the configuration file:
        if (cfgLoaded == false) {
            cfg.loadCfg(cfgFileName);
        }

        maxThreads = (cfg.getProperty("maxThreads") == null) ? maxThreads : Integer.valueOf(cfg.getProperty("maxThreads"));
        maxRunTimeInMinutes = (cfg.getProperty("maxRunTimeInMinutes") == null) ? maxRunTimeInMinutes : Integer.valueOf(cfg.getProperty("maxRunTimeInMinutes"));

        // Argument loaded db connector overides config loaded db connector:
        String jdbcTemp = cfg.getProperty("jdbcUrl");
        jdbcUrl = (jdbcUrl == null) ? jdbcTemp : (jdbcUrl.equals("")) ? jdbcTemp : jdbcUrl;
        jdbcTemp = cfg.getProperty("jdbcId");
        jdbcId = (jdbcId == null) ? jdbcTemp : (jdbcId.equals("")) ? jdbcTemp : jdbcId;
        jdbcTemp = cfg.getProperty("jdbcPwd");
        jdbcPwd = (jdbcPwd == null) ? jdbcTemp : (jdbcPwd.equals("")) ? jdbcTemp : jdbcPwd;

        pollModeMaxConnectionReuse = (cfg.getProperty("pollModeMaxConnectionReuse") == null) ? pollModeMaxConnectionReuse : Integer.valueOf(cfg.getProperty("pollModeMaxConnectionReuse"));
        pollModeMaxDbConnections = (cfg.getProperty("pollModeMaxDbConnections") == null) ? pollModeMaxDbConnections : Integer.valueOf(cfg.getProperty("pollModeMaxDbConnections"));
        pollModeMaxThreads = (cfg.getProperty("pollModeMaxThreads") == null) ? pollModeMaxThreads : Integer.valueOf(cfg.getProperty("pollModeMaxThreads"));
        pollModeMaxThreadRuntime = (cfg.getProperty("pollModeMaxThreadRuntime") == null) ? pollModeMaxThreadRuntime : Integer.valueOf(cfg.getProperty("pollModeMaxThreadRuntime"));
        pollModeActiveWaitTime = (cfg.getProperty("pollModeActiveWaitTime") == null) ? pollModeActiveWaitTime : Integer.valueOf(cfg.getProperty("pollModeActiveWaitTime"));
        pollModeInactiveWaitTime = (cfg.getProperty("pollModeInactiveWaitTime") == null) ? pollModeInactiveWaitTime : Integer.valueOf(cfg.getProperty("pollModeInactiveWaitTime"));
        pollModeMaxRecordsAtOnce = (cfg.getProperty("pollModeMaxRecordsAtOnce") == null) ? pollModeMaxRecordsAtOnce : Integer.valueOf(cfg.getProperty("pollModeMaxRecordsAtOnce"));
        webServicePort = (cfg.getProperty("webServicePort") == null) ? webServicePort : Integer.valueOf(cfg.getProperty("webServicePort"));
 
        cfgLoaded = true;
    }

    public void loadParameters(String[] args) {
        try {
            // Loop through and get all the command line arguments.
            for (int i = 0; i < args.length; i++) {
                String sTemp = "";
                // Get the database login constraints
                boolean bFoundConnectionString = false;
                if (args[i].startsWith("-constr=")) {
                    sTemp = args[i].substring(8);
                    bFoundConnectionString = true;
                } else if (args[i].startsWith("-c=")) {
                    sTemp = args[i].substring(3);
                    bFoundConnectionString = true;
                }
                if (bFoundConnectionString) {
                    sTemp = sTemp.trim();
                    String[] sVar = sTemp.split(",");
                    if (sVar.length == 3) {
                        jdbcUrl = sVar[0];
                        jdbcId = sVar[1];
                        jdbcPwd = sVar[2];
                    } else {
                        System.err.println("Error: Command line arguments are incorrectly formatted!");
                    }
                }

                sTemp = "";
                // Get the customer list (of cus_code). If specified.
                // If not specified will get the list of all customers in the customer table.
                if (args[i].startsWith("-cust=")) {
                    sTemp = args[i].substring(6);
                    sTemp = sTemp.trim();
                    siteList = sTemp.split(",");
                }

                boolean bFoundAuto = false;
                if (args[i].startsWith("-auto=")) {
                    sTemp = args[i].substring(6);
                    bFoundAuto = true;
                } else if (args[i].startsWith("-a=")) {
                    sTemp = args[i].substring(3);
                    bFoundAuto = true;
                }
                if (bFoundAuto) {
                    sTemp = sTemp.trim();
                    isAutoRestart = Boolean.valueOf(sTemp);
                }

                // Get the mode which this Schedule Manager will be running in.
                boolean bFoundMode = false;
                if (args[i].startsWith("-mode=")) {
                    sTemp = args[i].substring(6);
                    bFoundMode = true;
                } else if (args[i].startsWith("-m=")) {
                    sTemp = args[i].substring(3);
                    bFoundMode = true;
                }
               

                // Logger Name:
                if (args[i].startsWith("-log=")) {
                    sTemp = args[i].substring(5);
                    logName = sTemp.trim();
                }                             
                
                
                // Get the map database constraints
                bFoundConnectionString = false;
                if (args[i].startsWith("-mapdbconstr=")) {
                    sTemp = args[i].substring("-mapdbconstr=".length());
                    bFoundConnectionString = true;
                }
                if (bFoundConnectionString) {
                    sTemp = sTemp.trim();
                    String[] sVar = sTemp.split(",");
                    if (sVar.length == 3) {
                        mapJdbcUrl = sVar[0];
                        mapJdbcId = sVar[1];
                        mapJdbcPwd = sVar[2];    
                    } else {
                        System.err.println("Error: Command line arguments are incorrectly formatted!");
                    }
                }
            }
            System.out.println("Setup Parameters.");

            parametersLoaded = true;
        } catch (Exception e) {
            // Writing failed, report it.
            System.err.println("NON-FATAL loadParameters(): Error when reading the command line arguments. " + e);
        }//end catch
    }
   
}
