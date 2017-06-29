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

        //�򿪵���ģʽ������鿴log
        QQ5Sdk.getInstance().setDebugEnabled(true);

        /*
         *��ʼ���ӿ�
         * @param activity   ��ǰ��Ϸ��Activity
         * @param orientation    ���� HORIZONTAL ���� VERTICAL
         * @param appId    ��QQ5��Ϸ���������appid
         * @param appKey   ��QQ5��Ϸ���������appkey
         * @param splashVisibility �Ƿ��������� true or false
         * @param splashDismissCallBack  ��ʼ����ϻص��ӿ�H0ndi0tBTq
         */
        QQ5Sdk.getInstance().init(MainActivity.this, QQ5Sdk.HORIZONTAL, "2", "IilaQXIbzG2WFtKL", result, new SplashDismissCallBack() {
            @Override
            public void onDismiss() {
                setText("��ʼ���ص��ɹ�");

                mPayBtn.setEnabled(true);
                //�������ڳ�ʼ�����ʱ����
            }
        });

        //init �������أ�����splashVisibility Ĭ�Ͽ�������
//        QQ5Sdk.getInstance().init(MainActivity.this, QQ5Sdk.VERTICAL, "1", "H0ndi0tBTq", new SplashDismissCallBack() {
//            @Override
//            public void onDismiss() {
//                setText("��ʼ���ص��ɹ�");
//                //�������ڳ�ʼ�����ʱ����
//            }
//        });
    }

    public void pay(View view) {
        String amount = ((EditText) findViewById(R.id.qq5_demo_account)).getText().toString();
        if (amount.length() == 0) {
            Toast.makeText(this, "��������", Toast.LENGTH_SHORT).show();
            return;
        }
        /*
         * ֧���ӿ�
         * ����֧�����棬ע��֧���ص���������
         *
         * @param activity ��ǰ��Ϸ��Activity
         * @param gameOrderId ��Ϸ����id
         * @param gameCoin ��Ϸ������
         * @param gameCoinName ��Ϸ�����ƣ�Ԫ����
         * @param amount ��ֵ��������0.1��֧��һλС������λ��Ԫ��
         * @param extra ͸���ֶΣ�SDK����˻ص�ԭ�����أ�
         * @param payCallBack ֧���ص��ӿ�
         */
        QQ5Sdk.getInstance().onPay(this, System.currentTimeMillis() + "", "1", "����", amount, "", new PayCallBack() {
            @Override
            public void failed(String message) {
                setText("֧��ʧ�ܣ�ԭ��:" + message);
            }

            @Override
            public void success(String message) {
                setText("֧���ɹ���" + message);
            }

            @Override
            public void cancel() {
                setText("֧������رջص���");
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

    //�л��������ص�ʱ����ýӿ�
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
         * ��Ϸ���淵�ؼ������˳��¼���
         * ���˳�Ӧ����ʾ
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
