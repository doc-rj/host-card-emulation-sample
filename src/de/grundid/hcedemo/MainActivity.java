package de.grundid.hcedemo;

import android.app.Activity;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.ReaderCallback;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.widget.ListView;
import de.grundid.hcedemo.IsoDepTransceiver.OnMessage;

public class MainActivity extends Activity implements OnMessage, ReaderCallback {

    private NfcAdapter mNfcAdapter;
    private ListView mListView;
    private IsoDepAdapter mIsoDepAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView)findViewById(R.id.listView);
        mIsoDepAdapter = new IsoDepAdapter(getLayoutInflater());
        mListView.setAdapter(mIsoDepAdapter);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mNfcAdapter.enableReaderMode(this, this,
                NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
                null);
    }

    @Override
    public void onPause() {
        super.onPause();
        mNfcAdapter.disableReaderMode(this);
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        IsoDep isoDep = IsoDep.get(tag);
        if (isoDep == null) {
            byte[] msg = getString(R.string.wrong_tag_err).getBytes();
            onError(msg);
        } else {
            IsoDepTransceiver transceiver = new IsoDepTransceiver(isoDep, this);
            Thread thread = new Thread(transceiver);
            thread.start();
        }
    }

    @Override
    public void onMessageRcv(final byte[] message) {
        onMessageAndType(new String(message), 0);
    }
    
    @Override
    public void onMessageSend(final String message) {
        onMessageAndType(message, 1);
    }

    @Override
    public void onError(final byte[] message) {
        onMessageAndType(new String(message), -1);
    }
    
    private void onMessageAndType(final String message, final int type) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mIsoDepAdapter.addMessage(message, type);
            }
        });
    }
}
