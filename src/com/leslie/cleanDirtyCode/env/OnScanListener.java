package com.leslie.cleanDirtyCode.env;

/**
 * @Desc：
 * @Author：Leslie
 * @Date： 2021-07-28 18:27
 * @Email： mr_feeling_heart@yeah.net
 */
public interface OnScanListener {

    void onSuccess(String msg);

    void onErr(String msg);
}
