package manager.background.map;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import manager.background.objects.Bay;
import manager.background.objects.SyncSite;
import manager.background.settings.ManagerSettings;

public class RunBayStatusMapSync implements Callable {

    private static final ManagerSettings settings = ManagerSettings.getInstance();
    private static final Logger log = Logger.getLogger(settings.logName);
    public static final int TIME_TO_WAIT_FOR_A_CONNECTION = 500; // In milisec

    private static final DBConManager CON_MANAGER = DBConManager.getInstance();  

    private final SyncSite syncSite;

    public RunBayStatusMapSync(SyncSite site) {
        this.syncSite = site;
    }

    @Override
    public Boolean call() throws Exception {
        boolean success = false;
        Connection con = null;
        Connection mapcon = null;
        try {            
            syncSite.processingRequest = true;

            // fix thread name
            String threadName[] = Thread.currentThread().getName().split("_");
            if (threadName.length > 0) {
                Thread.currentThread().setName(threadName[0] + "_" + syncSite.getSiteDbid());
            } else {
                Thread.currentThread().setName(Thread.currentThread().getName() + "_" + syncSite.getSiteDbid());
            }

            con = CON_MANAGER.getConnection();
            if (con == null) {
                log.log(Level.INFO, "{0} Could not initially get a connection. Hopefully get one next time.", Thread.currentThread().getName());
                con = CON_MANAGER.getConnection(TIME_TO_WAIT_FOR_A_CONNECTION);
            }
           
            if (mapcon == null || con == null) {
                log.log(Level.INFO, "{0} Could not get a db connection. Hopefully get one next time.", Thread.currentThread().getName());
                return success;
            }

            GetBayUpdates getBayUpdates = new GetBayUpdates(con);
            Timestamp lastAlertChangedTime = null;
            boolean isPrevAlertChangedTimeMatched = false;
            
            GetBayUpdates.Result r = getBayUpdates.getAvailableBayUpdates(syncSite.getSiteDbid(), lastAlertChangedTime, 
                    isPrevAlertChangedTimeMatched);
            
            List<Bay> bays = r.bays;
            // noting to update
            if (bays.isEmpty()) {              
                return success;
            }

            success = syncBayUpdatesWithMap(mapcon, bays);            

        } catch (Exception e) {
            log.log(Level.WARNING, Thread.currentThread().getName() + " Failed RunBayStatusMapSync.call()", e);
        } finally {
            // Release the connection back into the pool.
            if (con != null) {
                CON_MANAGER.freeConnection(con);
            }            
            syncSite.processingRequest = false;
            syncSite.completedRequest = true;
        }

        return success;
    }

    private boolean syncBayUpdatesWithMap(Connection mapcon, List<Bay> bays) {
        boolean success = updateBayBySingleQuery(mapcon, bays);
        if (success == false) {
            log.log(Level.INFO, "{0} Failed to update with single query. Update with single update statements", Thread.currentThread().getName());
            success = updateBayByBay(mapcon, bays);
        }
        return success;
    }

    private boolean updateBayBySingleQuery(Connection mapcon, List<Bay> bays) {
        if (bays.isEmpty()) {
            return true;
        }
        int BAY_UPDATE_LIMIT = settings.mapBaySyncModeMaxInsertsForQuery;
        int st = 0;
        int end = (BAY_UPDATE_LIMIT > bays.size()) ? bays.size() : BAY_UPDATE_LIMIT;
        boolean success = false;
        while (st < end) {
            success = runUpdateBayBySingleQuery(mapcon, bays, st, end);
            if (success == false) {
                break;
            }
            st = st + BAY_UPDATE_LIMIT + 1;
            end = ((BAY_UPDATE_LIMIT + st) > bays.size()) ? bays.size() : (BAY_UPDATE_LIMIT + st);

        }
        return success;
    }

    private boolean updateBayByBay(Connection mapcon, List<Bay> bays) {
        PreparedStatement stmt = null;
        try {
            BayUpdateBuilder bayUpdateBuilder = new BayUpdateBuilder();

            for (Bay bay : bays) {

                String query = bayUpdateBuilder.createSingleBayUpdateStr(bay);
                stmt = mapcon.prepareStatement(query);

                int paramIndex = 0;
                bayUpdateBuilder.populateSingleBayUpdateStr(bay, paramIndex, stmt);
                stmt.executeUpdate();
                stmt.close();
                stmt = null;
            }
            return true;
        } catch (Exception e) {
            log.log(Level.WARNING, Thread.currentThread().getName() + " updateBayByBay() Failed", e);
            return false;
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    // Do Nothing
                }
                stmt = null;
            }
        }
    }

    private boolean runUpdateBayBySingleQuery(Connection mapcon, List<Bay> bays, int start, int end) {
        PreparedStatement stmt = null;
        try {
            BayUpdateBuilder bayUpdateBuilder = new BayUpdateBuilder();
            String query = bayUpdateBuilder.createMultyBaySingleUpdateStr(bays, start, end);
            stmt = mapcon.prepareStatement(query);
            int parameterIndex = 0;
            bayUpdateBuilder.populateMultibaySingleUpdateStr(syncSite.getSiteDbid(), bays, start, end, parameterIndex, stmt);
            stmt.executeUpdate();

            return true;
        } catch (Exception e) {
            log.log(Level.WARNING, Thread.currentThread().getName() + " runUpdateBayBySingleQuery() Failed", e);
            return false;
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    // Do Nothing
                }
                stmt = null;
            }
        }

    }

}
