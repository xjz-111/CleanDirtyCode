package com.leslie.cleanDirtyCode.clean;



import com.leslie.cleanDirtyCode.env.OnScanListener;
import com.leslie.cleanDirtyCode.env.Type;
import com.leslie.cleanDirtyCode.scan.ScanUtils;

import java.io.*;

import static com.leslie.cleanDirtyCode.env.IEnv.*;


/**
 * @Desc：  根据扫描文件来做清除处理
 * @Author：Leslie
 * @Date：  2021-07-28 12:27
 * @Email： mr_feeling_heart@yeah.net
 */
public class CleanUtils {
    private static CleanUtils instance;
    private OnScanListener onScanListener;

    public static synchronized CleanUtils getInstance() {
        if (null == instance) {
            synchronized (ScanUtils.class) {
                if (null == instance) {
                    instance = new CleanUtils();
                }
            }
        }
        return instance;
    }


    public void startClean(OnScanListener onScanListener){
        this.onScanListener = onScanListener;
        BufferedReader read = null;
        int count = 1;
        try {
            while (count > 0){
                count = 0;
                read = new BufferedReader(new FileReader(CACHE_FILE_CODE));
                String line;
                while ((line = read.readLine()) != null && !"".equals(line.trim())) {
                    line = line.trim();
                    if (!"".equals(line)) {
                        String[] array = line.split(File.separator);
                        String name = array[array.length - 1].replace(".java", "").replace("kt", "");
                        String absName = getAbsName(line, array[array.length - 1]);

                        // 是否在代码中使用
                        boolean isUseInCode = isUsed(Type.CODE, CACHE_FILE_CODE, line, absName, name);

                        // 是否在Manifest中使用
                        boolean isUseInManifest = isUsed(Type.MANIFEST, CACHE_FILE_MANIFEST, line, absName, name);

                        // 是否在layout中使用
                        boolean isUseLayout = isUsed(Type.LAYOUT, CACHE_FILE_LAYOUT, line, absName, name);

                        if (!isUseInCode && !isUseLayout && !isUseInManifest){
                            writFile(line, DEL_FILE_RECORD, false);
                            del(line);
                            // 只要有一个未被使用的存在，则继续循环 - 防止其他的只在这个未被使用的类中被引入
                            count++;
                            print("没有被用到的类 : " + line);
                        }

                    }
                }
                read.close();
            }
        } catch (IOException e) {
            err(e.getMessage());
        } finally {
            close(read);
        }
    }



    /**
     * 是否被使用
     * @param path
     * @param absName
     * @param name
     * @return
     */
    private boolean isUsed(Type type, String fileName, String path, String absName, String name){
        BufferedReader read = null;
        try {
            read = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = read.readLine()) != null) {

                if (!path.equals(line)){
                    boolean isUsed = false;
                    if (type == Type.CODE){
                        isUsed = isContainInCode(line, absName, name);
                    } else if (type == Type.LAYOUT){
                        isUsed = isContainInLayout(line, absName, name);
                    } else if (type == Type.MANIFEST){
                        isUsed = isContainInManifest(line, absName, name);
                    }
                    if (isUsed) return true;
                }

            }
            read.close();
        } catch (IOException e) {
            err(e.getMessage());
        } finally {
            close(read);
        }
        return false;
    }


    /**
     * 获取文件绝对路径
     * @param path
     * @param fileName
     * @return
     */
    private String getAbsName(String path, String fileName){
        BufferedReader read = null;
        try {
            File f = new File(path);
            read = new BufferedReader(new FileReader(f));
            String line;
            while ((line = read.readLine()) != null) {
                line = line.trim();
                if (line.startsWith(PACKAGE_START)){
                    return line.replace(PACKAGE_START, "").replace(";", "") + "." + fileName;
                }
            }
            return fileName;

        } catch (IOException e) {
            err(e.getMessage());
        } finally {
            close(read);
        }

        return "";
    }


    /**
     * 判断是否在单个文件中出现
     * @param path
     * @param absName
     * @param name
     * @return
     */
    private boolean isContainInCode(String path, String absName, String name){
        BufferedReader read = null;
        try {
            File f = new File(path);
            read = new BufferedReader(new FileReader(f));
            String line;
            while ((line = read.readLine()) != null) {
                line = line.trim();
                if (line.startsWith(IMPORT_START) && line.replace(IMPORT_START, "").replace(";", "").equals(absName)){
                    return true;
                } else if (line.contains(name)){
                    return true;
                }
            }
        } catch (IOException e) {
            err(e.getMessage());
        } finally {
            close(read);
        }
        return false;
    }


    /**
     * 判断是否在Manifest文件中出现
     * @param path
     * @param absName
     * @param name
     * @return
     */
    private boolean isContainInManifest(String path, String absName, String name){
        BufferedReader read = null;
        try {
            File f = new File(path);
            read = new BufferedReader(new FileReader(f));
            String line;
            while ((line = read.readLine()) != null) {
                line = line.trim();
                if (line.contains(absName) || line.endsWith(name)){
                    return true;
                }
            }
        } catch (IOException e) {
            err(e.getMessage());
        } finally {
            close(read);
        }
        return false;
    }


    /**
     * 判断是否在布局文件中出现
     * @param path
     * @param absName
     * @param name
     * @return
     */
    private boolean isContainInLayout(String path, String absName, String name){
        BufferedReader read = null;
        try {
            File f = new File(path);
            read = new BufferedReader(new FileReader(f));
            String line;
            while ((line = read.readLine()) != null) {
                line = line.trim();
                if (line.contains(absName) || line.endsWith(name)){
                    return true;
                }
            }
        } catch (IOException e) {
            err(e.getMessage());
        } finally {
            close(read);
        }
        return false;
    }



    private void del(String path){
        File file = new File(path);
        if (file.exists()){
            file.delete();
        }

        writFile(getNewCotent(path, CACHE_FILE_CODE), CACHE_FILE_CODE, true);
    }

    /**
     * 删除某行后返回剩余内容
     * @param path
     * @param fileName
     * @return
     */
    private String getNewCotent(String path, String fileName){
        BufferedReader read = null;
        StringBuilder sb = new StringBuilder();
        try {
            File f = new File(fileName);
            read = new BufferedReader(new FileReader(f));
            String line;
            while ((line = read.readLine()) != null) {
                if (!line.equals(path)){
                    sb.append(line + "\n");
                }
            }
        } catch (IOException e) {
            err(e.getMessage());
        } finally {
            close(read);
            return sb.toString();
        }
    }

    private void writFile(String content, String filePath, boolean isDel){
        File cacheFile = new File(filePath);
        if (cacheFile.exists() && isDel) {
            cacheFile.delete();
        }

        BufferedWriter bufferedWriter = null;
        try {
            cacheFile.createNewFile();
            bufferedWriter = new BufferedWriter(new FileWriter(filePath, true));
            bufferedWriter.write(content + "\n");
        } catch (IOException e) {
            err(e.getMessage());
        } finally {
            close(bufferedWriter);
        }
    }

    private void close(Closeable closeable){
        if (null != closeable) {
            try {
                closeable.close();
            } catch (IOException e) {
                err(e.getMessage());
            }
        }
    }

    private void print(String msg) {
        System.out.println("" + msg);
    }

    private void err(String msg) {
        if (null != onScanListener) {
            onScanListener.onErr("CleanDirtyCode::" + msg);
        }
    }
}
