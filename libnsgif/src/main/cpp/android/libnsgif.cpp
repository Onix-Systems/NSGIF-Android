#include <jni.h>
#include <string>
#include <ctime>
#include <android/log.h>
#include <android/bitmap.h>
#include <cstring>

#include <sys/stat.h>
#include <cstdio>
#include <cstdlib>
#include <cmath>

#include <vector>
#include "../libnsgif/nsgif.h"

class libnsgif;

#ifdef __cplusplus
extern "C" {
#endif

std::vector<nsgif*> data;

bool existGIF(int _id){
    std::vector<nsgif*>::iterator i;
    for ( i = data.begin(); i != data.end(); ++i ){
        nsgif* img = *i;
        if(img->getId()==_id){
            return true;
        }
    }
    return false;
}

bool removeGIF(int _id){
    std::vector<nsgif*>::iterator i;
    for ( i = data.begin(); i != data.end(); ++i ){
        nsgif* img = *i;
        if(img->getId()==_id){
            delete(img);
            data.erase(i);
            return true;
        }
    }
    return false;
}

bool addGIF(nsgif *_image){
    if(_image!=NULL){
        if(existGIF(_image->getId())){
            if(!removeGIF(_image->getId())){
                false;
            }
        }
        data.push_back(_image);
        return true;
    }
    return false;
}


nsgif* getGIF(int _id){
    std::vector<nsgif*>::iterator i;
    for ( i = data.begin(); i != data.end(); ++i ){
        nsgif* img = *i;
        if(img->getId()==_id){
            return img;
        }
    }
    return NULL;
}


JNIEXPORT jint JNICALL Java_com_libnsgif_NsGifLib_loadGifFile(
        JNIEnv * env, jobject obj, jstring name, int _id) {
    const char *_name = env->GetStringUTFChars(name, NULL);
    char realname[256];
    strcpy(realname, _name);
    struct stat sb;
    size_t size;

    if (stat(realname, &sb)) {
        return 0;
    }
    size = sb.st_size;

    FILE *fd;
    fd = fopen(realname, "rb");

    nsgif* cpp_gif = new nsgif(fd, size);

    bool code2 = cpp_gif->decode_frame(0);
    if (!code2) {
        return -1;
    }
    cpp_gif->setId(_id);
    if(!addGIF(cpp_gif)){
        return -1;
    }
    return 1;
}

JNIEXPORT jint JNICALL Java_com_libnsgif_NsGifLib_loadGifArray(
        JNIEnv * env, jobject obj, jbyteArray array, int _id) {

    jbyte* array_j;
    array_j = env->GetByteArrayElements(array, 0);
    jsize size = env->GetArrayLength(array);

    nsgif* cpp_gif = new nsgif(array_j, size);
    env->ReleaseByteArrayElements(array, array_j, 0);
    bool code2 = cpp_gif->decode_frame(0);
    if (!code2) {
        return -1;
    }
    cpp_gif->setId(_id);
    if(!addGIF(cpp_gif)){
        return -1;
    }
    return 1;
}

JNIEXPORT jint JNICALL
Java_com_libnsgif_NsGifLib_loadGifStream(JNIEnv *env, jobject obj, jobject inputStreamObj, int _id) {
    jclass inputStreamClass = env->GetObjectClass(inputStreamObj);

    jclass fileInputStreamClass = env->FindClass("java/io/FileInputStream");

    jboolean isFileInputStream = env->IsInstanceOf(inputStreamObj, fileInputStreamClass);

    jmethodID readMethod = env->GetMethodID(inputStreamClass, "read", "([B)I");
    if (readMethod == nullptr) {
        return -1;
    }

    jbyteArray byteArray = env->NewByteArray(1024);

    if (byteArray == nullptr) {
        return -1;
    }

    jbyte *cArray;

    if (isFileInputStream) {
        jmethodID getChannelMethod = env->GetMethodID(fileInputStreamClass, "getChannel", "()Ljava/nio/channels/FileChannel;");

        jobject fileChannelObj = env->CallObjectMethod(inputStreamObj, getChannelMethod);

        jclass fileChannelClass = env->FindClass("java/nio/channels/FileChannel");
        jmethodID sizeMethod = env->GetMethodID(fileChannelClass, "size", "()J");

        jlong fileSize = env->CallLongMethod(fileChannelObj, sizeMethod);

        cArray = (jbyte *)malloc(fileSize);
        env->DeleteLocalRef(fileChannelObj);
        env->DeleteLocalRef(fileChannelClass);
        if (cArray == nullptr) {
            return -1;
        }
    } else {
        cArray = nullptr;
    }

    jint bytesRead;
    int totalBytes = 0;
    while (true) {

        bytesRead = env->CallIntMethod(inputStreamObj, readMethod, byteArray);
        if (bytesRead < 0) {
            break;
        }

        jbyte *bytes = env->GetByteArrayElements(byteArray, nullptr);

        if (!isFileInputStream){
            auto *newCArray = (jbyte *)realloc(cArray, (totalBytes + bytesRead) * sizeof(jbyte));
            if (newCArray == nullptr) {
                env->ReleaseByteArrayElements(byteArray, bytes, JNI_ABORT);
                env->DeleteLocalRef(byteArray);
                break;
            }
            cArray = newCArray;
        }

        memcpy(&cArray[totalBytes], bytes, bytesRead * sizeof(jbyte));

        totalBytes += bytesRead;

        env->ReleaseByteArrayElements(byteArray, bytes, JNI_ABORT);
    }

    env->DeleteLocalRef(inputStreamClass);
    env->DeleteLocalRef(fileInputStreamClass);
    env->DeleteLocalRef(inputStreamObj);

    auto size = (jsize) totalBytes;

    auto* cpp_gif = new nsgif(cArray, size);

    bool code2 = cpp_gif->decode_frame(0);
    if (!code2) {
        return -1;
    }
    cpp_gif->setId(_id);

    env->DeleteLocalRef(byteArray);

    addGIF(cpp_gif);

    return 1;
}

JNIEXPORT void JNICALL Java_com_libnsgif_NsGifLib_destroyGif(JNIEnv * env, jobject obj, int _id) {
    removeGIF(_id);
}

JNIEXPORT jint JNICALL Java_com_libnsgif_NsGifLib_getGifWidth(
        JNIEnv * env, jobject obj, int _id) {
    if(getGIF(_id)!=NULL){
        return getGIF(_id)->get_width();
    }
    return -1;
}

JNIEXPORT jint JNICALL Java_com_libnsgif_NsGifLib_getGifHeight(
        JNIEnv * env, jobject obj, int _id) {
    if(getGIF(_id)!=NULL){
        return getGIF(_id)->get_height();
    }
    return -1;
}

JNIEXPORT jint JNICALL Java_com_libnsgif_NsGifLib_getGifCurrentFrame(
        JNIEnv * env, jobject obj, int _id) {
    if(getGIF(_id)!=NULL){
        return getGIF(_id)->get_current_frame();
    }
    return -1;
}

JNIEXPORT jint JNICALL Java_com_libnsgif_NsGifLib_getGifResult(
        JNIEnv * env, jobject obj, int _id) {
    if(getGIF(_id)!=NULL){
        return getGIF(_id)->get_gif_result();
    }
    return -1;
}

JNIEXPORT jint JNICALL Java_com_libnsgif_NsGifLib_getGifFrameCount(
        JNIEnv * env, jobject obj, int _id) {
    if(getGIF(_id)!=NULL){
        return getGIF(_id)->get_count();
    }
    return -1;
}

JNIEXPORT jint JNICALL Java_com_libnsgif_NsGifLib_setGifFrame(
        JNIEnv * env, jobject obj, jint number, int _id) {
    if(getGIF(_id)!=NULL){
        if( number>=0 && number<getGIF(_id)->get_count()){
            return getGIF(_id)->decode_frame(number);
        }
    }
    return -1;
}

JNIEXPORT jint JNICALL Java_com_libnsgif_NsGifLib_getGifFrameTime(
        JNIEnv * env, jobject obj, jint number, int id) {
    if(getGIF(id)!=NULL){
        if( number>=0 && number<getGIF(id)->get_count()){
            return getGIF(id)->get_time(number);
        }
    }
    return -1;
}

JNIEXPORT jint JNICALL Java_com_libnsgif_NsGifLib_getGifImageExist(JNIEnv * env, jobject  obj, jintArray intArray, int _id)
{
    if (getGIF(_id) != nullptr) {
        jint* dstElements = env->GetIntArrayElements(intArray, nullptr);
        jsize length = env->GetArrayLength(intArray);
        unsigned char* cArray = getGIF(_id)->get_image();

        size_t size = getGIF(_id)->get_width() * getGIF(_id)->get_height();

        if (length != size) {
            return -2;
        }

        for (int i = 0; i < length; i++) {
            auto red = (uint8_t) cArray[i * 4];
            auto green = (uint8_t) cArray[i * 4 + 1];
            auto blue = (uint8_t) cArray[i * 4 + 2];
            auto alpha = (uint8_t) cArray[i * 4 + 3];

            dstElements[i] = (alpha << 24) | (red << 16) | (green << 8) | blue;
        }

        env->ReleaseIntArrayElements(intArray, dstElements, 0);
        return 1;
    }
    return -1;
}

#ifdef __cplusplus
}
#endif
