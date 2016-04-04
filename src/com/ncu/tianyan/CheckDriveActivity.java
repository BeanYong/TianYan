package com.ncu.tianyan;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ncu.tianyan.util.ToastUtil;

/**
 * Created by BeanYong on 2015/8/14.
 */
public class CheckDriveActivity extends BaseActivity implements View.OnClickListener {
    private ImageView mBack, mInput;
    private TextView mTitle,mCheckFromSenser;
    private EditText mETWeight, mETTime, mETAlcoholCount, mETAlcoholConcentration;
    protected static ProgressDialog sProgressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkdrive_activity);
        initViews();
        initEvents();
    }

    private void initEvents() {
        mBack.setOnClickListener(this);
        mInput.setOnClickListener(this);
        mTitle.setText(R.string.check);
        mCheckFromSenser.setOnClickListener(this);
    }

    private void initViews() {
        mBack = (ImageView) findViewById(R.id.id_iv_back);
        mInput = (ImageView) findViewById(R.id.id_iv_self);
        mTitle = (TextView) findViewById(R.id.id_tv_title);
        mCheckFromSenser = (TextView) findViewById(R.id.id_tv_checkFromSenser);

        mBack.setImageResource(R.drawable.back);
        mInput.setImageResource(R.drawable.ok);


        mETWeight = (EditText) findViewById(R.id.id_et_weight);
        mETTime = (EditText) findViewById(R.id.id_et_time);
        mETAlcoholCount = (EditText) findViewById(R.id.id_et_alcoholCount);
        mETAlcoholConcentration = (EditText) findViewById(R.id.id_et_alcoholConcentration);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_iv_back:
                finish();
                break;
            case R.id.id_iv_self:
                getBloodAlcoholConcentration();
                break;
            case R.id.id_tv_checkFromSenser:
                checkFromSenser();
                break;
        }
    }

    /**
     * 通过吹气检测判别吹气着是否有酒驾风险
     */
    private void checkFromSenser() {
        sProgressDialog = new ProgressDialog(this);
        sProgressDialog.setMessage("请您对手持式酒精测试仪吹气...");
        sProgressDialog.show();
    }

    /**
     * 获取血液酒精浓度（x mg/100ml）
     */
    private void getBloodAlcoholConcentration() {
        String strWeight = mETWeight.getText().toString();
        String strTime = mETTime.getText().toString();
        String strAlcoholCount = mETAlcoholCount.getText().toString();
        String strAlcoholConcentration = mETAlcoholConcentration.getText().toString();

        if (TextUtils.isEmpty(strWeight) || TextUtils.isEmpty(strTime) || TextUtils.isEmpty(strAlcoholCount)
                || TextUtils.isEmpty(strAlcoholConcentration)) {
            ToastUtil.ToastShort("查询输入的必要数据尚未输入，请输入完成后再进行查询");
            return;
        }

        double weight = Float.parseFloat(strWeight);//体重

        if (weight <= 0) {//防止发生算术异常
            ToastUtil.ToastShort("体重必须大于0！");
            return;
        }

        double time = Float.parseFloat(strTime);//饮酒结束时间距离查询时间的小时数
        double alcoholCount = Float.parseFloat(strAlcoholCount);//饮酒数量
        double alcoholConcentration = Float.parseFloat(strAlcoholConcentration);//酒精度数

        int result = (int) ((alcoholCount * 1.05 * alcoholConcentration - 1000 * time) * 800 / (weight * 75));//血液中的酒精含量 mg/100ml
        float restHour = (float) ((alcoholCount * 0.105 - time) * alcoholConcentration / 100);//人体分解完酒精所需要的时间 h

        if (result < 0) {
            result = 0;
        }

        if (restHour < 0) {
            restHour = 0;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.tip);
        if (result < 20) {
            builder.setMessage("您现在血液中酒精含量为" + result + "mg/100ml，可正常驾驶,祝您出行愉快！");
        } else if (result >= 20 && result < 80) {
            builder.setMessage("您现在血液中酒精含量为" + result + "mg/100ml，属于酒驾，请休息" + restHour + "小时后驾车或寻找代驾！");
            builder.setNegativeButton(R.string.find_driver, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(CheckDriveActivity.this, WebViewActivity.class));
                }
            });
        } else {
            builder.setMessage("您现在血液中酒精含量为" + result + "mg/100ml，属于醉驾，请休息" + restHour + "小时后驾车或寻找代驾！");
            builder.setNegativeButton(R.string.find_driver, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(CheckDriveActivity.this, WebViewActivity.class));
                }
            });
        }
        builder.setPositiveButton(R.string.sure, null);
        builder.create().show();
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
