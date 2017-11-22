package org.coreocto.dev.hf.androidclient.fragment.cryptotest;

import java.util.ArrayList;
import java.util.List;

public class CryptoTestContent {
    /**
     * An array of sample (dummy) items.
     */
    public static final List<CryptoTestItem> ITEMS = new ArrayList<CryptoTestItem>();

    public static final String MD_MD5 = "MD5";
    public static final String MD_SHA_1 = "SHA-1";
    public static final String MD_SHA_224 = "SHA-224";
    public static final String MD_SHA_256 = "SHA-256";
    public static final String MD_SHA_384 = "SHA-384";
    public static final String MD_SHA_512 = "SHA-512";

    public static final String AES_CBC_PKCS5 = "AES/CBC/PKCS5Padding"; //(128)
    public static final String AES_ECB_PKCS5 = "AES/ECB/PKCS5Padding"; //(128)
    public static final String DES_CBC_PKCS5 = "DES/CBC/PKCS5Padding"; //(56)
    public static final String DES_ECB_PKCS5 = "DES/ECB/PKCS5Padding"; //(56)
    public static final String DESede_CBC_PKCS5 = "DESede/CBC/PKCS5Padding"; //(168)
    public static final String DESede_ECB_PKCS5 = "DESede/ECB/PKCS5Padding"; //(168)

    static {
        // Add some sample items.
        // CryptoSchemeItem(int id, boolean enabled, boolean inProgress, String name, int keySize, int dataSize)
        addItem(new CryptoTestItem(CryptoTestContent.MD_SHA_1));
        addItem(new CryptoTestItem(CryptoTestContent.MD_MD5));
        addItem(new CryptoTestItem(CryptoTestContent.AES_CBC_PKCS5));
        addItem(new CryptoTestItem(CryptoTestContent.AES_ECB_PKCS5));
        addItem(new CryptoTestItem(CryptoTestContent.DES_CBC_PKCS5));
        addItem(new CryptoTestItem(CryptoTestContent.DES_ECB_PKCS5));
        addItem(new CryptoTestItem(CryptoTestContent.DESede_CBC_PKCS5));
        addItem(new CryptoTestItem(CryptoTestContent.DESede_ECB_PKCS5));
    }

    private static void addItem(CryptoTestItem item) {
        ITEMS.add(item);
    }

    public static void resetCheckStatus() {
        for (CryptoTestItem item : ITEMS) {
            if (!item.isEnabled()) {
                item.setEnabled(true);
            }
        }
    }
}
