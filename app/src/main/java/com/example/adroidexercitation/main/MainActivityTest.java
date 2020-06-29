package com.example.adroidexercitation.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.adroidexercitation.R;
import com.example.adroidexercitation.chat.MessageActivity;
import com.example.adroidexercitation.login.LoginActivity;
import com.example.adroidexercitation.model.User;

public class MainActivityTest extends Activity {
    private TextView tv1,tv2;
    private ImageButton btn_back;
    private Button btn_send;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_layout);
        init();
        Intent getData=getIntent();
        user = (User)getData.getSerializableExtra("user");
        tv1.setText("用户名为：" +user.getUsername());
        tv2.setText("密码为："+user.getPassword());
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivityTest.this, LoginActivity.class);
                intent.putExtra("username_logout",user.getUsername());
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivityTest.this, MessageActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }
    private void init(){
        tv1=findViewById(R.id.info_username);
        tv2=findViewById(R.id.info_password);
        btn_back = findViewById(R.id.ib_login_return);
    }

    // 双击返回键直接退回桌面
    /*
    private static final int TIME_EXIT=2000;
    private long mBackPressed;
    @Override
    public void onBackPressed() {
        if(mBackPressed+TIME_EXIT>System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        }else{
            Toast.makeText(this,"再点击一次返回退出程序", Toast.LENGTH_SHORT
            ).show();
            mBackPressed=System.currentTimeMillis();
        }
    }
    */

    // 单击返回键退回桌面，不结束程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}