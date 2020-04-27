package einheit.model;

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

public class Einheit {

    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty tag = new SimpleStringProperty();
    private final StringProperty raum = new SimpleStringProperty();
    private final StringProperty notizen = new SimpleStringProperty();
    private final ObjectProperty<Number> stunde = new SimpleObjectProperty<>();

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
    }

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
        /*String sqlQuery
           = "  select svnr "
            + "       ,nname "
            + "       ,vname "
            + "       ,gebdat "
            + "       ,groesse "
            + "       ,geschlecht "
            + " from Person "
            + " order by nname, vname";

    // Datenbankzugriff
    ResultSet rSet = statement.executeQuery(sqlQuery);

    List<Person> personen = new LinkedList<>();

    // ReesultSet durchgehen
    while (rSet.next()) {
      // Neue Person anlegen ...
      Person person = new Person();

      // Attribute belegen ...
      person.setSvnr(rSet.getString("svnr"));
      person.setNname(rSet.getString("nname"));
      person.setVname(rSet.getString("vname"));
      person.setGebDat(rSet.getDate("gebdat"));
      person.setGeschlecht(rSet.getString("geschlecht"));

      // getDouble() liefert 0, wenn Null!!!
      Double groesseDb = rSet.getDouble("groesse");
      if (rSet.wasNull()) {
        person.setGroesse(null);
      }
      else {
        person.setGroesse(groesseDb);
      }

      // und an Liste anhängen
      personen.add(person);
    }
    return personen;*/
        return null;
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

        // Datenbankzugriff
        statement.executeUpdate(sql);
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
}
