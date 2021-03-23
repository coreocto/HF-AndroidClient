#include <jni.h>
#include <string>

#include <android/log.h>
#include "openssl/md5.h"
#include "openssl/aes.h"

#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, "native-lib",__VA_ARGS__)

extern "C"{

//JNIEXPORT jstring JNICALL
//Java_org_coreocto_dev_hf_androidclient_MainActivity_stringFromJNI(
//        JNIEnv* env,
//        jobject /* this */) {
//    std::string hello = "Hello from C++";
//    return env->NewStringUTF(hello.c_str());
//}

//added on 8 Oct 2017
JNIEXPORT jbyteArray JNICALL
Java_org_coreocto_dev_hf_androidclient_util_NativeAes128CbcImpl_aesCbcEncrypt(JNIEnv *env, jobject instance,
                                                                     jbyteArray key_,
                                                                     jbyteArray data_) {
    jbyte *key = env->GetByteArrayElements(key_, NULL);
    jbyte *data = env->GetByteArrayElements(data_, NULL);

    // TODO
    int keyLen = env->GetArrayLength(key_);
    unsigned char *keyBuf = (unsigned char *) key;

    int dataLen = env->GetArrayLength(data_);
    unsigned char *dataBuf = (unsigned char *) data;

    // prepare the iv
    unsigned char iv_enc[AES_BLOCK_SIZE];
    memset(iv_enc, 0, AES_BLOCK_SIZE);

    // buffers for encryption
    const size_t encslength = ((dataLen + AES_BLOCK_SIZE) / AES_BLOCK_SIZE) * AES_BLOCK_SIZE;
    unsigned char enc_out[encslength];
    memset(enc_out, 0, sizeof(enc_out));

    // perform pkcs5padding on original data
    unsigned char org_data[encslength];
    memset(org_data, encslength - dataLen, encslength);
    memcpy(org_data, dataBuf, dataLen);

    // i don't know why, but it seems that if I don't copy key from pointer first
    // it gives me a invalid address error
    // start copy key to array
    const int keyLength = 128;

    // prepare the encryption key
    unsigned char aes_key[keyLength / 8];
    memset(aes_key, 0, keyLength / 8);
    memcpy(aes_key, key, keyLen);
    // end copy key to array

    // perform encryption
    AES_KEY enc_key;
    AES_set_encrypt_key(aes_key, keyLength, &enc_key);
    AES_cbc_encrypt(org_data, enc_out, encslength, &enc_key, iv_enc, AES_ENCRYPT);

//    try return a byte array
    jbyteArray result = env->NewByteArray(encslength);
    env->SetByteArrayRegion(result, 0, encslength, (jbyte *) enc_out);

    //free mem
    env->ReleaseByteArrayElements(key_, key, 0);
    env->ReleaseByteArrayElements(data_, data, 0);

    return result;
}

//added on 8 Oct 2017
JNIEXPORT jbyteArray JNICALL
Java_org_coreocto_dev_hf_androidclient_util_NativeAes128CbcImpl_aesCbcDecrypt(JNIEnv *env, jobject instance,
                                                                     jbyteArray key_,
                                                                     jbyteArray data_) {
    jbyte *key = env->GetByteArrayElements(key_, NULL);
    jbyte *data = env->GetByteArrayElements(data_, NULL);

    // TODO
    int keyLen = env->GetArrayLength(key_);
    unsigned char *keyBuf = (unsigned char *) key;

    int dataLen = env->GetArrayLength(data_);
    unsigned char *dataBuf = (unsigned char *) data;

    // prepare the iv
    unsigned char iv_dec[AES_BLOCK_SIZE];
    memset(iv_dec, 0, AES_BLOCK_SIZE);

    // buffer for decryption
    unsigned char dec_out[dataLen];
    memset(dec_out, 0, sizeof(dec_out));

    // i don't know why, but it seems that if I don't copy key from pointer first
    // it gives me a invalid address error
    // start copy key to array
    const int keyLength = 128;

    // prepare the encryption key
    unsigned char aes_key[keyLength / 8];
    memset(aes_key, 0, keyLength / 8);
    memcpy(aes_key, key, keyLen);
    // end copy key to array

    AES_KEY dec_key;
    AES_set_decrypt_key(aes_key, keyLength, &dec_key);
    AES_cbc_encrypt(dataBuf, dec_out, dataLen, &dec_key, iv_dec, AES_DECRYPT);

    const int padByteCnt = dec_out[dataLen-1];

    //try return a byte array
    jbyteArray result = env->NewByteArray(dataLen - padByteCnt);
    env->SetByteArrayRegion(result, 0, dataLen - padByteCnt, (jbyte *) dec_out);

    //free mem
    env->ReleaseByteArrayElements(key_, key, 0);
    env->ReleaseByteArrayElements(data_, data, 0);

    return result;
}

}

