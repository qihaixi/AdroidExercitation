package com.example.adroidexercitation.login;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.adroidexercitation.MainActivity;
import com.example.adroidexercitation.database.DBUtils;
import com.example.adroidexercitation.MainActivityTest;
import com.example.adroidexercitation.R;
import com.example.adroidexercitation.database.MySQLiteHelper;
import com.example.adroidexercitation.signup.SignUpActivity;
import com.example.adroidexercitation.model.User;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

import java.util.Objects;

public class LoginActivity extends Activity implements View.OnClickListener {
    private TextView tvLogin, tvSignUp, tvFindPsw;
    private EditText username, password;
    private View progress;
    private View inputLayout;
    private LinearLayout mName, mPsw;
    private User user;
    private MySQLiteHelper mySQLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_layout);
        initView();

        //把信息传回到登录页面
        Intent intent = getIntent();
        if (intent != null) {
            if (Objects.requireNonNull(intent.getExtras()).containsKey("username_logout")) {
                // 用户点击注销后，把用户名传递给login界面
                String usernameValue = intent.getStringExtra("username_logout");
                username.setText(usernameValue);
            } else if (Objects.requireNonNull(intent.getExtras()).containsKey("username_signup")) {
                // 用户注册成功后，把用户名传递给login界面
                String usernameValue = intent.getStringExtra("username_signup");
                username.setText(usernameValue);
            } else if(Objects.requireNonNull(intent.getExtras()).containsKey("alusername_succ")) {
                // 自动登录成功后，把用户名和密码传递给login界面，并直接登录
                String usernameValue=intent.getStringExtra("alusername_succ");
                String passwordValue=intent.getStringExtra("alpassword_succ");
                username.setText(usernameValue);
                password.setText(passwordValue);
                user = new User();
                user.setUsername(usernameValue);
                user.setPassword(passwordValue);
                mName.setVisibility(View.INVISIBLE);
                mPsw.setVisibility(View.INVISIBLE);
                inputAnimator(inputLayout);
                checkLogin(user);
            } else if(Objects.requireNonNull(intent.getExtras()).containsKey("alusername_fail")){
                // 自动登录失败后，把用户名传递到login界面
                String usernameValue=intent.getStringExtra("alusername_fail");
                username.setText(usernameValue);
            } else if(Objects.requireNonNull(intent.getExtras()).containsKey("login_us_fail")){
                // 用户名密码错误或者无法连接到数据库时，把用户名和密码传回到login界面
                String usernameValue=intent.getStringExtra("login_us_fail");
                String passwordValue=intent.getStringExtra("login_pa_fail");
                username.setText(usernameValue);
                password.setText(passwordValue);
            }
        }

    }

    private void initView() {
        progress = findViewById(R.id.layout_progress);
        inputLayout = findViewById(R.id.input_layout);
        mName = findViewById(R.id.input_layout_name);
        mPsw = findViewById(R.id.input_layout_psw);

        username = findViewById(R.id.et_username);
        password = findViewById(R.id.et_password);
        tvLogin = findViewById(R.id.main_btn_login);
        tvSignUp = findViewById(R.id.tv_signup);
        tvFindPsw = findViewById(R.id.tv_find_password);

        //实例化数据库帮助类
        mySQLiteHelper = new MySQLiteHelper(this);

        //实例化user
        user = new User();

        //登录注册按钮监听
        tvLogin.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);
        tvFindPsw.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Toast toast;
        switch (v.getId()){
            case R.id.main_btn_login:
                user.setUsername(username.getText().toString().trim());
                user.setPassword(password.getText().toString().trim());
                toast = Toast.makeText(LoginActivity.this, null, Toast.LENGTH_SHORT);
                if (user.getUsername().equals("")) {
                    toast.setText("用户名/邮箱不能为空");
                    toast.show();
                } else if (user.getPassword().equals("")) {
                    toast.setText("密码不能为空");
                    toast.show();
                }else {
                    // 隐藏输入框
                    mName.setVisibility(View.INVISIBLE);
                    mPsw.setVisibility(View.INVISIBLE);
                    inputAnimator(inputLayout);
                    checkLogin(user);
//                    doLogin();
//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    intent.putExtra("user",user);
//                    startActivity(intent);
//                    finish();
                }
                break;
            case R.id.tv_signup:
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            default:
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
            int result = DBUtils.Login(user);
            Looper.prepare();
            Intent intent;
            Toast toast = Toast.makeText(LoginActivity.this, null, Toast.LENGTH_SHORT);
            if (result == 1) {
                // 这一步是连接网易云信的账号，实现即时通讯功能
                saveLoginLogs();
                doLogin();
                intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("user",user);
                context.startActivity(intent);
                finish();
            } else if (result == 0) {
                toast.setText("密码错误，请重试");
                toast.show();
                intent=new Intent(LoginActivity.this,LoginActivity.class);
                intent.putExtra("login_us_fail",user.getUsername());
                intent.putExtra("login_pa_fail",user.getPassword());
                context.startActivity(intent);
                finish();
            } else if (result == -1) {
                toast.setText("用户名不存在");
                toast.show();
                intent=new Intent(LoginActivity.this,LoginActivity.class);
                intent.putExtra("login_us_fail",user.getUsername());
                intent.putExtra("login_pa_fail",user.getPassword());
                context.startActivity(intent);
                finish();
            } else if (result == -2) {
                toast.setText("连接超时，请稍后重试");
                toast.show();
                intent=new Intent(LoginActivity.this,LoginActivity.class);
                intent.putExtra("login_us_fail",user.getUsername());
                intent.putExtra("login_pa_fail",user.getPassword());
                context.startActivity(intent);
                finish();
            }
            Looper.loop();
        }
    }

    // 登陆检测
    private void checkLogin(User user) {
        DBThread dt = new DBThread();
        dt.setUser(user);
        dt.setContext(this);
        Thread thread = new Thread(dt);
        thread.start();
    }

    // 输入框的动画效果
    private void inputAnimator(final View view) {

        ValueAnimator animator = ValueAnimator.ofFloat(0, 350);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view
                        .getLayoutParams();
                params.leftMargin = (int) value;
                params.rightMargin = (int) value;
                view.setLayoutParams(params);
            }
        });
        animator.setDuration(300);
        animator.start();

        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                /**
                 * 动画结束后，先显示加载的动画，然后再隐藏输入框
                 */
                progress.setVisibility(View.VISIBLE);
                progressAnimator(progress);
                inputLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
    }

    /**
     * 出现进度动画

     *
     * @param view
                */
        private void progressAnimator(final View view) {
            PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX",
                    0.5f, 1f);
            PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY",
                    0.5f, 1f);
            ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(view,
                    animator, animator2);
            animator3.setDuration(1000);
            animator3.setInterpolator(new JellyInterpolator());
            animator3.start();
    }

    public void doLogin(){
        LoginInfo info = new LoginInfo("429784048","123456");
        RequestCallback<LoginInfo> callback = new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo param) {
                //Toast toast = Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT);;
                //toast.show();
            }

            @Override
            public void onFailed(int code) {
                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onException(Throwable exception) {
                Toast.makeText(LoginActivity.this, "登录异常", Toast.LENGTH_SHORT).show();
            }
        };
        NIMClient.getService(AuthService.class).login(info).setCallback(callback);
    }

    // 保存登陆成功用户的登陆信息，以备下次自动登录
    public void saveLoginLogs(){
        new Thread(){
            @Override
            public void run() {
                DBUtils.LoginLogs(user,mySQLiteHelper,0);
            }
        }.start();
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

