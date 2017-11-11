package org.coreocto.dev.hf.androidclient;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import org.coreocto.dev.hf.androidclient.util.ByteArrayWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ByteArrayEqualsTest {

    private static final String TAG = "ByteArrayEqualsTest";

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        byte[] a = new byte[8];
        byte[] b = new byte[8];

        Set<byte[]> set = new HashSet<>();
        set.add(a);
        Log.d(TAG,"set.size() = "+set.size());
        set.add(b);
        Log.d(TAG,"set.size() = "+set.size());

        Log.d(TAG, "set.contains(a) = "+set.contains(a));
        Log.d(TAG, "set.contains(b) = "+set.contains(b));

        set.remove(b);

        Log.d(TAG, "set.contains(a) = "+set.contains(a));
        Log.d(TAG, "set.contains(b) = "+set.contains(b));

        Log.d(TAG,"a.hashCode()"+Arrays.hashCode(a));
        Log.d(TAG,"b.hashCode()"+Arrays.hashCode(b));

        Set<ByteArrayWrapper> newSet = new HashSet<>();

        byte[] c = new byte[8];

        for (int i=0;i<c.length;i++){
            c[i]=(byte)i;
        }

        ByteArrayWrapper baw = new ByteArrayWrapper(a);
        ByteArrayWrapper baw2 = new ByteArrayWrapper(b);
        ByteArrayWrapper baw3 = new ByteArrayWrapper(c);

        Log.d(TAG, "baw vs baw2 = "+baw.equals(baw2));
        Log.d(TAG, "baw vs baw3 = "+baw.equals(baw3));

        newSet.add(baw);
        newSet.add(baw2);

        Log.d(TAG, "newSet.contains(baw) = "+newSet.contains(baw));

        Log.d(TAG, "newSet.contains(baw2) = "+newSet.contains(baw2));

        newSet.add(baw3);

        Log.d(TAG, "newSet.contains(baw3) = "+newSet.contains(baw3));



//        set.con.binarySearch(set, new byte[8], new Comparator<byte[]>() {
//            @Override
//            public int compare(byte[] o1, byte[] o2) {
//                int hc = Arrays.hashCode(o1);
//                int hc2 = Arrays.hashCode(o2);
//                if (hc>hc2){
//                    return 1;
//                }else if (hc==hc2){
//                    return 0;
//                }else{
//                    return -1;
//                }
//            }
//        });

    }
}
