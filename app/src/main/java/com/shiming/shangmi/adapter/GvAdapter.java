package com.shiming.shangmi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.shiming.hement.R;
import com.shiming.shangmi.data.model.GvBeans;
import com.shiming.shangmi.utils.ResourcesUtils;

import java.util.List;


/**
 * Created by highsixty on 2018/3/13.
 * mail  gaolulin@sunmi.com
 */

public class GvAdapter extends BaseAdapter {

    private Context mContext;
    private List<GvBeans> mGvBeans;
    private int mFlag;

    public GvAdapter(Context context, List<GvBeans> gvBeans, int flag) {
        this.mContext = context;
        this.mGvBeans = gvBeans;
        this.mFlag = flag;
    }

    @Override
    public int getCount() {
        return mGvBeans == null ? 0 : mGvBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return mGvBeans == null ? null : mGvBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHold hold = null;
        if (null == convertView) {
            hold = new ViewHold();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.gv_menus_layout, null);
            hold.ivPhoto = (ImageView) convertView.findViewById(R.id.iv_photo);
            hold.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            hold.tvPrice = (TextView) convertView.findViewById(R.id.tv_price);
            hold.tvUnit = (TextView) convertView.findViewById(R.id.tv_unit);
            convertView.setTag(hold);
        } else {
            hold = (ViewHold) convertView.getTag();
        }
        hold.ivPhoto.setImageResource(mGvBeans.get(position).getImgId());
        hold.tvName.setText(mGvBeans.get(position).getName());
        hold.tvPrice.setText(mGvBeans.get(position).getPrice());
        switch (mFlag){
            case 1:
                hold.tvUnit.setText("/"+ ResourcesUtils.getString(mContext,R.string.units_tin));
                break;
            case 2:
                hold.tvUnit.setText("/"+ ResourcesUtils.getString(mContext,R.string.units_kg));
                break;
            case 3:
                hold.tvUnit.setText("/"+ ResourcesUtils.getString(mContext,R.string.units_bag));
                break;
        }

        return convertView;
    }

    public class ViewHold {
        private ImageView ivPhoto;
        private TextView tvName;
        private TextView tvPrice;
        private TextView tvUnit;
    }

}
