package de.grundid.hcedemo;

import java.io.IOException;

import android.content.Context;
import android.nfc.tech.IsoDep;

public class IsoDepTransceiver implements Runnable {

    public interface OnMessage {
        void onMessageRcv(byte[] message);
        void onMessageSend(String message);
        void onError(byte[] message);
    }

    private IsoDep mIsoDep;
    private OnMessage mOnMessage;
    private Context mContext;

    public IsoDepTransceiver(IsoDep isoDep, OnMessage onMessage) {
        this.mIsoDep = isoDep;
        this.mOnMessage = onMessage;
        this.mContext = (Context)onMessage;
    }

    private static final byte[] CLA_INS_P1_P2 = { 0x00, (byte)0xA4, 0x04, 0x00 };
    private static final byte[] AID_ANDROID = { (byte)0xF0, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 };
    
    private byte[] createSelectAidApdu(byte[] aid) {
        byte[] result = new byte[6 + aid.length];
        System.arraycopy(CLA_INS_P1_P2, 0, result, 0, CLA_INS_P1_P2.length);
        result[4] = (byte)aid.length;
        System.arraycopy(aid, 0, result, 5, aid.length);
        result[result.length - 1] = 0;
        return result;
    }

    @Override
    public void run() {
        int messageCounter = 0;
        try {
            mIsoDep.connect();
            mOnMessage.onMessageSend("00 A4 04 00 07 F0 01 02 03 04 05 06 00");
            byte[] response = mIsoDep.transceive(createSelectAidApdu(AID_ANDROID));
            if (new String(response).startsWith(mContext.getString(R.string.select_success_prefix))) { 
                mOnMessage.onMessageRcv(response);
                 while (mIsoDep.isConnected() && !Thread.interrupted()) {
                     String message = mContext.getString(R.string.reader_msg_prefix) + " " + messageCounter++;
                     mOnMessage.onMessageSend(message);
                     response = mIsoDep.transceive(message.getBytes());
                     mOnMessage.onMessageRcv(response);
                 }
                 mIsoDep.close();
            } else {
                mOnMessage.onError(mContext.getString(R.string.wrong_resp_err).getBytes());
            }
        }
        catch (IOException e) {
            mOnMessage.onError(e.getMessage().getBytes());
        }
    }
}
