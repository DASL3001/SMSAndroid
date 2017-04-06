package manager.background.main;

import manager.background.map.MapBayStatusUpdateManager;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import manager.background.settings.CommonConstants;
import manager.background.settings.ManagerSettings;

public class Main {

    private static final ManagerSettings settings = ManagerSettings.getInstance();
    
    public static void main(String[] args) {

        // Read arguments.
        settings.loadParameters(args);
        
        Logger log = Logger.getLogger(settings.logName);
        LogManager logMan = LogManager.getLogManager();
        try{
            String sUserDir = System.getProperty("user.dir");
            System.out.println("Current user directory:="+sUserDir);
            logMan.readConfiguration(new FileInputStream(settings.logName+"_log.properties"));
        }catch(Exception e){
            log.log(Level.WARNING, "main(): An Error occurred Setting up the Bay Manager log file!", e);
        }        
       
        try {            
            // Load config file
            settings.loadCfgSettings(log);
            
            switch(settings.mode){              

                case CommonConstants.MODE_MAP_SYNC_BAY_STAUS:{
                    log.log(Level.INFO, "Running in mode: {0}", settings.modeName);
//                    -m=mapsyncbaystatus -log=mapSync
//                    -m=mapsyncbaystatus -log=mapSync -mapdbconstr=jdbc:mysql://localhost:3306/test_map_db,user,password
                    final MapBayStatusUpdateManager manager = new MapBayStatusUpdateManager();
                    manager.startSyncing();
                     // Create the Shutdown hook:
                    Runtime.getRuntime().addShutdownHook(new Thread() {
                        @Override
                        public void run() {
                            manager.endApp();
                        }
                    });
                    break;
                }
            }
            
        } catch(Throwable t){            
            System.err.println(t);
        }
    }
}
