package sample.base;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public abstract class BaseController implements Initializable {

    public static void start(Class clazz, Stage stage, String title, String fxml) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
            fxmlLoader.setLocation(clazz.getResource(fxml));

            Parent root = fxmlLoader.load(clazz.getResourceAsStream(fxml));
            stage.setScene(new Scene(root));
            stage.setTitle(title);

            BaseController controller = fxmlLoader.getController();
            controller.setStage(stage);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void simpleStart(Class clazz, Stage stage, String title, String fxml) {
        try {
            Parent root = FXMLLoader.load(BaseController.class.getResource(fxml));
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Controller 持有 Stage，可以用于关闭当前页面
     */
    public Stage mStage;

    public void setStage(Stage stage) {
        this.mStage = stage;
    }

    /**
     * 显示警告对话框
     *
     * @param alertType 对话框类型
     * @param title     标题
     * @param header    头部
     * @param content   内容
     */
    protected void showAlertDialog(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }

    /**
     * 显示"该功能暂未上线"
     */
    protected void showUnOpenAlertDialog() {
        showAlertDialog(Alert.AlertType.INFORMATION, "Null", "提示", "该功能暂未上线，小哥哥正在研发中~~~");
    }
}