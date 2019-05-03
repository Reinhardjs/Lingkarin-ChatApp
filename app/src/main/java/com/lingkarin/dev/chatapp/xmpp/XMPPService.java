package com.lingkarin.dev.chatapp.xmpp;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class XMPPService extends Service {

    private static final String TAG = XMPPService.class.getSimpleName();

    private HandlerThread mWorkerThread;
    private Handler mHandler;
//    private DataRepository mDataRepository;
    private XMPPConnectionManager mXMPPConnectionManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        mWorkerThread = new HandlerThread(TAG, android.os.Process.THREAD_PRIORITY_BACKGROUND);
        mWorkerThread.start();
        mHandler = new Handler(mWorkerThread.getLooper());
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        init(intent);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWorkerThread.quitSafely();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void init(final Intent intent) {
//        if (mDataRepository == null) {
//            mDataRepository = DataRepository.getInstance(getApplication());
//        }
//        mDataRepository.getCurrentMemberId(new RemoteDataSource.GetCurrentMemberIdCallBack() {
//            @Override
//            public void onGetCurrentMemberIdSuccess(@NonNull String memberId) {
//                Log.d(TAG, "getCurrentMemberId(success)");
//                XMPPAccount xmppAccount = new XMPPAccount(memberId);
//                mXMPPConnectionManager = XMPPConnectionManager.getInstance(xmppAccount);
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        handleIntent(intent);
//                    }
//                });
//            }
//
//            @Override
//            public void onGetCurrentMemberIdFailure() {
//                Log.d(TAG, "getCurrentMemberId(failure)");
//            }
//        });
    }

    private void handleIntent(Intent intent) {
        if (intent == null) {
            handleConnect();
            return;
        }

        String action = intent.getAction();

        if (XMPPServiceCommand.ACTION_CONNECT.equals(action)) {
            handleConnect();
        } else if (XMPPServiceCommand.ACTION_DISCONNECT.equals(action)) {
            handleDisconnect();
        }
    }

    private void handleConnect() {
        try {
            mXMPPConnectionManager.connect();
        } catch (Exception e) {
            Log.e(TAG, "handleConnect(failure)", e);
        }
    }

    private void handleDisconnect() {
        try {
            mXMPPConnectionManager.disconnect();
        } catch (Exception e) {
            Log.e(TAG, "handleConnect(failure)", e);
        }

        stopSelf();
    }
}
