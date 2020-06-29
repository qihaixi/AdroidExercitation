package com.example.adroidexercitation;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adroidexercitation.model.User;

public class AddressActivity extends Activity {
    private RecyclerView contactList;
    private String[] contactNames;
    private LinearLayoutManager layoutManager;
    private LetterView letterView;
    private ContactAdapter adapter;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        contactNames = new String[] {"啊","白菜","计洪", "韦远洪", "刘康富", "严锐", "李海东", "刘子扬", "杨汉华", "123", "456", "789", "陈晓燕", "$6", "左林", "陈圆圆", "老郭", "郭襄", "穆念慈", "东方不败", "梅超风", "林平之", "林远图", "灭绝师太", "段誉", "鸠摩智"};
        contactList = findViewById(R.id.contact_list);
        letterView = findViewById(R.id.letter_view);
        layoutManager = new LinearLayoutManager(this);
        adapter = new ContactAdapter(this, contactNames);

        //接收user信息
        Intent getData=getIntent();
        user = (User)getData.getSerializableExtra("user");

        contactList.setLayoutManager(layoutManager);
        contactList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        contactList.setAdapter(adapter);

        letterView.setCharacterListener(new LetterView.CharacterClickListener() {
            @Override
            public void clickCharacter(String character) {
                layoutManager.scrollToPositionWithOffset(adapter.getScrollPosition(character), 0);
            }

            @Override
            public void clickArrow() {
                layoutManager.scrollToPositionWithOffset(0, 0);
            }
        });

    }

    public void onTheClick(View view){
        int user = contactList.getChildAdapterPosition(view);
        view = contactList.getChildAt(user);
        TextView tv_contact_name = view.findViewById(R.id.contact_name);
        Toast.makeText(this, "点击了" + tv_contact_name.getText().toString(), Toast.LENGTH_SHORT).show();
    }
}
