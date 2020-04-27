/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package einheit.viewController;

import einheit.model.Einheit;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.stage.Modality;
import javafx.stage.Stage;
import teacher.model.Teacher;

/**
 * FXML Controller class
 *
 * @author Jan Donnerbauer
 */
public class EinheitC {

    @FXML
    private Label beschreibungLa;
    @FXML
    private TextField tfFach;
    @FXML
    private TextField tfRoom;
    @FXML
    private TextArea taBeschreibung;
    @FXML
    private ComboBox<Teacher> cbTeacher;
    @FXML
    private Button btSave;

    // aktuelle Models
    private ObservableList<Teacher> liTeacher;

    // Controller für diese View
    private final static String VIEWNAME = "EinheitV.fxml";

    // Verbindung zur Datenbank
    private Statement statement;

    private Einheit current;

    /**
     * Initializes the controller class.
     *
     * @param stage
     * @param statement
     * @param parent
     */
    public static void show(Stage stage, Statement statement, Stage parent) {
        try {
            // View und Controller erstellen
            FXMLLoader loader = new FXMLLoader(EinheitC.class.getResource(VIEWNAME));
            Parent root = (Parent) loader.load();

            // Scene erstellen
            Scene scene = new Scene(root);

            // Stage: Entweder übergebene Stage verwenden (Primary Stage) oder neue erzeugen
            if (stage == null) {
                stage = new Stage();
            }
            stage.setScene(scene);
            stage.initOwner(parent);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setTitle("Create Einheit");

            // Controller ermitteln
            EinheitC einheitC = (EinheitC) loader.getController();

            // Datenbankzugriff und Verantwortlichen für die ToDo's merken
            einheitC.statement = statement;

            // View initialisieren
            einheitC.init();
            // alles anzeigen
            stage.show();

            //todoListC.init();
            // Anzeigen
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(EinheitC.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Something wrong with " + VIEWNAME + "!");
            ex.printStackTrace(System.err);
            System.exit(1);
        } catch (SQLException ex) {
            Logger.getLogger(EinheitC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void init() throws SQLException {

        liTeacher = Teacher.findAllTeacher(statement);
        refreshLvTeacher();
        // Ausgangszustand einstellen
        reset_display();

    }

    private void refreshLvTeacher() {
        //lvTodo.refresh(); //funktioniert nicht???
        cbTeacher.setItems(liTeacher);

        String msg;
        if (liTeacher.size() > 0) {
            msg = "Ok, bitte alles erledigen!";
        } else {
            msg = "Super, keine weiteren Aufgaben mehr!";
        }
        //showSuccessMessage(msg);
    }

    private void reset_display() {
        // Leeres Model verbinden
        setCurrent(new Einheit());

        // Wenn der Benutzer jetzt die Anzeige füllt, erstellt er ein neues Objekt
        //newObject.setValue(true);
    }

    private void setCurrent(Einheit einheit) {
        // alte Bindings mit Controls entfernen
        if (current != null) {
            tfFach.textProperty().unbindBidirectional(current.nameProperty());
            tfRoom.textProperty().unbindBidirectional(current.raumProperty());
            taBeschreibung.textProperty().unbindBidirectional(current.notizenProperty());
        }

        // Current setzen
        current = einheit;

        // Controls mit Current verbinden
        if (current != null) {
            tfFach.textProperty().bindBidirectional(current.nameProperty());
            tfRoom.textProperty().bindBidirectional(current.raumProperty());
            taBeschreibung.textProperty().bindBidirectional(current.notizenProperty());
        }
    }

    @FXML
    private void btSaveOnClick(ActionEvent event) {
        save();
    }

    private void save() {
        try {
            current.create(statement);
        } catch (SQLException ex) {
            Logger.getLogger(EinheitC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
