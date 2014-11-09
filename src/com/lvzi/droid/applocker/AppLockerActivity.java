package com.lvzi.droid.applocker;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.glory.droid.applocker.R;
import com.lviz.droid.applocker.model.AppInfo;
import com.lviz.droid.applocker.model.lockapp;
import com.lvzi.droid.applocker.service.AppLockerService;
/**
 * @author 不是驴子	
 * 
 */

public class AppLockerActivity extends Activity implements OnClickListener {

	private ListView installedAppLv;

	private List<AppInfo> installedApps = null;
	private PackageManager pkgMgr = null;
	private AppListAdater appListAdapter;
	private ProgressDialog progressDialog = null;

	private Button selectDoneBtn = null;
	private Button setupBtn = null;
	private lockapp apps = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		pkgMgr = getPackageManager();
		apps = (lockapp)getApplication();
		installedAppLv = (ListView) findViewById(R.id.installed_app_list);
		selectDoneBtn = (Button) findViewById(R.id.select_done_btn);
		setupBtn = (Button) findViewById(R.id.set_up_password);
		selectDoneBtn.setOnClickListener(this);
		setupBtn.setOnClickListener(this);
		Intent service = new Intent(this, AppLockerService.class);
		startService(service);
		initProgressDialog();
		 new LoadDataTask().execute();
		 checkFirstLoad();
		 
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		
//		apps.addValue(getPackageName());
	}

	private void checkFirstLoad() {
		// TODO Auto-generated method stub
		SharedPreferences pref = getSharedPreferences("password", MODE_PRIVATE);
		String tmp = pref.getString("password", "");
		System.out.println("z这不好吧"+tmp+"zhebuhaoba ");
//		Toast.makeText(this, tmp, Toast.LENGTH_LONG).show();
		if(tmp == "")
		{
			Toast.makeText(this, "第一次使用，请先设置密码！", Toast.LENGTH_LONG).show();
			Intent intent = new Intent();
			intent.putExtra("one", "set_up_password");
			intent.setClass(getApplication(), PwdUI.class);
            startActivity(intent);
		}
	}

	private void initProgressDialog() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("正在加载....");
		progressDialog.setIndeterminate(true);
	}

	private void getInstalledApps() {
		if (installedApps != null) {
			installedApps.clear();
		} else {
			installedApps = new ArrayList<AppInfo>();
		}
		List<PackageInfo> packages = pkgMgr.getInstalledPackages(0);
		for (PackageInfo pkgInfo : packages) {
			AppInfo tmpInfo = new AppInfo();
			tmpInfo.setAppName(pkgInfo.applicationInfo.loadLabel(getPackageManager()).toString());
			tmpInfo.setPackageName(pkgInfo.packageName);
			tmpInfo.setVersionCode(pkgInfo.versionCode);
			tmpInfo.setVersionName(pkgInfo.versionName);
			tmpInfo.setAppIcon(pkgInfo.applicationInfo.loadIcon(pkgMgr));
			tmpInfo.print();
			installedApps.add(tmpInfo);
		}
	}

	class LoadDataTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			appListAdapter = new AppListAdater();
			installedAppLv.setAdapter(appListAdapter);
			installedAppLv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View arg1, int position, long id) {
					appListAdapter.toggle(position);
				}
			});
			installedAppLv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			
		}

		@Override
		protected Void doInBackground(Void... params) {
			getInstalledApps();
			return null;
		}
		
	}

	class AppListAdater extends BaseAdapter {
		boolean[] itemStatus;
		{
			itemStatus = new boolean[installedApps.size()];

		}
		private LayoutInflater layoutInflater = null;

		public AppListAdater() {
			layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public void toggle(int position) {
			if (itemStatus[position] == true) {
				itemStatus[position] = false;
			} else {
				itemStatus[position] = true;
			}
			this.notifyDataSetChanged();
		}

		public int[] getSelectedItemIndexes() {

			if (itemStatus == null || itemStatus.length == 0) {
				return new int[0];
			} else {
				int size = itemStatus.length;
				int counter = 0;
				// TODO how can we skip this iteration?
				for (int i = 0; i < size; i++) {
					if (itemStatus[i] == true)
						++counter;
				}
				int[] selectedIndexes = new int[counter];
				int index = 0;
				for (int i = 0; i < size; i++) {
					if (itemStatus[i] == true)
						selectedIndexes[index++] = i;
				}
				return selectedIndexes;
			}
		};

		@Override
		public int getCount() {
			return installedApps.size();
		}

		@Override
		public Object getItem(int position) {
			if (position < installedApps.size()) {
				return installedApps.get(position);
			} else {
				return null;
			}
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			ViewHolderEdit viewHolder = null;
			if (convertView == null) {
				view = layoutInflater.inflate(R.layout.list_item, parent, false);
				viewHolder = new ViewHolderEdit();
				viewHolder.appIconIv = (ImageView) view.findViewById(R.id.app_icon_iv);
				viewHolder.appNameTv = (TextView) view.findViewById(R.id.app_name_tv);
				viewHolder.versionNameTv = (TextView) view.findViewById(R.id.app_package_tv);
				viewHolder.checkBox = (CheckBox) view.findViewById(R.id.checkBoxEdit);
				view.setTag(viewHolder);
			} else {
				view = convertView;
				viewHolder = (ViewHolderEdit) view.getTag();
			}
			AppInfo appInfo = installedApps.get(position);
			viewHolder.appIconIv.setImageDrawable(appInfo.getAppIcon());
			viewHolder.appNameTv.setText(appInfo.getAppName());
			viewHolder.versionNameTv.setText(appInfo.getVersionName());
			viewHolder.checkBox.setOnCheckedChangeListener(new MyCheckBoxChangedListener(position));
			if (itemStatus[position] == true) {
				viewHolder.checkBox.setChecked(true);
			} else {
				viewHolder.checkBox.setChecked(false);
			}
			return view;
		}

		class MyCheckBoxChangedListener implements OnCheckedChangeListener {
			int position;

			MyCheckBoxChangedListener(int position) {
				this.position = position;
			}

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				System.out.println("" + position + "Checked?:" + isChecked);
				System.out.println(installedApps.get(position).appName+".....");

				
				if (isChecked)
					itemStatus[position] = true;
				else
					itemStatus[position] = false;
			}
		}
	}

	static class ViewHolderEdit {
		ImageView appIconIv;
		TextView appNameTv;
		TextView versionNameTv;
		CheckBox checkBox;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.select_done_btn:
//			SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
			int[] indexes = appListAdapter.getSelectedItemIndexes();
			
			StringBuilder sb = new StringBuilder();
			
			apps.clearValue();
			for (int i = 0; i < indexes.length; ++i) {
				apps.addValue(installedApps.get(indexes[i]).getPackageName());
				AppInfo appInfo = installedApps.get(indexes[i]);
//				sb.append(appInfo.getPackageName()).append(";");
				System.out.println(sb);
			}
//			Editor editor = prefs.edit();
//			editor.putString("lock_apps", sb.toString());
//			editor.commit();
			Toast.makeText(this, getString(R.string.set_success_msg), Toast.LENGTH_LONG).show();
			break;
		case R.id.set_up_password:
			Intent intent = new Intent();
			intent.putExtra("one", "set_up_password");
			intent.setClass(getApplication(), PwdUI.class);
            startActivity(intent);
		default:
			break;
		}

	}
}