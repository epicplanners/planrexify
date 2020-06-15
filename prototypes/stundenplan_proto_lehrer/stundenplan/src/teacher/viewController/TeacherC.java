/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teacher.viewController;

import einheit.model.Einheit;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import stundenplan.viewController.StundenplanC;
import teacher.model.Teacher;

/**
 * FXML Controller class
 *
 * @author Nonsas
 */
public class TeacherC {

    @FXML
    private VBox vbRoot;
    @FXML
    private TextField tfNn;
    @FXML
    private TextField tfVn;
    @FXML
    private Label beschreibungLa;
    @FXML
    private Button btSave;

    // Controller für diese View
    private final static String VIEWNAME = "TeacherV.fxml";

    private static StundenplanC parentControll;

    // Verbindung zur Datenbank
    private Statement statement;

    private Teacher current;

    private boolean shouldUpdate = false;

    private static String displayUser;
    private String oldFach = null;

    /**
     * Initializes the controller class.
     *
     * @param stage
     * @param statement
     * @param parent
     * @param parentC
     * @param displayUser
     */
    public static void show(Stage stage, Statement statement, Stage parent, StundenplanC parentC, String displayUser) {
        try {
            // View und Controller erstellen
            FXMLLoader loader = new FXMLLoader(TeacherC.class.getResource(VIEWNAME));
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
            stage.setTitle("Create Einheit");

            // Controller ermitteln
            TeacherC teacherC = (TeacherC) loader.getController();

            // Datenbankzugriff und Verantwortlichen für die ToDo's merken
            teacherC.statement = statement;

            // View initialisieren
            teacherC.init();
            // alles anzeigen
            stage.show();

            //todoListC.init();
            // Anzeigen
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(TeacherC.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Something wrong with " + VIEWNAME + "!");
            ex.printStackTrace(System.err);
            System.exit(1);
        } catch (SQLException ex) {
            Logger.getLogger(TeacherC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void init() throws SQLException {
        setCurrent(new Teacher());
    }

    private void setCurrent(Teacher teacher) {
        // alte Bindings mit Controls entfernen
        if (current != null) {
            tfNn.textProperty().unbindBidirectional(current.nNameProperty());
            tfVn.textProperty().unbindBidirectional(current.vNameProperty());
        }

        // Current setzen
        current = teacher;

        // Controls mit Current verbinden
        if (current != null) {
            tfNn.textProperty().bindBidirectional(current.nNameProperty());
            tfVn.textProperty().bindBidirectional(current.vNameProperty());
        }
    }

    @FXML
    private void btSaveOnClick(ActionEvent event) throws SQLException {
        save();
    }

    private void save() throws SQLException {
        if (tfNn.getText() == null
                || tfNn.getText().trim().length() == 0
                || tfVn.getText() == null
                || tfVn.getText().trim().length() == 0) {
            System.out.println("Bitte geben sie in allen Pflichtfeldern etwas ein!");
            return;
        }

        shouldUpdate = false;
        Teacher existingTeacher = current.findTeacherByName(statement, tfVn.getText(), tfNn.getText());
        System.out.println(existingTeacher);
        if (existingTeacher != null) {
            shouldUpdate = true;
        }

        if (!shouldUpdate) {
            current.create(statement);
        } else {
            current.update(statement, existingTeacher);
        }

        ((Stage) (vbRoot.getScene().getWindow())).close();
        parentControll.init(null);
    }
}
