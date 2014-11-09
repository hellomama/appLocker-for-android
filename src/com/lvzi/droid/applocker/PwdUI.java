/**
 * 
 */
package com.lvzi.droid.applocker;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.glory.droid.applocker.R;
import com.lviz.droid.applocker.model.lockapp;

/**
 * @author 不是驴子
 * 
 */
public class PwdUI extends Activity implements OnClickListener {

	private static final boolean fasle = false;
	private static int[] digitBtnIds = { R.id.digit0, R.id.digit1, R.id.digit2, R.id.digit3, R.id.digit4, R.id.digit5, R.id.digit6,
			R.id.digit7, R.id.digit8, R.id.digit9 };
	private static long[] VIBRATOR_PATTERN = { 0, 50 }; // OFF/ON/OFF/ON...

	private Button submitBtn;
	private Button clearBtn;
	private View digitPad;
	private View passPad;
	private TextView userPwdTv;
	private TextView lcd;
	private Vibrator vibrator;

	private Animation shakeAnim;
	private Animation slideAnim;
	private Animation pushUpAnim;
	
	private boolean flag = false;
	private String name = null;
	private lockapp apps = null ;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pwd_enter_ui);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		lcd = (TextView) findViewById(R.id.lcd);

		clearBtn = (Button) findViewById(R.id.clear);
		clearBtn.setOnClickListener(this);

		submitBtn = (Button) findViewById(R.id.submit);
		submitBtn.setOnClickListener(this);
		digitPad = findViewById(R.id.pad);
		passPad = findViewById(R.id.passpad);
		userPwdTv = (TextView) findViewById(R.id.userpass);
		shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake);
		slideAnim = AnimationUtils.loadAnimation(this, R.anim.slide_right);
		slideAnim.setFillBefore(true);
		slideAnim.setFillAfter(true);
		pushUpAnim = AnimationUtils.loadAnimation(this, R.anim.push_up_in);
		pushUpAnim.setFillAfter(true);
		pushUpAnim.setFillBefore(true);

		apps = (lockapp)getApplication();
		 Intent intent = getIntent();
//		 String one = intent.getStringExtra("one"); 
		  name = intent.getStringExtra("one");
		 if(name.equals("set_up_password")){
			 lcd.setText("设置密码");
			 flag = true;
		 }
		for (int id : digitBtnIds) {
			findViewById(id).setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.clear) {
			clearPasswd();
			userPwdTv.setText("");
		} else if (v.getId() == R.id.submit) {
			System.out.println(userPwdTv.getText().toString());
			if(flag)//设置密码
			{
				SharedPreferences sharedPrefernce = getSharedPreferences("password", MODE_WORLD_READABLE);
				Editor editor=sharedPrefernce.edit();
				editor.putString("password", userPwdTv.getText().toString().trim());
				editor.commit();
				PwdUI.this.finish();
			}
			else {
				if (validatePwd()) {
				System.out.println("fffffff");
				submit();
			} else {
				lcd.setText(getString(R.string.passwd_wrong));
				lcd.setTextColor(Color.parseColor("#B72C22"));
				userPwdTv.setText("");
				userPwdTv.startAnimation(shakeAnim);
			}
		}
		}  else {
			// vibrate();
			String orig = userPwdTv.getText().toString();
			if (orig.length() > 7) {
				return;
			}
			String enterText = ((Button) v).getText().toString().trim();
			userPwdTv.setText(userPwdTv.getText().toString() + enterText);
		}
	}


	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		
		super.onRestart();
		
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onPause();
		System.out.println("我在onStop.....");				
		new Thread(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				while(true)
				{
					ActivityManager activityManager = null;
					activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
					String packname = activityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
//					System.out.println("packname is "+packname);
//					System.out.println("packname !!!!!!"+packname);
					if(!packname.equals(name))
						{
						System.out.println("packname is ");
						apps.addValue(name);
						break;
						}
				}
				PwdUI.this.finish();
			}
			
		}.start();
			
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
			
	}
	@Override
	protected void onResume() {
		super.onResume();
//		apps.addValue(name);
		AnimThread animThread = new AnimThread();
		passPad.postDelayed(animThread, 10L);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK&&flag==fasle) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private boolean validatePwd() {
		String pword = null;
		SharedPreferences sharedPrefernce = getSharedPreferences("password", MODE_PRIVATE);
		pword = sharedPrefernce.getString("password", "");
		System.out.println("我的密码是"+pword);
		if(pword.equals(userPwdTv.getText().toString().trim()))
			return true;
		return false;
	}

	private void submit() {
		PackageManager packageManager = PwdUI.this.getPackageManager();
		apps.removeValue(name);
		
		//通过包名启动程序
		Intent intent = new Intent();
		 intent =packageManager.getLaunchIntentForPackage(name); 
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		// TODO Auto-generated method stub
		super.startActivityForResult(intent, requestCode);
	}

	private void clearPasswd() {
		Log.i("Lock", "clearPasswd");
		userPwdTv.setText("");
	}

	protected void vibrate() {
		vibrator.vibrate(VIBRATOR_PATTERN, -1);
	}

	class AnimThread implements Runnable {

		@Override
		public void run() {
			passPad.startAnimation(slideAnim);
			digitPad.startAnimation(pushUpAnim);
		}
	}
}
