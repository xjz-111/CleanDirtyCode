package com.leslie.cleanDirtyCode.scan;




import com.leslie.cleanDirtyCode.clean.CleanUtils;
import com.leslie.cleanDirtyCode.env.OnScanListener;
import com.leslie.cleanDirtyCode.env.Type;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.leslie.cleanDirtyCode.env.IEnv.*;


/**
 * @Desc： 扫描写入文件
 * @Author：Leslie
 * @Date： 2021-07-28 09:37
 * @Email： mr_feeling_heart@yeah.net
 */
public class ScanUtils {
    private static ScanUtils instance;

    private List<String> codePaths = new ArrayList<>(10000);
    private List<String> layoutPaths = new ArrayList<>(10000);
    private List<String> manifestPaths = new ArrayList<>();
    private OnScanListener onScanListener;

    public static synchronized ScanUtils getInstance() {
        if (null == instance) {
            synchronized (ScanUtils.class) {
                if (null == instance) {
                    instance = new ScanUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 开启扫描
     */
    public void startScan(final String projectPath, OnScanListener onScanListener) {
        this.onScanListener = onScanListener;
        CACHE_FILE_DIR = projectPath + File.separator + PATH_DIR;
        CACHE_FILE_CODE = CACHE_FILE_DIR + File.separator + CODE_PATH_FILE;
        CACHE_FILE_LAYOUT = CACHE_FILE_DIR + File.separator + LAYOUT_PATH_FILE;
        CACHE_FILE_MANIFEST = CACHE_FILE_DIR + File.separator + MANIFEST_PATH_FILE;
        DEL_FILE_RECORD = CACHE_FILE_DIR + File.separator + DEL_PATH_FILE;

        initDir();

        // 扫描代码
        scan(projectPath, Type.CODE);

        // 扫描AndroidManifest.xml
        scan(projectPath, Type.MANIFEST);

        // 扫描layout中的xml - 自定义View有可能只在xml中出现
        scan(projectPath, Type.LAYOUT);

        writContent();

        CleanUtils.getInstance().startClean(onScanListener);

        success();
        print("扫描并清除完成");
    }


    /**
     * 扫描
     *
     * @param path
     */
    private void scan(String path, Type type) {
        File file = new File(path);
        File[] fs = file.listFiles();
        if (null != fs) {
            for (File f : fs) {
                if (f.isDirectory() && !isIgnoreDir(f.getName())) {    //目录，递归
                    scan(f.getPath(), type);
                }
                if (f.isFile() && !isIgnoreDir(f.getParentFile().getName())) {    //文件
                    if (type == Type.CODE && (f.getName().endsWith(".java") || f.getName().endsWith(".kt"))) {
                        addPath(f.getAbsolutePath(), codePaths);
                    } else if (type == Type.LAYOUT && isLayout(f)) {
                        addPath(f.getAbsolutePath(), layoutPaths);
                    } else if (type == Type.MANIFEST && MANIFEST.equals(f.getName())) {
                        addPath(f.getAbsolutePath(), manifestPaths);
                    }
                }
            }
        }
    }

    private boolean isLayout(File f) {
        File parent = f.getParentFile();
        if (null != parent && f.getName().endsWith(".xml") && !MANIFEST.equals(f.getName()) && !parent.getName().startsWith("values")) {
            return true;
        }
        return false;
    }

    private void writContent() {

        // 代码
        writPath(CACHE_FILE_CODE, codePaths);

        // layout
        writPath(CACHE_FILE_LAYOUT, layoutPaths);

        // Manifest
        writPath(CACHE_FILE_MANIFEST, manifestPaths);
    }

    private void writPath(String filePath, List<String> paths) {
        File cacheFile = new File(filePath);
        if (cacheFile.exists()) {
            cacheFile.delete();
        }

        try {
            cacheFile.createNewFile();
            changeFolderPermission(cacheFile);

            for (String path : paths) {
                writ(filePath, path);
            }
        } catch (IOException e) {
            err(e.getMessage());
        }
    }


    private void writ(String path, String content) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path, true))) {
            bufferedWriter.write(content + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void addPath(String path, List<String> list) {
        list.add(path);
    }


    private void initDir() {
        try {
            File cacheDir = new File(CACHE_FILE_DIR);
            if (!cacheDir.exists()) {
                cacheDir.mkdir();
            }
            print("初始化目录成功 : " + CACHE_FILE_DIR);
        } catch (Exception e) {
            err(e.getMessage());
        }
    }

    private void changeFolderPermission(File dirFile) {
        Set<PosixFilePermission> perms = new HashSet<>();
        perms.add(PosixFilePermission.OWNER_READ);
        perms.add(PosixFilePermission.OWNER_WRITE);
        perms.add(PosixFilePermission.OWNER_EXECUTE);
        perms.add(PosixFilePermission.GROUP_READ);
        perms.add(PosixFilePermission.GROUP_WRITE);
        perms.add(PosixFilePermission.GROUP_EXECUTE);
        try {
            Path path = Paths.get(dirFile.getAbsolutePath());
            Files.setPosixFilePermissions(path, perms);
        } catch (Exception e) {
            err(e.getMessage());
        }
    }

    private boolean isIgnoreDir(String name){
        return "gradle".equals(name)
                || ".gradle".equals(name)
                || "libs".equals(name)
                || ".idea".equals(name)
                || "androidTest".equals(name);
    }


    private void print(String msg) {
        System.out.println("" + msg);
    }


    private void success() {
        if (null != onScanListener) {
            onScanListener.onSuccess("CleanDirtyCode::Success");
        }
        delFile(CACHE_FILE_CODE);
        delFile(CACHE_FILE_LAYOUT);
        delFile(CACHE_FILE_MANIFEST);
    }

    private void err(String msg) {
        if (null != onScanListener) {
            onScanListener.onErr("CleanDirtyCode::" + msg);
        }
    }


    private void delFile(String path){
        File file = new File(path);
        if (file.exists()){
            file.delete();
        }
    }

}
