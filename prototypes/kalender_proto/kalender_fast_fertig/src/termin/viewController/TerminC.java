/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package termin.viewController;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.LocalTimeStringConverter;
import kalender.viewController.KalenderC;
import termin.model.Termin;
import timespinner.*;

/**
 * FXML Controller class
 *
 * @author Jan Donnerbauer
 */
public class TerminC {

    @FXML
    private Label wiederholenLa;
    @FXML
    private ChoiceBox<?> wiederholenCb;
    @FXML
    private Label benachrichtigungLa;
    @FXML
    private ChoiceBox<?> benachrichtigungCb;
    @FXML
    private Label beschreibungLa;
    @FXML
    private TextArea beschreibungTa;
    @FXML
    private Button btSave;
    @FXML
    private TextField tfTitel;
    @FXML
    private DatePicker dpStart;
    @FXML
    private DatePicker dpEnd;
    @FXML
    private TextField tfOrt;
    @FXML
    private TextField benachrichtigungTf;
    @FXML
    private HBox hbDate;
    @FXML
    private VBox vbRoot;

    TimeSpinner spinner_start;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss");

    TimeSpinner spinner_end;

    // Controller für diese View
    private final static String VIEWNAME = "TerminV.fxml";

    private static KalenderC parentControll;

    // Verbindung zur Datenbank
    private Statement statement;

    private Termin current;

    private boolean old = false;

    //private Termin current;
    public static void show(Stage stage, Statement statement, Stage parent, KalenderC parentC, Termin termin) {
        try {
            // View und Controller erstellen
            FXMLLoader loader = new FXMLLoader(TerminC.class.getResource(VIEWNAME));
            Parent root = (Parent) loader.load();

            // Scene erstellen
            Scene scene = new Scene(root);

            parentControll = parentC;

            // Stage: Entweder übergebene Stage verwenden (Primary Stage) oder neue erzeugen
            if (stage == null) {
                stage = new Stage();
            }

            stage.setScene(scene);
            stage.initOwner(parent);

            stage.initModality(Modality.WINDOW_MODAL);
            stage.setTitle("Create Termin");

            // Controller ermitteln
            TerminC terminC = (TerminC) loader.getController();

            // Datenbankzugriff und Verantwortlichen für die ToDo's merken
            terminC.statement = statement;

            // View initialisieren
            terminC.init(termin);
            // alles anzeigen
            stage.show();

        } catch (IOException ex) {
            Logger.getLogger(TerminC.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Something wrong with " + VIEWNAME + "!");
            ex.printStackTrace(System.err);
            System.exit(1);
        } catch (SQLException ex) {
            Logger.getLogger(TerminC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void init(Termin termin) throws SQLException {

        dpStart.setValue(LocalDate.now());

        if (termin != null) {
            spinner_start = new TimeSpinner(termin.startTimeProperty().get());
            spinner_end = new TimeSpinner(termin.endTimeProperty().get());
            //System.out.println(termin.startTimeProperty().get());
            //System.out.println(spinner_start.valueProperty());

            old = true;
            reset_display(termin);
        } else {
            spinner_start = new TimeSpinner();
            spinner_end = new TimeSpinner();

            old = false;
            reset_display(null);
        }
        spinner_start.setMaxWidth(130);
        spinner_end.setMaxWidth(130);

        hbDate.getChildren().add(1, spinner_start);

        hbDate.getChildren().add(spinner_end);

    }

    private void reset_display(Termin termin) {
        if (termin != null) {
            setCurrent(termin);
        } else {
            setCurrent(new Termin());
        }

    }

    private void setCurrent(Termin termin) {
        // alte Bindings mit Controls entfernen
        if (current != null) {
            tfTitel.textProperty().unbindBidirectional(current.titelProperty());
            tfOrt.textProperty().unbindBidirectional(current.ortProperty());
            beschreibungTa.textProperty().unbindBidirectional(current.beschreibungProperty());

            dpStart.valueProperty().unbindBidirectional(current.startDateProperty());
            dpEnd.valueProperty().unbindBidirectional(current.endDateProperty());

        }

        // Current setzen
        current = termin;

        // Controls mit Current verbinden
        if (current != null) {
            tfTitel.textProperty().bindBidirectional(current.titelProperty());
            tfOrt.textProperty().bindBidirectional(current.ortProperty());
            beschreibungTa.textProperty().bindBidirectional(current.beschreibungProperty());

            dpStart.valueProperty().bindBidirectional(current.startDateProperty());
            dpEnd.valueProperty().bindBidirectional(current.endDateProperty());

            System.out.println(current.endDateProperty());

//tfStartTime.textProperty().bindBidirectional(current.startTimeProperty());
        }
    }

    @FXML
    private void btSaveOnClick(ActionEvent event) {
        save();
        //System.out.println("moinnn");
    }

    private void save() {
        try {
            current.setStartTimeProperty(spinner_start.valueProperty().get());
            current.setEndTimeProperty(spinner_end.valueProperty().get());

            if (old) {
                current.update(statement);
            } else {
                current.create(statement);
            }

            ((Stage) (vbRoot.getScene().getWindow())).close();
            parentControll.init(parentControll);
            //parentControll.init();
        } catch (SQLException ex) {
            Logger.getLogger(TerminC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
