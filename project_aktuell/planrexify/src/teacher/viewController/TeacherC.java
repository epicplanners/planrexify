package teacher.viewController;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

public class TeacherC {

    // FXML Variablen
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

    // Viewname
    private final static String VIEWNAME = "TeacherV.fxml";
    // parentControll (Stundenplan)
    private static StundenplanC parentControll;
    // Lehrer der Upgedated werden soll
    private Teacher existingTeacher;

    // Verbindung zur Datenbank
    private Statement statement;
    // derzeitiger Lehrer
    private Teacher current;
    // soll der Lehrer geupdated werden?
    private boolean shouldUpdate = false;
    // User der angezeigt werden soll
    private static String displayUser;
    // altes Fach
    private String oldFach = null;

    // FXML laden und anzeigen
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

    // Initialisierung
    private void init() throws SQLException {
        setCurrent(new Teacher());
    }

    // derzeitigen Lehrer setzen
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

    // wenn Savebutton geklickt wird
    @FXML
    private void btSaveOnClick(ActionEvent event) throws SQLException {
        // wenn Button auf update ist
        if (btSave.getText() == "update") {
            save(true);
            btSave.setText("save");
        } else {
            // Lehrer soll nicht upgedated werden
            save(false);
        }
    }

    // Lehrer speichern
    private void save(boolean setUpdate) throws SQLException {
        if (tfNn.getText() == null
                || tfNn.getText().trim().length() == 0
                || tfVn.getText() == null
                || tfVn.getText().trim().length() == 0) {
            System.out.println("Bitte geben sie in allen Pflichtfeldern etwas ein!");
            return;
        }

        // wenn der Lehrer gupdated werden soll
        if (setUpdate) {
            current.update(statement, existingTeacher);
            ((Stage) (vbRoot.getScene().getWindow())).close();
            parentControll.init(null);
            return;
        }

        // Lehrer finden
        shouldUpdate = false;
        existingTeacher = current.findTeacherByName(statement, tfVn.getText(), tfNn.getText());
        if (existingTeacher != null) {
            shouldUpdate = true;
        }

        // soll der User geupdated werden
        if (!shouldUpdate) {
            current.create(statement);
            ((Stage) (vbRoot.getScene().getWindow())).close();
            parentControll.init(null);
        } else {
            // wenn ja dann View reseten
            tfVn.setText("");
            tfNn.setText("");
            btSave.setText("update");
        }
    }
}
