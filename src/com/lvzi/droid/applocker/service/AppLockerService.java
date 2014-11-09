/**
 * 
 */
package com.lvzi.droid.applocker.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.glory.droid.applocker.R;
import com.lviz.droid.applocker.model.AppInfo;
import com.lviz.droid.applocker.model.lockapp;
import com.lvzi.droid.applocker.AppLockerActivity;
import com.lvzi.droid.applocker.PwdUI;

/**
 * @author 不是驴子
 * 
 */
public class AppLockerService extends Service {

	private ExecutorService executorService;
	private Process monitorProcess;
	private Process cleanProcess;
	private ActivityManager activityManager = null;
	public AppInfo myInfo;
	private lockapp apps = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		apps = (lockapp)getApplication();
		executorService = Executors.newSingleThreadExecutor();
		// AppMonitor monitor = new AppMonitor();
		// executorService.submit(monitor);
		activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		LockerThread thread = new LockerThread();
		executorService.submit(thread);
		Notification notification = new Notification(R.drawable.lvzi, getText(R.string.ticker_text),
		        System.currentTimeMillis());
		Intent notificationIntent = new Intent(this, AppLockerActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, getText(R.string.ticker_text),
		        getText(R.string.app_name), pendingIntent);
		startForeground(1, notification);

	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (executorService != null) {
			executorService.shutdown();
		}
		if (monitorProcess != null) {
			monitorProcess.destroy();
		}
		if (cleanProcess != null) {
			cleanProcess.destroy();
		}
	}

	class LockerThread implements Runnable {

		Intent pwdIntent = null;

		public LockerThread() {
			pwdIntent = new Intent(AppLockerService.this, PwdUI.class);
			pwdIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}

		@Override
		public void run() {
			while (true) {
				Log.i("lock", "lockerThread run....");
				System.out.println("--------------------------");
                
				String packname = activityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
				System.out.println(packname);
				for(String name:apps.getLockapps())
				{
					
					if (name.equals(packname)) {
						pwdIntent.putExtra("one", packname);
					startActivity(pwdIntent);
				}
				}
//				if ("com.android.mms".equals(packname)) {
//					startActivity(pwdIntent);
//				}
				try {
					Log.i("lock","packname");
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class AppMonitor implements Runnable {

		@Override
		public void run() {
//			List<String> monitorCommandList = getMonitorCMD();
//			List<String> cleanCommandList = getCleanCMD();
			try {
//				String[] cleanCommand = cleanCommandList.toArray(new String[cleanCommandList.size()]);
//				cleanProcess = Runtime.getRuntime().exec(cleanCommand);
//				cleanProcess.waitFor();
//				String[] monitorCommand = monitorCommandList.toArray(new String[monitorCommandList.size()]);
//				monitorProcess = Runtime.getRuntime().exec(monitorCommand);
				InputStream inputStream = monitorProcess.getInputStream();
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					String myPackageName = AppLockerService.this.getPackageName();
					ActivityManager activityManager = (ActivityManager) AppLockerService.this.getSystemService(Context.ACTIVITY_SERVICE);
					String packageName = activityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
					if (myPackageName.equals(packageName)) {
						continue;
					}
					Log.v("app", line);
//					if (line.contains("com.dolphin.browser")) {
//						Intent authIntent = new Intent(AppLockerService.this, UnlockActivity.class);
//						authIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//						AppLockerService.this.startActivity(authIntent);
//					}
				}
			} catch (Exception e) {
				Log.e("app", "error", e);
			}
		}
	}

//	private List<String> getCleanCMD() {
//		List<String> commandList = new ArrayList<String>();
//		commandList.add("logcat");
//		commandList.add("-c");
//		return commandList;
//	}

//	private List<String> getMonitorCMD() {
//		List<String> commandList = new ArrayList<String>();
//		commandList.add("logcat");
//		commandList.add("ActivityManager:I");
//		commandList.add("*:S");
//		return commandList;
//	}
//	private boolean checkApp(String packname){
//		myInfo = 
//		return false;
		
//	}
}
