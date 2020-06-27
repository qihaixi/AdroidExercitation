package com.example.adroidexercitation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class StartActivity extends Activity {
    private ImageView pic_bg;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        pic_bg = findViewById(R.id.pic_bg);
        startAnimation();
        CheckAutoLogin();
    }

    public void startAnimation() {
        AlphaAnimation anim = new AlphaAnimation(0f, 1f);
        anim.setDuration(2000);
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
                user = DBUtils.AutoLogin();
//                Log.i("tag",user.getUsername());
                if (user.getUsername().equals("")) {
                    LoginFail();
                }else {
                    LoginSuccess();
                }

            }
        }.start();
    }

    public void LoginSuccess(){
        //Log.i("tag","成功");
        Intent intent = new Intent(StartActivity.this, LoginActivity.class);
        intent.putExtra("username",user.getUsername());
        intent.putExtra("password",user.getPassword());
        startActivity(intent);
        finish();
    }

    public void LoginFail() {
        //Log.i("tag","失败");
        Intent intent = new Intent(StartActivity.this, LoginActivity.class);
        intent.putExtra("null","null");
        startActivity(intent);
        finish();
    }

}