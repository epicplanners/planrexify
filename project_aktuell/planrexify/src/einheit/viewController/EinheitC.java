package einheit.viewController;

import einheit.model.Einheit;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import stundenplan.viewController.StundenplanC;
import teacher.model.Teacher;

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
    @FXML
    private VBox vbRoot;

    // aktuelle Models
    private ObservableList<Teacher> liTeacher;

    // Controller für diese View
    private final static String VIEWNAME = "EinheitV.fxml";

    private static StundenplanC parentControll;

    // Verbindung zur Datenbank
    private Statement statement;

    private Einheit current;

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
            FXMLLoader loader = new FXMLLoader(EinheitC.class.getResource(VIEWNAME));
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
            EinheitC einheitC = (EinheitC) loader.getController();

            // Datenbankzugriff und Verantwortlichen für die ToDo's merken
            einheitC.statement = statement;

            EinheitC.displayUser = displayUser;

            // View initialisieren
            einheitC.init();
            // alles anzeigen
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
        //Alle bereits bestehenden Teacher selektieren und einfügen
        liTeacher = Teacher.findAllTeacher(statement);
        refreshLvTeacher();
        
        if (EinheitC.displayUser != null) {
            current = Einheit.find(displayUser, statement);
            current.setTeacher(Teacher.findTeacher(statement, current.teacher_cbProperty().get()));
            cbTeacher.getSelectionModel().select(current.getTeacher());

            // Ausgangszustand einstellen
            reset_display(current);
            shouldUpdate = true;
            oldFach = tfFach.getText();
        } else {
            reset_display(null);
            shouldUpdate = false;
        }
    }

    private void refreshLvTeacher() {
        //Combobox Teacher
        cbTeacher.setItems(liTeacher);
    }

    private void reset_display(Einheit einheit) {
        if (einheit != null) {
            setCurrent(einheit);
        } else {
            setCurrent(new Einheit());
        }
        if (current.getTeacher().toString() == null) {
            cbTeacher.getSelectionModel().select(0);
        }
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
            if (tfFach.getText() == null || tfFach.getText().trim().length() == 0) {
                System.out.println("Bitte vergeben sie dem Fach einen Namen.");
                return;
            }
            current.setTeacher(cbTeacher.getValue());
            if (!shouldUpdate) {
                current.create(statement);

                ((Stage) (vbRoot.getScene().getWindow())).close();
                parentControll.init(null);
            } else {
                current.update(statement, oldFach);

                ((Stage) (vbRoot.getScene().getWindow())).close();
                parentControll.init("update");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
