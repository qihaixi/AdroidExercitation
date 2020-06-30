package com.example.adroidexercitation.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adroidexercitation.address.ContactAdapter;
import com.example.adroidexercitation.address.LetterView;
import com.example.adroidexercitation.database.DBUtils;
import com.example.adroidexercitation.database.MySQLiteHelper;
import com.example.adroidexercitation.main.MainActivity;
import com.example.adroidexercitation.R;
import com.example.adroidexercitation.model.User;

import java.util.ArrayList;


public class ContactsFragment extends Fragment {
    private static final String TAG = "ContactsFragment";
//    private String mTagtext;
    private String mUsername;
    private ArrayList<String> list = new ArrayList<>();
    private MySQLiteHelper mySQLiteHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取Activity传值
        Bundle arguments = getArguments();
        mUsername = arguments.getString("username");
//        mTagtext = arguments.getString(MainActivity.TAG);
        mySQLiteHelper = new MySQLiteHelper(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");

        View inflate = inflater.inflate(R.layout.fragment_contact_temp, null);
        search_for_address();
        String[] contact = new String[list.size()];
        list.toArray(contact);
//        String[] contactNames = new String[] {"啊","白菜","计洪", "韦远洪", "刘康富", "严锐", "李海东", "刘子扬", "杨汉华", "123", "456", "789", "陈晓燕", "$6", "左林", "陈圆圆", "老郭", "郭襄", "穆念慈", "东方不败", "梅超风", "林平之", "林远图", "灭绝师太", "段誉", "鸠摩智"};
        RecyclerView contactList = inflate.findViewById(R.id.contact_list);
        LetterView letterView = inflate.findViewById(R.id.letter_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        final ContactAdapter adapter = new ContactAdapter(getContext(), contact);

        contactList.setLayoutManager(layoutManager);
        contactList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
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


//        TextView tvText = (TextView) inflate.findViewById(R.id.tv_text);
//        if (mTagtext != null && !TextUtils.isEmpty(mTagtext)) {
//            tvText.setText(mTagtext);
//        } else {
//            Log.i(TAG, "onCreateView: mTagText -- " + mTagtext);
//            tvText.setText("Null");
//        }
        return inflate;
    }

    //查找当前用户联系人
    public void search_for_address(){
        new Thread(){
            @Override
            public void run() {
                list = DBUtils.search_for_address(mUsername, mySQLiteHelper);
            }
        }.start();
    }

}
