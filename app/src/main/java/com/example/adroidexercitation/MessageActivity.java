package com.example.adroidexercitation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends Activity {
    private ChatAdapter chatAdapter;
    private ListView lv_chat_dialog;
    private List<PersonChat> personChats = new ArrayList<>();
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg){
            int what = msg.what;
            switch (what) {
                case 1:
                    lv_chat_dialog.setSelection(personChats.size());
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, false);
    }

    // 接收消息
    Observer<List<IMMessage>> incomingMessageObserver =
            new Observer<List<IMMessage>>() {
                @Override
                public void onEvent(List<IMMessage> messages) {
                    // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
                    for(IMMessage message : messages){
                        //tv_receive.setText(message.getContent());
                        PersonChat personChat = new PersonChat();
                        personChat.setMeSend(false);
                        personChat.setChatMessage(message.getContent());
                        personChats.add(personChat);
                        chatAdapter.notifyDataSetChanged();
                        lv_chat_dialog.setSelection(personChats.size()-1);
                    }
                }
            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.message_layout);
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, true);
        lv_chat_dialog = findViewById(R.id.lv_chat_dialog);
        Button btn_chat_message_send = findViewById(R.id.btn_chat_message_send);
        final EditText et_chat_message = findViewById(R.id.et_chat_message);
        chatAdapter = new ChatAdapter(this,personChats);
        lv_chat_dialog.setAdapter(chatAdapter);
        btn_chat_message_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(et_chat_message.getText().toString())){
                    Toast.makeText(MessageActivity.this, "发送内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                PersonChat personChat = new PersonChat();
                personChat.setMeSend(true);
                personChat.setChatMessage(et_chat_message.getText().toString());
                personChats.add(personChat);
                chatAdapter.notifyDataSetChanged();
                handler.sendEmptyMessage(1);
                sendText(et_chat_message.getText().toString());
                et_chat_message.setText("");
            }
        });
    }

    public void sendText(String text){
        String account = "429784048";
        SessionTypeEnum sessionType = SessionTypeEnum.P2P;
        IMMessage textMessage = MessageBuilder.createTextMessage(account, sessionType, text);
        NIMClient.getService(MsgService.class).sendMessage(textMessage, false).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                //Toast.makeText(MessageActivity.this, "成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(int code) {
                //Toast.makeText(MessageActivity.this, "失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onException(Throwable exception) {

            }
        });
    }



}