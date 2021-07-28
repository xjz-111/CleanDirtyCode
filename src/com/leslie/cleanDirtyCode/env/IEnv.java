package com.leslie.cleanDirtyCode.env;

/**
 * @Desc：
 * @Author：Leslie
 * @Date： 2021-07-28 14:32
 * @Email： mr_feeling_heart@yeah.net
 */
public class IEnv {
    private static String PATH_FILE_SUFFIX = ".txt";
    public static String PATH_DIR = "Temp_CleanDirtyCode";
    public static String CODE_PATH_FILE = "codePath" + PATH_FILE_SUFFIX;
    public static String LAYOUT_PATH_FILE = "layoutPath" + PATH_FILE_SUFFIX;
    public static String MANIFEST_PATH_FILE = "manifestPath" + PATH_FILE_SUFFIX;
    public static String DEL_PATH_FILE = "del" + PATH_FILE_SUFFIX;
    public static String CACHE_FILE_DIR;
    public static String CACHE_FILE_CODE;
    public static String CACHE_FILE_LAYOUT;
    public static String CACHE_FILE_MANIFEST;
    public static String DEL_FILE_RECORD;
    public static String MANIFEST = "AndroidManifest.xml";
    public static String PACKAGE_START = "package ";
    public static String IMPORT_START = "import ";
}
