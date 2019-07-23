package sample.app;

import javafx.application.Application;
import javafx.stage.Stage;
import sample.login.LoginController;

public class MyApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * 所有JavaFX应用程序的主要入口点。
     */
    @Override
    public void start(Stage stage) throws Exception {

        // 启动登录页面
        LoginController.start(stage);
    }
}