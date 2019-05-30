package com.sean.aidlpractice.service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author sean
 */
public class MessageService extends Service {
    private static final String TAG = "MessageService";

    private List<Message> mMessages = new ArrayList<>();

    private Disposable mDisposable;

    private RemoteCallbackList<INewMessageListener> mINewMessageListenerRemoteCallbackList = new RemoteCallbackList<>();

    private Binder mBinder = new IMessageTransfer.Stub() {
        @Override
        public void sendMessage(Message message) throws RemoteException {
            receivedMessages(message);
        }

        @Override
        public Message receivedMessage(Message message) throws RemoteException {
            return message;
        }

        @Override
        public List<Message> getMessages() throws RemoteException {
            return mMessages;
        }

        @Override
        public void startReceiveMessage(INewMessageListener listener) throws RemoteException {
            mINewMessageListenerRemoteCallbackList.register(listener);
            simulateReceivedMessage();
        }

        @Override
        public void endReceiveMessage(INewMessageListener listener) throws RemoteException {
            mINewMessageListenerRemoteCallbackList.unregister(listener);
            if (mDisposable != null && !mDisposable.isDisposed()) {
                mDisposable.dispose();
            }
        }

        @Override
        public void cutNetWork() throws RemoteException {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    };

    private void receivedMessages(Message message) {
        mMessages.add(message);
        int count = mINewMessageListenerRemoteCallbackList.beginBroadcast();
        for (int i = 0; i < count; i++) {
            INewMessageListener listener = mINewMessageListenerRemoteCallbackList.getBroadcastItem(i);
            if (null != listener) {
                try {
                    listener.newMessageReceived(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        mINewMessageListenerRemoteCallbackList.finishBroadcast();
    }


    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public IBinder onBind(Intent intent) {
        int checkPermission = checkCallingOrSelfPermission("com.sean.aidlpractice.TEST_MESSAGE_PERMISSION");
        if (checkPermission == PackageManager.PERMISSION_DENIED){
            return  null;
        }
        Log.d(TAG, "onBind: "+"连接了");
        return mBinder;
    }

    private void simulateReceivedMessage() {
        mDisposable = Observable.interval(3, TimeUnit.SECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        receivedMessages(new Message(2, "男朋友", "女朋友发来的第" + aLong.intValue() + "条消息", new Date().toString(), 1, "女朋友"));
                    }
                });
    }
}
