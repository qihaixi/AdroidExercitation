package com.example.adroidexercitation.chat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.example.adroidexercitation.main.MainActivity;
import com.example.adroidexercitation.R;
import com.example.adroidexercitation.model.User;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MessageActivity extends Activity {
    //返回
    private ImageButton fanhui;
    //相机
    private ImageButton xiangji;
    private ImageButton imgIcon;//显示拍照照片，暂时没用
    private Uri imageUri;   //  通用资源标志符
    public static final int TAKE_PHOTO =1;  //  TAKE_PHOTO来作为case处理图片的标识

    //定义当前用户和对方用户
    private String ac_user;
    private String ta_user;
    private String ta_username;
    private ArrayList<String> msg_receiver;

    private TextView title_text;

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
                    if(!ta_user.equals(ac_user)) {
                        for(IMMessage message : messages){
                        //tv_receive.setText(message.getContent());
                        PersonChat personChat = new PersonChat();
                        personChat.setMeSend(false);
                        personChat.setChatMessage(message.getContent());
                        personChats.add(personChat);
                        chatAdapter.notifyDataSetChanged();
                        lv_chat_dialog.setSelection(personChats.size()-1);
                    }}
                }
            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_message);
        Intent intent = getIntent();
        if (intent != null) {
            ac_user = intent.getStringExtra("ac_user");
            ta_user = intent.getStringExtra("ta_user");
//            user = (User)intent.getSerializableExtra("user");
            ta_username = intent.getStringExtra("ta_username");
            msg_receiver = intent.getStringArrayListExtra("chat");
        }
        //显示对方发来的消息
        if (msg_receiver != null) {
            for (int i=0; i<msg_receiver.size(); i++) {
                PersonChat personChat = new PersonChat();
                personChat.setMeSend(false);
                personChat.setChatMessage(msg_receiver.get(i));
                personChats.add(personChat);
            }
        }


        //初始化云信sdk
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, true);

        lv_chat_dialog = findViewById(R.id.lv_chat_dialog);
        title_text = findViewById(R.id.title_text);
        Button btn_chat_message_send = findViewById(R.id.btn_chat_message_send);
        final EditText et_chat_message = findViewById(R.id.et_chat_message);

        chatAdapter = new ChatAdapter(this,personChats);
        lv_chat_dialog.setAdapter(chatAdapter);

        //设置聊天标题
        title_text.setText(ta_username);
        //发送消息按钮实现
        btn_chat_message_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(et_chat_message.getText().toString())){
                    Toast.makeText(MessageActivity.this, "发送内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                PersonChat personChat = new PersonChat();
                //true代表自己发送
                personChat.setMeSend(true);
                //从输入框中得到发送内容
                personChat.setChatMessage(et_chat_message.getText().toString());
                personChats.add(personChat);
                //刷新ListView
                chatAdapter.notifyDataSetChanged();
                handler.sendEmptyMessage(1);
                //发送消息
                sendText(et_chat_message.getText().toString());
                //清空输入框
                et_chat_message.setText("");
            }
        });

        //聊天页面左上角返回功能
        fanhui = findViewById(R.id.fanhui);
        fanhui.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //设置显式Intent实现从MessageActivity到MainActivity的跳转
//                Intent intent =  new Intent(MessageActivity.this,MainActivity.class);
//                intent.putExtra("user","user");
//                startActivity(intent);
//                finish();
                //无需跳转，直接结束当前页面
                MessageActivity.this.finish();
            }
        });

        //聊天页面拍照功能
        xiangji = (ImageButton)findViewById(R.id.xiangji);
        xiangji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建时间对象作为拍照照片文件命名，防止多张照片覆盖
                SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
                Date curDate = new Date(System.currentTimeMillis());
                String str = format.format(curDate);
                /**
                 *          创建File对象，用于存储拍照后的照片
                 *          第一个参数：  是这张照片存放在手机SD卡的对应关联缓存应用
                 *          第二个参数：  这张图片的命名
                 */
                File outputImage = new File(getExternalCacheDir(),str+".jpg");
                try {
                    if (outputImage.exists()){          //  检查与File对象相连接的文件和目录是否存在于磁盘中
                        outputImage.delete();           //  删除与File对象相连接的文件和目录
                    }
                    outputImage.createNewFile();        //  如果与File对象相连接的文件不存在，则创建一个空文件
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= 24){       //  如果运行设备的系统版本高于 Android7.0
                    /**
                     *          将File对象转换成一个封装过的Uri对象
                     *          第一个参数：  要求传入Context参数
                     *          第二个参数：  可以是任意唯一的字符串
                     *          第三个参数：  刚刚创建的File对象
                     */
                    imageUri = FileProvider.getUriForFile(MessageActivity.this,"com.example.adroidexercitation.chat.fileprovider",outputImage);
                }else{                  //  如果运行设备的系统版本低于 Android7.0
                    //  将File对象转换成Uri对象，这个Uri对象表示着 str + ".jpg" 这张图片的本地真实路径
                    imageUri = Uri.fromFile(outputImage);
                }
                /**
                 *      启动相机程序
                 */
                //  将Intent的action指定为 拍照到指定目录 —— android.media.action.IMAGE_CAPTURE
                Intent intent = new Intent("android.media.action.STILL_IMAGE_CAMERA");
                //  指定图片的输出地址
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                /**
                 *      在通过startActivityForResult()，来启动活动，因此拍完照后会有结果返回到 onActivityResult()方法中
                 */
                startActivityForResult(intent,TAKE_PHOTO);
            }
        });
    }

    //第三方云信即时通信
    public void sendText(String text){
        //判断当前用户，判断当前用户点击对象的用户
        String account = ta_user;
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

    //用来显示拍照图片，暂时没用
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK){       //  当拍照成功后，会返回一个返回码，这个值为 -1 — RESULT_OK
                    try{
                        //  根据Uri找到这张照片的资源位置，将它解析成Bitmap对象，然后将把它设置到imageView中显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        imgIcon.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

//	private void initView() {
//		xiangji = findViewById(R.id.xiangji);
//		imgIcon = findViewById(R.id.imgIcon);
//	}

}