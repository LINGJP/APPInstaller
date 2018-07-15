package pers.cz.appinstaller.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.*;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.StatFs;

import java.io.*;
import java.nio.channels.FileLock;
import java.util.List;

public class InstallUtil {
    private static final int NONEEDDECODEAPK = 17;
    private static final int NONEEDDECODEBPK = 18;
    private static final int NEEDDECODEBPK = 19;

    public static synchronized void openApp(String packageName, Context context) throws NameNotFoundException {
        Intent resolveIntent = new Intent("android.intent.action.MAIN");
        resolveIntent.addCategory("android.intent.category.LAUNCHER");
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS);
        resolveIntent.setPackage(packageInfo.packageName);
        ResolveInfo resolveInfo = packageManager.queryIntentActivities(resolveIntent, 0).iterator().next();
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

    public static synchronized String getAppVersionByFilePath(Context context, String archiveFilePath) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);
        if (packageInfo == null)
            return null;
        return packageInfo.versionName;
    }

    public static synchronized Drawable getAppIconByFilePath(Context context, String apkAbsolutePath) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(apkAbsolutePath, PackageManager.GET_ACTIVITIES);
        if (packageInfo == null)
            return null;
        ApplicationInfo appInfo = packageInfo.applicationInfo;
        appInfo.sourceDir = apkAbsolutePath;
        appInfo.publicSourceDir = apkAbsolutePath;
        return packageManager.getApplicationIcon(appInfo);
    }

    public static synchronized String[] getAppPermissionsByFilePath(Context context, String apkAbsolutePath) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(apkAbsolutePath, PackageManager.GET_PERMISSIONS);
        String[] permissionNames = packageInfo.requestedPermissions;
        if (permissionNames == null)
            return new String[0];
        for (int i = 0; i < permissionNames.length; i++) {
            String permissionName = permissionNames[i];
            try {
                permissionName = packageManager.getPermissionInfo(permissionName, 0).loadLabel(packageManager).toString();
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            permissionNames[i] = permissionName;
        }
        return permissionNames;
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
            return "outOfMemory";
        String outputPath = Environment.getExternalStorageDirectory() + "/bpk-decode/" + getFileNameExceptExtendNameByFilePath(apkAbsolutePath);
        File outfile = new File(outputPath);
        int offset = 0;
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        FileLock fileLock = null;
        try {
            inputStream = new FileInputStream(file);
            outputStream = new FileOutputStream(outfile);
            fileLock = outputStream.getChannel().lock();
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
            outfile.delete();
            return "";
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
                if (outputStream != null)
                    outputStream.close();
                if (fileLock != null)
                    fileLock.release();
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
                return InstallUtil.NONEEDDECODEBPK;
            for (int i = 0; i < encodeBPKHeader.length; i++)
                if (buffer[i] != encodeBPKHeader[i])
                    return InstallUtil.NONEEDDECODEAPK;
            return InstallUtil.NEEDDECODEBPK;
        } catch (Exception e) {
            e.printStackTrace();
            return InstallUtil.NONEEDDECODEAPK;
        } finally {
            try {
                if (fileInputStream != null)
                    fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static synchronized String installApk(String apkAbsolutePath) {
        ProcessBuilder processBuilder = new ProcessBuilder("pm", "install", "-r", apkAbsolutePath);
        Process process = null;
        ByteArrayOutputStream outputStream = null;
        InputStream errorStream = null;
        InputStream inputStream = null;
        try {
            process = processBuilder.start();
            outputStream = new ByteArrayOutputStream();
            errorStream = process.getErrorStream();
            inputStream = process.getInputStream();
            int length;
            byte[] buffer = new byte[1024];
            while ((length = errorStream.read(buffer)) != -1)
                outputStream.write(buffer, 0, length);
            while ((length = inputStream.read(buffer)) != -1)
                outputStream.write(buffer, 0, length);
            return new String(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close();
                if (errorStream != null)
                    errorStream.close();
                if (inputStream != null)
                    inputStream.close();
                if (process != null)
                    process.destroy();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getMachineId() {
        BufferedReader fileReader = null;
        try {
            fileReader = new BufferedReader(new FileReader("/proc/bbksn"));
            String line;
            StringBuilder machineId = new StringBuilder();
            while ((line = fileReader.readLine()) != null)
                machineId.append(line);
            return machineId.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileReader != null)
                    fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
