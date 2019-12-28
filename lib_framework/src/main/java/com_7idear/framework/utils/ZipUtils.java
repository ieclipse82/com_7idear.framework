package com_7idear.framework.utils;

import com_7idear.framework.log.LogUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


/**
 * ZIP工具类
 * @author ieclipse 19-12-10
 * @description 实现字节压缩解压，文件压缩解压，文件目录压缩
 */
public class ZipUtils {

    private static final String TAG = "ZipUtils";

    private static final int    BUFFER   = 4096; //缓冲区大小
    private static final String EXT      = ".zip"; //扩展名
    private static final String BASE_DIR = ""; //压缩包相对目录

    /**
     * 字节压缩
     * @param bytes 字节数组
     * @return
     */
    public static byte[] compress(byte[] bytes) {
        if (bytes == null) return null;
        byte[] output = null;
        Deflater compresser = new Deflater();
        compresser.reset();
        compresser.setInput(bytes);
        compresser.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(bytes.length);
        try {
            byte[] buf = new byte[BUFFER];
            while (!compresser.finished()) {
                int i = compresser.deflate(buf);
                bos.write(buf, 0, i);
            }
            output = bos.toByteArray();
        } catch (Exception e) {
            output = bytes;
            LogUtils.catchException(e);
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                LogUtils.catchException(e);
            }
        }
        compresser.end();
        return output;
    }

    /**
     * 字节压缩
     * @param bytes 字节数组
     * @param os    输出流
     */
    public static void compress(byte[] bytes, OutputStream os) {
        DeflaterOutputStream dos = new DeflaterOutputStream(os);
        try {
            dos.write(bytes, 0, bytes.length);
            dos.finish();
            dos.flush();
        } catch (IOException e) {
            LogUtils.catchException(e);
        }
    }

    /**
     * 字节解压缩
     * @param bytes 字节数组
     * @return
     */
    public static byte[] decompress(byte[] bytes) {
        if (bytes == null) return null;
        byte[] output = null;
        Inflater decompresser = new Inflater();
        decompresser.reset();
        decompresser.setInput(bytes);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length);
        try {
            byte[] buf = new byte[BUFFER];
            while (!decompresser.finished()) {
                int i = decompresser.inflate(buf);
                baos.write(buf, 0, i);
            }
            output = baos.toByteArray();
        } catch (Exception e) {
            output = bytes;
            LogUtils.catchException(e);
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                LogUtils.catchException(e);
            }
        }
        decompresser.end();
        return output;
    }

    /**
     * 输入流解压缩
     * @param is 输入流
     * @return
     */
    public static byte[] decompress(InputStream is) {
        if (is == null) return null;
        InflaterInputStream iis = new InflaterInputStream(is);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte[] buf = new byte[BUFFER];
            int size = 0;
            while ((size = iis.read(buf, 0, BUFFER)) > 0) {
                baos.write(buf, 0, size);
            }
        } catch (IOException e) {
            LogUtils.catchException(e);
        }
        return baos.toByteArray();
    }

    /**
     * 文件或目录压缩
     * @param srcPath 文件或目录
     * @throws Exception
     */
    public static void compress(String srcPath)
            throws Exception {
        compress(new File(srcPath));
    }

    /**
     * 文件或目录压缩
     * @param srcPath  文件或目录
     * @param destPath 目标地址
     */
    public static void compress(String srcPath, String destPath)
            throws Exception {
        compress(new File(srcPath), destPath);
    }

    /**
     * 文件压缩
     * @param srcFile 文件
     * @throws Exception
     */
    public static void compress(File srcFile)
            throws Exception {
        String name = srcFile.getName();
        String basePath = srcFile.getParent();
        String destPath = basePath + name + EXT;
        compress(srcFile, destPath);
    }

    /**
     * 文件压缩
     * @param srcFile  文件
     * @param destPath 目标地址
     * @throws Exception
     */
    public static void compress(File srcFile, String destPath)
            throws Exception {
        compress(srcFile, new File(destPath));
    }

    /**
     * 文件压缩
     * @param srcFile  文件
     * @param destFile
     * @throws Exception
     */
    public static void compress(File srcFile, File destFile)
            throws Exception {
        CheckedOutputStream cos = new CheckedOutputStream(new FileOutputStream(destFile),
                new CRC32());
        ZipOutputStream zos = new ZipOutputStream(cos);
        compress(srcFile, zos, BASE_DIR);
        zos.flush();
        zos.close();
    }

    /**
     * 文件压缩
     * @param srcFile  文件
     * @param zos      输出流
     * @param basePath 压缩包内相对路径
     * @throws Exception
     */
    private static void compress(File srcFile, ZipOutputStream zos, String basePath)
            throws Exception {
        if (srcFile.isDirectory()) {
            compressDir(srcFile, zos, basePath);
        } else {
            compressFile(srcFile, zos, basePath);
        }
    }

    /**
     * 压缩目录
     * @param dir      目录
     * @param zos      输出流
     * @param basePath 压缩包内相对路径
     * @throws Exception
     */
    private static void compressDir(File dir, ZipOutputStream zos, String basePath)
            throws Exception {
        File[] files = dir.listFiles();
        if (files.length == 0) {
            ZipEntry entry = new ZipEntry(basePath + dir.getName() + File.separator);
            zos.putNextEntry(entry);
            zos.closeEntry();
        }
        for (File file : files) {
            compress(file, zos, basePath + dir.getName() + File.separator);
        }
    }

    /**
     * 文件压缩
     * @param file     文件
     * @param zos      输出流
     * @param basePath 压缩文件中的当前路径
     * @throws Exception
     */
    private static void compressFile(File file, ZipOutputStream zos, String basePath)
            throws Exception {
        ZipEntry entry = new ZipEntry(basePath + file.getName());
        zos.putNextEntry(entry);
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        int size;
        byte buf[] = new byte[BUFFER];
        while ((size = bis.read(buf, 0, BUFFER)) != -1) {
            zos.write(buf, 0, size);
        }
        bis.close();
        zos.closeEntry();
    }

    /**
     * 文件或目录解压缩
     * @param srcPath 文件或目录
     * @throws Exception
     */
    public static void decompress(String srcPath)
            throws Exception {
        decompress(new File(srcPath));
    }

    /**
     * 文件或目录解压缩
     * @param srcPath  文件或目录
     * @param destPath 目标地址
     * @throws Exception
     */
    public static void decompress(String srcPath, String destPath)
            throws Exception {
        decompress(new File(srcPath), destPath);
    }

    /**
     * 文件解压缩
     * @param srcFile 文件
     * @throws Exception
     */
    public static void decompress(File srcFile)
            throws Exception {
        String basePath = srcFile.getParent();
        decompress(srcFile, basePath);
    }

    /**
     * 文件解压缩
     * @param srcFile  文件
     * @param destPath 目标地址
     * @throws Exception
     */
    public static void decompress(File srcFile, String destPath)
            throws Exception {
        decompress(srcFile, new File(destPath));
    }

    /**
     * 文件解压缩
     * @param srcFile  文件
     * @param destFile 目标文件
     * @throws Exception
     */
    public static void decompress(File srcFile, File destFile)
            throws Exception {
        CheckedInputStream cis = new CheckedInputStream(new FileInputStream(srcFile), new CRC32());
        ZipInputStream zis = new ZipInputStream(cis);
        decompress(destFile, zis);
        zis.close();

    }

    /**
     * 文件解压缩
     * @param destFile 文件
     * @param zis      输入流
     * @throws Exception
     */
    private static void decompress(File destFile, ZipInputStream zis)
            throws Exception {
        ZipEntry entry = null;
        while ((entry = zis.getNextEntry()) != null) {
            String dir = destFile.getPath() + File.separator + entry.getName();
            File dirFile = new File(dir);
            checkDir(dirFile);
            if (entry.isDirectory()) {
                dirFile.mkdirs();
            } else {
                decompressFile(dirFile, zis);
            }
            zis.closeEntry();
        }
    }

    /**
     * 文件解压缩
     * @param destFile 文件
     * @param zis      输入流
     * @throws Exception
     */
    private static void decompressFile(File destFile, ZipInputStream zis)
            throws Exception {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile));
        int size;
        byte buf[] = new byte[BUFFER];
        while ((size = zis.read(buf, 0, BUFFER)) != -1) {
            bos.write(buf, 0, size);
        }
        bos.close();
    }

    /**
     * 检查文件上级目录，如果不存在则创建
     * @param dirFile 文件
     */
    private static void checkDir(File dirFile) {
        File parentFile = dirFile.getParentFile();
        if (!parentFile.exists()) {
            checkDir(parentFile);
            parentFile.mkdir();
        }

    }
}
