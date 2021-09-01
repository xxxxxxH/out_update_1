package net.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    public static void saveFile(String content) {
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = System.currentTimeMillis() + ".testupdate";
        File file = new File(filePath + File.separator + fileName);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(content.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
