package com.example.adroidexercitation.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.example.adroidexercitation.R;
import com.example.adroidexercitation.database.DBUtils;
import com.example.adroidexercitation.database.MySQLiteHelper;
import com.example.adroidexercitation.login.LoginActivity;
import com.example.adroidexercitation.model.User;

public class StartActivity extends Activity {
    private ImageView pic_bg;
    private MySQLiteHelper mySQLiteHelper;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        pic_bg = findViewById(R.id.pic_bg);
        mySQLiteHelper = new MySQLiteHelper(this);
        startAnimation();
        CheckAutoLogin();
    }

    public void startAnimation() {
        AlphaAnimation anim = new AlphaAnimation(0f, 1f);
        anim.setDuration(1500);
        anim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
            }
        });
        pic_bg.startAnimation(anim);
    }

    public void CheckAutoLogin(){
        new Thread(){
            @Override
            public void run() {
                user = DBUtils.AutoLogin(mySQLiteHelper);
                if (user.getPassword().equals("")) {
                    LoginFail();
                }else {
                    LoginSuccess();
                }
            }
        }.start();
    }

    public void LoginSuccess(){
        Intent intent = new Intent(StartActivity.this, LoginActivity.class);
        intent.putExtra("alusername_succ",user.getUsername());
        intent.putExtra("alpassword_succ",user.getPassword());
        startActivity(intent);
        finish();
    }

    public void LoginFail() {
        // 自动登录失败时，把上一次登录的用户名传给login页面。
        Intent intent = new Intent(StartActivity.this, LoginActivity.class);
        intent.putExtra("alusername_fail",user.getUsername());
        startActivity(intent);
        finish();
    }

}