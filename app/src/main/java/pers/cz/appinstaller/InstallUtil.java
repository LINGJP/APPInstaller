package pers.cz.appinstaller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class InstallUtil {
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
}
