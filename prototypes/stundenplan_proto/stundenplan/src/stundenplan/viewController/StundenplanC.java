/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stundenplan.viewController;

import einheit.model.*;
import einheit.viewController.*;
import java.io.IOException;
import java.net.URL;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Jan Donnerbauer
 */
public class StundenplanC {

    @FXML
    private BorderPane bpRoot;
    @FXML
    private Label label;
    @FXML
    private VBox centerArea;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private GridPane calendarGrid;
    @FXML
    private GridPane calendarGrid1;
    @FXML
    private TreeView<Einheit> tvEinheiten;
    @FXML
    private Button btAdd;

    // Verbindung zur Datenbank
    private Statement statement;

    private final static String VIEWNAME = "StundenplanV.fxml";

    public static void show(Stage stage, Statement statement) {
        try {
            // View und Controller erstellen
            FXMLLoader loader = new FXMLLoader(StundenplanC.class.getResource(VIEWNAME));
            Parent root = (Parent) loader.load();

            // Scene erstellen
            Scene scene = new Scene(root, 1080, 720);

            // Stage: Entweder übergebene Stage verwenden (Primary Stage) oder neue erzeugen
            if (stage == null) {
                stage = new Stage();
            }
            stage.setScene(scene);
            stage.setTitle("Stundenplan");

            // Controller ermitteln
            StundenplanC stundenplanC = (StundenplanC) loader.getController();

            // Datenbankzugriff merken
            stundenplanC.statement = statement;

            // View initialisieren
            stundenplanC.init();

            // Anzeigen
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(StundenplanC.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Something wrong with " + VIEWNAME + "!");
            ex.printStackTrace(System.err);
            System.exit(1);
        } catch (Exception ex) {
            Logger.getLogger(StundenplanC.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace(System.err);
            System.exit(2);
        }
    }

    private void init() {

    }

    @FXML
    private void btAddOnClick(ActionEvent event) {
        Platform.runLater(() -> {
            EinheitC.show(null, statement, getStage());
        });
    }

    public Stage getStage() {
        return (Stage) bpRoot.getScene().getWindow();
    }

}
