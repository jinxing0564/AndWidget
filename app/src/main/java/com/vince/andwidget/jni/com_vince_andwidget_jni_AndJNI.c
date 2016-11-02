//
// Created by tianweixin on 2016-11-2.
//

#include "com_vince_andwidget_jni_AndJNI.h"

JNIEXPORT jint JNICALL Java_com_vince_andwidget_jni_AndJNI_andloop
  (JNIEnv *env, jclass cls, jintArray pixels)
  {
    jint *body = (*env)->GetIntArrayElements(env, pixels, 0);
    jint len = (*env)->GetArrayLength(env, pixels);
    int emptyCount = 0;
    //int length=sizeof(pixels)/sizeof(pixels[0]);
    for(int i = 0; i < len; i++){
        if(body[i] == 0){
            emptyCount++;
        }
    }
    return emptyCount;
  }
