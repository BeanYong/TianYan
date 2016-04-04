package com.ncu.tianyan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public class LogoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //取消标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.logo);

        ImageView ImageLogo = (ImageView) this.findViewById(R.id.image_logo);

        AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);     //渐变
        animation.setDuration(2000);

        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent1 = new Intent(LogoActivity.this, NavActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.anim_left_in,R.anim.anim_right_out);
            }
        });

        ImageLogo.setAnimation(animation);
    }

    protected void onStop() {
        finish();
        super.onStop();
    }
}
