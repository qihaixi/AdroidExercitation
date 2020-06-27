package com.example.adroidexercitation;

import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class DBUtils {
    // mysql版本5.6
    private static String driver = "com.mysql.jdbc.Driver";//MySQL 驱动
    // ip地址:mysql端口号，ip地址为电脑ip地址，手机和电脑需连同一个无线网
    private static String url = "jdbc:mysql://172.20.10.3:3326/test?useUnicode=true&characterEncoding=utf8";//MYSQL数据库连接Url
    //private static String url = "jdbc:mysql://172.20.69.229:3326/test";
    private static String user = "root";//用户名
    private static String password = "123456";//密码

    private static Connection getConn(){
        Connection connection = null;
        try{
            Class.forName(driver);// 动态加载类
            connection = DriverManager.getConnection(url,user,password);
        }catch (Exception e){
            e.printStackTrace();
        }
        return connection;
    }
    //登录
    public static int Login(User user) {
        Connection conn = getConn();
        PreparedStatement ps;
        try {
            SystemClock.sleep(1500);
            String sql = "select password from userlogin where username = ? or mail = ?";
            ps = conn.prepareStatement(sql);
            // 统一由username接收
            ps.setString(1,user.getUsername());
            ps.setString(2,user.getUsername());
            ps.execute();
            ResultSet rs = ps.getResultSet();
            if (rs.next()) {
                if (!rs.getString("password").equals(user.getPassword())) {
                    ps.close();
                    conn.close();
                    return 0;
                } else {
                    ps.close();
                    conn.close();
                    return 1;
                    }
            }else{
                ps.close();
                conn.close();
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -2;
        }
    }

    //注册
    public static int Signup(User user) {
        Connection conn = getConn();
        PreparedStatement ps;
        try {
            String sql= "select * from userlogin where username = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1,user.getUsername());
            ps.execute();
            ResultSet rs = ps.getResultSet();
            if (!rs.next()) {
                sql= "select * from userlogin where mail = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1,user.getMail());
                ps.execute();
                ResultSet rs2 = ps.getResultSet();
                if (!rs2.next()) {
                    sql= "insert into userlogin values(?,?,?,?)";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1,user.getUsername());
                    ps.setString(2,user.getMail());
                    ps.setString(3,user.getSex());
                    ps.setString(4,user.getPassword());
                    int res = ps.executeUpdate();
                    if (res == 0) {
                        // 插入错误
                        ps.close();
                        conn.close();
                        return -3;
                    }else {
                        ps.close();
                        conn.close();
                        // 成功注册
                        return 1;
                    }
                }else {
                    // 邮箱已被注册
                    ps.close();
                    conn.close();
                    return -1;
                }
            }else{
                // 用户名已存在
                ps.close();
                conn.close();
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 连接超时
            return -2;
        }
    }

    // 自动登录
    public static User AutoLogin(){
        SystemClock.sleep(1500);
        Connection conn = getConn();
        PreparedStatement ps;
        User user = new User();
        user.setUsername("");
        user.setPassword("");
        try{
            String sql = "select * from login_logs where loginId=0";
            ps = conn.prepareStatement(sql);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            if (rs.next()) {
                if (rs.getInt("is_log_out") == 0) {
                    user.setUsername(rs.getString("login_account"));
                    user.setPassword(rs.getString("login_password"));
                    ps.close();
                    conn.close();
                    return user;
                }
            }
            ps.close();
            conn.close();
            return user;
        }catch (Exception e){
            e.printStackTrace();
            return user;
        }
    }

}