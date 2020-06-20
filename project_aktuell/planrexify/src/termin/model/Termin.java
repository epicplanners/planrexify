package termin.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Termin {

    private ObjectProperty<Number> termin_id = new SimpleObjectProperty<>();
    private final StringProperty titel = new SimpleStringProperty();
    private final StringProperty ort = new SimpleStringProperty();
    private final StringProperty beschreibung = new SimpleStringProperty();

    private final ObjectProperty<LocalDate> start_date = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> end_date = new SimpleObjectProperty<>();

    private final ObjectProperty<LocalTime> start_time = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> end_time = new SimpleObjectProperty<>();

    private final ObjectProperty<LocalDateTime> start_datetime = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> end_datetime = new SimpleObjectProperty<>();

    /**
     * NoArg-Konstruktor.
     * <br>
     * Erzeugt ein leeeres Model, dass in Folge mit Daten gefüllt und der
     * Datenbank synchronisert werden kann.
     */
    public Termin() {
        titel.setValue(null);
        ort.setValue(null);
        beschreibung.setValue(null);
    }

    public static ObservableList<Termin> findAll(Statement statement, LocalDate calendarDate) throws SQLException {

        Timestamp time_start = new Timestamp(calendarDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC));

        Timestamp time_end = new Timestamp(calendarDate.atTime(23, 59).toEpochSecond(ZoneOffset.UTC));

        Connection connection = statement.getConnection();
        //String query = "SELECT * FROM termin where start=?";
        String query = "SELECT * FROM termin";
        PreparedStatement pstmt = connection.prepareStatement(query);
        ObservableList<Termin> liTermine = FXCollections.observableArrayList();

        //System.out.println(calendarDate);
        //pstmt.setTimestamp(1, time);
        //System.out.println(pstmt.getMetaData());
        //System.out.println(pstmt);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            //Überprüft ob Termine an diesem Tag stattfinden
            Date date1 = new Date(rs.getTimestamp(3).getTime());
            Date date2 = Date.valueOf(calendarDate);

            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(date1);
            cal2.setTime(date2);

            if (cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
                    && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
                // Neues Model anlegen
                Termin termin = new Termin();

                // ... belegen
                termin.termin_id.set(rs.getInt("terminID"));
                termin.titel.set(rs.getString("titel"));
                termin.ort.set(rs.getString("ort"));
                termin.beschreibung.set(rs.getString("beschreibung"));
                termin.start_date.set(rs.getDate("start").toLocalDate());
                termin.start_time.set(rs.getTime("start").toLocalTime());

                termin.end_date.set(rs.getDate("ende").toLocalDate());
                termin.end_time.set(rs.getTime("ende").toLocalTime());

                // ... und in Liste hängen
                liTermine.add(termin);
            }

        }

        return liTermine;

    }

    public static Termin find(String svnr, Statement statement) throws SQLException {
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
    //public static List<Termin> findAll(Statement statement) throws SQLException {
    /*String sqlQuery
                = "  select * "
                + " from einheit "
                + " order by name";

        // Datenbankzugriff
        ResultSet rSet = statement.executeQuery(sqlQuery);

        // Spezielle Liste für JavaFX 
        ObservableList<Termin> liTermin = FXCollections.observableArrayList();

        // Den ResultSet durchgehen
        while (rSet.next()) {
            // Neues Model anlegen
            Termin einheit = new Termin();

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
        return liEinheit;*/
    //}
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
        //killAndFill();

        LocalTime lt_start = start_time.get();
        LocalDateTime ldt_start = lt_start.atDate(start_date.get());

        System.out.println(ldt_start);

        LocalTime lt_end = end_time.get();
        LocalDateTime ldt_end = lt_end.atDate(end_date.get());

        System.out.println(ldt_end.toString());

        Connection connection = statement.getConnection();
        String query = "INSERT INTO termin (titel, start, ende, ort, beschreibung) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        pstmt.setString(1, titel.get());

        Timestamp time_start = Timestamp.valueOf(ldt_start);
        pstmt.setTimestamp(2, time_start);
        Timestamp time_end = Timestamp.valueOf(ldt_end);
        pstmt.setTimestamp(3, time_end);
        pstmt.setString(4, ort.get());
        pstmt.setString(5, beschreibung.get());

        pstmt.executeUpdate();
        ResultSet rs = pstmt.getGeneratedKeys();

        if (rs.next()) {
            System.out.println(rs.getInt(1));
            this.termin_id.set(rs.getInt(1));
        } else {

            // throw an exception from here
        }
    }

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

    public ObjectProperty<Number> getTermin_id() {
        return termin_id;
    }

    public StringProperty titelProperty() {
        return titel;
    }

    public StringProperty ortProperty() {
        return ort;
    }

    public StringProperty beschreibungProperty() {
        return beschreibung;
    }

    public ObjectProperty<LocalDate> endDateProperty() {
        return end_date;
    }

    public ObjectProperty<LocalDate> startDateProperty() {
        return start_date;
    }

    public ObjectProperty<LocalTime> endTimeProperty() {
        return end_time;
    }

    public ObjectProperty<LocalTime> startTimeProperty() {
        return start_time;
    }

    public void setEndTimeProperty(LocalTime l) {
        this.end_time.set(l);
    }

    public void setStartTimeProperty(LocalTime l) {
        this.start_time.set(l);
    }

    public ObjectProperty<LocalDateTime> endProperty() {
        return end_datetime;
    }

    public ObjectProperty<LocalDateTime> startProperty() {
        return start_datetime;
    }

    @Override
    public String toString() {
        if (titel != null) {
            return titel.getValue();
        } else {
            return " ";
        }
    }

    public void update(Statement statement) {

        try {
            Connection conn = statement.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE Termin SET titel = ?, start = ?, ende = ?, ort = ?, beschreibung = ? WHERE terminID = ? ");

            LocalTime lt_start = start_time.get();
            LocalDateTime ldt_start = lt_start.atDate(start_date.get());

            System.out.println(ldt_start);

            LocalTime lt_end = end_time.get();
            LocalDateTime ldt_end = lt_end.atDate(end_date.get());

            System.out.println(ldt_end.toString());
            
            pstmt.setString(1, titel.get());

            Timestamp time_start = Timestamp.valueOf(ldt_start);
            pstmt.setTimestamp(2, time_start);
            Timestamp time_end = Timestamp.valueOf(ldt_end);
            pstmt.setTimestamp(3, time_end);
            pstmt.setString(4, ort.get());
            pstmt.setString(5, beschreibung.get());
            System.out.println(termin_id);
            
            pstmt.setInt(6, (int) termin_id.get());

            pstmt.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(Termin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
