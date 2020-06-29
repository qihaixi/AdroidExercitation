package com.example.adroidexercitation.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import com.example.adroidexercitation.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
            String sql = "select * from userlogin where username = ? or mail = ?";
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
                    int result = rs.getInt("user_id");
                    ps.close();
                    conn.close();
                    return result;
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
            //查询用户个数
            String sql = "select * from userlogin";
            ps = conn.prepareStatement(sql);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            rs.last();
            int row = rs.getRow() + 1;
            //查询用户名是否重复
            sql= "select * from userlogin where username = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1,user.getUsername());
            ps.execute();
            ResultSet rs1 = ps.getResultSet();
            if (!rs1.next()) {
                sql= "select * from userlogin where mail = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1,user.getMail());
                ps.execute();
                ResultSet rs2 = ps.getResultSet();
                if (!rs2.next()) {
                    sql= "insert into userlogin values(?,?,?,?,?)";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1,user.getUsername());
                    ps.setString(2,user.getMail());
                    ps.setString(3,user.getSex());
                    ps.setString(4,user.getPassword());
                    ps.setInt(5,row);
                    int res = ps.executeUpdate();
                    ps.close();
                    conn.close();
                    if (res == 0) {
                        // 插入错误
                        return -3;
                    }else {
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

    // 使用sqlite保存用户登录信息，当用户没有注销时，is_log_out设置为0，注销时设置为1
    public static void LoginLogs(User user,MySQLiteHelper mySQLiteHelper,int is_logout){
        SQLiteDatabase db;
        db = mySQLiteHelper.getWritableDatabase();
        Cursor cursor = db.query("userLogs",null,null,null,null,null,null);
        if (is_logout == 0) {
            if (cursor.getCount() == 0) {
                db.execSQL("insert into userLogs values(0,?,?,0)",new Object[]{user.getUsername(),user.getPassword()});
            } else {
                db.execSQL("update userLogs set login_account=?,login_password=?,is_log_out=0 where _id=0",new Object[]{user.getUsername(),user.getPassword()});
            }
        } else {
            db.execSQL("update userLogs set is_log_out=1 where _id=0");
        }

    }

    // 使用sqlite进行自动登录（数据库在本地，速度较快）
    public static User AutoLogin(MySQLiteHelper mySQLiteHelper){
        SystemClock.sleep(1500);
        SQLiteDatabase db;
        db = mySQLiteHelper.getWritableDatabase();
        User user = new User();
        user.setUsername("");
        user.setPassword("");
        Cursor cursor = db.query("userLogs",null,null,null,null,null,null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            if (cursor.getInt(cursor.getColumnIndex("is_log_out")) == 1) {
                user.setUsername(cursor.getString(cursor.getColumnIndex("login_account")));
            } else {
                user.setUsername(cursor.getString(cursor.getColumnIndex("login_account")));
                user.setPassword(cursor.getString(cursor.getColumnIndex("login_password")));
            }
        }
        cursor.close();
        db.close();
        return user;
    }

    // 通过mysql实现自动登录（数据库在服务器，速度较慢）
    public static User AutoLogin1(){
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