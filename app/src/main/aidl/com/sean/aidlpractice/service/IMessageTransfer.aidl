// IMessageTransfer.aidl
package com.sean.aidlpractice.service;

// Declare any non-default types here with import statements
import com.sean.aidlpractice.service.Message;
import com.sean.aidlpractice.service.INewMessageListener;

interface IMessageTransfer {

    //发送消息
    void sendMessage(in Message message);

    //收到新消息消息
    Message receivedMessage(in Message message);

    // 获取消息列表
    List<Message> getMessages();

    // 开始接收消息
    void startReceiveMessage(INewMessageListener listener);

    // 停止接收消息
    void endReceiveMessage(INewMessageListener listener);


    // 模拟女朋友断网
    void cutNetWork();
}
