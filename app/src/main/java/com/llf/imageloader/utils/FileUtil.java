package com.llf.imageloader.utils;

import android.content.Context;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by llf on 2017/9/20.
 * 访问内存的工具类
 */

public class FileUtil {
    private static FileUtil instance;

    private Context context;

    private FileUtil(Context context) {
        this.context = context.getApplicationContext();
    }

    public static FileUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (FileUtil.class) {
                if (instance == null) {
                    instance = new FileUtil(context);
                }
            }
        }
        return instance;
    }

    /**
     * 将文件存储到内存中
     */
    public void writeFileToStorage(String fileName, byte[] b) {
        FileOutputStream fos = null;
        try {
            File file = new File(context.getFilesDir(), fileName);
            fos = new FileOutputStream(file);
            fos.write(b, 0, b.length);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从内存中读取文件的字节码
     */
    public byte[] readBytesFromStorage(String fileName) {
        byte[] b = null;
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        try {
            fis = context.openFileInput(fileName);
            baos = new ByteArrayOutputStream();
            byte[] tmp = new byte[1024];
            int len = 0;
            while ((len = fis.read(tmp)) != -1) {
                baos.write(tmp, 0, len);
            }
            b = baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return b;
    }
}
