package teacher.model;

import einheit.model.Einheit;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Teacher {
    
    // Nachname des Lehrers
    private final StringProperty nname = new SimpleStringProperty(); 
    // Vorname des Lehrers
    private final StringProperty vname = new SimpleStringProperty(); 
    // Die in der Datenbank gespeicherte id
    private final ObjectProperty<Number> teacher_id = new SimpleObjectProperty<>(); 
    // Liste aller Einheiten
    private final List<Einheit> einheiten;

    // Lehrer Constructor
    public Teacher() {
        nname.setValue(null);
        vname.setValue(null);
        einheiten = new LinkedList<>();
    }

    // Beidseitige Beziehung mit Einheit Klasse
    // >> add 
    public void addEinheit(Einheit newEinheit) {
        if (!einheiten.contains(newEinheit)) {
            this.einheiten.add(newEinheit);

            newEinheit.setTeacher(this);
        }
    }
    //und remove Einheit
    public void removeEinheit(Einheit oldEinheit) {
        if (this.einheiten.contains(oldEinheit)) {
            this.einheiten.remove(oldEinheit);

            oldEinheit.setTeacher(null);
        }

    }

    //Lehrer in Datenbank suchen
    public static ObservableList<Teacher> findAllTeacher(Statement statement) throws SQLException {
        // select Statement
        String sqlQuery
                = "  select lehrerID "
                + "       ,nachname "
                + "       ,vorname "
                + " from lehrer "
                + " order by nachname";

        // Datenbankzugriff
        ResultSet rSet = statement.executeQuery(sqlQuery);

        // Spezielle Lehrer-Liste für JavaFX 
        ObservableList<Teacher> liTodo = FXCollections.observableArrayList();

        // Den ResultSet des Selects durchgehen
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

    // Lehrer mit id finden
    public static Teacher findTeacher(Statement statement, String id) throws SQLException {
        // select Statement
        String sqlQuery
                = "  select * "
                + " from lehrer "
                + " where lehrerID = " + id;

        // Datenbankzugriff
        ResultSet rSet = statement.executeQuery(sqlQuery);

        // Neues Lehrerobjekt anlegen
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
        // Lehererobjekt returnen
        return teacher;
    }

    // tostring, was den Nachnamen zurückgibt
    @Override
    public String toString() {
        return nname.getValue();
    }

    // Lehrer-ID Getter
    public ObjectProperty<Number> getTeacher_id() {
        return teacher_id;
    }

    // Nachnamen Getter
    public Property<String> nNameProperty() {
        return nname;
    }

    // Vorname Getter
    public Property<String> vNameProperty() {
        return vname;
    }

    // Lehrer erstellen
    public void create(Statement statement) throws SQLException {
        // Überprüfung und Defaulting
        killAndFill();

        // insert Statement
        String sql
                = " insert "
                + " into lehrer (nachname, vorname) "
                + " values ( '"
                + nname.get() + "', '"
                + vname.get() + "' )";

        // SQL Statement ausführen und ausgeben
        System.out.println(sql);
        statement.executeUpdate(sql);
    }

    // Lehrer in der Datenbank updaten
    public void update(Statement statement, Teacher existingTeacher) throws SQLException {
        // Überprüfung und Defaulting
        killAndFill();

        // update Statement
        String sql
                = " update lehrer "
                + " set nachname = '" + nname.get() + "', "
                + " vorname = '" + vname.get() + "'"
                + " where nachname like '" + existingTeacher.getNname().get() + "'"
                + " and vorname like '" + existingTeacher.getVname().get() + "'";

        // SQL Statement ausführen und ausgeben
        System.out.println(sql);
        statement.executeUpdate(sql);
    }

    // Überprüfung und Defaulting
    private void killAndFill() {
        // valide Daten?
        if (nname.get() == null
                || nname.get().length() == 0
                || vname.get() == null
                || vname.get().length() == 0) {
            throw new IllegalArgumentException("Name muss angegeben werden!");
        }
    }

    // Lehrer per Namen in der Datenbank finden
    public static Teacher findTeacherByName(Statement statement, String vname, String nname) throws SQLException {
        // select Statement
        String sqlQuery
                = "  select * "
                + " from lehrer "
                + " where nachname Like '" + nname + "'"
                + " and vorname Like '" + vname + "'";
        
        // Für debugging sql-Statement ausgeben
        System.out.println(sqlQuery);
        // Datenbankzugriff
        ResultSet rSet = statement.executeQuery(sqlQuery);

        // Den ResultSet durchgehen
        if (rSet.next()) {
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
            return teacher;
        }
        // Wenn kein Lehrer gefunden wird
        return null;
    }

    // getter für Nachname
    public StringProperty getNname() {
        return nname;
    }

    // getter für Vorname
    public StringProperty getVname() {
        return vname;
    }
}
