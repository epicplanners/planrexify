package kalender.viewController;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import kalender.model.AnchorPaneNode;
import kalender.model.FullCalendarView;
import termin.viewController.TerminC;

public class KalenderC {

    // Get the pane to put the calendar on
    @FXML
    private ScrollPane calendarPane;
    private VBox view;
    private Text calendarTitle;
    private YearMonth currentYearMonth;
    @FXML
    private Label laCurrentMonth;
    @FXML
    private Button month_back;
    @FXML
    private Button month_forward;
    @FXML
    private Button addTermin;
    @FXML
    private VBox centerArea;
    @FXML
    private BorderPane bpRoot;

    private Statement statement;

    private static FullCalendarView fullCalenderView;

    private final static String VIEWNAME = "monatsansicht.fxml";

    public static void show(Stage stage, Statement statement) {
        try {
            // View und Controller erstellen
            FXMLLoader loader = new FXMLLoader(KalenderC.class.getResource(VIEWNAME));
            Parent root = (Parent) loader.load();

            // Scene erstellen
            Scene scene = new Scene(root, 1080, 720);

            // Stage: Entweder übergebene Stage verwenden (Primary Stage) oder neue erzeugen
            if (stage == null) {
                stage = new Stage();
            }

            stage.setScene(scene);
            stage.setTitle("Kalender");

            // Controller ermitteln
            KalenderC kalenderC = (KalenderC) loader.getController();

            fullCalenderView = new FullCalendarView(YearMonth.now(), statement, stage, kalenderC);

            kalenderC.calendarPane.setContent(fullCalenderView.getView());
            
            
            // Datenbankzugriff merken
            kalenderC.statement = statement;

            kalenderC.calendarPane.getContent().onMouseClickedProperty().addListener((observable, oldValue, newValue) -> {
                System.out.println("ksjadfjklsdfkj");
            });

            // View initialisieren
            kalenderC.init();

            // Anzeigen
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(KalenderC.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Something wrong with " + VIEWNAME + "!");
            ex.printStackTrace(System.err);
            System.exit(1);
        } catch (Exception ex) {
            Logger.getLogger(KalenderC.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace(System.err);
            System.exit(2);
        }
    }

    public void init() throws SQLException {
        
        fullCalenderView.populateCalendar(fullCalenderView.yearMonthProperty().get());

        laCurrentMonth.textProperty().bind(fullCalenderView.yearMonthProperty().asString());
        //Kalender soll mit jetzigem Monat starten

    }

    @FXML
    private void month_backOnClick(ActionEvent event) throws SQLException {
        fullCalenderView.previousMonth();
    }

    @FXML
    private void month_forwardOnClick(ActionEvent event) throws SQLException {
        fullCalenderView.nextMonth();
    }

    @FXML
    private void addTerminOnClick(ActionEvent event) {
        Platform.runLater(() -> {
            TerminC.show(null, statement, getStage(), this, null);

        });
    }

    public Stage getStage() {
        return (Stage) bpRoot.getScene().getWindow();
    }

}
