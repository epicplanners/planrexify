package kalender.model;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.time.LocalDate;

public class AnchorPaneNode extends AnchorPane {

    // Kalenderdatum
    private LocalDate date;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
