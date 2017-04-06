package manager.background.settings;

import java.io.FileInputStream;
import java.util.Properties;

public class Config {
    private static Config cfg;

    private String filename; // e.g: "cfg/fileProc.conf";
    private Properties config;

    static synchronized public Config getInstance() {
        if (cfg == null) {
            cfg = new Config();
        }
        return cfg;
    }

    public Config() {
        config = new Properties();
    }

    public void loadCfg(String filename) throws Exception {
        this.filename = filename;
        config.load(new FileInputStream(filename));
    }

    public String getProperty(String key) throws Exception {
        return config.getProperty(key);
    }

    public String getFilename() {
        return filename;
    }
}
