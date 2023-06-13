package com.hnhy.nfc_demo;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.moniuliuma.android.mupl.sm;

/**
 * @author Martin-harry
 * @date 2021/11/11
 * @address
 * @Desc 子类在onNewIntent方法中进行NFC标签相关操作。
 * 在onNewIntent方法中执行intent传递过来的Tag数据
 */
public abstract class BaseNfcActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(createViews());
        initView();
        initData();
    }

    protected abstract void initView();

    protected abstract void initData();

    protected abstract int createViews();

    //打开继电器
    public void openRelay(final boolean useRelay, final int delaySecond, final int relayType, final int relayPort) {
        if (useRelay) {
            Thread myTread = new Thread() {
                @Override
                public void run() {
                    try {
                        //relay on
                        sm sm = new sm();
                        sm.gpio_debug(true);
                        if (relayPort == 2) {
                            sm.gpio_set_value(1000, 14, 1);
                        } else if (relayPort == 3) {
                            sm.gpio_set_value(relayType == 72903 ? 8000 : 1000, 1, 1);
                        } else {
                            sm.gpio_set_value(1000, 0, 1);
                        }
                        int mDelay = 1000 * delaySecond;
                        Log.i("delaySecond2", "" + delaySecond);
                        Log.d("delayTime", "" + mDelay);
                        Thread.sleep(mDelay);
                        if (relayPort == 2) {
                            sm.gpio_set_value(1000, 14, 0);
                        } else if (relayPort == 3) {
                            sm.gpio_set_value(relayType == 72903 ? 8000 : 1000, 1, 0);
                        } else {
                            sm.gpio_set_value(1000, 0, 0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            myTread.start();
        }
    }


    public long toDec1(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
//            result += value * factor;
//            factor *= 256l;
            if (i < 4) {
                result += value * factor;
                factor *= 256l;
            }
        }
        return result;
    }

    public long toDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }
}

