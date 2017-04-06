package manager.background.map;

/**
 *
 * @author Don
 */
public class BayStatUpdateSettings {

    private static BayStatUpdateSettings instance;

    private BayStatUpdateSettings() {
    }

    public static synchronized BayStatUpdateSettings getInstance() {
        if (instance == null) {
            instance = new BayStatUpdateSettings();
        }
        return instance;
    }
    
    public static String lastSyncSentTime;

}
