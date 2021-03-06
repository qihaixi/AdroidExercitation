package com.example.adroidexercitation.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.util.Log;

import com.example.adroidexercitation.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {
    // mysql版本5.6
    private static String driver = "com.mysql.jdbc.Driver";//MySQL 驱动
    // ip地址:mysql端口号，ip地址为电脑ip地址，手机和电脑需连同一个无线网
    private static String url = "jdbc:mysql://172.20.10.4:3326/test?useUnicode=true&characterEncoding=utf8";//MYSQL数据库连接Url
//    private static String url = "jdbc:mysql://192.168.43.19:3326/test?useUnicode=true&characterEncoding=utf8";
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
            String sql = "select * from userlogin where username = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1,user.getUsername());
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
            //查询用户个数（每添加一个用户便把user_id+1，对应网易云信创建的账号）
            //如第二个创建的用户为user2，其user_id为2，在登陆调用dologin时传入参数2，网易云信登陆为test2.
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
        // 创建通讯录表
        db.execSQL("CREATE TABLE IF NOT EXISTS " + user.getUsername() + "(addr_list varchar(20) primary key)");
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
        cursor.close();
        db.close();

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

    // 查找通讯录
    public static ArrayList<String> search_for_address(String username, MySQLiteHelper mySQLiteHelper) {
        SQLiteDatabase db;
        ArrayList<String> list = new ArrayList<>();
        db = mySQLiteHelper.getWritableDatabase();
        Cursor cursor = db.query(username,null,null,null,null,null,null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            list.add(cursor.getString(0));
            while (cursor.moveToNext()) {
                list.add(cursor.getString(0));
            }
        }
        cursor.close();
        db.close();
        return list;
    }

    //查询所有用户
    public static ArrayList<String> find_user(String username){
        Connection conn = getConn();
        PreparedStatement ps;
        ArrayList<String> list = new ArrayList<>();
        try{
            String sql = "select username from userlogin where username<>'" + username + "'";
            ps = conn.prepareStatement(sql);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            while (rs.next()) {
                list.add(rs.getString("username"));
            }
            ps.close();
            conn.close();
            return list;
        } catch (Exception e){
            e.printStackTrace();
            return list;
        }

    }

    //添加用户到通讯录
    public static void add_user(String ac_username, String ta_username, MySQLiteHelper mySQLiteHelper){
        SQLiteDatabase db;
        ContentValues values;
        db = mySQLiteHelper.getWritableDatabase();
        values = new ContentValues();
        values.put("addr_list",ta_username);
        db.insert(""+ac_username,null,values);
//        db.delete(""+ac_username,"addr_list=?",new String[]{""+ta_username});
        db.close();
    }

    //搜索用户id
    public static int select_userid(String ta_username){
        Connection conn = getConn();
        PreparedStatement ps;
        try {
            String sql = "select user_id from userlogin where username=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, ta_username);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            rs.next();
            int result = rs.getInt("user_id");
            ps.close();
            conn.close();
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    //通过id查找用户名
    public static String find_username(int user_id){
        Connection conn = getConn();
        PreparedStatement ps;
        try {
            String sql = "select username from userlogin where user_id=?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, user_id);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            rs.next();
            String result = rs.getString("username");
            ps.close();
            conn.close();
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return "未知用户";
        }
    }

    //新建聊天记录表
    public static void add_chat_logs(String ac_username, String ta_username, MySQLiteHelper mySQLiteHelper) {
        SQLiteDatabase db;
        db = mySQLiteHelper.getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ac_username + "_" + ta_username + "(_id integer primary key autoincrement,chat_info varchar(100) not null,chat_person intrger not null)");
        db.close();
    }

    //保存自己发送的聊天记录
    public static void save_send_logs(String ac_username, String ta_username, String logs, MySQLiteHelper mySQLiteHelper) {
        SQLiteDatabase db;
        db = mySQLiteHelper.getWritableDatabase();
        //chat_person:0代表自己，1代表对方
        db.execSQL("insert into " + ac_username + "_" + ta_username + "(chat_info,chat_person) values(?,?)",new Object[]{logs, 0});
        db.close();
    }

    //保存接收到的聊天记录
    public static boolean save_receive_logs(String ac_username, String ta_username, String logs, MySQLiteHelper mySQLiteHelper) {
        SQLiteDatabase db;
        db = mySQLiteHelper.getWritableDatabase();
        //chat_person:0代表自己，1代表对方
        try{
            db.execSQL("insert into " + ac_username + "_" + ta_username + "(chat_info,chat_person) values(?,?)",new Object[]{logs, 1});
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        db.close();
        return true;
    }

    //查询聊天记录
    public static List<List<String>> select_ChatLogs(String ac_username, String ta_username, MySQLiteHelper mySQLiteHelper){
        SQLiteDatabase db;
        List<List<String>> list = new ArrayList<>();
        List<String> msg_logs = new ArrayList<>();
        List<String> msg_person = new ArrayList<>();
        list.add(msg_logs);
        list.add(msg_person);
        db = mySQLiteHelper.getWritableDatabase();
        Cursor cursor = db.query(ac_username + "_" + ta_username,null,null,null,null,null,null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            msg_logs.add(cursor.getString(cursor.getColumnIndex("chat_info")));
            msg_person.add(Integer.toString(cursor.getInt(cursor.getColumnIndex("chat_person"))));
            while (cursor.moveToNext()) {
                msg_logs.add(cursor.getString(cursor.getColumnIndex("chat_info")));
                msg_person.add(Integer.toString(cursor.getInt(cursor.getColumnIndex("chat_person"))));
            }
        }
        cursor.close();
        db.close();
        return list;
    }

    public static String search_lastest_message(String ac_username, String ta_username, MySQLiteHelper mySQLiteHelper){
        SQLiteDatabase db;
        String logs = "";
        db = mySQLiteHelper.getWritableDatabase();
        Cursor cursor = db.query(ac_username + "_" + ta_username,null,null,null,null,null,null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            logs = cursor.getString(cursor.getColumnIndex("chat_info"));
            while (cursor.moveToNext()) {
                logs = cursor.getString(cursor.getColumnIndex("chat_info"));
            }
        }
        cursor.close();
        db.close();
        return logs;
    }

    public static void change_password(String username, String new_password){
        Connection conn = getConn();
        PreparedStatement ps;
        try {
            String sql = "update userlogin set password=? where username=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, new_password);
            ps.setString(2, username);
            ps.execute();
            ps.close();
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}