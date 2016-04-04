package com.ncu.tianyan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by BeanYong on 2015/8/10.
 */
public class NavActivity extends BaseActivity implements View.OnClickListener {

    public static final int SEARCH_ADD = 1;//查询地点
    public static final int SEARCH_LL = 2;//查询经纬度
    public static final int LOC_CAR = 3;//定位爱车
    public static final int LOC_DRIVER = 4;//定位酒驾司机

    private LinearLayout mMap = null;
    private RelativeLayout mQueryAdd, mQueryLL, mLocCar, mLocDriver, mFindDriver, mCheck, mShareLoc;
    private LayoutInflater mInflater = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_activity);

        initViews();
        initEvents();
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        mMap = (LinearLayout) findViewById(R.id.id_ll_map);
        mQueryAdd = (RelativeLayout) findViewById(R.id.id_rl_queryAdd);
        mQueryLL = (RelativeLayout) findViewById(R.id.id_rl_queryLL);
        mLocCar = (RelativeLayout) findViewById(R.id.id_rl_locCar);
        mLocDriver = (RelativeLayout) findViewById(R.id.id_rl_locDriver);
        mFindDriver = (RelativeLayout) findViewById(R.id.id_rl_findDriver);
        mCheck = (RelativeLayout) findViewById(R.id.id_rl_check);
        mShareLoc = (RelativeLayout) findViewById(R.id.id_rl_shareLoc);

        mInflater = LayoutInflater.from(this);
    }

    /**
     * 初始化事件
     */
    private void initEvents() {
        mMap.setOnClickListener(this);
        mQueryAdd.setOnClickListener(this);
        mQueryLL.setOnClickListener(this);
        mLocCar.setOnClickListener(this);
        mLocDriver.setOnClickListener(this);
        mFindDriver.setOnClickListener(this);
        mCheck.setOnClickListener(this);
        mShareLoc.setOnClickListener(this);
    }

    /**
     * 寻找代驾
     */
    private void findDriver() {
        Intent intent = new Intent(this,WebViewActivity.class);
        startActivity(intent);
    }

    /**
     * 定位爱车
     */
    private void locCar() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("type", LOC_CAR);
        startActivity(intent);
    }

    /**
     * 定位酒驾司机
     */
    private void locDriver() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("type", LOC_DRIVER);
        startActivity(intent);
    }

    /**
     * 查询经纬度
     */
    private void searchLL() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.input_ll);
        final View view = mInflater.inflate(R.layout.search_ll, null);
        builder.setView(view);
        builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(NavActivity.this, MainActivity.class);
                EditText et1 = (EditText) view.findViewById(R.id.lat);
                EditText et2 = (EditText) view.findViewById(R.id.lon);
                intent.putExtra("type", SEARCH_LL);
                intent.putExtra("lat", et1.getText().toString());
                intent.putExtra("lon", et2.getText().toString());
                startActivity(intent);
            }
        });
        builder.create().show();
    }

    /**
     * 查询地点
     */
    private void searchAdd() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.input_add);
        final View view = mInflater.inflate(R.layout.search_city, null);
        builder.setView(view);
        builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(NavActivity.this, MainActivity.class);
                EditText et1 = (EditText) view.findViewById(R.id.city);
                EditText et2 = (EditText) view.findViewById(R.id.addr);
                intent.putExtra("type", SEARCH_ADD);
                intent.putExtra("cityName", et1.getText().toString());
                intent.putExtra("addInCity", et2.getText().toString());
                startActivity(intent);
            }
        });
        builder.create().show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_ll_map://地图
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.id_rl_check://饮酒后查询
                startActivity(new Intent(this,CheckDriveActivity.class));
                break;
            case R.id.id_rl_findDriver://寻找代驾
                findDriver();
                break;
            case R.id.id_rl_locCar://定位爱车
                locCar();
                break;
            case R.id.id_rl_locDriver://定位酒驾司机
                locDriver();
                break;
            case R.id.id_rl_queryAdd://查询地点
                searchAdd();
                break;
            case R.id.id_rl_queryLL://查询经纬度
                searchLL();
                break;
            case R.id.id_rl_shareLoc://位置共享
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
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
}
