// INewMessageListener.aidl
package com.sean.aidlpractice.service;

// Declare any non-default types here with import statements
import com.sean.aidlpractice.service.Message;
interface INewMessageListener {
    void newMessageReceived(in Message message);
}
