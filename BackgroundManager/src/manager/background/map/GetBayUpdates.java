package manager.background.map;

 
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Logger;
import manager.background.objects.Bay;
import manager.background.settings.ManagerSettings;


/**
 *
 * @author Don
 */
public class GetBayUpdates {

    private Connection con;
    private static final ManagerSettings settings = ManagerSettings.getInstance();
    private static final Logger log = Logger.getLogger(settings.logName);

    public GetBayUpdates(Connection con) {
        this.con = con;
    }

    public Result getAvailableBayUpdates(long siteDbid, Timestamp lastAlertChangedTime, boolean isPrevMatched) {        
        return null;
    }

    public static class Result {

        public Timestamp lastUpdateTime;
        public List<Bay> bays;
        public boolean isPrevAlertChangedTimeMatched;

        public Result() {
        }

        public Result(Timestamp lastUpdateTime, List<Bay> bays) {
            this.lastUpdateTime = lastUpdateTime;
            this.bays = bays;
        }

    }

}
