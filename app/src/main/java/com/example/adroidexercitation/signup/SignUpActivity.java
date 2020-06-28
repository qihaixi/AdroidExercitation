package com.example.adroidexercitation.signup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adroidexercitation.database.DBUtils;
import com.example.adroidexercitation.R;
import com.example.adroidexercitation.model.User;
import com.example.adroidexercitation.login.LoginActivity;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton ib_signup_return;
    private TextView tv_btn_signup;
    private EditText et_username,et_mail,et_sex,et_password;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);
        getSupportActionBar().hide();
        init();
    }
    private void init(){
        et_username = findViewById(R.id.et_sign_username);
        et_mail = findViewById(R.id.et_sign_mail);
        et_sex = findViewById(R.id.et_sign_sex);
        et_password = findViewById(R.id.et_sign_password);
        ib_signup_return = findViewById(R.id.ib_signup_return);
        tv_btn_signup = findViewById(R.id.main_btn_signup);
        ib_signup_return.setOnClickListener(this);
        tv_btn_signup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Toast toast = Toast.makeText(this, null, Toast.LENGTH_SHORT);
        switch (v.getId()){
            case R.id.ib_signup_return:
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                intent.putExtra("null","null");
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            case R.id.main_btn_signup:
                user = new User();
                user.setUsername(et_username.getText().toString().trim());
                user.setMail(et_mail.getText().toString().trim());
                user.setSex(et_sex.getText().toString().trim());
                user.setPassword(et_password.getText().toString().trim());
                if (user.getUsername().equals("")) {
                    toast.setText("用户名不能为空");
                    toast.show();
                } else if (user.getMail().equals("")) {
                    toast.setText("邮箱不能为空");
                    toast.show();
                } else if (user.getSex().equals("")) {
                    toast.setText("性别不能为空");
                    toast.show();
                } else if (user.getPassword().equals("")) {
                    toast.setText("密码不能为空");
                    toast.show();
                } else if (!user.getUsername().matches("[a-zA-Z0-9]{6,16}")) {
                    toast.setText("请输入6-16位只包含数字和字母的用户名");
                    toast.show();
                } else if (!user.getMail().matches("^([a-z0-9A-Z]+[-|.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$")) {
                    toast.setText("请输入正确的邮箱格式");
                    toast.show();
                } else if (!user.getSex().equals("男") && !user.getSex().equals("女")) {
                    toast.setText("请输入真正的性别");
                    toast.show();
                } else {
                    toast.setText("正在注册，请稍等……");
                    toast.show();
                    Signup(user);
                }
                break;
        }
    }

    class DBThread implements Runnable {
        private User user;
        private Context context;

        public void setUser(User user) {
            this.user = user;
        }

        public void setContext(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            int result= DBUtils.Signup(user);
            Looper.prepare();
            Intent intent;
            Toast toast = Toast.makeText(SignUpActivity.this, null, Toast.LENGTH_SHORT);
            if (result == 1) {
                toast.setText("注册成功");
                toast.show();
                intent = new Intent(SignUpActivity.this,LoginActivity.class);
                intent.putExtra("username_signup",user.getUsername());
                context.startActivity(intent);
                finish();
            } else if (result == 0) {
                toast.setText("用户名已存在");
                toast.show();
            } else if (result == -1) {
                toast.setText("邮箱已被注册");
                toast.show();
            } else if (result == -2) {
                toast.setText("连接超时，请稍后重试");
                toast.show();
            } else if (result == -3) {
                toast.setText("注册失败，请稍后重试");
                toast.show();
            }
            Looper.loop();
        }
    }

    private void Signup(User user){
        DBThread dt = new DBThread();
        dt.setUser(user);
        dt.setContext(this);
        Thread thread = new Thread(dt);
        thread.start();
    }

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