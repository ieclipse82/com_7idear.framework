package com_7idear.framework.utils;

import android.text.TextUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 文件工具类
 * @author iEclipse 2019/7/11
 * @description
 */
public class FileUtils {

    /**
     * 判断文件是否存在
     * @param filePath 文件路径及名称
     * @return
     */
    public static boolean existsFile(String filePath) {
        if (TxtUtils.isEmpty(filePath)) return false;
        return existsFile(new File(filePath));
    }

    /**
     * 判断文件是否存在
     * @param file 文件
     * @return
     */
    public static boolean existsFile(File file) {
        if (file == null) return false;
        return file.exists();
    }

    /**
     * 建立文件
     * @param filePath 文件路径及名称
     * @param isNew    是否新建
     * @return
     */
    public static File createFile(String filePath, boolean isNew) {
        if (TxtUtils.isEmpty(filePath)) return null;
        File file = null;
        try {
            file = new File(filePath);
            if (file.exists()) {
                if (isNew) {
                    file.delete();
                    file.createNewFile();
                }
            } else {
                createDir(file.getParentFile());
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 建立文件夹
     * @param filePath 文件路径及名称
     * @return
     */
    public static File createDir(String filePath) {
        if (TxtUtils.isEmpty(filePath)) return null;
        return createDir(new File(filePath));
    }

    /**
     * 建立文件夹
     * @param file 文件
     * @return
     */
    public static File createDir(File file) {
        if (file == null) return null;
        if (!file.exists()) file.mkdirs();
        return file;
    }

    /**
     * 删除文件
     * @param filePath 文件路径及名称
     * @return
     */
    public static boolean deleteFile(String filePath) {
        if (TxtUtils.isEmpty(filePath)) return false;
        return deleteFile(new File(filePath));
    }

    /**
     * 删除文件
     * @param file 文件
     * @return
     */
    public static boolean deleteFile(File file) {
        if (file != null && file.exists() && !file.isDirectory()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 删除文件夹或文件
     * @param filePath 文件路径及名称
     * @return
     */
    public static boolean deleteDirOrFile(String filePath) {
        if (TxtUtils.isEmpty(filePath)) return false;
        return deleteDirOrFile(new File(filePath));
    }

    /**
     * 删除文件夹或文件
     * @param file 文件
     * @return
     */
    public static boolean deleteDirOrFile(File file) {
        if (file == null) return false;
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        deleteDirOrFile(f);
                    }
                }
            }
            file.delete();
        }
        return true;
    }

    /**
     * 移动文件
     * @param srcFilePath 源文件路径及名称
     * @param newFilePath 新文件路径及名称
     * @return
     */
    public static boolean moveFile(String srcFilePath, String newFilePath) {
        if (TxtUtils.isEmpty(srcFilePath)) return false;
        return moveFile(new File(srcFilePath), newFilePath);
    }

    /**
     * 移动文件
     * @param srcFile     源文件
     * @param newFilePath 新文件路径及名称
     * @return
     */
    public static boolean moveFile(File srcFile, String newFilePath) {
        return moveFile(srcFile, new File(newFilePath));
    }

    /**
     * 移动文件
     * @param srcFile 源文件
     * @param newFile 新文件
     * @return
     */
    public static boolean moveFile(File srcFile, File newFile) {
        if (!existsFile(srcFile) || newFile == null) return false;
        createDir(newFile.getParentFile());
        return srcFile.renameTo(newFile);
    }

    /**
     * 保存字节数组到文件
     * @param bytes    字节数组
     * @param filePath 文件的路径
     * @return
     */
    public static boolean saveFile(byte[] bytes, String filePath) {
        if (bytes == null || TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        if (file.exists()) file.delete();
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write(bytes);
            fos.flush();
            fos.close();
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 写入文件
     * @param s
     * @param filePath
     */
    public static void writeToFile(String s, String filePath) {
        if (TxtUtils.isEmptyOR(s, filePath)) return;
        BufferedWriter bw = null;
        try {
            File f = createFile(filePath, false);
            bw = new BufferedWriter(new FileWriter(f, true));
            bw.write(s);
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bw = null;
            }
        }
    }
}
