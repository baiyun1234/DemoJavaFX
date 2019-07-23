package sample.adb;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import sample.base.BaseController;
import sample.util.StringUtil;
import sample.util.SystemCmd;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class ADBController extends BaseController {

    public static void start() {
        start(ADBController.class, new Stage(), "ADB", "ADB.fxml");
    }

    @FXML
    private TextField txt_record_time;
    @FXML
    private TextField txt_record_date;
    @FXML
    private Button btn_logcat;
    @FXML
    private TextArea ta_logcat;
    @FXML
    private ProgressBar pb_record;
    @FXML
    private Label label_device;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void onCheckDevice(ActionEvent actionEvent) {
        printLog("onCheckDevice start");

        try {
            String result = SystemCmd.exe("adb devices");
            printLog(result);
            String[] results = result.split(System.lineSeparator());
            if (result.length() > 2) {
                label_device.setText(results[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            printLog(e.toString());
            label_device.setText("Null");
        }

        printLog("onCheckDevice finish");
    }

    @FXML
    public void onScreenCapture(ActionEvent actionEvent) {

        printLog("onScreenCapture start");

        printLog("user.dir=" + System.getProperty("user.dir"));

        try {

            /*
                MaxOS的GUI程序和终端程序使用了两套不同的环境变量设置，修改profile文件仅仅在使用终端的时候生效，
                如果想要GUI程序也能继承相应的环境变量设置，则需要从终端启动应用程序才可以。

                或者通过open命令启动应用：
                open /Applications/Sunflower.app
             */
//            sb.append("\n").append("ANDROID_HOME: " + System.getenv("ANDROID_HOME"));

            String imageFileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis())) + ".png";

            printLog(SystemCmd.exe("adb shell screencap -p /sdcard/" + imageFileName));

            File file = new File("image/");
            if (!file.exists()) {
                file.mkdirs();
            }
            printLog(SystemCmd.exe("adb pull /sdcard/" + imageFileName + " image/" + imageFileName));

            printLog(SystemCmd.exe("adb shell rm /sdcard/" + imageFileName));

        } catch (Exception e) {
            e.printStackTrace();
            printLog("Error:" + e);
        }

        printLog("onScreenCapture finish");
    }

    private Service recordService;
    private Service timerService;
    private static long currentTime = 0;
    private boolean isRecording = false;

    @FXML
    public void onScreenRecord(ActionEvent actionEvent) {
        printLog("onScreenRecord start");

        if (isRecording) {
            showAlertDialog(Alert.AlertType.WARNING, "警告", "录屏", "正在进行录制，请稍后");
            return;
        }
        isRecording = true;

        printLog("user.dir=" + System.getProperty("user.dir"));

        // 指定录制时间，ADB默认和最大录制时间均为 180s
        String timeStr = txt_record_time.getText().trim();
        if (StringUtil.isNullOrEmpty(timeStr)) {
            showAlertDialog(Alert.AlertType.ERROR, "错误", "录屏", "请输入时间");
            return;
        }
        int time = Integer.parseInt(timeStr);
        if (time > 180) {
            showAlertDialog(Alert.AlertType.ERROR, "错误", "录屏", "时间不能大于180s");
            isRecording = false;
            return;
        }

        // 指定比特率，默认为4000000，即4Mbps。可以增加比特率以提高视频质量，或为了让文件更小而降低比特率
        String rateStr = txt_record_date.getText().trim();
        if (StringUtil.isNullOrEmpty(rateStr)) {
            showAlertDialog(Alert.AlertType.ERROR, "错误", "录屏", "请输入比特率");
            return;
        }
        int rate = Integer.parseInt(rateStr);

        // 录制视频
        if (recordService != null) {
            recordService.cancel();
        }
        recordService = new Service() {
            @Override
            protected Task createTask() {
                return new Task() {

                    @Override
                    protected Object call() throws Exception {

                        try {
                            String videoFileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis())) + ".mp4";

                            updateMessage(SystemCmd.exe("adb shell screenrecord"
                                    + " --time-limit " + time
                                    + " --bit-rate " + rate
                                    + " /sdcard/" + videoFileName));

                            File file = new File("video/");
                            if (!file.exists()) {
                                file.mkdirs();
                            }
                            updateMessage(SystemCmd.exe("adb pull /sdcard/" + videoFileName + " video/" + videoFileName));

                            updateMessage(SystemCmd.exe("adb shell rm /sdcard/" + videoFileName));

                        } catch (Exception e) {
                            e.printStackTrace();
                            updateMessage("Error:" + e);
                        }

                        updateMessage("onScreenRecord finish");

                        return null;
                    }

                    @Override
                    protected void updateMessage(String message) {
                        super.updateMessage(message);
                        printLog(message);
                    }

                    @Override
                    protected void succeeded() {
                        super.succeeded();
                        isRecording = false;
                    }
                };
            }
        };
        recordService.start();

        // 进度指示器
        if (timerService != null) {
            timerService.cancel();
        }
        timerService = new Service() {
            @Override
            protected Task createTask() {
                return new Task<Void>() {

                    private Timer timer;

                    @Override
                    protected Void call() throws Exception {
                        currentTime = 0;
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                currentTime++;

                                // 多余的2s主要用于pull视频文件，销毁资源，如果多次录屏间隔时间太少，会造成异常
                                updateProgress(currentTime, time + 2);
                            }
                        }, 0L, 1000L);
                        return null;
                    }

                    @Override
                    protected void updateProgress(long workDone, long max) {
                        super.updateProgress(workDone, max);
                        double progress = workDone * 1.0 / max;
                        pb_record.setProgress(progress);
//                        printLog("updateProgress|max=" + max + ", workDone=" + workDone + ", progress=" + progress);
                        System.out.println("updateProgress|max=" + max + ", workDone=" + workDone + ", progress=" + progress);
                        if (progress >= 1.0) {
                            printLog("stop timer!");
                            timer.cancel();
                            timer = null;
                        }
                    }
                };
            }
        };
        timerService.start();
    }

    private boolean isLogcat = false;

    @FXML
    public void onLogcat(ActionEvent actionEvent) {
        if (isLogcat) {
            printLog("onLogcat stop");
            isLogcat = false;
            btn_logcat.setText("开始记录日志");
            SystemCmd.stopCurrentProcess();
        } else {
            printLog("onLogcat start");
            printLog("user.dir=" + System.getProperty("user.dir"));
            isLogcat = true;
            btn_logcat.setText("停止记录日志");
            Service service = new Service() {
                @Override
                protected Task createTask() {
                    return new Task() {

                        StringBuilder sb = new StringBuilder();

                        @Override
                        protected Object call() throws Exception {

                            // 子线程

                            sb.append("Task|call");

                            try {
                                String logFileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis())) + ".txt";

                                sb.append(SystemCmd.exe("adb shell logcat -v time > /sdcard/" + logFileName));

                                File file = new File("log/");
                                if (!file.exists()) {
                                    file.mkdirs();
                                }

                                sb.append(SystemCmd.exe("adb pull /sdcard/" + logFileName + " log/" + logFileName));

                                sb.append(SystemCmd.exe("adb shell rm /sdcard/" + logFileName));
                            } catch (Exception e) {
                                e.printStackTrace();
                                sb.append("Error:" + e);
                            }
                            return null;
                        }

                        @Override
                        protected void succeeded() {
                            super.succeeded();
                            sb.append("Task|succeeded");
                            // UI线程
                            printLog(sb.toString());

                            sb.append("onLogcat finish");
                        }
                    };
                }
            };

            service.start();
        }
    }

    @FXML
    public void onLogcatDelete(ActionEvent actionEvent) {
        System.out.println("onLogcatDelete");
        ta_logcat.clear();
    }

    /**
     * 显示日志
     */
    synchronized private void printLog(String msg) {
        // 输出日志到控制台
        System.out.println(msg);
        // 输入日志到界面
        ta_logcat.appendText("\n" + msg);
    }
}