package com.hnhy.nfc_demo;

import static com.hnhy.nfc_demo.BuildConfig.DEBUG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.moniuliuma.android.mupl.sm;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainActivity extends BaseNfcActivity {

    private TextView mMsg;
    private NfcAdapter mNfcAdapter;
    private PendingIntent pi;

    @Override
    protected void initView() {
        mMsg = findViewById(R.id.nfc_msg);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    @Override
    protected void initData() {
        if (mNfcAdapter == null) {
            showToastMsg("设备不支持NFC");
            finish();
            return;
        }
        if (!mNfcAdapter.isEnabled()) {
            showToastMsg("请打开手机NFC");
            finish();
            return;
        }
        pi = PendingIntent.getActivity(this, 0, new Intent(this, getClass())
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    @Override
    protected int createViews() {
        return R.layout.activity_main;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mNfcAdapter.isEnabled() && mNfcAdapter != null) {
            //启动
            mNfcAdapter.enableForegroundDispatch(this, pi, null, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            // 取消调度
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleNfcIntent(intent);
    }

    /**
     * 处理nfc标签
     *
     * @param intent
     */
    private void handleNfcIntent(Intent intent) {
        String action = intent.getAction();
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        //showMsg("支持的Tag列表：" + tag.toString());
        //卡片uid
//        String uid = bytes2HexStr(tag.getId());
//        1548079529
        long aa = toDec(tag.getId());
        showMsg("卡片uid:" + aa);
        //根据不同的Action进行获取不同的标签
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            try {
                processTag(tag);
            } catch (Exception e) {
                showMsg("通讯异常：" + e.getMessage());
            }
        }
        if (tag.getId() != null) {
            //打开继电器
            openRelay(true,1, 72903,1);
        }
    }

    private void processTag(Tag tag) {
        IsoDep isodep = IsoDep.get(tag);
        try {
            if (isodep != null) {
                // 建立连接
                isodep.connect();
                //data是要发送的命令数据，自己组装
                byte[] data = new byte[20];
                // 发送命令
                byte[] response = isodep.transceive(data);
                // 解析响应数据
            }
        } catch (Exception e) {
            showMsg("通讯异常：" + e.getMessage());
        } finally {
            if (isodep != null) {
                // 关闭连接
                try {
                    isodep.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showToastMsg(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void showMsg(String msg) {
        //String s = mMsg.getText().toString();
        mMsg.setText("");
        mMsg.setText(msg);
    }

    public static String bytes2HexStr(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return "";
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            builder.append(buffer);
        }
        return builder.toString().toUpperCase();
    }

}