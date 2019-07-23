package sample.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SystemCmd {

    private static Process currentProcess = null;

    public static String exe(String cmd) throws IOException, InterruptedException {

        System.out.println("exe, cmd=" + cmd);

        StringBuilder sb = new StringBuilder();

        Process process = Runtime.getRuntime().exec(cmd);

        currentProcess = process;

        process.waitFor();

        getResult(sb, process.getInputStream());

        getResult(sb, process.getErrorStream());

        return sb.toString();
    }

    public static void getResult(StringBuilder sb, InputStream is) {
        try {
            BufferedReader rb = new BufferedReader(new InputStreamReader(is));
            String line = rb.readLine();
            while (line != null) {
                sb.append(line).append(System.getProperty("line.separator"));
                line = rb.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopCurrentProcess() {
        System.out.println("stopCurrentProcess");
        try {
            if (currentProcess != null) {
                currentProcess.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}