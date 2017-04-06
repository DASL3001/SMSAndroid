package manager.background.map;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import manager.background.objects.SyncSite;
import manager.background.settings.ManagerSettings;

public class MapBayStatusUpdateManager {

    private static final ManagerSettings settings = ManagerSettings.getInstance();
    private static final Logger log = Logger.getLogger(settings.logName);
    private static final DBConManager _connMgr = DBConManager.getInstance();
   
    private boolean _isContinue = true;
    public static final int TIME_TO_WAIT_FOR_A_CONNECTION = 5; //Was 40
    private final String _LockupFile = "map_bay_status_lock.txt";
    private final ExecutorService _pool = Executors.newFixedThreadPool(settings.mapBaySyncModeMaxThreads, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread th = new Thread(r);
            return th;
        }
    });

    public void startSyncing() throws Exception {
        // Setup the connection poolMAX MESSAGES
        _connMgr.setIMaxConnections(settings.mapBaySyncModeMaxSRDbConnections);
        _connMgr.init(log, settings.jdbcUrl, settings.jdbcId, settings.jdbcPwd);

        // Managers connector:
        Connection con = openConnection();     
      
        
        MapSyncLookup mapSyncLookup = new MapSyncLookup();
        // Get a list of Sites needing syncing.
        List<SyncSite> siteList = mapSyncLookup.getVdsSitesForBaySync(con, settings.siteList);
        boolean isActive = true;
        // The main thread should manage it's own connection to 
        // the database seperate from the connection pool setup from request threads.
        // This is a fail safe to ensure that the main thread can get things going again should 
        // a deadlock occur within the pool.
        int iConUseCount = 0; // When this reached iConUseLimit. Connection is reopened.
        while (_isContinue) {
            // Loop looking for schedule report requests.
            try {

                if (con != null) {
                    if (con.isClosed()) {
                        con = null;
                        // See if you can establish a connection
                        con = openConnection();
                        iConUseCount = 0;
                    } else if (iConUseCount >= settings.mapBaySyncModeMaxConnectionReuse) {
                        con.close();
                        con = null;
                        // See if you can establish a connection
                        con = openConnection();
                        iConUseCount = 0;
                    }
                } else {
                    // See if you can establish a connection
                    con = openConnection();
                    iConUseCount = 0;
                }

                if (con == null) {
                    log.info("Still no connection loop and try again!");
                } else {
                    // Query generate_composite check if any reports to generate at the moment.
                    // Add up to 3? at the same time and generate:
                    siteList = mapSyncLookup.updateVdsSitesForBaySync(con, siteList, settings.siteList);

                    if (siteList.size() > 0) {
                        boolean foundUnstartedEntry = false;
                        for (SyncSite site : siteList) {
                            if (site.processingRequest == false && site.completedRequest == false) {
                                foundUnstartedEntry = true;
                                break;
                            }
                        }
                        isActive = foundUnstartedEntry;
                    } else {
                        isActive = false;
                    }

                    for (SyncSite site : siteList) {
                        if (site.processingRequest == false && site.completedRequest == false) {

                            // Give the site data send request a thread.
                            // Has it already got a thread?
                            if (site.thread != null && site.thread.isDone() == false) {
                                // Must have forgot to set thread to processing. Set it up now.
                                site.processingRequest = true;
                                if (site.threadAddedServerTime == null) {
                                    site.threadAddedServerTime = new Date();
                                }
                            } else if (site.thread == null) {

                                site.processingRequest = true;
                                site.threadAddedServerTime = new Date();
                                RunBayStatusMapSync runSyncRequest = new RunBayStatusMapSync(site);
                                site.thread = _pool.submit(runSyncRequest);

                            } else // Thread must be added and processing to get here?
                            // Check to make sure it hasn't taken too long to generate.
                            {
                                if (site.threadAddedServerTime.getTime() < (new Date()).getTime() - settings.mapBaySyncModeMaxThreadRuntime) { // defaults to 10mins?
                                    // Cancel the thread and delete the request, send an email to the user to say that the requested report was too big to generate.
                                    if (site.thread.cancel(true) == false) {
                                        log.log(Level.WARNING, "Thread would not cancel! for site sync manager= {0}", site.getSiteDbid());
                                    }
                                }
                            }
                        }
                    }
                }

                iConUseCount++;
                if (isActive) {
                    Thread.sleep(settings.mapBaySyncModeActiveWaitTime);
                } else {
                    Thread.sleep(settings.mapBaySyncModeInactiveWaitTime);
                }

            } catch (Exception e) {
                log.log(Level.WARNING, "Error in main loop looking for sendSyncRequests!", e);
                // Exit unexpectedly.
                reportLockup();
                endApp();
            }
        }

    }

    private Connection openConnection() {
        try {
            // Register the jdbc driver we wish to use.
            Class.forName("com.mysql.jdbc.Driver");

            // See if you can establish a connection
            Connection con = DriverManager.getConnection(settings.jdbcUrl, settings.jdbcId, settings.jdbcPwd);

            return con;
        } catch (Exception e) {
            log.log(Level.WARNING, "openConnection(), Error: Unable to open a connection!", e);
            return null;
        }
    }

    public void endApp() {
        // Stop all threads from continuing.
        log.info("-- Stopping MaQueOutUpdateManager Sync Mode --");
        // By setting continue to false no new threads will be started
        _isContinue = false;
        _pool.shutdown();

    }

    /**
     * Report a lockup if you wish to automatically restart.
     */
    public void reportLockup() {
        // Create the lockup file used by the sh script to restart the system.
        if (settings.isAutoRestart) {
            // Create the shutdown file
            try {
                FileOutputStream out = new FileOutputStream(_LockupFile);
                PrintStream p = new PrintStream(out);
                p.close();
            } catch (Exception e) {
                // Do nothing, exiting.
                log.warning("endApp(): Could not create the shutdown file. Will not restart!");
            }
        }
    }
}
