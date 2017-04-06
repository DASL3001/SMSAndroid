package manager.background.settings;

import java.util.logging.Level;
import java.util.logging.Logger;


public class CommonConstants {
    
    private static CommonConstants globalConstants;
    
//    public static final SimpleDateFormat sdfTimeStamp = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
//    public static final SimpleDateFormat sdfCSVDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//    public static final SimpleDateFormat sdfCSVFileNameFormat = new SimpleDateFormat("yyyy-MM-dd_H-mm-ss");
    
    // Bay Manager Modes:
    public static final int MODE_RESERVE_CHECK = 0;
    public static final int MODE_SYNC_MANAGER = 1;
    public static final int MODE_MAP_SYNC_MAP_QUEUQ_OUT = 2;
    public static final int MODE_MAP_DATA_MIGRATE = 3;
    public static final int MODE_MAP_SYNC_BAY_STAUS = 4;

    // DAY_IN_MILISEC = (1000 * 60 * 60 * 24)
    public static final long DAY_IN_MILISEC = 86400000;
    public static final long HOUR_IN_MILISEC = 3600000;
    public static final long MINUTE_IN_MILISEC = 60000;
    public static final long HOURS_IN_DAY = 24;
    public static final long MINS_IN_HOUR = 60;
   
    
    public CommonConstants(){
        // Do Nothing.
    }

    /** This should be moved to a new project called CommonUtil or similar.
     * 
     * @param log
     * @param desc
     */
    public void logMemory(Logger log, String desc){
        log.log(Level.INFO, "{0} {1} Free/Max/Total Memory = {2}/{3}/{4}", new Object[]{Thread.currentThread().getName(), desc, Runtime.getRuntime().freeMemory(), Runtime.getRuntime().maxMemory(), Runtime.getRuntime().totalMemory()});
    }
    
    //Ensures that there is only one instance of Common Constants.
    public static CommonConstants InstanceOf(){
        if (globalConstants == null){
            globalConstants = new CommonConstants();
        }
        return globalConstants;
    }
}
