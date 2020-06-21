package einheit.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    private final StringProperty teacher_cb = new SimpleStringProperty();

    private Teacher teacher;

    public Einheit() {
        name.setValue(null);
        tag.setValue(null);
        raum.setValue(null);
        notizen.setValue(null);
        stunde.setValue(null);
        teacher_cb.setValue(null);
        teacher = new Teacher();
    }

    public void setTeacher(Teacher newTeacher) {
        if (this.teacher != newTeacher) {
            if (this.teacher != null) {
                this.teacher.removeEinheit(this);
            }

            this.teacher = newTeacher;

            if (newTeacher != null) {
                newTeacher.addEinheit(this);
            }
        }
    }

    public static Einheit find(String name, Statement statement) throws SQLException {
        String sqlQuery
                = " select e.name, e.tag, e.raum, e.notizen, e.einheitID, e.stunde, l.lehrerID "
                + " from einheit e "
                + " join unterrichtet using (einheitID) "
                + " join lehrer l using (lehrerID) "
                + " where e.name like '" + name + "'"
                + " order by e.name";

        // Datenbankzugriff
        ResultSet rSet = statement.executeQuery(sqlQuery);

        Einheit einheit = null;

        // Den ResultSet durchgehen
        if (rSet.next()) {
            // Neues Model anlegen
            einheit = new Einheit();

            // ... belegen
            einheit.name.set(rSet.getString("name"));
            einheit.tag.set(rSet.getString("tag"));
            einheit.raum.set(rSet.getString("raum"));
            einheit.notizen.set(rSet.getString("notizen"));
            Double einheit_ID = rSet.getDouble("einheitID");
            einheit.teacher_cb.set(rSet.getString("lehrerID"));
            if (rSet.wasNull()) {
                einheit.einheit_id.set(null);
            } else {
                einheit.einheit_id.set(einheit_ID);
            }

            Double stunde = rSet.getDouble("stunde");
            if (rSet.wasNull()) {
                einheit.stunde.set(null);
            } else {
                einheit.stunde.set(stunde);
            }
        }
        return einheit;
    }

    public static List<Einheit> findAll(Statement statement) throws SQLException {
        String sqlQuery
                = "  select * "
                + " from einheit "
                + " order by name";

        // Datenbankzugriff
        ResultSet rSet = statement.executeQuery(sqlQuery);

        // List
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
                einheit.einheit_id.set(null);
            } else {
                einheit.einheit_id.set(einheit_ID);
            }

            Double stunde = rSet.getDouble("stunde");
            if (rSet.wasNull()) {
                einheit.stunde.set(null);
            } else {
                einheit.stunde.set(stunde);
            }

            liEinheit.add(einheit);
        }
        return liEinheit;
    }

    public void create(Statement statement) {
        try {

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

            //zweites Statement, um generierten Key zu erhalten
            Connection connection = statement.getConnection();
            PreparedStatement statement2 = connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);

            statement2.executeUpdate();
            ResultSet rs = statement2.getGeneratedKeys();
            rs.next();
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

            statement.executeUpdate(sql2);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void update(Statement statement, String oldFach) throws SQLException {

        if (name.get() != null && name.get().trim().length() != 0) {
            String sql2
                    = "  update unterrichtet "
                    + " set lehrerID = "
                    + (teacher.getTeacher_id() == null ? " null " : teacher.getTeacher_id().get().intValue())
                    + " where einheitId = ("
                    + " select einheitID "
                    + " from einheit"
                    + " where name like '" + oldFach + "')";

            System.out.println(sql2);
            statement.executeUpdate(sql2);

            String sql = "update Einheit set "
                    + " name = '" + name.get().trim() + "' , "
                    + " raum = " + (raum.get() == null || raum.get().trim().length() == 0 ? "null" : "'" + raum.get() + "'") + ", "
                    + " notizen = " + (notizen.get() == null || notizen.get().trim().length() == 0 ? "null" : "'" + notizen.get() + "'") + " "
                    + " where name like '" + oldFach + "'";
            System.out.println(sql);
            statement.executeUpdate(sql);
        }
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

    public StringProperty teacher_cbProperty() {
        return teacher_cb;
    }

    public Teacher getTeacher() {
        return teacher;
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
