package manager.background.objects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Bay {

    private long mpoId;
    private long bayGuid;
    private long dbid;
    private int bayNumber;
    private String bayName;
    private int number;
    private String name;
    private int bayType;
    private String bayTypeName;
    private long custId;
    private long sectorId;
    private long sectorDbid;
    private String sectorName;
    private long lotId;
    private long lotDbid;
    private String lotName;
    private long customGroupTypeId;
    private long customGroupId;
    private String CustomGroupName;
    private int bayState;
    private int baySecondaryState;
    private boolean isReserved;
    private String deviceId;
    private int bay_power_on;
    private Timestamp bayArrivalExpiryTime;
    private Timestamp bayBookingExpiryTime;
    private Timestamp bayLastArrivalTime;
    private Timestamp bayOverstayTime;
    private Timestamp alertChangeTime;
    private Timestamp customerAlertChangeTime;
    private Timestamp bayLastDepartedTime;


    private String displayCustomerAlertChangeTime;
    private String displayLastArrivalTime;
    private String displayOverstayTime;
    private String displayLastDipartedTime;

    private String batIconName;
    private boolean bayIsReserved;
    private boolean bayIsOverstay;
    private boolean batteryStatus;
    private int shapeType;
    private boolean isPowerOn;

    private int rotation; // Only used if object is a rectangle or triangle. Shape rotated N degrees (-180 to 180) around point 1.
    private long skewX; // Only used if object is a rectangle. Points 3 + 4 move X plus or minus by N pixels.
    private long skewY; // Only used if object is a rectangle. Points 3 + 4 move X plus or minus by N pixels.
    private int orientation;
    private int width;
    private int length;
    private int x;
    private int y;

    private Timestamp bayLastMsgTime;
    private int bayLastMsgState;

    public Bay() {

    }

    public Bay(ResultSet rs, boolean basic) throws SQLException {
        this.bayGuid = rs.getLong("bay_guid");
        this.dbid = rs.getLong("bay_guid");
        this.batIconName = rs.getString("bat_icon_name");
        setLotId(rs.getLong("lot_guid"));      
        this.lotName = rs.getString("lot_name");   
        this.custId = rs.getLong("cus_guid");
        this.bayNumber = rs.getInt("bay_number");
        this.bayName = rs.getString("bay_name");
        this.number = this.bayNumber;
        this.name = this.bayName;
        this.bayType = rs.getInt("bay_type");
        this.bayTypeName = rs.getString("bat_name");
        this.bayState = rs.getInt("bay_state");
        this.sectorId = rs.getLong("sct_guid");
        this.sectorDbid = rs.getLong("sct_guid");
        this.sectorName = rs.getString("sct_name");
        this.alertChangeTime = rs.getTimestamp("alert_change_time");

    }

    public Bay(ResultSet rs) {
        try {
            this.bayGuid = rs.getLong("bay_guid");
            this.batIconName = rs.getString("bat_icon_name");
            this.bayIsReserved = rs.getBoolean("bay_is_reserved");
            this.bayIsOverstay = rs.getBoolean("bay_is_overstay");
            this.bayArrivalExpiryTime = rs.getTimestamp("bay_arrival_expiry_time");
            this.bayBookingExpiryTime = rs.getTimestamp("bay_booking_expiry_time");
            // this.mpoId = rs.getLong("mpo_guid");
            this.customGroupTypeId = rs.getLong("cgt_guid");
            this.customGroupId = rs.getLong("cgp_guid");
            this.CustomGroupName = rs.getString("cgp_name") == null ? "" : rs.getString("cgp_name");
            setLotId(rs.getLong("lot_guid"));
            this.lotName = rs.getString("lot_name");
            this.dbid = rs.getLong("bay_guid");
            this.custId = rs.getLong("cus_guid");
            this.bayNumber = rs.getInt("bay_number");
            this.bayName = rs.getString("bay_name");
            this.number = this.bayNumber;
            this.name = this.bayName;
            this.bayType = rs.getInt("bay_type");
            this.bayTypeName = rs.getString("bat_name");
            this.bayState = rs.getInt("bay_state");
            this.sectorId = rs.getLong("sct_guid");
            this.sectorDbid = rs.getLong("sct_guid");
            this.sectorName = rs.getString("sct_name");
            this.alertChangeTime = rs.getTimestamp("alert_change_time");
            this.customerAlertChangeTime = rs.getTimestamp("customer_alert_change_time");
            this.baySecondaryState = rs.getInt("bay_secondary_state");
            this.isReserved = rs.getBoolean("bay_is_reserved");
            this.deviceId = rs.getString("bay_device_id");
            this.bay_power_on = rs.getInt("bay_power_on");
            this.bayArrivalExpiryTime = rs.getTimestamp("bay_arrival_expiry_time");
            this.bayBookingExpiryTime = rs.getTimestamp("bay_booking_expiry_time");
            this.bayLastArrivalTime = rs.getTimestamp("customer_last_arrival_time");
            this.bayOverstayTime = rs.getTimestamp("customer_overstay_time");
            this.bayLastDepartedTime = rs.getTimestamp("customer_last_depart_time");
            this.mpoId = rs.getLong("mpo_guid");
            this.isPowerOn = rs.getBoolean("bay_power_on");
            try {
                this.shapeType = rs.getInt("spt_type");
            } catch (Exception e) {
            }
        } catch (SQLException sqlE) {

        }
    }

    public int getShapeType() {
        return shapeType;
    }

    public void setShapeType(int shapeType) {
        this.shapeType = shapeType;
    }
   
    public String getDisplayLastArrivalTime() {
        return displayLastArrivalTime;
    }

    public void setDisplayLastArrivalTime(String displayLastArrivalTime) {
        this.displayLastArrivalTime = displayLastArrivalTime;
    }

    public String getDisplayOverstayTime() {
        return displayOverstayTime;
    }

    public void setDisplayOverstayTime(String displayOverstayTime) {
        this.displayOverstayTime = displayOverstayTime;
    }

    public Timestamp getBayLastArrivalTime() {
        return bayLastArrivalTime;
    }

    public void setBayLastArrivalTime(Timestamp bayLastArrivalTime) {
        this.bayLastArrivalTime = bayLastArrivalTime;
    }

    public Timestamp getBayOverstayTime() {
        return bayOverstayTime;
    }

    public void setBayOverstayTime(Timestamp bayOverstayTime) {
        this.bayOverstayTime = bayOverstayTime;
    }

    public String getBatIconName() {
        return batIconName;
    }

    public void setBatIconName(String batIconName) {
        this.batIconName = batIconName;
    }

    public boolean isBayIsReserved() {
        return bayIsReserved;
    }

    public void setBayIsReserved(boolean bayIsReserved) {
        this.bayIsReserved = bayIsReserved;
    }

    public boolean isBayIsOverstay() {
        return bayIsOverstay;
    }

    public void setBayIsOverstay(boolean bayIsOverstay) {
        this.bayIsOverstay = bayIsOverstay;
    }

    public boolean isBatteryStatus() {
        return batteryStatus;
    }

    public void setBatteryStatus(boolean batteryStatus) {
        this.batteryStatus = batteryStatus;
    }

    public long getMpoId() {
        return mpoId;
    }

    public void setMpoId(long mpoId) {
        this.mpoId = mpoId;
    }

   

    public long getCustomGroupTypeId() {
        return customGroupTypeId;
    }

    public void setCustomGroupTypeId(long customGroupTypeId) {
        this.customGroupTypeId = customGroupTypeId;
    }

    public String getDisplayCustomerAlertChangeTime() {
        return displayCustomerAlertChangeTime;
    }

    public void setDisplayCustomerAlertChangeTime(String displayCustomerAlertChangeTime) {
        this.displayCustomerAlertChangeTime = displayCustomerAlertChangeTime;
    }

    public long getBayGuid() {
        return bayGuid;
    }

    public void setBayGuid(long bayGuid) {
        this.bayGuid = bayGuid;
        this.dbid = bayGuid;
    }

    public long getCustId() {
        return custId;
    }

    public void setCustId(long custId) {
        this.custId = custId;
    }

    public long getSectorId() {
        return sectorId;
    }

    public void setSectorId(long sectorId) {
        this.sectorId = sectorId;
        this.sectorDbid = sectorId;
    }

    public long getLotId() {
        return lotId;
    }

    public final void setLotId(long lotId) {
        this.lotId = lotId;
        this.lotDbid = lotId;
    }

    public long getCustomGroupId() {
        return customGroupId;
    }

    public void setCustomGroupId(long customGroupId) {
        this.customGroupId = customGroupId;
    }

    public int getBayNumber() {
        return bayNumber;
    }

    public void setBayNumber(int bayNumber) {
        this.bayNumber = bayNumber;
        this.number = bayNumber;
    }

    public String getBayTypeName() {
        return bayTypeName;
    }

    public void setBayTypeName(String bayTypeName) {
        this.bayTypeName = bayTypeName;     
    }

    public String getBayName() {
        return bayName;
    }

    public void setBayName(String bayName) {
        this.bayName = bayName;
        this.name = bayName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSectorName() {
        return sectorName;
    }

    public void setSectorName(String sectorName) {
        this.sectorName = sectorName;
    }

    public String getLotName() {
        return lotName;
    }

    public void setLotName(String lotName) {
        this.lotName = lotName;
    }

    public String getCustomGroupName() {
        return CustomGroupName;
    }

    public void setCustomGroupName(String CustomGroupName) {
        this.CustomGroupName = CustomGroupName;
    }

    public int getBay_power_on() {
        return bay_power_on;
    }

    public void setBay_power_on(int bay_power_on) {
        this.bay_power_on = bay_power_on;
    }

    public Timestamp getBayArrivalExpiryTime() {
        return bayArrivalExpiryTime;
    }

    public void setBayArrivalExpiryTime(Timestamp bayArrivalExpiryTime) {
        this.bayArrivalExpiryTime = bayArrivalExpiryTime;
    }

    public Timestamp getBayBookingExpiryTime() {
        return bayBookingExpiryTime;
    }

    public void setBayBookingExpiryTime(Timestamp bayBookingExpiryTime) {
        this.bayBookingExpiryTime = bayBookingExpiryTime;
    }

    public int getBayType() {
        return bayType;
    }

    public void setBayType(int bayTye) {
        this.bayType = bayTye;
    }

    public int getBayState() {
        return bayState;
    }

    public void setBayState(int bayState) {
        this.bayState = bayState;
    }

    public int getBaySecondaryState() {
        return baySecondaryState;
    }

    public void setBaySecondaryState(int baySecondaryState) {
        this.baySecondaryState = baySecondaryState;
    }

    public Timestamp getAlertChangeTime() {
        return alertChangeTime;
    }

    public void setAlertChangeTime(Timestamp aletChangeTime) {
        this.alertChangeTime = aletChangeTime;
    }

    public Timestamp getCustomerAlertChangeTime() {
        return customerAlertChangeTime;
    }

    public void setCustomerAlertChangeTime(Timestamp customerAlertChangeTime) {
        this.customerAlertChangeTime = customerAlertChangeTime;
    }   
    public boolean isIsReserved() {
        return isReserved;
    }

    public void setIsReserved(boolean isReserved) {
        this.isReserved = isReserved;
    }

    public Timestamp getBayLastDepartedTime() {
        return bayLastDepartedTime;
    }

    public void setBayLastDepartedTime(Timestamp bayLastDepartedTime) {
        this.bayLastDepartedTime = bayLastDepartedTime;
    }

    public String getDisplayLastDipartedTime() {
        return displayLastDipartedTime;
    }

    public void setDisplayLastDipartedTime(String displayLastDipartedTime) {
        this.displayLastDipartedTime = displayLastDipartedTime;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public long getSkewX() {
        return skewX;
    }

    public void setSkewX(long skewX) {
        this.skewX = skewX;
    }

    public long getSkewY() {
        return skewY;
    }

    public void setSkewY(long skewY) {
        this.skewY = skewY;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isIsPowerOn() {
        return isPowerOn;
    }

    public void setIsPowerOn(boolean isPowerOn) {
        this.isPowerOn = isPowerOn;
    }

    public Timestamp getBayLastMsgTime() {
        return bayLastMsgTime;
    }

    public void setBayLastMsgTime(Timestamp bayLastMsgTime) {
        this.bayLastMsgTime = bayLastMsgTime;
    }

    public int getBayLastMsgState() {
        return bayLastMsgState;
    }

    public void setBayLastMsgState(int bayLastMsgState) {
        this.bayLastMsgState = bayLastMsgState;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public long getDbid() {
        return dbid;
    }

    public void setDbid(long dbid) {
        this.dbid = dbid;
    }

    public long getSectorDbid() {
        return sectorDbid;
    }

    public void setSectorDbid(long sectorDbid) {
        setSectorId(sectorDbid);
    }

    public long getLotDbid() {
        return lotDbid;
    }

    public void setLotDbid(long lotDbid) {
        setLotId(lotId);
    }
    

}
