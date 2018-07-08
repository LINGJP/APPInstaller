package pers.cz.appinstaller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Util {
    public static final int NONEEDDECODEAPK = 17;
    public static final int NONEEDDECODEBPK = 18;
    public static final int NEEDDECODEBPK = 19;

    public static synchronized void openApp(String packageName, Context context) throws NameNotFoundException {
        Intent resolveIntent = new Intent("android.intent.action.MAIN");
        resolveIntent.addCategory("android.intent.category.LAUNCHER");
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS);
        resolveIntent.setPackage(packageInfo.packageName);
        ResolveInfo resolveInfo = packageManager.queryIntentActivities(resolveIntent, PackageManager.MATCH_DEFAULT_ONLY).iterator().next();
        if (resolveInfo != null) {
            String className = resolveInfo.activityInfo.name;
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.LAUNCHER");
            intent.setComponent(new ComponentName(packageName, className));
            context.startActivity(intent);
        }
    }

    public static synchronized String getPackageNameByFilePath(Context context, String archiveFilePath) {
        PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);
        if (packageInfo == null)
            return null;
        ApplicationInfo appInfo = packageInfo.applicationInfo;
        appInfo.sourceDir = archiveFilePath;
        appInfo.publicSourceDir = archiveFilePath;
        return appInfo.packageName;
    }

    public static synchronized String getAppNameByFilePath(Context context, String archiveFilePath) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);
        if (packageInfo == null)
            return null;
        ApplicationInfo appInfo = packageInfo.applicationInfo;
        appInfo.sourceDir = archiveFilePath;
        appInfo.publicSourceDir = archiveFilePath;
        return packageManager.getApplicationLabel(appInfo).toString();
    }

    public static synchronized Drawable getAppIconByByFilePath(Context context, String apkAbsolutePath) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(apkAbsolutePath, PackageManager.GET_ACTIVITIES);
        if (packageInfo == null)
            return null;
        ApplicationInfo appInfo = packageInfo.applicationInfo;
        appInfo.sourceDir = apkAbsolutePath;
        appInfo.publicSourceDir = apkAbsolutePath;
        return packageManager.getApplicationIcon(appInfo);
    }

    public static synchronized PackageInfo getPackageInfoByPackageName(Context context, String packageName) {
        try {
            return context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getFileNameExceptExtendNameByFilePath(String filepath) {
        return filepath.substring(filepath.lastIndexOf("/") + 1, filepath.lastIndexOf("."));
    }

    public static long getSDCardAvailMemory() {
        if (!"mounted".equals(Environment.getExternalStorageState()))
            return 0;
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return ((long) statFs.getBlockSize()) * ((long) statFs.getAvailableBlocks());
    }

    /**
     * s系列不支持
     *
     * @param apkAbsolutePath
     * @throws Exception
     */
    public static String decodeApkFile(String apkAbsolutePath) {
        byte[] xorCode = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".getBytes();
        File file = new File(apkAbsolutePath);
        if (!Environment.getExternalStorageDirectory().exists() || getSDCardAvailMemory() <= file.length())
            return "";
        String outputPath = Environment.getExternalStorageDirectory() + "/bpk-decode/" + getFileNameExceptExtendNameByFilePath(apkAbsolutePath);
        File outfile = new File(outputPath);
        int offset = 0;
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(file);
            outputStream = new FileOutputStream(outfile);
            byte[] buffer = new byte[1024];
            while (true) {
                int len = inputStream.read(buffer);
                if (len == -1) {
                    break;
                }
                for (int i = 0; i < len; i++) {
                    buffer[i] = (byte) (buffer[i] ^ xorCode[offset % xorCode.length]);
                    offset++;
                }
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
            return outputPath;
        } catch (Exception e) {
            e.printStackTrace();
            outfile.deleteOnExit();
            return "";
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 17-外部应用 18-内部应用 19-加密应用
     *
     * @param apkAbsolutePath
     * @return
     * @throws Exception
     */
    public static int checkApkIfEncode(String apkAbsolutePath) {
        byte[] originBPKHeader = new byte[]{(byte) 66, (byte) 80, (byte) 75, (byte) 3};    //42 50 4B 03 BPK头部
        byte[] encodeBPKHeader = new byte[]{(byte) 35, (byte) 50, (byte) 40, (byte) 103};  //23 32 28 67 加密头部
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(apkAbsolutePath);
            byte[] buffer = new byte[4];
            fileInputStream.read(buffer);
            boolean isOriginBPKHeader = true;
            for (int i = 0; i < originBPKHeader.length; i++) {
                if (buffer[i] != originBPKHeader[i]) {
                    isOriginBPKHeader = false;
                    break;
                }
            }
            if (isOriginBPKHeader)
                return Util.NONEEDDECODEBPK;
            for (int i = 0; i < encodeBPKHeader.length; i++)
                if (buffer[i] != encodeBPKHeader[i])
                    return Util.NONEEDDECODEAPK;
            return Util.NEEDDECODEBPK;
        } catch (Exception e) {
            e.printStackTrace();
            return Util.NONEEDDECODEAPK;
        } finally {
            try {
                if (fileInputStream != null)
                    fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
