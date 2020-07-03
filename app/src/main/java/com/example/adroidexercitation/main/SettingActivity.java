package com.example.adroidexercitation.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.adroidexercitation.R;
import com.example.adroidexercitation.database.DBUtils;
import com.example.adroidexercitation.database.MySQLiteHelper;
import com.example.adroidexercitation.login.LoginActivity;
import com.example.adroidexercitation.model.User;

public class SettingActivity extends AppCompatActivity {
    private TextView tv_change_pwd, tv_logout;
    User user;
    MySQLiteHelper mySQLiteHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.setting_layout);
        init();

        Intent getData=getIntent();
        user = (User)getData.getSerializableExtra("user");

        tv_change_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this,ChangePasswordActivity.class);
                intent.putExtra("old_password",user.getPassword());
                intent.putExtra("username",user.getUsername());
                startActivity(intent);
            }
        });
        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogout();
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                intent.putExtra("username_logout",user.getUsername());
                intent.putExtra("password_logout",user.getPassword());
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
    }

    private void init() {
        tv_change_pwd = findViewById(R.id.change_pwd);
        tv_logout = findViewById(R.id.logout);
        mySQLiteHelper = new MySQLiteHelper(this);
    }

    //注销
    private void doLogout(){
        new Thread(){
            @Override
            public void run() {
                DBUtils.LoginLogs(user,mySQLiteHelper,1);
            }
        }.start();
    }
}