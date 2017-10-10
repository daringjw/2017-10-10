package com.jkkc.update;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.download.DownloadListener;
import com.tencent.bugly.beta.download.DownloadTask;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class UpgradeActivity extends AppCompatActivity {
    private TextView mTvVersion;
    private TextView mTvSize;
    private TextView mTvContent;
    private TextView mTvTime;
    private NumberProgressBar mPbProgress;
    private Button mBtnUpdate;
    private ImageView mImgCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);

        mTvVersion = getView(R.id.tv_version);
        mTvSize = getView(R.id.tv_size);
        mTvTime = getView(R.id.tv_time);
        mTvContent = getView(R.id.tv_content);
        mPbProgress = getView(R.id.pb_progress);
        mBtnUpdate = getView(R.id.btn_update);
        mImgCancel = getView(R.id.img_cancel);

        mPbProgress.setProgressTextColor(getResources().getColor(R.color.colorPrimary));
        mPbProgress.setReachedBarColor(getResources().getColor(R.color.colorPrimaryDark));
        mPbProgress.setMax(100);

            /*获取下载任务，初始化界面信息*/
        updateBtn(Beta.getStrategyTask());
        mTvVersion.setText(mTvVersion.getText().toString() + Beta.getUpgradeInfo().versionName +"."+Beta.getUpgradeInfo().versionCode+ "版本？");

            /*获取策略信息，初始化界面信息*/
        mTvSize.setText(mTvSize.getText().toString() + ConvertUtils.byte2FitMemorySize(Beta.getUpgradeInfo().fileSize) + "");
        mTvContent.setText(Beta.getUpgradeInfo().newFeature);
        mTvTime.setText(mTvTime.getText().toString() + TimeUtils.millis2String(Beta.getUpgradeInfo().publishTime,new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()))+"");

            /*为下载按钮设置监听*/
        mBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadTask task = Beta.startDownload();
                updateBtn(task);
//                if (task.getStatus() == DownloadTask.DOWNLOADING) {
//                    finish();
//                }
            }
        });

        /*为取消按钮设置监听*/
        mImgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Beta.cancelDownload();
                finish();
            }
        });

            /*注册下载监听，监听下载事件*/
        Beta.registerDownloadListener(new DownloadListener() {

            @Override
            public void onReceive(DownloadTask task) {
                updateBtn(task);
                Float aFloat = Float.valueOf((task.getSavedLength() / (float) task.getTotalLength()) * 100);
                int progress = aFloat.intValue();
                mPbProgress.setVisibility(View.VISIBLE);
                mPbProgress.setProgress(progress);
                Log.e("UpgradeActivity.this: ", "onReceive: " + progress);

            }

            @Override
            public void onCompleted(DownloadTask task) {
                updateBtn(task);
                finish();
            }

            @Override
            public void onFailed(DownloadTask task, int code, String extMsg) {
                updateBtn(task);
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
            /*注销下载监听*/
        Beta.unregisterDownloadListener();
    }


    public void updateBtn(DownloadTask task) {

            /*根据下载任务状态设置按钮*/
        switch (task.getStatus()) {
            case DownloadTask.INIT:
            case DownloadTask.DELETED:
            case DownloadTask.FAILED: {
                mBtnUpdate.setText("立即升级");
            }
            break;
            case DownloadTask.COMPLETE: {
                mBtnUpdate.setText("安装");
                Float aFloat = Float.valueOf((task.getSavedLength() / (float) task.getTotalLength()) * 100);
                int progress = aFloat.intValue();
                mPbProgress.setVisibility(View.VISIBLE);
                mPbProgress.setProgress(progress);
            }
            break;
            case DownloadTask.DOWNLOADING: {
                mBtnUpdate.setText("暂停");
            }
            break;
            case DownloadTask.PAUSED: {
                mBtnUpdate.setText("继续下载");
                Float aFloat = Float.valueOf((task.getSavedLength() / (float) task.getTotalLength()) * 100);
                int progress = aFloat.intValue();
                mPbProgress.setVisibility(View.VISIBLE);
                mPbProgress.setProgress(progress);
            }
            break;
        }
    }

    public <T extends View> T getView(int id) {
        return (T) findViewById(id);
    }


}
