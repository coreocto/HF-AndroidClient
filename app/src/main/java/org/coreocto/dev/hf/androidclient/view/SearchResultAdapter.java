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
import org.coreocto.dev.hf.commonlib.util.IBase64;
import org.coreocto.dev.hf.commonlib.util.Registry;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static org.coreocto.dev.hf.androidclient.util.AndroidAes128CbcImpl.CIPHER;
import static org.coreocto.dev.hf.commonlib.util.IAes128Cbc.CIPHER_TRANSFORMATION;

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

        SecretKeySpec secretKeySpec = new SecretKeySpec(key1, CIPHER);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[16]);
        try {
            Cipher encCipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            encCipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            decTxt = encCipher.doFinal(itemBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }


        txt1.setText(new String(decTxt));


        return convertView;
    }
}
