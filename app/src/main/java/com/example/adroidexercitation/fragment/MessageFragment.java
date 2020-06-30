package com.example.adroidexercitation.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.adroidexercitation.R;
import com.example.adroidexercitation.main.MainActivity;

import java.util.LinkedList;
import java.util.List;

public class MessageFragment extends Fragment {
    private static final String TAG = "MessageFragment";
    private String mTagtext;
    private List<Data> mData = null;
    private Context mContext;
    private DataAdapter mAdapter = null;
    private ListView list;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mTagtext = arguments.getString(MainActivity.TAG);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: ");
        View inflate = inflater.inflate(R.layout.fragment_message, null);
        mContext = getContext();
        list = inflate.findViewById(R.id.list);
        mData = new LinkedList<Data>();
        mData.add(new Data("用户名", "聊天内容", R.drawable.user_head));
        mData.add(new Data("用户名", "聊天内容", R.drawable.user_head));

        mAdapter = new DataAdapter((LinkedList<Data>) mData, mContext);
        list.setAdapter(mAdapter);

        return inflate;
    }
}
