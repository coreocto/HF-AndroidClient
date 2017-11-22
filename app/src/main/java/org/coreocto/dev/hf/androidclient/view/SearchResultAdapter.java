package org.coreocto.dev.hf.androidclient.view;

import android.content.Context;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import org.coreocto.dev.hf.androidclient.R;
import org.coreocto.dev.hf.androidclient.bean.AppSettings;
import org.coreocto.dev.hf.commonlib.crypto.BlockCipherFactory;
import org.coreocto.dev.hf.commonlib.util.IBase64;
import org.coreocto.dev.hf.commonlib.util.Registry;

import javax.crypto.Cipher;
import java.util.List;

public class SearchResultAdapter extends ArrayAdapter<String> {

    private Registry registry;

    public SearchResultAdapter(Context context, int textViewResourceId, List<String> items) {
        super(context, textViewResourceId, items);
    }

    public SearchResultAdapter(Context context, int textViewResourceId, List<String> items, Registry registry) {
        super(context, textViewResourceId, items);
        this.registry = registry;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View view = super.getView(position, convertView, parent);

        String itemTxt = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.search_result_list_item, parent, false);
        }

        TextView txt1 = (TextView) convertView.findViewById(android.R.id.text1);

        byte[] key1 = AppSettings.getInstance().getSuiseClient().getKey1();

        byte[] itemBytes = new IBase64() {

            @Override
            public String encodeToString(byte[] bytes) {
                return null;
            }

            @Override
            public byte[] decodeToByteArray(String s) {
                return Base64.decode(s, Base64.NO_WRAP);
            }
        }.decodeToByteArray(itemTxt);

        byte[] decTxt = null;

        try {
            Cipher decCipher = BlockCipherFactory.getCipher(BlockCipherFactory.CIPHER_AES,
                    BlockCipherFactory.CIPHER_AES + BlockCipherFactory.SEP + BlockCipherFactory.MODE_CBC + BlockCipherFactory.SEP + BlockCipherFactory.PADDING_PKCS5,
                    Cipher.DECRYPT_MODE,
                    key1,
                    new byte[16]);
            decTxt = decCipher.doFinal(itemBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        txt1.setText(new String(decTxt));


        return convertView;
    }
}
