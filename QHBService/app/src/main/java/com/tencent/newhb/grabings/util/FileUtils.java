package com.tencent.newhb.grabings.util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    public static void write(Context context, String fileName, InputStream data) {
        // TODO Auto-generated method stub
        FileOutputStream out;

        byte[] buf = new byte[2048];
        try {
            out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            int read = 0;
            while (read != -1) {
                read = data.read(buf);
                out.write(buf, 0, read);
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void write(Context context, File file, InputStream data) {
        // TODO Auto-generated method stub
        FileOutputStream out;

        byte[] buf = new byte[2048];
        try {
            out = new FileOutputStream(file);
            int read = 0;
            while (read != -1) {
                read = data.read(buf);
                out.write(buf, 0, read);
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean check(Context context, String filename) {
        String[] list = context.getFilesDir().list();
        for (int i = 0; i < list.length; i++) {
            if (list[i].equals(filename)) {
                return true;
            }
        }
        return false;
    }

    public static boolean check(String url) {
        File file = new File(url);
        if (file != null) {
            return file.exists();
        }

        return false;
    }

    public static String read(Context context, String fileName) {
        // TODO Auto-generated method stub
        try {
            FileInputStream fileInputStream = context.openFileInput(fileName);
            byte[] bs = new byte[1024];
            int hasRead = 0;
            StringBuilder stringBuilder = new StringBuilder("");
            while ((hasRead = fileInputStream.read(bs)) > 0) {
                stringBuilder.append(new String(bs, 0, hasRead));
            }
            fileInputStream.close();
            return stringBuilder.toString();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public static boolean deleteDirectory(String filePath) {
        boolean flag = false;
        // 如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        // 遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                // 删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            } else {
                // 删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        return flag;
    }

    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    public static boolean deleteFile(File file) {
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }


    public static String encodeBase64File(String path) {
        try {
            File file = new File(path);
            FileInputStream inputFile = new FileInputStream(file);
            byte[] buffer = new byte[(int)file.length()];
            inputFile.read(buffer);
            inputFile.close();
            return Base64.encodeToString(buffer, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getBasePath(Context context) {
        return generateUrl(context);
    }

    public static String generateUrl(Context context) {
        File file = createCacheFile(context, true);
        String path = file.getAbsolutePath() + "/QHBSQ/";
        return path;
    }

    public static File createCacheFile(Context context, boolean useExternalCache) {
        File file = null;

        String url;
        try {
            url = Environment.getExternalStorageState();
        } catch (NullPointerException var5) {
            url = "";
        }

        if(useExternalCache && Environment.MEDIA_MOUNTED.equals(url) && checkPermission(context)) {
            file = createExternalCacheFile(context);
        }

        if(file == null) {
            file = context.getCacheDir();
        }

        if(file == null) {
            String var4 = "/data/data/" + context.getPackageName() + "/files/cache/";
            file = new File(var4);
        }

        return file;
    }

    private static File createExternalCacheFile(Context context) {
        File file = context.getExternalCacheDir();
        if(file == null) {
            String path = Environment.getExternalStorageDirectory() + File.separator + "Android" + File.separator + "data" + File.separator + context.getPackageName() + File.separator + "cache";
            file = new File(path);
        }

        if(!file.exists() && !file.mkdirs()) {
            return null;
        } else {
            try {
                (new File(file, ".nomedia")).createNewFile();
                (new File(file, ".nomedia")).delete();
                return file;
            } catch (Exception var3) {
                return null;
            }
        }
    }

    private static boolean checkPermission(Context context) {
        int permission = context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
        return permission == 0;
    }

    public static String getLocationPath(Context context, String var1) {
        if(TextUtils.isEmpty(var1)) {
            return null;
        } else {
            String[] strings = selectFileUrlByUri(context, Uri.parse(var1));
            if(strings == null) {
                return null;
            } else {
                String path = strings[0];

                return !TextUtils.isEmpty(path)? path : null;
            }
        }
    }

    public static String[] selectFileUrlByUri(Context context, Uri uri) {
        Cursor cursor = null;

        String[] strings;
        try {
            String[] proj = new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.TITLE};
            cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if(!cursor.moveToFirst()) {
                return null;
            }

            String id = cursor.getString(0);
            String data = cursor.getString(1);
            strings = new String[]{id, data};
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }

        return strings;
    }

    public static void saveImageToGallery(Context context, File file) {

        // 其次把文件插入到系统图库
        try {
            String locationPath = MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getAbsolutePath(), null);
            locationPath = FileUtils.getLocationPath(context, locationPath);
            if(!TextUtils.isEmpty(locationPath)) {
                context.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(new File(locationPath))));
                file.delete();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
