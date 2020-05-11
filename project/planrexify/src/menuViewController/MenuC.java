package menuViewController;

import java.io.IOException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MenuC {

    @FXML
    private VBox background;

    private Statement statement;

    private static final String VIEWNAME = "MenuV.fxml";
    @FXML
    private ImageView menuImageIcon;
    @FXML
    private ImageView menuImageText;
    private Stage stage;
    @FXML
    private HBox displayLogo;

    public static void show(Stage stage, Statement statement) {
        try {
            FXMLLoader loader = new FXMLLoader(MenuC.class.getResource(VIEWNAME));
            Parent root = (Parent) loader.load();

            Scene scene = new Scene(root);

            if (stage == null) {
                stage = new Stage();
            }
            stage.setScene(scene);
            stage.setTitle("Planrexify");

            MenuC menuC = (MenuC) loader.getController();
            menuC.stage = stage;
            menuC.init();

            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(MenuC.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Something wrong with " + VIEWNAME + "!");
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private void init() {
//        displayLogo.setPreserveRatio(false);

//        displayLogo.fitWidthProperty().bind(stage.widthProperty());
//        displayLogo.fitHeightProperty().bind(Bindings.subtract(stage.heightProperty(), Bindings.add(Bindings.add(hboxControlls.heightProperty(), tfMsg.heightProperty()), 90)));
        
        menuImageIcon.setTranslateX(-1280);
        menuImageText.setTranslateX(1280);
        
        final Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.setAutoReverse(true);
        final KeyValue kvIcon = new KeyValue(menuImageIcon.translateXProperty(), 0);
        final KeyFrame kfIcon = new KeyFrame(Duration.millis(500), kvIcon);
        timeline.getKeyFrames().add(kfIcon);
        
        final KeyValue kvText = new KeyValue(menuImageText.translateXProperty(), 0);
        final KeyFrame kfText = new KeyFrame(Duration.millis(500), kvText);
        timeline.getKeyFrames().add(kfText);
        timeline.play();
    }

}
