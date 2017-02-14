package com.example.viewpagertest.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.viewpagertest.R;
import com.example.viewpagertest.bean.Message;
import com.example.viewpagertest.db.AddCounty;
import com.example.viewpagertest.gson.Forecast;
import com.example.viewpagertest.gson.Weather;
import com.example.viewpagertest.util.HttpUtil;
import com.example.viewpagertest.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.R.id.message;


public class WeatherViewPagerFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleNowDegree;
    private TextView updateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carwashText;
    private TextView sportText;
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefresh;
    private DrawerLayout drawerLayout;

    private String mWeatherId;
    private String weatherString;
    private List<AddCounty> addCountyList;

    public WeatherViewPagerFragment() {
        // Required empty public constructor
    }

    public static WeatherViewPagerFragment newInstance(String weatherId) {
        WeatherViewPagerFragment fragment = new WeatherViewPagerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, weatherId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mWeatherId = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_screen_slide_page, container, false);
        //初始化各控件
        weatherLayout = (ScrollView) view.findViewById(R.id.weather_layout);
        titleCity = (TextView) getActivity().findViewById(R.id.toolbar_textview);
        titleNowDegree = (TextView) getActivity().findViewById(R.id.toolbar_nowdegree_textview);
        degreeText = (TextView) view.findViewById(R.id.degree_text);
        weatherInfoText = (TextView) view.findViewById(R.id.weather_info_text);
        updateTime = (TextView) view.findViewById(R.id.update_time);
        forecastLayout = (LinearLayout) view.findViewById(R.id.forecast_layout);
        aqiText = (TextView) view.findViewById(R.id.aqi_text);
        pm25Text = (TextView) view.findViewById(R.id.pm25_text);
        comfortText = (TextView) view.findViewById(R.id.comfort_text);
        carwashText = (TextView) view.findViewById(R.id.car_wash_text);
        sportText = (TextView) view.findViewById(R.id.sport_text);
        bingPicImg = (ImageView) view.findViewById(R.id.bing_pic_img);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);

        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        addCountyList = new ArrayList<>();

        addCountyList = DataSupport.where("weatherId = ?", mWeatherId).find(AddCounty.class);
        weatherString = addCountyList.get(0).getWeatherString();
        if (null != weatherString){
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        }else{
            //无缓存时去服务器查询天气
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadBingPic();
                requestWeather(mWeatherId);
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String bingPic = prefs.getString("bing_pic", null);
        if (null != bingPic) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }

    }
    /*
    * 根据天气id请求城市天气信息
    * */
    private void requestWeather(String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (null != weather && "ok".equals(weather.status)) {
                            AddCounty updateAddCounty = new AddCounty();
                            updateAddCounty.setWeatherString(responseText);
                            updateAddCounty.updateAll("weatherId = ?",mWeatherId);
                            mWeatherId = weather.basic.weatherId;
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(getContext(), "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
    }

    /*
    * 加载必应每日一图
    * */
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "获取图片失败，检查网络", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getActivity()).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }
    /*
    * 处理并展示Weather实体类中的数据
    * */
    private void showWeatherInfo(Weather weather) {
        titleCity.setText(weather.basic.cityName);
        titleNowDegree.setText((weather.now.temperature + "℃"));
        degreeText.setText((weather.now.temperature + "℃"));
        weatherInfoText.setText(weather.now.more.info);
        updateTime.setText(weather.basic.update.updateTime.split(" ")[1]);
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);

            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText((forecast.temperature.max + "℃"));
            minText.setText((forecast.temperature.min + "℃"));

            forecastLayout.addView(view);
        }
        if (null != weather.aqi) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        comfortText.setText(("舒适度：" + weather.suggestion.comfort.info));
        carwashText.setText(("洗车指数：" + weather.suggestion.carwash.info));
        sportText.setText(("运动建议：" + weather.suggestion.sport.info));

        weatherLayout.setVisibility(View.VISIBLE);
    }
}
