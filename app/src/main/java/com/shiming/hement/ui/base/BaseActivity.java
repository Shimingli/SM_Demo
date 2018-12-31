package com.shiming.hement.ui.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.shiming.base.ui.QMUIActivity;
import com.shiming.base.utils.QMUIDisplayHelper;
import com.shiming.base.utils.QMUIStatusBarHelper;
import com.shiming.hement.HementApplication;
import com.shiming.hement.data.DataManager;
import com.shiming.hement.ui.permission.RxPermissionsActivity;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.concurrent.atomic.AtomicLong;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.shiming.base.BaseApplication.getContext;

/**
 * <p>
 * 抽象应用程序中的其他活动必须实现的活动。它处理Dagger组件的创建，并确保ConfigPersistentComponent的实例跨配置更改存活。
 * 没有结合着Fragment来使用
 * </p>
 *
 * @author shiming
 * @version v1.0
 * @since 2018/11/28 10:04
 */

public class BaseActivity extends QMUIActivity {

    private static final String KEY_ACTIVITY_ID = "KEY_ACTIVITY_ID";
    /**
     * AtomicLong是作用是对长整形进行原子操作。 线程安全
     */
    private static final AtomicLong NEXT_ID = new AtomicLong(0);
    /**
     * java1.8中新加入了一个新的原子类LongAdder，该类也可以保证Long类型操作的原子性，
     * 相对于AtomicLong，LongAdder有着更高的性能和更好的表现，可以完全替代AtomicLong的来进行原子操作
     * 但是对 java的版本有要求，这里就不使用 LongAdder了
     */
    // private static final LongAdder NEXT_ID = new LongAdder();


    private long mActivityId;
    private RxPermissions mRxPermissions;
    public DataManager mDataManager;
     // 生命周期的问题
//    private LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
//    private ViewModelProvider.Factory e = new ViewModelProvider.Factory() {
//        public <T extends ViewModel> T create(Class<T> cls) {
//            return new SharedVM();
//        }
//    };
//    private static class SharedVM extends ViewModel {
//        private MutableLiveData<Bundle> a;
//
//        private SharedVM() {
//            this.a = new MutableLiveData();
//        }
//
//        /* synthetic */ SharedVM(AnonymousClass1 anonymousClass1) {
//            this();
//        }
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //创建ActivityComponent，如果配置更改后调用缓存的ConfigPersistentComponent，则重用它。
        mActivityId = savedInstanceState != null ? savedInstanceState.getLong(KEY_ACTIVITY_ID) : NEXT_ID.getAndIncrement();
        mRxPermissions = new RxPermissions(this);
        mRxPermissions.setLogging(true);
        //状态栏的颜色
//        QMUIStatusBarHelper.setStatusBarLightMode(this);
        mDataManager = HementApplication.getDataManager();

//        getLifecycle().addObserver(mPresenter);
    }

    protected String getClassName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(KEY_ACTIVITY_ID, mActivityId);
    }

    /**
     * isChangingConfigurations()函数在是Api level 11（Android 3.0.x） 中引入的
     * 也就是用来检测当前的Activity是否 因为Configuration的改变被销毁了，然后又使用新的Configuration来创建该Activity。
     * 常见的案例就是 Android设备的屏幕方向发生变化，比如从横屏变为竖屏。
     */
    @Override
    protected void onDestroy() {
        //检查此活动是否处于销毁过程中，以便用新配置重新创建。
        if (!isChangingConfigurations()) {
            Timber.tag(getClassName()).i("销毁的configPersistentComponent id=%d", mActivityId);
        }
        super.onDestroy();
    }


    @Override
    protected int backViewInitOffset() {
        return QMUIDisplayHelper.dp2px(getContext(), 100);
    }

    public interface PermissionCallBack {
        void permissionSuccess();

        void permissionDeath();

        void needOpenSetting();
    }

    /**
     *
     * @param permissionCallBack  权限回调
     * @param permissions 权限的详情
     */
    @SuppressLint("CheckResult")
    public void needPermisson(final PermissionCallBack permissionCallBack, String... permissions) {
        mRxPermissions
                .requestEach(permissions)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Permission>() {
                               @Override
                               public void accept(Permission permission) throws Exception {
                                   if (permission.granted) {
//                                       // 获取了权限
//                                       Toast.makeText(RxPermissionsActivity.this,
//                                               "获取两个权限",
//                                               Toast.LENGTH_SHORT).show();
                                       permissionCallBack.permissionSuccess();
                                   } else if (permission.shouldShowRequestPermissionRationale) {
                                       //没有获取权限，但是用户没有点不在询问
//                                       Toast.makeText(RxPermissionsActivity.this,
//                                               "权限拒绝，但是没有点不在询问",
//                                               Toast.LENGTH_SHORT).show();
                                       permissionCallBack.permissionDeath();
                                   } else {
                                       //用户已经点了不在询问，需要去启动设置开启权限
//                                       Toast.makeText(RxPermissionsActivity.this,
//                                               "权限拒绝，并且不能询问",
//                                               Toast.LENGTH_SHORT).show();
                                       permissionCallBack.needOpenSetting();
                                   }
                               }
                           },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable t) {
                                Timber.tag(getClassName()).i("发生异常" + t);
                            }
                        },
                        new Action() {
                            @Override
                            public void run() {
                                Timber.tag(getClassName()).i("完成");
                            }
                        });
    }

}
