package org.coreocto.dev.hf.androidclient;

import org.coreocto.dev.hf.commonlib.Constants;

public class AppConstants {
    public static final String EMPTY_STRING = "";
    public static final String SPACE = " ";
    public static final String PREF_EMPTY_VAL_PLACEHOLDER = "<empty>";
    public static final String PREF_CLIENT_KEY1 = "client.key1";
    public static final String PREF_CLIENT_KEY2 = "client.key2";
    public static final String PREF_SERVER_HOSTNAME = "server.hostname";
    public static final String PREF_SERVER_RPT_STAT = "server.reportStat";
    public static final String PREF_CLIENT_DATA_DIR = "client.datadir";
    public static final String PREF_CLIENT_SSE_TYPE = "client.ssetype";
    public static final String PREF_CLIENT_DATA_PROTECT = "client.dataprotect";

//    public static final String PREF_CLIENT_KEY3 = "client.key3";
//    public static final String PREF_CLIENT_KEY4 = "client.key4";
//    public static final String PREF_CLIENT_KEYD = "client.keyd";
//    public static final String PREF_CLIENT_KEYC = "client.keyc";
//    public static final String PREF_CLIENT_KEYL = "client.keyl";

    public static final String PREF_CLIENT_SSE_TYPE_SUISE = "SUISE";
    public static final String PREF_CLIENT_SSE_TYPE_VASST = "VASST16";
    //    public static final String PREF_CLIENT_SSE_TYPE_MCES = "MCES";
    public static final String PREF_CLIENT_SSE_TYPE_CHLH = "CHLH15";
    public static final String PREF_CLIENT_SSE_TYPE_SUISE_2 = "SUISE2";
    public static final String PREF_CLIENT_SSE_TYPE_SUISE_3 = "SUISE3";

    public static final String REQ_UPLOAD_URL = "upload";
    public static final String REQ_PING_URL = "ping";
    public static final String REQ_STAT_URL = "stat";

    public static final String SW_TYPE_ADD_TOKEN = "add-token";
    public static final String SW_TYPE_ENCRYPT = "encrypt";

    public static final int ERR_CANNOT_CONNECT_SERVER = -100;
    public static final int ERR_GOOGLE_DRIVE_FILE_NOT_READY = -101;

    //preference item for crypto test
    public static final String PREF_CT_DEFAULT_RUN_CNT = "1";
    public static final String PREF_CT_DEFAULT_DATA_SIZE = "1024";
    public static final String PREF_CT_NUM_OF_EXEC = "num_of_exec";
    public static final String PREF_CT_DATA_SIZE = "data_size";
    public static final String PREF_CT_ALLOC_MEM = "alloc_mem";
    public static final String PREF_CT_EXPLICIT_GC = "explicit_gc";

    public static final int FRAGMENT_CHART_RESULT = -500;

    public static final String LOCAL_APP_DB = "hfapp.db";
    public static final String TABLE_REMOTE_DOCS = "tremote_docs";
    public static final String TABLE_AUTO_COMPLETE = "tauto_complete";

    public static final String LOCAL_APP_FOLDER = ".hfandroidclient";

    public static final String FILE_EXT_ENCRYPTED = ".enc";
    public static final String FILE_EXT_DECRYPTED = ".txt";

    public static final int SSE_TYPE_SUISE_2 = Constants.SSE_TYPE_SUISE - 1;    //suise + all keyword suffix search
    public static final int SSE_TYPE_SUISE_3 = SSE_TYPE_SUISE_2 - 1;            //suise + all keyword prefix search
}
