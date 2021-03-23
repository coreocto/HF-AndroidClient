package org.coreocto.dev.hf.androidclient.util;

public class SystemUtil {
    public static void freeMemory(){
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }
}
