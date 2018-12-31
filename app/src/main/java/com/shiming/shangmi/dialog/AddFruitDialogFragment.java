package com.shiming.shangmi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.shiming.hement.R;
import com.shiming.shangmi.data.model.GvBeans;
import com.shiming.shangmi.utils.ResourcesUtils;


import java.text.DecimalFormat;

import timber.log.Timber;

/**
 * Created by highsixty on 2018/3/15.
 * mail  gaolulin@sunmi.com
 */

public class AddFruitDialogFragment extends AppCompatDialogFragment implements View.OnClickListener {

    private static final String TAG ="AddFruitDialogFragment" ;
    private Button btnCancel;
    private Button btnAdd;
    private int Flag = 0; //0苹果 、1梨子 、2香蕉、 3火龙果
    private TextView tvDes;
    private ImageView ivLogo;
    private DecimalFormat decimalFormat = new DecimalFormat("0.000");
    private DecimalFormat meonyFormat = new DecimalFormat("0.00");

    private float price;
    private String name;
    private GvBeans gvBeans;
    private String total = "0.00";//总价
    boolean isShow = false;//防多次点击
    static int defaultNet = -10;
    private int now_net = defaultNet;
    public static float[] goodsAmount={
            16.66f,10.01f,12.85f,18.59f

    };
    private LinearLayout mLlIcons;
    private LinearLayout mLlBing;

    public AddFruitDialogFragment() {
        super();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.addfruit_dialog_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initAction();
        initData();
        mLlIcons.removeAllViews();
        mLlBing.removeAllViews();
        addIcon(mLlIcons,"大");
        addIcon(mLlIcons,"小");
        addIcon(mLlBing,"少冰");
        addIcon(mLlBing,"多冰");
    }
    private void addIcon(ViewGroup parent, final String text) {
        final TextView icon = new TextView(getActivity());
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dip2px(getActivity(), 50),
                dip2px(getActivity(), 50));
        iconParams.setMargins(10, 0, dip2px(getActivity(), 10), 0);
        icon.setLayoutParams(iconParams);
        icon.setText(text);


        Object tag = icon.getTag();
        if (tag!=null&&tag.equals("1")){
            icon.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));
        }else{
            icon.setTextColor(ContextCompat.getColor(getActivity(),R.color.app_color_theme));
        }
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.tag(TAG).i(" 点击了什么   =%s",text);
                icon.setTag("1");
//                icon.notify();
            }
        });
        parent.addView(icon);
    }
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    private void initView(View view) {
        btnAdd = (Button) view.findViewById(R.id.btn_add);
        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        tvDes = (TextView) view.findViewById(R.id.tv_des);
        ivLogo = (ImageView) view.findViewById(R.id.iv_logo);
        //规格
        mLlIcons = view.findViewById(R.id.ll_guige);
        //加冰
        mLlBing = view.findViewById(R.id.ll_bing);


    }

    private void initAction() {
        btnAdd.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnAdd.setEnabled(false);
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return true;
            }
        });
    }

    private void initData() {
        Bundle bundle = getArguments();
        int net=bundle.getInt("net", 0);
        name = gvBeans.getName();
        price = Float.parseFloat(gvBeans.getPrice().substring(1));
        tvDes.setText(gvBeans.getName());
        ivLogo.setImageResource(gvBeans.getLogo());
        Timber.tag(TAG).i(" gvBeas  =%s",gvBeans);
        if(net!=0) {
            btnAdd.setEnabled(true);
            btnAdd.setAlpha(1);
            btnCancel.setAlpha(1);
            total = meonyFormat.format(net * price / 1000);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_add:

                if (listener != null) {
                    listener.onAddResult(total, name);
                }
                dismiss();
                break;
            default:
                break;
        }
    }

    /**
     * 防抖动
     *
     * @param net
     * @return
     */
    private boolean unShake(int net) {
        if (Math.abs(net - now_net) < -defaultNet) {
            return false;
        }
        now_net = net;
        return true;
    }


    public void update(int status, int net) {
        if (status == 1 && net > 0) {
            btnAdd.setEnabled(true);
            btnAdd.setAlpha(1);
            btnCancel.setAlpha(1);
            if (!unShake(net)) {
                return;
            }
        } else {
            now_net = defaultNet;
            btnAdd.setEnabled(false);
            btnCancel.setAlpha(0.6f);
            btnAdd.setAlpha(0.6f);
        }
        Log.d("SUNMI", "update: ----------------->" + decimalFormat.format(net * 1.0f / 1000));
        total = meonyFormat.format(net * price / 1000);

    }
    public void updateView(GvBeans gvBeans){
        this.gvBeans=gvBeans;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (isShow) {
            return;
        }
        super.show(manager, tag);
        isShow = true;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        isShow = false;
    }

    private AddListener listener = null;

    public void setListener(AddListener listener) {
        this.listener = listener;
    }

    public interface AddListener {
        void onAddResult(String total, String name);
    }
}
