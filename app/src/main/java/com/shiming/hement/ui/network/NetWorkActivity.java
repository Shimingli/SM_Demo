package com.shiming.hement.ui.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.shiming.base.login.PreferenceFileNames;
import com.shiming.base.login.PreferenceKeys;
import com.shiming.hement.R;
import com.shiming.hement.ui.MainActivity;
import com.shiming.hement.ui.base.BaseActivity;
import com.shiming.hement.data.DataManager;
import com.shiming.hement.data.model.TodayBean;
import com.shiming.shangmi.utils.ScanGunKeyEventHelper;

import java.util.ArrayList;
import java.util.HashMap;

import timber.log.Timber;


/**
 * <p>
 *
 * </p>
 *
 * @author shiming
 * @version v1.0
 * @since 2018/11/28 15:23
 */

public class NetWorkActivity extends BaseActivity implements NetWorkView, View.OnClickListener, ScanGunKeyEventHelper.OnScanSuccessListener {

    String key = "b15674dbd34ec00ded57b369dfdabd90";

    NetWorkPresenter mMainPresenter;

    private Button mBtn;
    private EditText mDay;
    private EditText mMonth;
    private RecyclerView mRecyclerView;
    private SMAdapter mSmAdapter;
    private String barCode;
    private ScanGunKeyEventHelper mScanGunKeyEventHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_work);
        mMainPresenter = new NetWorkPresenter(this);
        Timber.tag(getClassName()).i("mMainPresenter   =%s", mMainPresenter);
        mMainPresenter.attachView(this);
        initView();
        initListener();
        mScanGunKeyEventHelper=new ScanGunKeyEventHelper(this);
//        UsbDevice device = (UsbDevice) getIntent().getParcelableExtra(UsbManager.EXTRA_DEVICE);
//
//        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
//        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
//        UsbDevice device = deviceList.get("deviceName");

//        BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
//            public void onReceive(Context context, Intent intent) {
//                String action = intent.getAction();
//
//                if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
//                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//                    if (device != null) {
//                        // call your method that cleans up and closes communication with the device
//                        String s = device.toString();
//                        System.out.println("NetWorkActivity  :"+s);
//                    }
//                }
//            }
//        };
    }


    //    ArrayList<Integer> scannedCodes = new ArrayList<Integer>();
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode != KeyEvent.KEYCODE_ENTER) { //扫码枪以回车为结束
//            scannedCodes.add(keyCode);
//        } else { //结束
//            handleKeyCodes();
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    private void handleKeyCodes() {
//        String s = scannedCodes.toString();
//        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
//        scannedCodes.clear();
//    }
    private void initView() {
        mBtn = (Button) findViewById(R.id.btn);
        mMonth = (EditText) findViewById(R.id.et_month);
        mDay = (EditText) findViewById(R.id.et_day);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
    }

    private void initListener() {
        mBtn.setOnClickListener(this);
        mSmAdapter = new SMAdapter(this, null);
        mRecyclerView.setAdapter(mSmAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void getDataFail(String errorCode, String errorMsg) {
        Toast.makeText(this, errorMsg + errorCode, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(Throwable e) {
        Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void getDataSuccess(ArrayList<TodayBean> result) {
        String s = new Gson().toJson(result);
        Timber.tag(getClassName()).i(s);
        Thread thread = Thread.currentThread();
        Timber.tag(getClassName()).i(thread.toString());
        mSmAdapter.addData(result);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMainPresenter.detachView();
    }

    @Override
    public void onClick(View v) {
        if (TextUtils.isEmpty(mMonth.getText()) || TextUtils.isEmpty(mDay.getText())) {
            Toast.makeText(NetWorkActivity.this, "不能为空", Toast.LENGTH_SHORT).show();
        } else {
            mMainPresenter.loadData(key, mMonth.getText() + "/" + mDay.getText());
        }
    }

    @Override
    public void onScanSuccess(String barcode) {
        barCode = barcode;
        System.out.println("NetWorkActivity   "+barcode);
    }

//    //重写捕捉到扫码枪事件
//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        mScanGunKeyEventHelper.analysisKeyEvent(event);
//        return true;
//    }
}