package com.smsparking.parkmanobjects;

public class Site {	   
    public String name;
    public String dbid;    
    public String address;
    
	public Site(String name, String dbid, String address) {
		this.name = name;
		this.dbid = dbid;
		this.address = address;
	}
	public String getName(){
		return name;
	}
	public String getDbid(){
		return dbid;
	}
	public String getAddress(){
		return address;
	}
	
}
