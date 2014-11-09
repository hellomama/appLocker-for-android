package com.lviz.droid.applocker.model;

import java.util.ArrayList;

import java.util.List;

import android.app.Application;

/**
 * @author 不是驴子	
 * 
 */
public class lockapp extends Application {
    private List<String> lockapps = new ArrayList<String>();;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		setLockapps(lockapps);
	}
	
	public List<String> getLockapps() {
		return lockapps;
	}

	public void setLockapps(List<String> lockapps) {
		this.lockapps = lockapps;
	}

	public void addValue(String packageName) {
		// TODO Auto-generated method stub
		System.out.println("........."+packageName);
		if(!this.lockapps.contains(packageName))
		this.lockapps.add(packageName);
	}

	public void removeValue(String packageName) {
		// TODO Auto-generated method stub
		this.lockapps.remove(packageName);
	}

	public boolean clearValue() {
		// TODO Auto-generated method stub
		 
		return this.lockapps.removeAll(getLockapps());
	}

	
	

}
