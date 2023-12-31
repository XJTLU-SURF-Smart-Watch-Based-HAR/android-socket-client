package com.example.uiforwearabledevices;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.os.Bundle;
public class AnimationActivity extends AppCompatActivity {
    private ImageView welcomeImg = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);//这个是新建的empty Activity对应的xml部署文件名字
        ActionBar actionBar = getSupportActionBar();//消除APP该Activity界面标题栏
        if(actionBar!=null){ //消除APP该Activity界面标题栏
            actionBar.hide(); //消除APP该Activity界面标题栏
        }
                welcomeImg = (ImageView) findViewById(R.id.wrap);
        AlphaAnimation anima = new AlphaAnimation(0.3f, 1.0f);
        anima.setDuration(3000);// 设置简单动画的显示时间
        welcomeImg.startAnimation(anima);
        anima.setAnimationListener(new AnimationImpl());
    }
    private class AnimationImpl implements AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
            welcomeImg.setBackgroundResource(R.mipmap.exercise);//这个是APP开机的图片，exercise是图片名字
        }
        @Override
        public void onAnimationEnd(Animation animation) {
            skip(); // 动画结束后跳转到别的页面
        }
        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }
    private void skip() {
        startActivity(new Intent(this, MainActivity.class));//动画开屏后返回APP主界面
        finish(); //结束动画Activity进程
    }
}

