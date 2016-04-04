package com.ncu.tianyan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.ncu.tianyan.ArcMenu.OnMenuItemClickListener;
import com.ncu.tianyan.MyOrientationListener.OnOrientationListener;
import com.ncu.tianyan.util.FileUtils;
import com.ncu.tianyan.util.StringUtils;
import com.ncu.tianyan.util.ToastUtil;

import java.util.Locale;

public class MainActivity extends Activity implements
        OnGetGeoCoderResultListener, OnMenuItemClickListener {
    protected static MapView mMapView = null; // 地图View
    protected static BaiduMap mBaiduMap = null;
    protected static boolean isGetOpposite = false;// 是否要获取对方位置
    private final String targetTel = "18270883648";
    // 配置文件
    protected static String locInfoStr = "";
    protected static String[] locInfos = null;

    private String locInfo = "";// 当前位置字符串

    // 对话框搜索所需变量
    private int DIALOG_TYPE = 0;// 0为搜索城市，1为搜索经纬度
    private EditText et1, et2;

    // 地理编码所需变量
    protected static GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用

    // 定位相关
    LocationClient mLocClient;
    protected static boolean isFirstLoc = true;// 是否首次定位
    private int mNeedLoc = 0;//是否需要定位的标志，0为需要
    private double mLatitude;
    private double mLongtitude;
    private BitmapDescriptor mIconLocation;// 自定义定位图标
    private LocationMode mLocationMode;

    // 方向传感器相关
    private MyOrientationListener myOrientationListener;
    private float mCurrentX;

    // TTS相关
    public static TextToSpeech mTts = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.addActivity(this);

        // 在使用SDK各组件之前初始化context信息，传入ApplicationContext
        // 注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        initValues();
        initMap();
        initLocation();
        initArcMenu();
        initTTS();
        // //如果saveInfo.ini存在，定位酒驾司机
        // if(FileUtils.isInfoExists(this))
        // locateDriver();

        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        Intent intent = getIntent();
        switch (mNeedLoc) {
            case NavActivity.SEARCH_ADD://地点查询
                searchByCityName(intent.getStringExtra("cityName"), intent.getStringExtra("addInCity"));
                break;
            case NavActivity.SEARCH_LL://经纬度查询
                searchByLL(intent.getStringExtra("lat"), intent.getStringExtra("lon"));
                break;
            case NavActivity.LOC_CAR://定位爱车
                sendMessage(targetTel, "这是一条自动发出的短信");
                break;
            case NavActivity.LOC_DRIVER://定位酒驾司机
                locateDriver();
                break;
        }
    }

    /**
     * 初始化变量
     */
    private void initValues() {
        isFirstLoc = true;
        mNeedLoc = getIntent().getIntExtra("type", 0);
    }

    private void initTTS() {
        mTts = new TextToSpeech(this, new OnInitListener() {
            public void onInit(int status) {
                // 如果装载TTS引擎成功
                if (status == TextToSpeech.SUCCESS) {
                    // 设置使用美式英语朗读
                    int result = mTts.setLanguage(Locale.US);
                    // 如果不支持所设置的语言
                    if (result != TextToSpeech.LANG_COUNTRY_AVAILABLE
                            && result != TextToSpeech.LANG_AVAILABLE) {
                        ToastUtil.ToastShort("TTS暂时不支持这种语言的朗读。");
                    }
                }
            }

        });
    }

    private void initArcMenu() {
        ArcMenu arcmenu = (ArcMenu) findViewById(R.id.arc_menu);
        arcmenu.setOnMenuItemClickListener(this);
    }

    /**
     * 初始化地图
     */
    private void initMap() {
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 设置地图精度
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);
        mBaiduMap.setTrafficEnabled(true); // 开启实时交通图
        mBaiduMap.setMyLocationEnabled(true);// 开启定位图层
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        mLocClient = new LocationClient(this);
        MyLocationListenner myListener = new MyLocationListenner();
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        mLocClient.setLocOption(option);
        // 初始化图标
        mIconLocation = BitmapDescriptorFactory
                .fromResource(R.drawable.navi_map_gps_locked);
        // 注册方向传感器的监听器
        myOrientationListener = new MyOrientationListener(this);
        myOrientationListener
                .setOnOrientationListener(new OnOrientationListener() {
                    @Override
                    public void onOrientationChanged(float x) {
                        mCurrentX = x;
                    }
                });

        mLocClient.start();
    }

    @SuppressLint("DefaultLocale")
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            ToastUtil.ToastShort("抱歉，未能找到结果");
            return;
        }
        mBaiduMap.clear();
        mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_markf)));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
                .getLocation()));
        String strInfo = String.format("纬度：%f 经度：%f",
                result.getLocation().latitude, result.getLocation().longitude);
        Toast.makeText(MainActivity.this, strInfo, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            ToastUtil.ToastShort("抱歉，未能找到结果");
            return;
        }
        mBaiduMap.clear();
        mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_markf)));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
                .getLocation()));
        Toast.makeText(MainActivity.this, result.getAddress(),
                Toast.LENGTH_LONG).show();
    }

    /**
     * 发送短信
     */
    public void sendMessage(String tel, String msgContent) {
        PendingIntent paIntent = PendingIntent.getBroadcast(this, 0,
                new Intent(), 0); // 发送短信使用的带有延迟效果的intent
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(tel, null, msgContent, paIntent, null);
        ToastUtil.ToastShort("正在为您定位，请稍候");
    }

    /**
     * 定位酒驾司机
     */
    public void locateDriver() {
        locInfoStr = FileUtils.queryInfo(this);
        if (!TextUtils.isEmpty(locInfoStr)) {
            locInfos = StringUtils.splitStr(MainActivity.locInfoStr);
            LatLng ptCenter = new LatLng((Float.valueOf(locInfos[1])),
                    (Float.valueOf(locInfos[2])));
            // 反Geo搜索
            mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                    .location(ptCenter));
        } else {
            ToastUtil.ToastShort("您的亲人没有在酒后驾驶 (^o^)~");
        }
    }

    /**
     * ****************************************************************************
     * 位置信息搜索模块 显示对话框，在对话框中输入需要搜索的位置信息
     * *****************************************************************************
     */
    AlertDialog.Builder builder;
    AlertDialog dialog;

    public void searchLoc() {
        builder = new AlertDialog.Builder(this);
        final View dialogView1 = LayoutInflater.from(this).inflate(
                R.layout.search_city, null);
        final View dialogView2 = LayoutInflater.from(this).inflate(
                R.layout.search_ll, null);

        if (DIALOG_TYPE == 0) {
            builder.setView(dialogView1);
            builder.setTitle(getString(R.string.input_add));
        } else {
            builder.setView(dialogView2);
            builder.setTitle(getString(R.string.input_ll));
        }
        builder.setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (DIALOG_TYPE == 0) {
                    et1 = (EditText) dialogView1.findViewById(R.id.city);
                    et2 = (EditText) dialogView1.findViewById(R.id.addr);
                    searchByCityName(et1.getText().toString(), et2.getText().toString());
                } else {
                    et1 = (EditText) dialogView2.findViewById(R.id.lat);
                    et2 = (EditText) dialogView2.findViewById(R.id.lon);
                    searchByLL(et1.getText().toString(), et2.getText().toString());
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();// 获取dialog
        dialog.show();// 显示对话框
    }

    /**
     * 通过经纬度搜索
     *
     * @param searchLatitude   纬度
     * @param searchLongtitude 经度
     */
    public void searchByLL(String searchLatitude, String searchLongtitude) {
        if (TextUtils.isEmpty(searchLatitude) || TextUtils.isEmpty(searchLongtitude)) {
            ToastUtil.ToastShort("输入不合法");
            return;
        }
        LatLng ptCenter = new LatLng((Float.valueOf(searchLatitude)),
                (Float.valueOf(searchLongtitude)));
        // 反Geo搜索
        mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));
    }

    /**
     * 通过城市名称搜索
     */
    public void searchByCityName(String cityName, String addrInCity) {
        if (TextUtils.isEmpty(cityName) || TextUtils.isEmpty(addrInCity)) {
            ToastUtil.ToastShort("输入不合法");
            return;
        }
        // Geo搜索
        mSearch.geocode(new GeoCodeOption().city(cityName).address(addrInCity));
    }

    /**
     * ***************************************************************
     * 定位模块 定位SDK监听函数
     * ****************************************************************
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentX).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            // 设置自定义图标
            MyLocationConfiguration config = new MyLocationConfiguration(
                    mLocationMode, true, mIconLocation);
            mBaiduMap.setMyLocationConfigeration(config);
            // 获取经纬度信息，保证经纬度信息为最新
            mLatitude = location.getLatitude();
            mLongtitude = location.getLongitude();

            if (isFirstLoc) {
                locInfo = location.getAddrStr();
                if (mNeedLoc == 0) {
                    isFirstLoc = false;
                    centerToMyLoc();
                }
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    /**
     * 定位到当前位置
     */
    public void centerToMyLoc() {
        LatLng ll = new LatLng(mLatitude, mLongtitude);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
        mBaiduMap.animateMapStatus(u);
        if (!TextUtils.isEmpty(locInfo)) {// 位置信息不为空，共享位置
//            sendMessage(targetTel, "我的位置：" + locInfo + "\n经度：" + mLongtitude
//                    + "\n纬度：" + mLatitude);
        }

        isGetOpposite = true;

        // 如果获取到的地址为空，输出提示
        if (locInfo == null) {
            Toast.makeText(MainActivity.this, "获取您的位置失败，请检查是否连接网络",
                    Toast.LENGTH_LONG).show();
            return;
        }
        ToastUtil.ToastShort("我在这儿：" + locInfo);
    }

    /**
     * *****************************************************************
     * Activity生命周期相关方法
     * ******************************************************************
     */
    @Override
    protected void onDestroy() {
        ActivityManager.removeActivity(this);

        // 退出时销毁定位
        if (mLocClient != null) {
            mLocClient.stop();
        }
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mSearch.destroy();
        mMapView.onDestroy();
        // 停止方向传感器
        if (myOrientationListener != null) {
            myOrientationListener.stop();
        }
        // 清空info.ini
        FileUtils.clearInfo(this);
        // 关闭TTS引擎
        if (mTts != null) {
            mTts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mLocClient != null) {
            // 开启定位
            mBaiduMap.setMyLocationEnabled(true);
            if (!mLocClient.isStarted())
                mLocClient.start();
            // 开启方向传感器
            myOrientationListener.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // 停止定位
        if (mLocClient != null) {
            mBaiduMap.setMyLocationEnabled(false);
            mLocClient.stop();
            // 停止方向传感器
            myOrientationListener.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    /**
     * *********************************************************************************
     * 选项菜单部分
     * **********************************************************************************
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.exit_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_menu_exit:
                ActivityManager.finishAllActivities();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 点击卫星菜单按钮时调用
     */
    public void onClick(View view, int pos) {
        switch (view.getId()) {
            // 位置共享
            case R.id.arc_myLoc:
                centerToMyLoc();
                break;
            // 发送短信获取对方位置
            case R.id.arc_locateOpposite:
                sendMessage(targetTel, "这是一条自动发出的短信");
                break;
            // 定位酒驾司机
            case R.id.arc_locateDriver:
                locateDriver();
                break;
            // 通过城市名称发起搜索
            case R.id.arc_searchByCityName:
                DIALOG_TYPE = 0;
                searchLoc();
                break;
            // 通过经纬度发起搜索
            case R.id.arc_searchByLL:
                DIALOG_TYPE = 1;
                searchLoc();
                break;
        }
    }
}
