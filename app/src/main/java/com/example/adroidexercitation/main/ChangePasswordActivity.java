package com.example.adroidexercitation.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.adroidexercitation.R;
import com.example.adroidexercitation.chat.MessageActivity;
import com.example.adroidexercitation.database.DBUtils;
import com.example.adroidexercitation.login.LoginActivity;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText old_pwd, new_pwd, confirm_pwd;
    private Button btn_change_pwd;
    private String old_password;
    private String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.change_password);
        Intent intent = getIntent();
        if (intent != null) {
            old_password = intent.getStringExtra("old_password");
            username = intent.getStringExtra("username");
        }
        old_pwd = findViewById(R.id.old_pwd);
        new_pwd = findViewById(R.id.new_pwd);
        confirm_pwd = findViewById(R.id.confirm_pwd);
        btn_change_pwd = findViewById(R.id.btn_change_password);
        btn_change_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!new_pwd.getText().toString().trim().equals(confirm_pwd.getText().toString().trim())) {
                    Toast.makeText(ChangePasswordActivity.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                } else if(!old_pwd.getText().toString().trim().equals(old_password)) {
                    Toast.makeText(ChangePasswordActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                } else {
                    change_password(username, new_pwd.getText().toString().trim());
                    Toast.makeText(ChangePasswordActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                    intent.putExtra("username_cp",username);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            }
        });

    }
    public static class Mythread extends Thread{
        private String new_pwd;
        private String username;
        Mythread(String username, String new_pwd){
            this.username = username;
            this.new_pwd = new_pwd;
        }
        @Override
        public void run() {
            DBUtils.change_password(username, new_pwd);
        }
    }
    private void change_password(String username, String new_password){
        Mythread mythread = new Mythread(username, new_password);
        mythread.start();
        try {
            mythread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}