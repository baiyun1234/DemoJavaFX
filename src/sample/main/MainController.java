package sample.main;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sample.adb.ADBController;
import sample.base.BaseController;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController extends BaseController {

    @FXML
    private ImageView iv_adb;

    @FXML
    private ImageView iv_bank_card;

    public static void start() {
        start(MainController.class, new Stage(), "", "Main.fxml");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        URL icon_adb = getClass().getResource("adb.png");
        iv_adb.setImage(new Image(icon_adb.toString()));

        URL icon_bank_card = getClass().getResource("bankcard.png");
        iv_bank_card.setImage(new Image(icon_bank_card.toString()));
    }

    public void onClickADB(MouseEvent mouseEvent) {
        // 打开ADB页面
        ADBController.start();
        // 关闭主页面
        mStage.close();
    }

    public void onClickBankCard(MouseEvent mouseEvent) {
        showUnOpenAlertDialog();
    }

    /**
     * 鼠标悬浮效果
     */
    public void onMouseEnterAndExit(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof VBox) {
            VBox vb = (VBox) mouseEvent.getSource();
            if (mouseEvent.getEventType().getName() == MouseEvent.MOUSE_ENTERED.getName()) {
                vb.setStyle("-fx-background-color: #aaaaaa;");
            } else if (mouseEvent.getEventType().getName() == MouseEvent.MOUSE_EXITED.getName()) {
                vb.setStyle("-fx-background-color: transparent;");
            }
        }
    }
}
