package einheit.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
import teacher.model.Teacher;

public class Einheit {

    private final ObjectProperty<Number> einheit_id = new SimpleObjectProperty<>();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty tag = new SimpleStringProperty();
    private final StringProperty raum = new SimpleStringProperty();
    private final StringProperty notizen = new SimpleStringProperty();
    private final ObjectProperty<Number> stunde = new SimpleObjectProperty<>();

    private Teacher teacher;

    /**
     * NoArg-Konstruktor.
     * <br>
     * Erzeugt ein leeeres Model, dass in Folge mit Daten gefüllt und der
     * Datenbank synchronisert werden kann.
     */
    public Einheit() {
        name.setValue(null);
        tag.setValue(null);
        raum.setValue(null);
        notizen.setValue(null);
        stunde.setValue(null);
        teacher = new Teacher();
    }

    public void setTeacher(Teacher newTeacher) {
        if (this.teacher != newTeacher) {
            // Alte Beziehung aufheben
            if (this.teacher != null) {
                this.teacher.removeEinheit(this);
            }

            // Neue Beziehung erstellen
            // Richtung Konto -> InhaberIn
            this.teacher = newTeacher;

            // Richtung InhaberIn -> Konto
            if (newTeacher != null) {
                newTeacher.addEinheit(this);
            }
        }
    }

    /**
     * Konto-Zuordnung aufheben.
     * <p>
     * @param oldKonto Zu enfernendes Konto
     * <p>
     * @throws bank.BankException
     */
    /*
    public void removeKonto(Teacher oldTeacher) throws BankException {
        if (this.konto.contains(oldKonto)) {
            // Richtung Person -> Konto
            this.konto.remove(oldKonto);

            // Richtung Konto -> Person
            oldKonto.setInhaberIn(null);
        }
    }
     */
    public static Einheit find(String svnr, Statement statement) throws SQLException {
        /*String sqlQuery
           = "  select svnr "
            + "       ,nname "
            + "       ,vname "
            + "       ,gebdat "
            + "       ,groesse "
            + "       ,geschlecht "
            + " from Person "
            + " where svnr = '" + svnr + "' ";

    // Datenbankzugriff
    ResultSet rSet = statement.executeQuery(sqlQuery);

    Person person = null;

    // Gefunden?
    if (rSet.next()) {
      person = new Person();

      // Attribute mit Werten aus Datenbank belegen
      person.setSvnr(rSet.getString("svnr"));
      person.setNname(rSet.getString("nname"));
      person.setVname(rSet.getString("vname"));
      person.setGebDat(rSet.getDate("gebdat"));
      person.setGeschlecht(rSet.getString("geschlecht"));

      // Besonderheit bei "nummerischen Nullvalues":
      // getDouble() liefert 0, wenn Null!!!
      Double groesseDb = rSet.getDouble("groesse");
      if (rSet.wasNull()) {
        // Attribut soll nicht 0 sondern null sein.
        person.setGroesse(null);
      }
      else {
        person.setGroesse(groesseDb);
      }
    }

    return person;*/
        return null;
    }

    /**
     * Alle Models aus der Datenbank ermitteln.
     * <br>
     * Diese Methode ermittelt alle Models aus der Datenbank und gibt sie als
     * Liste zurück. Wird kein Model gefunden, so wird eine leere Liste
     * geliefert.
     *
     * @param statement Datenbankverbindung
     *
     * @return Liste von allen Models in der Datenbank, kann auch leer sein.
     *
     * @throws SQLException
     */
    public static List<Einheit> findAll(Statement statement) throws SQLException {
        String sqlQuery
                = "  select * "
                + " from einheit "
                + " order by name";

        // Datenbankzugriff
        ResultSet rSet = statement.executeQuery(sqlQuery);

        // Spezielle Liste für JavaFX 
        ObservableList<Einheit> liEinheit = FXCollections.observableArrayList();

        // Den ResultSet durchgehen
        while (rSet.next()) {
            // Neues Model anlegen
            Einheit einheit = new Einheit();

            // ... belegen
            einheit.name.set(rSet.getString("name"));
            einheit.tag.set(rSet.getString("tag"));
            einheit.raum.set(rSet.getString("raum"));
            einheit.notizen.set(rSet.getString("notizen"));
            Double einheit_ID = rSet.getDouble("einheitID");
            if (rSet.wasNull()) {
                // Attribut soll nicht 0 sondern null sein.
                einheit.einheit_id.set(null);
            } else {
                einheit.einheit_id.set(einheit_ID);
            }

            Double stunde = rSet.getDouble("stunde");
            if (rSet.wasNull()) {
                // Attribut soll nicht 0 sondern null sein.
                einheit.stunde.set(null);
            } else {
                einheit.stunde.set(stunde);
            }

            // ... und in Liste hängen
            liEinheit.add(einheit);
        }
        return liEinheit;
    }

    /**
     * Model in Datenbank erstellen.
     * <br>
     * Diese Methode fügt das Model als neuen Datensatz in die Datenbank ein.
     *
     * @param statement Datenbakverbindung
     *
     * @throws SQLException eigtl. eine Sicherheitslücke ...
     */
    public void create(Statement statement) throws SQLException {
        // Überprüfung und Defaulting
        killAndFill();

        //Teacher und EInheit noch zusammenführen
        String sql
                = "  insert "
                + " into Einheit (name "
                + "             ,raum "
                + "             ,notizen "
                + "             ) "
                + " values ( '" + name.get() + "' "
                // Achtung auf String-Null-Values: Die Hochkommas müssen gesondert behandelt werden!
                + (raum.get() == null
                ? "        , null "
                : "        , '" + raum.get() + "' ")
                + (notizen.get() == null
                ? "        , null "
                : "        , '" + notizen.get() + "' ")
                + "        )";

        //zweites Statement
        Connection connection = statement.getConnection();
        PreparedStatement statement2 = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS);

        statement2.executeUpdate();
        ResultSet rs = statement2.getGeneratedKeys();
        rs.next();
        System.out.println(rs.getInt(1));
        einheit_id.set(rs.getInt(1));
        String sql2
                = "  insert "
                + " into unterrichtet (einheitID "
                + "             ,lehrerID "
                + "             ) "
                + " values ( "
                // Achtung auf String-Null-Values: Die Hochkommas müssen gesondert behandelt werden!
                + (einheit_id.get() == null
                ? "        null "
                : "        " + einheit_id.get() + " ")
                + (teacher.getTeacher_id() == null
                ? "        , null "
                : "        , " + teacher.getTeacher_id().get().intValue() + " ")
                + "        )";

        System.out.println(sql2);
        statement.executeUpdate(sql2);
    }

    /**
     * Veränderung an Model in Datenbank speichern.
     * <br>
     * Diese Methode schreibt das geänderte Model in die Datenbank, verändert
     * also den Datensatz.
     *
     * @param statement Datenbakverbindung
     *
     * @throws SQLException eigtl. eine Sicherheitslücke ...
     */
    public void edit(Statement statement) throws SQLException {
        /*// Überprüfung und Defaulting
        killAndFill();

        String sql
                = "  update Person "
                + " set  nname      = '" + nname.get() + "' "
                // Achtung auf String-Null-Values: Die Hochkommas müssen gesondert behandelt werden!
                + "     ,vname      =  " + (vname.get() == null
                ? " null "
                : " '" + vname.get() + "' ")
                + "     ,gebdat     = '" + gebDat.get() + "' "
                + "     ,groesse    =  " + groesse.get() + "  "
                + "     ,geschlecht = '" + geschlecht.get() + "' "
                + " where svnr = '" + svnr.get() + "' ";

        // Datenbankzugriff
        statement.executeUpdate(sql);*/
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty tagProperty() {
        return tag;
    }

    public StringProperty raumProperty() {
        return raum;
    }

    public StringProperty notizenProperty() {
        return notizen;
    }

    public ObjectProperty<Number> stundeProperty() {
        return stunde;
    }

    private void killAndFill() {
        if (name.get() == null || name.get().length() == 0) {
            throw new IllegalArgumentException("Sozialversicherungsnummer muss angegeben werden!");
        }
    }

    @Override
    public String toString() {
        if (name != null) {
            return name.getValue();
        } else {
            return " ";
        }
    }
}
