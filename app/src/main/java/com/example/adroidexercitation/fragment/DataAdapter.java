package com.example.adroidexercitation.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.adroidexercitation.R;
import com.example.adroidexercitation.view.CircleImageView;

import java.util.LinkedList;

public class DataAdapter extends BaseAdapter {
    private LinkedList<Data> mData;
    private Context mContext;

    public DataAdapter(LinkedList<Data> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_message, null);
        CircleImageView img_icon = convertView.findViewById(R.id.image);
        TextView txt_aName = convertView.findViewById(R.id.tv1);
        TextView txt_aSpeak = convertView.findViewById(R.id.tv2);

//        img_icon.setBackgroundResource(mData.get(position).getaIcon());
        Bitmap bm=BitmapFactory.decodeResource(mContext.getResources(),mData.get(position).getaIcon());
        Bitmap bitmap = CircleImageView.getCroppedBitmap(bm, 400);
        img_icon.setImageBitmap(bitmap);

        txt_aName.setText(mData.get(position).getaName());
        txt_aSpeak.setText(mData.get(position).getaSpeak());
        return convertView;
    }
}