package einheit.model;

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

public class Stunde {

    private final ObjectProperty<Number> stundeID = new SimpleObjectProperty<>();
    private StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<Number> col = new SimpleObjectProperty<>();
    private final ObjectProperty<Number> row = new SimpleObjectProperty<>();

    public Stunde() {
        name.set(null);
        col.set(null);
        row.set(null);
    }

    public static List<Stunde> findAll(Statement statement) throws SQLException {
        String sqlQuery
                = "  select * "
                + " from stunde "
                + " order by name";

        // Datenbankzugriff
        ResultSet rSet = statement.executeQuery(sqlQuery);

        // Spezielle Liste für JavaFX 
        ObservableList<Stunde> liStunde = FXCollections.observableArrayList();

        // Den ResultSet durchgehen
        while (rSet.next()) {
            // Neues Model anlegen
            Stunde stunde = new Stunde();

            // ... belegen
            stunde.name.set(rSet.getString("name"));
            stunde.col.set(rSet.getInt("col"));
            stunde.row.set(rSet.getInt("row"));
            Double stundeID = rSet.getDouble("stundeID");
            if (rSet.wasNull()) {
                // Attribut soll nicht 0 sondern null sein.
                stunde.stundeID.set(null);
            } else {
                stunde.stundeID.set(stundeID);
            }

            // ... und in Liste hängen
            liStunde.add(stunde);
        }
        return liStunde;
    }

    public String getName() {
        return this.name.getValue();
    }
    
    public Integer getCol() {
        return (Integer) this.col.getValue();
    }
    
    public Integer getRow() {
        return (Integer) this.row.getValue();
    }
}
