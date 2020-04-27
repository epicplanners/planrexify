package teacher.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Teacher {

    private final StringProperty nname = new SimpleStringProperty();
    private final StringProperty vname = new SimpleStringProperty();

    /**
     * NoArg-Konstruktor.
     * <br>
     * Erzeugt ein leeeres Model, dass in Folge mit Daten gefüllt und der
     * Datenbank synchronisert werden kann.
     */
    public Teacher() {
        nname.setValue(null);
        vname.setValue(null);
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

            // ... und in Liste hängen
            liTodo.add(teacher);
        }
        return liTodo;
    }

    @Override
    public String toString() {
        return nname.getValue();
    }
}
