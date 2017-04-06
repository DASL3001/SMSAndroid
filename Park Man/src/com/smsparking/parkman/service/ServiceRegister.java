package com.smsparking.parkman.service;


public interface ServiceRegister {
	
	public void registerServiceListner(ServiceListner o);
	public void removeServiceListner(ServiceListner o);
	public void notifyServiceListner(Object o);
	
}