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
    private ArrayList<String> msg_receive;
    private String ta_username, ta_user;
    private int ac_userid;

    @Override
    public void onDestroy() {
        super.onDestroy();
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mTagtext = arguments.getString(MainActivity.TAG);
        ac_userid = arguments.getInt("userid");
        msg_receive = new ArrayList<>();


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
                        ta_user = message.getFromAccount();
                        findUsername(ta_user);
//                        Log.i("sql",ta_username);
                        msg_receive.add(message.getContent());
                        if (msg_receive.size() == 1) {
                            mData.add(new Data(ta_username, msg_receive.get(msg_receive.size()-1), R.drawable.user_head));
                        } else {
                            mData.remove(0);
                            mData.add(new Data(ta_username, msg_receive.get(msg_receive.size()-1), R.drawable.user_head));
                        }
                        mAdapter = new DataAdapter((LinkedList<Data>) mData, mContext);
                        list.setAdapter(mAdapter);
                    }

                }
            };

    public void findUsername(String ta_user) {
        ta_user = ta_user.replace("test","");
        int user_id = Integer.parseInt(ta_user);
        Mythread mythread = new Mythread(user_id);
        mythread.start();
        try {
            mythread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public class Mythread extends Thread {
        private int user_id;
        Mythread(int user_id){
            this.user_id = user_id;
        }
        @Override
        public void run() {
            ta_username = DBUtils.find_username(user_id);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        View inflate = inflater.inflate(R.layout.fragment_message, null);
        mContext = getContext();
        list = inflate.findViewById(R.id.list);
        mData = new LinkedList<Data>();
        mAdapter = new DataAdapter((LinkedList<Data>) mData, mContext);
        list.setAdapter(mAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), MessageActivity.class);
                intent.putExtra("chat",msg_receive);
                intent.putExtra("ac_user", "test"+ac_userid);
                intent.putExtra("ta_user",ta_user);
                intent.putExtra("ta_username",ta_username);
                startActivity(intent);
            }
        });

        return inflate;
    }

}
