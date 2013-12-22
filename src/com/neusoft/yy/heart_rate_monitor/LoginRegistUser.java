package com.neusoft.yy.heart_rate_monitor;

import com.neusoft.yy.bean.User;
import com.neusoft.yy.dao.UserDao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class LoginRegistUser extends Activity {

	private EditText regist_username = null;
	private EditText regist_password = null;
	private RelativeLayout regist_registbutton = null;
	
	private UserDao userDao = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.login_registuser);
		userDao = new UserDao(this);
		
		regist_username = (EditText) findViewById(R.id.regist_username);
		regist_password = (EditText) findViewById(R.id.regist_password);
		regist_registbutton = (RelativeLayout) findViewById(R.id.regist_registbutton);
		regist_registbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				regist_registbutton.setClickable(false);
				String username = regist_username.getText().toString();
				if(TextUtils.isEmpty(username)) {
					Toast.makeText(LoginRegistUser.this, R.string.noempty_regist_username, Toast.LENGTH_SHORT).show();
					regist_registbutton.setClickable(true);
					return;
				}
				String password = regist_password.getText().toString();
				if(TextUtils.isEmpty(password)) {
					Toast.makeText(LoginRegistUser.this, R.string.noempty_regist_password, Toast.LENGTH_SHORT).show();
					regist_registbutton.setClickable(true);
					return;
				}
				int count = userDao.getCountUser(username);
				if(count > 0) {
					Toast.makeText(LoginRegistUser.this, R.string.userdb_uniqname, Toast.LENGTH_LONG).show();
					regist_registbutton.setClickable(true);
					return;
				}
				User user = new User();
				user.setUsername(username);
				user.setPassword(password);
				long result = userDao.addUser(user);
				if(result == -1) {
					Toast.makeText(LoginRegistUser.this, R.string.userdb_error, Toast.LENGTH_LONG).show();
					regist_registbutton.setClickable(true);
					return;
				}
				Toast.makeText(LoginRegistUser.this, R.string.userdb_add_success, Toast.LENGTH_LONG).show();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Intent data=new Intent();
	            data.putExtra("username", username);
	            setResult(HeartRateMonitor.HEARTRATE_TWO, data);
	            finish();
			}
		});
	}

	@Override
	public void onBackPressed() {
		Intent data=new Intent();
        data.putExtra("username", "");
        setResult(HeartRateMonitor.HEARTRATE_ZERO, data);
        finish();
	}
	
	
}
