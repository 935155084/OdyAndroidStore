package com.huaye.odyandroidstore;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.MenuItem;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.huaye.odyandroidstore.base.BaseActivity;
import com.huaye.odyandroidstore.library.LibraryFragment;
import com.huaye.odyandroidstore.my.MyFragment;
import com.huaye.odyandroidstore.welfare.WelfareFragment;

import cn.bmob.v3.Bmob;

public class MainActivity extends BaseActivity {
    private LibraryFragment libraryFragment;
    private WelfareFragment welfareFragment;
    private FragmentManager fm;
    private BottomNavigationView navigationView;

    @Override
    protected void init() {
        super.init();
        fm = getSupportFragmentManager();
        Bmob.initialize(this, "c1bc52fe17266edafa7b25b1ccf4edbd");
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        navigationView = (BottomNavigationView) findViewById(R.id.nav);
        libraryFragment = new LibraryFragment();
        fm.beginTransaction().replace(R.id.container, libraryFragment).commitAllowingStateLoss();
    }

    @Override
    protected void initListener() {
        super.initListener();
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_library:
                        fm.beginTransaction().replace(R.id.container, libraryFragment).commitAllowingStateLoss();
                        break;
                    case R.id.menu_welfare:
                        fm.beginTransaction().replace(R.id.container, new WelfareFragment()).commitAllowingStateLoss();
                        break;
                    case R.id.menu_my:
                        fm.beginTransaction().replace(R.id.container, new MyFragment()).commitAllowingStateLoss();
                        break;
                }
                return true;
            }
        });

        ARouter.getInstance().build("/xxx/xxx").navigation(this, new NavCallback() {
            @Override
            public void onFound(Postcard postcard) {
                Log.d("ARouter", "找到了");
            }

            @Override
            public void onLost(Postcard postcard) {
                Log.d("ARouter", "找不到了");
            }

            @Override
            public void onArrival(Postcard postcard) {
                Log.d("ARouter", "跳转完了");
            }

            @Override
            public void onInterrupt(Postcard postcard) {
                Log.d("ARouter", "被拦截了");
            }
        });
    }
}
