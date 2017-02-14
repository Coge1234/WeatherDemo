package com.example.viewpagertest;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.viewpagertest.adapter.ScreenSlidePagerAdapter;
import com.example.viewpagertest.bean.MessageEvent;
import com.example.viewpagertest.db.AddCounty;
import com.example.viewpagertest.fragment.WeatherViewPagerFragment;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<AddCounty> addCountyList;

    private ViewPager viewPager;
    public DrawerLayout drawerLayout;
    private List<Fragment> fragmentList;
    private ScreenSlidePagerAdapter adapter;
    private Toolbar toolbar;
    private Button toolbarhomeBtn;

    private static int i;
    private int[] resId = {R.drawable.pic01, R.drawable.pic02, R.drawable.pic03, R.drawable.pic04, R.drawable.pic05};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_main);

        fragmentList = new ArrayList<>();
        addCountyList = new ArrayList<>();

        viewPager = (ViewPager) findViewById(R.id.viewpagerId);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), fragmentList);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarhomeBtn = (Button) findViewById(R.id.toolbar_homebtn);


        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //发送EventBus消息
                EventBus.getDefault().post(new MessageEvent("queryAddCounty"));
            }
        };
        drawerLayout.setDrawerListener(toggle);

        viewPager.setAdapter(adapter);

        toolbarhomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        ShowWeatherFragment();
    }

    public void ShowWeatherFragment() {
        addCountyList = DataSupport.findAll(AddCounty.class);
        if (null != addCountyList && addCountyList.size() > 0) {
            fragmentList.clear();
            for (AddCounty addCounty : addCountyList) {
                fragmentList.add(WeatherViewPagerFragment.newInstance(addCounty.getWeatherId()));
            }
            adapter.setFragments(fragmentList);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbarmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_item:
                Toast.makeText(this, "you chick add", Toast.LENGTH_SHORT).show();
                break;
            case R.id.remove_item:
                Toast.makeText(this, "you click remove", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }

    /*
* 当drawerlayout弹出时，点击Back键是隐藏侧滑菜单，而不是退出Activity
* */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
