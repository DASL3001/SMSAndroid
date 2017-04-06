package manager.background.objects;

import java.util.Date;
import java.util.concurrent.Future;

public class SyncSite {

    private long siteDbid;
    private String cusCode;
    public Future thread;
    public Date threadAddedServerTime;
    
    public boolean completedRequest;
    public boolean processingRequest;


    public SyncSite() {
    }

    public String getCusCode() {
        return cusCode;
    }

    public void setCusCode(String cusCode) {
        this.cusCode = cusCode;
    }

    public long getSiteDbid() {
        return siteDbid;
    }

    public void setSiteDbid(long siteDbid) {
        this.siteDbid = siteDbid;
    }

    public Future getThread() {
        return thread;
    }

    public void setThread(Future thread) {
        this.thread = thread;
    }
}
