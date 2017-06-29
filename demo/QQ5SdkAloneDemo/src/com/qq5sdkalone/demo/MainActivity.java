package com.qq5sdkalone.demo;

import com.qq5sdk.standalone.api.ExitCallBack;
import com.qq5sdk.standalone.api.PayCallBack;
import com.qq5sdk.standalone.api.QQ5Sdk;
import com.qq5sdk.standalone.api.SplashDismissCallBack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private TextView mTextView;
    private Button mPayBtn;
    private CheckBox mCheckBox;

    private boolean mIsPortrait = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text_result);
        mPayBtn = (Button) findViewById(R.id.pay_button);
        mCheckBox = (CheckBox) findViewById(R.id.checkbox);

        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences activityPreferences = getSharedPreferences(
                        "sdk_demo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = activityPreferences.edit();
                editor.putBoolean("splash_visibility", isChecked);
                editor.commit();
            }
        });

        SharedPreferences setPreferences = getSharedPreferences(
                "sdk_demo", Context.MODE_PRIVATE);
        Boolean result = setPreferences.getBoolean("splash_visibility", true);

        mCheckBox.setChecked(result);

        //打开调试模式，方便查看log
        QQ5Sdk.getInstance().setDebugEnabled(true);

        /*
         *初始化接口
         * @param activity   当前游戏的Activity
         * @param orientation    横屏 HORIZONTAL 竖屏 VERTICAL
         * @param appId    在QQ5游戏中心申请的appid
         * @param appKey   在QQ5游戏中心申请的appkey
         * @param splashVisibility 是否启动闪屏 true or false
         * @param splashDismissCallBack  初始化完毕回调接口H0ndi0tBTq
         */
        QQ5Sdk.getInstance().init(MainActivity.this, QQ5Sdk.HORIZONTAL, "2", "IilaQXIbzG2WFtKL", result, new SplashDismissCallBack() {
            @Override
            public void onDismiss() {
                setText("初始化回调成功");

                mPayBtn.setEnabled(true);
                //方法将在初始化完毕时调用
            }
        });

        //init 方法重载，不传splashVisibility 默认开启闪屏
//        QQ5Sdk.getInstance().init(MainActivity.this, QQ5Sdk.VERTICAL, "1", "H0ndi0tBTq", new SplashDismissCallBack() {
//            @Override
//            public void onDismiss() {
//                setText("初始化回调成功");
//                //方法将在初始化完毕时调用
//            }
//        });
    }

    public void pay(View view) {
        String amount = ((EditText) findViewById(R.id.qq5_demo_account)).getText().toString();
        if (amount.length() == 0) {
            Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
            return;
        }
        /*
         * 支付接口
         * 拉起支付界面，注册支付回调监听器。
         *
         * @param activity 当前游戏的Activity
         * @param gameOrderId 游戏订单id
         * @param gameCoin 游戏币数量
         * @param gameCoinName 游戏币名称（元宝）
         * @param amount 充值金额须大于0.1，支持一位小数（单位：元）
         * @param extra 透传字段（SDK服务端回调原样返回）
         * @param payCallBack 支付回调接口
         */
        QQ5Sdk.getInstance().onPay(this, System.currentTimeMillis() + "", "1", "刀币", amount, "", new PayCallBack() {
            @Override
            public void failed(String message) {
                setText("支付失败！原因:" + message);
            }

            @Override
            public void success(String message) {
                setText("支付成功！" + message);
            }

            @Override
            public void cancel() {
                setText("支付弹框关闭回调！");
            }
        });
    }

    public void changeScreenClick(View view) {
        if (mIsPortrait) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        mIsPortrait = !mIsPortrait;
    }

    //切换横竖屏回调时接入该接口
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        QQ5Sdk.getInstance().orientationChange(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Bundle extras = data.getExtras();
            if (extras != null)
                Log.v("MainActivity", extras.toString());
        }
        QQ5Sdk.getInstance().handlerActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        /*
         * 游戏界面返回键处理（退出事件）
         * 打开退出应用提示
         *
         */
        QQ5Sdk.getInstance().handleBackAction(MainActivity.this, new ExitCallBack() {
            @Override
            public void exit() {
                finish();
            }
        });
    }

    private void setText(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextView.setText(text);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MainActivity", "onPause");
    }
}
