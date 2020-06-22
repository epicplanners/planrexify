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
import java.time.ZoneOffset;
import java.util.Calendar;
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

    //Zeitpunkte
    private final ObjectProperty<LocalDate> start_date = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> end_date = new SimpleObjectProperty<>();

    private final ObjectProperty<LocalTime> start_time = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> end_time = new SimpleObjectProperty<>();

    private final ObjectProperty<LocalDateTime> start_datetime = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> end_datetime = new SimpleObjectProperty<>();

    //null-Konstruktor
    public Termin() {
        titel.setValue(null);
        ort.setValue(null);
        beschreibung.setValue(null);
    }

    
    //liefert alle Termine an einem bestimmten Tag
    public static ObservableList<Termin> findAll(Statement statement, LocalDate calendarDate) throws SQLException {

        Connection connection = statement.getConnection();
        String query = "SELECT * FROM termin";
        PreparedStatement pstmt = connection.prepareStatement(query);
        ObservableList<Termin> liTermine = FXCollections.observableArrayList();

        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            //Überprüft ob Termine an diesem Tag stattfinden

            //Termindatum
            Date date1 = new Date(rs.getTimestamp(3).getTime());
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);

            //übergegebenen Kalendertag
            Date date2 = Date.valueOf(calendarDate);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);

            //Termindatum und übergegbener Kalendertag müssen übereinstimmen
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

        //Liste an Terminen
        return liTermine;
    }

    //neuer Termin wird erstellt
    public void create(Statement statement) throws SQLException {

        
        //Start und Enzeitpunkt werden zusammengeüfgt
        LocalTime lt_start = start_time.get();
        LocalDateTime ldt_start = lt_start.atDate(start_date.get());

        System.out.println(ldt_start);

        LocalTime lt_end = end_time.get();
        LocalDateTime ldt_end = lt_end.atDate(end_date.get());

        System.out.println(ldt_end.toString());

        //Prepared-Statement
        Connection connection = statement.getConnection();
        String query = "INSERT INTO termin (titel, start, ende, ort, beschreibung) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        pstmt.setString(1, titel.get());

        //Timestamps
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
        }
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

    //bereits bestehenden Termin ändern
    public void update(Statement statement) {

        try {
            //PreparedStatement
            Connection conn = statement.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE Termin SET titel = ?, start = ?, ende = ?, ort = ?, beschreibung = ? WHERE terminID = ? ");

            LocalTime lt_start = start_time.get();
            LocalDateTime ldt_start = lt_start.atDate(start_date.get());

            //System.out.println(ldt_start);

            LocalTime lt_end = end_time.get();
            LocalDateTime ldt_end = lt_end.atDate(end_date.get());

            //System.out.println(ldt_end.toString());

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
