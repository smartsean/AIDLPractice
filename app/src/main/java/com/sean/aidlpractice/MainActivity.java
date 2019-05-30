package com.sean.aidlpractice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sean.aidlpractice.service.IMessageTransfer;
import com.sean.aidlpractice.service.INewMessageListener;
import com.sean.aidlpractice.service.Message;
import com.sean.aidlpractice.service.MessageService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author sean
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MessageService";

    private List<Message> mMessages;
    private Context mContext;
    private TextView mTextView;
    private IMessageTransfer mIMessageTransfer;
    private StringBuilder mStringBuilder = new StringBuilder();

    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            bindService();
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIMessageTransfer = IMessageTransfer.Stub.asInterface(service);
            try {
                mIMessageTransfer.asBinder().linkToDeath(mDeathRecipient,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                mMessages = mIMessageTransfer.getMessages();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private INewMessageListener mINewMessageListener = new INewMessageListener.Stub() {
        @Override
        public void newMessageReceived(com.sean.aidlpractice.service.Message message) throws RemoteException {
            receivedMessage(message);
        }
    };

    private void receivedMessage(com.sean.aidlpractice.service.Message message) {
        Log.d(TAG, "receivedMessage: " + message.toString());

        mMessages.add(message);
        mStringBuilder.append("\n").append(message.getDate()).append("   ").append(message.getContent());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, "收到了新消息", Toast.LENGTH_SHORT).show();
                mTextView.setText(mStringBuilder.toString());
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mTextView = findViewById(R.id.message_content);
        mMessages = new ArrayList<com.sean.aidlpractice.service.Message>();
        setClickListener();
    }

    private void bindService(){
        Intent intent = new Intent(mContext, MessageService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    private void setClickListener() {
        findViewById(R.id.start_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindService();
            }
        });
        findViewById(R.id.start_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mIMessageTransfer != null && mINewMessageListener != null) {
                        mIMessageTransfer.startReceiveMessage(mINewMessageListener);
                        com.sean.aidlpractice.service.Message message = new com.sean.aidlpractice.service.Message();
                        message.setId(2);
                        message.setContent("男朋友发起了会话");
                        message.setDate(new Date().toString());
                        message.setName("男朋友");
                        message.setFriendId(1);
                        message.setFriendName("女朋友");
                        try {
                            mIMessageTransfer.sendMessage(message);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.stop_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mIMessageTransfer != null && mINewMessageListener != null) {
                        mIMessageTransfer.endReceiveMessage(mINewMessageListener);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.send_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.sean.aidlpractice.service.Message message = new com.sean.aidlpractice.service.Message();
                message.setId(2);
                message.setContent("我上班呢,现在的时间是" + new Date().toString());
                message.setDate(new Date().toString());
                message.setName("男朋友");
                message.setFriendId(1);
                message.setFriendName("女朋友");
                try {
                    if (mIMessageTransfer != null) {
                        mIMessageTransfer.sendMessage(message);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.dis_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (null!=mIMessageTransfer){
                        mIMessageTransfer.cutNetWork();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
