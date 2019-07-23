package sample.login;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import sample.base.BaseController;
import sample.main.MainController;
import sample.util.StringUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController extends BaseController {

    public static void start(Stage stage) {
        start(LoginController.class, stage, "", "Login.fxml");
    }

    @FXML
    private ImageView iv_header;

    @FXML
    private TextField tf_login_id;

    @FXML
    private TextField tf_login_pwd;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 默认圆形头像
        URL header = getClass().getResource("header.png");
        iv_header.setImage(new Image(header.toString()));
        iv_header.setFitWidth(100);
        iv_header.setFitHeight(100);
        // 圆形裁剪
        iv_header.setClip(new Circle(50, 50, 50));
        Rectangle rectangle = new Rectangle(iv_header.getFitWidth(), iv_header.getFitHeight());
        // 矩形裁剪，圆角
//        rectangle.setArcHeight(15);
//        rectangle.setArcWidth(15);
//        iv_header.setClip(rectangle);

        tf_login_id.setText("admin");

        tf_login_pwd.setText("admin");
        tf_login_pwd.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    login();
                }
            }
        });
    }

    private void login() {

        String id = tf_login_id.getText().trim();
        if (StringUtil.isNullOrEmpty(id)) {
            showAlertDialog(Alert.AlertType.ERROR, "", "登录", "请输入账号");
            return;
        }

        String pwd = tf_login_pwd.getText().trim();
        if (StringUtil.isNullOrEmpty(pwd)) {
            showAlertDialog(Alert.AlertType.ERROR, "", "登录", "请输入密码");
            return;
        }

        if (id.equals("admin") && pwd.equals("admin")) {
            System.out.println("登录成功");
            // 跳转到主页面
            MainController.start();
            // 关闭登录页面
            mStage.close();
        } else {
            showAlertDialog(Alert.AlertType.ERROR, "", "登录", "账号或者密码输入错误");
        }
    }
}
