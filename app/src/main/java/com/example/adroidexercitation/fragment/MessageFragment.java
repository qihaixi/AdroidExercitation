package com.example.adroidexercitation.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.adroidexercitation.R;
import com.example.adroidexercitation.chat.MessageActivity;
import com.example.adroidexercitation.database.DBUtils;
import com.example.adroidexercitation.database.MySQLiteHelper;
import com.example.adroidexercitation.main.MainActivity;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MessageFragment extends Fragment {
    private static final String TAG = "MessageFragment";
    private String mTagtext;
    private List<Data> mData = null;
    private Context mContext;
    private DataAdapter mAdapter = null;
    private ListView list;
    private String ac_username, ta_username, ta_user;
    private int ac_userid;
    private MySQLiteHelper mySQLiteHelper;
    private ArrayList<String> msg_username;
    private boolean is_save;


    @Override
    public void onDestroy() {
        super.onDestroy();
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取Activity传值
        Bundle arguments = getArguments();
        mTagtext = arguments.getString(MainActivity.TAG);
        ac_userid = arguments.getInt("userid");
        ac_username = arguments.getString("username");

        mySQLiteHelper = new MySQLiteHelper(getContext());
        msg_username = new ArrayList<>();

        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, true);
    }

    // 接收消息
    Observer<List<IMMessage>> incomingMessageObserver =
            new Observer<List<IMMessage>>() {
                @Override
                public void onEvent(List<IMMessage> messages) {
                    // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
                    for (IMMessage message : messages) {
                        //接收信息并展示
                        //每收到一条消息就存到数据库中，ac_username，ta_username
                        ta_user = message.getFromAccount();//得到发送方的账户
                        findUsername(ta_user);//得到发送方用户名ta_username
                        saveReceiveText(message.getContent());//保存接收消息
                        if (is_save) {
                            //建立一个数组，存储接收消息的用户名，如果数组里没有这个用户名，便增加一个聊天框，如果有，便修改聊天框并提前
                            if (msg_username.contains(ta_username)) {
                                int index = -1;
                                for (int i = 0; i < mData.size(); i++) {
                                    if (ta_username.equals(mData.get(i).getaName())) {
                                        index = i;
                                    }
                                }
                                mData.remove(index);
                                Data data = new Data(ta_username, message.getContent(), R.drawable.user_head);
                                mData.add(data);
                            } else {
                                msg_username.add(ta_username);
                                Data data = new Data(ta_username, message.getContent(), R.drawable.user_head);
                                mData.add(data);
                            }
                            mAdapter = new DataAdapter((LinkedList<Data>) mData, mContext);
                            list.setAdapter(mAdapter);
                        }
                    }
                }
            };

    private void saveReceiveText(String logs) {
        Mythread_save mythread_save = new Mythread_save(logs);
        mythread_save.start();
        try {
            mythread_save.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public class Mythread_save extends Thread {
        private String logs;
        Mythread_save(String logs){
            this.logs = logs;
        }
        @Override
        public void run() {
            is_save = DBUtils.save_receive_logs(ac_username, ta_username, logs, mySQLiteHelper);
        }
    }

    public void findUsername(String ta_user) {
        ta_user = ta_user.replace("test","");
        int user_id = Integer.parseInt(ta_user);
        Mythread_find mythread_find = new Mythread_find(user_id);
        mythread_find.start();
        try {
            mythread_find.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public class Mythread_find extends Thread {
        private int user_id;
        Mythread_find(int user_id){
            this.user_id = user_id;
        }
        @Override
        public void run() {
            ta_username = DBUtils.find_username(user_id);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            if (requestCode == 1) {
                if (data != null) {
                    String msg_latest_log = data.getExtras().getString("latest_msg");
                    String ta_username = data.getExtras().getString("ta_username");
                    Log.i("test123",msg_latest_log);
                    if (msg_username.contains(ta_username)) {
                        int index = -1;
                        for (int i = 0; i < mData.size(); i++) {
                            if (ta_username.equals(mData.get(i).getaName())) {
                                index = i;
                            }
                        }
                        mData.remove(index);
                        mData.add(new Data(ta_username, msg_latest_log, R.drawable.user_head));
                    } else {
                        msg_username.add(ta_username);
                        mData.add(new Data(ta_username, msg_latest_log, R.drawable.user_head));
                    }
                    mAdapter = new DataAdapter((LinkedList<Data>) mData, mContext);
                    list.setAdapter(mAdapter);
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        final View inflate = inflater.inflate(R.layout.fragment_message, null);
        mContext = getContext();
        list = inflate.findViewById(R.id.list);
        mData = new LinkedList<Data>();
        //这里添加最近会话

        //从数据库中查询最新一条记录并显示
//        searchForLastestMessage();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), MessageActivity.class);
                intent.putExtra("ac_user", "test"+ac_userid);
                intent.putExtra("ta_user",ta_user);
                intent.putExtra("ac_username",ac_username);
                intent.putExtra("ta_username",ta_username);
                startActivityForResult(intent,1);
            }
        });

        return inflate;
    }

}
