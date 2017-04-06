package manager.background.objects;

import java.util.concurrent.Future;

public class Site {

    private long siteDbid;
    private String cusCode;
    private String name;
    private Future future;

    public Site() {
    }
    
    public Site(long _cusguid, String _cuscode, String _name){
        this.siteDbid = _cusguid;
        this.cusCode = _cuscode;
        this.name = _name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Future getFuture() {
        return future;
    }

    public void setFuture(Future future) {
        this.future = future;
    }
}
