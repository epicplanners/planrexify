package teacher.model;

import einheit.model.Einheit;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Teacher {

    private final StringProperty nname = new SimpleStringProperty();
    private final StringProperty vname = new SimpleStringProperty();
    private final ObjectProperty<Number> teacher_id = new SimpleObjectProperty<>();

    private final List<Einheit> einheiten;

    /**
     * NoArg-Konstruktor.
     * <br>
     * Erzeugt ein leeeres Model, dass in Folge mit Daten gefüllt und der
     * Datenbank synchronisert werden kann.
     */
    public Teacher() {
        nname.setValue(null);
        vname.setValue(null);
        einheiten = new LinkedList<>();
    }

    public void addEinheit(Einheit newEinheit) {
        if (!einheiten.contains(newEinheit)) {
            // Richtung Person -> Konto
            this.einheiten.add(newEinheit);

            // Richtung Konto -> Person
            newEinheit.setTeacher(this);
        }
    }

    public void removeEinheit(Einheit oldEinheit) {
        if (this.einheiten.contains(oldEinheit)) {
            // Richtung Person -> Konto
            this.einheiten.remove(oldEinheit);

            // Richtung Konto -> Person
            oldEinheit.setTeacher(null);
        }

    }

    public static ObservableList<Teacher> findAllTeacher(Statement statement) throws SQLException {
        String sqlQuery
                = "  select lehrerID "
                + "       ,nachname "
                + "       ,vorname "
                + " from lehrer "
                + " order by nachname";

        // Datenbankzugriff
        ResultSet rSet = statement.executeQuery(sqlQuery);

        // Spezielle Liste für JavaFX 
        ObservableList<Teacher> liTodo = FXCollections.observableArrayList();

        // Den ResultSet durchgehen
        while (rSet.next()) {
            // Neues Model anlegen
            Teacher teacher = new Teacher();

            // ... belegen
            teacher.nname.set(rSet.getString("nachname"));
            teacher.vname.set(rSet.getString("vorname"));
            Double teacher_ID = rSet.getDouble("lehrerID");
            if (rSet.wasNull()) {
                // Attribut soll nicht 0 sondern null sein.
                teacher.teacher_id.set(null);
            } else {
                teacher.teacher_id.set(teacher_ID);
            }

            // ... und in Liste hängen
            liTodo.add(teacher);
        }
        return liTodo;
    }

    public static Teacher findTeacher(Statement statement, String id) throws SQLException {
        String sqlQuery
                = "  select * "
                + " from lehrer "
                + " where lehrerID = " + id;

        // Datenbankzugriff
        ResultSet rSet = statement.executeQuery(sqlQuery);

        Teacher teacher = new Teacher();
        // Den ResultSet durchgehen
        if (rSet.next()) {
            // Neues Model anlegen

            // ... belegen
            teacher.nname.set(rSet.getString("nachname"));
            teacher.vname.set(rSet.getString("vorname"));
            Double teacher_ID = rSet.getDouble("lehrerID");
            
            if (rSet.wasNull()) {
                // Attribut soll nicht 0 sondern null sein.
                teacher.teacher_id.set(null);
            } else {
                teacher.teacher_id.set(teacher_ID);
            }
        }
        return teacher;
    }

    @Override
    public String toString() {
        return nname.getValue();
    }

    public ObjectProperty<Number> getTeacher_id() {
        return teacher_id;
    }
}
