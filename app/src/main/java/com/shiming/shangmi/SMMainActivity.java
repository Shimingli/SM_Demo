package com.shiming.shangmi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shiming.base.login.PreferenceKeys;
import com.shiming.base.utils.QMUIStatusBarHelper;
import com.shiming.hement.R;
import com.shiming.hement.ui.MainActivity;
import com.shiming.hement.ui.base.BaseActivity;
import com.shiming.shangmi.adapter.GvAdapter;
import com.shiming.shangmi.adapter.MenusAdapter;
import com.shiming.shangmi.data.Config;
import com.shiming.shangmi.data.model.GoodsCode;
import com.shiming.shangmi.data.model.GvBeans;
import com.shiming.shangmi.data.model.MenusBean;
import com.shiming.shangmi.dialog.AddFruitDialogFragment;
import com.shiming.shangmi.doublescreen.BasePresentationHelper;
import com.shiming.shangmi.doublescreen.VideoDisplay;
import com.shiming.shangmi.utils.ResourcesUtils;
import com.shiming.shangmi.utils.ScreenManager;
import com.shiming.shangmi.utils.ToastUtils;
import com.shiming.shangmi.weight.MyGridView;
import com.shiming.shangmi.weight.QMUITopBar;
import com.sunmi.extprinterservice.ExtPrinterService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import woyou.aidlservice.jiuiv5.IWoyouService;

import static com.shiming.base.login.PreferenceFileNames.APP_CONFIG;

/**
 * <p>
 * 35dp 商米系统的导航栏高度
 * </p>
 *
 * @author shiming
 * @version v1.0
 * @since 2018/12/12 11:21
 */

public class SMMainActivity extends BaseActivity {

    private QMUITopBar mTopBar;
    private int mStatusbarHeight;
    public static boolean isVertical = false;
    private List<MenusBean> menus = new ArrayList<>();
    private IWoyouService woyouService = null;//横屏台式 打印服务
    private ExtPrinterService extPrinterService = null;//k1 打印服务
    private PrinterPresenter printerPresenter;
    private ScreenManager screenManager = ScreenManager.getInstance();
    private VideoDisplay videoDisplay = null;
    private String goods_data;
    private TextView tvPrice;
    private TextView btnClear;
    private RelativeLayout rtlEmptyShopcar, rl_no_goods;
    private LinearLayout llyShopcar, ll_drinks, ll_snacks, ll_fruits, ll_vegetables, main_ll_pay;

    private ImageView ivCar;
    private RelativeLayout rlCar;
    private TextView tvCar, tvCarMoeny;
    private TextView tvVipPay, tvVipK1Pay;

    private Button btnPay;//去付款
    private LinearLayout llK1ShoppingCar;


    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private Button btnMore;//更多功能
    private TextView tv_face_pay;//去付款
    private ListView lvMenus;
    private FrameLayout flShoppingCar;
    private MyGridView gvDrink;
    private MyGridView gvFruit;
    private MyGridView gv_snacks;
    private MyGridView gv_vegetables;

    private List<GvBeans> mDrinksBean = new ArrayList<>();
    private List<GvBeans> mFruitsBean = new ArrayList<>();
    private List<GvBeans> mSnacksBean = new ArrayList<>();
    private List<GvBeans> mVegetablesBean = new ArrayList<>();
    private GvAdapter drinkAdapter;
    private GvAdapter fruitAdapter;
    private GvAdapter snackAdapter;
    private GvAdapter vegetableAdapter;
    private MenusAdapter menusAdapter;
    SoundPool soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
    private AddFruitDialogFragment dialogFragment = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sm_demo);
        QMUIStatusBarHelper.translucent(this);
        initPrint();
        initView();
        initData();

        initAction();

    }

    private void initAction() {
        gvDrink.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                showData(mDrinksBean,position);
            }
        });
        gv_snacks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showData(mSnacksBean,position);
            }
        });
        gv_vegetables.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showData(mVegetablesBean,position);
            }
        });
        gvFruit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                showData(mFruitsBean,position);
            }
        });
    }

    private void showData(List<GvBeans> vegetablesBean, int position) {

        String name= vegetablesBean.get(position).getName();
        ToastUtils.toast(SMMainActivity.this,"点击了什么:::"+name);
        Timber.tag(getClassName()).i("点击 =%s", name);
        Bundle bundle = new Bundle();
        bundle.putInt("FLAG", position);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getSupportFragmentManager(), "AddFruitDialogFragment");
        dialogFragment.updateView(vegetablesBean.get(position));
    }


    private void initData() {
        screenManager.init(this);
        Display[] displays = screenManager.getDisplays();
        Timber.tag(getClassName()).i("屏幕数量 =%d", displays.length);
        for (int i = 0; i < displays.length; i++) {
            Timber.tag(getClassName()).i("屏幕 =%s", displays[i]);
        }
        //屏幕数量大于1 ，同时呢 这个是那种坐着的收银机器，不是那种简单的
        if (displays.length > 1 && !isVertical) {
            videoDisplay = new VideoDisplay(this, displays[1], Environment.getExternalStorageDirectory().getPath() + "/xfxb/video_01.mp4");
            // 后面的屏幕 有可能不一样的情况产生
            // videoMenuDisplay = new VideoMenuDisplay(this, displays[1], Environment.getExternalStorageDirectory().getPath() + "/video_02.mp4");
            // textDisplay = new TextDisplay(this, displays[1]);
        }

        mDrinksBean.clear();
        for (GvBeans gvBeans : GoodsCode.getInstance().getDrinks()) {
            mDrinksBean.add(gvBeans);
        }
        mFruitsBean.clear();
        for (GvBeans gvBeans : GoodsCode.getInstance().getFruits()) {
            mFruitsBean.add(gvBeans);
        }
        mSnacksBean.clear();
        for (GvBeans gvBeans : GoodsCode.getInstance().getSnacks()) {
            mSnacksBean.add(gvBeans);
        }
        mVegetablesBean.clear();
        for (GvBeans gvBeans : GoodsCode.getInstance().getVegetables()) {
            mVegetablesBean.add(gvBeans);
        }

        drinkAdapter = new GvAdapter(this, mDrinksBean, 1);
        gvDrink.setAdapter(drinkAdapter);
        fruitAdapter = new GvAdapter(this, mFruitsBean, 2);
        gvFruit.setAdapter(fruitAdapter);

        snackAdapter = new GvAdapter(this, mSnacksBean, 3);
        gv_snacks.setAdapter(snackAdapter);
        vegetableAdapter = new GvAdapter(this, mVegetablesBean, 2);
        gv_vegetables.setAdapter(vegetableAdapter);
        menus.clear();
        tvPrice.setText(ResourcesUtils.getString(this, R.string.units_money_units) + "0.00");

        menusAdapter = new MenusAdapter(this, menus);
        lvMenus.setAdapter(menusAdapter);

        soundPool.load(SMMainActivity.this, R.raw.audio, 1);// 1
        soundPool.load(SMMainActivity.this, R.raw.alipay, 1);// 2


        dialogFragment = new AddFruitDialogFragment();
        dialogFragment.setListener(new AddFruitDialogFragment.AddListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onAddResult(String total, String name) {
                MenusBean bean = new MenusBean();
                bean.setId("" + (menus.size() + 1));
                bean.setMoney(ResourcesUtils.getString(SMMainActivity.this, R.string.units_money_units) + total);
                bean.setName(name);
                menus.add(bean);
                float price = 0.00f;
                for (MenusBean bean1 : menus) {
                    price = price + Float.parseFloat(bean1.getMoney().substring(1));
                }
                tvPrice.setText(ResourcesUtils.getString(SMMainActivity.this, R.string.units_money_units) + decimalFormat.format(price));
                menusAdapter.update(menus);
                buildMenuJson(menus, decimalFormat.format(price));
            }
        });
    }

    private void buildMenuJson(List<MenusBean> menus, String price) {
        try {
            JSONObject data = new JSONObject();
            data.put("title", "Sunmi " + ResourcesUtils.getString(this, R.string.menus_title));
            JSONObject head = new JSONObject();
            head.put("param1", ResourcesUtils.getString(this, R.string.menus_number));
            head.put("param2", ResourcesUtils.getString(this, R.string.menus_goods_name));
            head.put("param3", ResourcesUtils.getString(this, R.string.menus_unit_price));
            data.put("head", head);
            data.put("flag", "true");
            JSONArray list = new JSONArray();
            for (int i = 0; i < menus.size(); i++) {
                JSONObject listItem = new JSONObject();
                listItem.put("param1", "" + (i + 1));
                listItem.put("param2", menus.get(i).getName());
                listItem.put("param3", menus.get(i).getMoney());
                list.put(listItem);
            }
            data.put("list", list);
            JSONArray KVPList = new JSONArray();
            JSONObject KVPListOne = new JSONObject();
            KVPListOne.put("name", ResourcesUtils.getString(this, R.string.shop_car_total) + " ");
            KVPListOne.put("value", price);
            JSONObject KVPListTwo = new JSONObject();
            KVPListTwo.put("name", ResourcesUtils.getString(this, R.string.shop_car_offer) + " ");
            KVPListTwo.put("value", "0.00");
            JSONObject KVPListThree = new JSONObject();
            KVPListThree.put("name", ResourcesUtils.getString(this, R.string.shop_car_number) + " ");
            KVPListThree.put("value", "" + menus.size());
            JSONObject KVPListFour = new JSONObject();
            KVPListFour.put("name", ResourcesUtils.getString(this, R.string.shop_car_receivable) + " ");
            KVPListFour.put("value", price);
            KVPList.put(0, KVPListOne);
            KVPList.put(1, KVPListTwo);
            KVPList.put(2, KVPListThree);
            KVPList.put(3, KVPListFour);
            data.put("KVPList", KVPList);
            //需要使用打印机打印的商品的数据
            goods_data = data.toString();
            Timber.tag(getClassName()).i("需要使用打印机打印的商品的数据  =%s",goods_data);
//            if (videoMenuDisplay != null && !videoMenuDisplay.isShow) {
//                videoMenuDisplay.show();
//                videoMenuDisplay.update(menus, data.toString());
//            } else if (null != videoMenuDisplay) {
//                videoMenuDisplay.update(menus, data.toString());
//            }
            // 购物车有东西

            if (isVertical) {
                tvCarMoeny.setText(ResourcesUtils.getString(R.string.units_money_units) + price);
                tvCar.setText(menus.size() + "");
                tvCar.setVisibility(View.VISIBLE);
                ivCar.setImageResource(R.drawable.car_white);
                btnPay.setBackgroundColor(Color.parseColor("#FC5436"));
//                if (bottomSheetLayout.isSheetShowing()) {
//                    menusAdapter.notifyDataSetChanged();
//                    lvMenus.setSelection(menusAdapter.getCount() - 1);
//                    TextView tvPrice = bottomSheetLayout.findViewById(R.id.main_tv_price);
//                    tvPrice.setText(tvCarMoeny.getText().toString());
//                }
            } else {
                llyShopcar.setVisibility(View.VISIBLE);
                rtlEmptyShopcar.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playSound(final int payMode) {
        Observable.create(
                new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> emitter) {
                        soundPool.play(1, 1, 1, 10, 0, 1);
                        if (payMode == 2) {
                            soundPool.play(2, 1, 1, 10, 0, 1);
                        }
                    }
                }
        ).observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe();

    }

    private void initPrint() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;// 屏幕宽度 1920
        int height = dm.heightPixels;// 屏幕高度 1011
        Timber.tag(getClassName()).i(" 屏幕宽度 =%s", width);
        Timber.tag(getClassName()).i(" 屏幕高度 =%s", height);
        isVertical = height > width;
        if (isVertical) {
        } else {
            connectPrintService();
        }
    }



    private void initView() {
        mTopBar = findViewById(R.id.topbar);
        mTopBar.setTitle("商米测试Demo");
        mTopBar.setTitleViewColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        mTopBar.setBackgroundColor(ContextCompat.getColor(this, R.color.app_color_theme));
        mStatusbarHeight = QMUIStatusBarHelper.getStatusbarHeight(this);
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_still, R.anim.slide_out_right);
            }
        });

        lvMenus = (ListView) findViewById(R.id.lv_menus);
        tvPrice = (TextView) findViewById(R.id.main_tv_price);
        btnClear = (TextView) findViewById(R.id.main_btn_clear);
        llyShopcar = (LinearLayout) findViewById(R.id.lly_shopcar);
        rtlEmptyShopcar = (RelativeLayout) findViewById(R.id.rtl_empty_shopcar);
        flShoppingCar = (FrameLayout) findViewById(R.id.fl_shopping_car);
        tv_face_pay = findViewById(R.id.tv_face_pay);
        main_ll_pay = findViewById(R.id.main_ll_pay);
        btnMore = (Button) findViewById(R.id.main_btn_more);
        gvDrink = findViewById(R.id.gv_drinks);
        gvFruit = findViewById(R.id.gv_fruits);
        gv_snacks = findViewById(R.id.gv_snacks);
        gv_vegetables = findViewById(R.id.gv_vegetables);

        gvDrink.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gvFruit.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gv_snacks.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gv_vegetables.setSelector(new ColorDrawable(Color.TRANSPARENT));

        ll_drinks = findViewById(R.id.ll_drinks);
        ll_snacks = findViewById(R.id.ll_snacks);
        ll_fruits = findViewById(R.id.ll_fruits);
        ll_vegetables = findViewById(R.id.ll_vegetables);
        rl_no_goods = findViewById(R.id.rl_no_goods);
        btnPay = findViewById(R.id.main_k1_btn_pay);
        tvCarMoeny = findViewById(R.id.tv_car_money);
        tvCar = findViewById(R.id.tv_car_num);
        ivCar = findViewById(R.id.iv_car);
        rlCar = findViewById(R.id.main_btn_car);
        llK1ShoppingCar = (LinearLayout) findViewById(R.id.ll_k1_shopping_car);

        tvVipPay = findViewById(R.id.vip_pay);
        tvVipK1Pay = findViewById(R.id.vip_k1__pay);
        if (isVertical) {
            llK1ShoppingCar.setVisibility(View.VISIBLE);
            flShoppingCar.setVisibility(View.GONE);
        } else {
            llK1ShoppingCar.setVisibility(View.GONE);
            flShoppingCar.setVisibility(View.VISIBLE);
            llyShopcar.setVisibility(View.GONE);
            rtlEmptyShopcar.setVisibility(View.VISIBLE);
        }
        findViewById(R.id.main_btn_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound(1);
            }
        });

        findViewById(R.id.main_btn_print).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                Observable.timer(2, TimeUnit.SECONDS)
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) {
                                printerPresenter.print(Config.textJson, 1);
                            }
                        });
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("CheckResult")
    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, mStatusbarHeight + "", Toast.LENGTH_LONG).show();

        Boolean mIsMovie = mDataManager.getPreferencesHelper().getBoolean(APP_CONFIG, PreferenceKeys.IS_MOIVE);
        if (mIsMovie) {
            Timber.tag(getClassName()).i("sd 有视屏了");
            //需要存储的权限
            needPermisson(new PermissionCallBack() {
                @Override
                public void permissionSuccess() {
                    if (videoDisplay != null) {
                        // TODO: 2018/12/21
//                        videoDisplay.show();
                    }
                }

                @Override
                public void permissionDeath() {
                }

                @Override
                public void needOpenSetting() {
                }
            }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            Timber.tag(getClassName()).i("sd 没有视屏");
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onPause() {
        super.onPause();
        if (videoDisplay != null) {
            videoDisplay.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        soundPool.release();
        super.onDestroy();
        if (woyouService != null || extPrinterService != null) {
            unbindService(connService);
        }
        BasePresentationHelper.getInstance().dismissAll();
    }

    //连接打印服务
    private void connectPrintService() {
        Intent intent = new Intent();
        intent.setPackage("woyou.aidlservice.jiuiv5");
        intent.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
        bindService(intent, connService, Context.BIND_AUTO_CREATE);

    }

    private ServiceConnection connService = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            woyouService = null;
            extPrinterService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (isVertical) {

            } else {
                woyouService = IWoyouService.Stub.asInterface(service);
                printerPresenter = new PrinterPresenter(SMMainActivity.this, woyouService);
            }
        }
    };
    //退出时的时间
    private long mExitTime;

    //对返回键进行监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(SMMainActivity.this, ResourcesUtils.getString(this, R.string.tips_exit), Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
