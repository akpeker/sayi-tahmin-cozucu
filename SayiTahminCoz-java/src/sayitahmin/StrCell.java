package sayitahmin;

import javafx.beans.property.SimpleStringProperty;

public class StrCell {
    private final SimpleStringProperty cellVal;

    public StrCell(String val) {
        this.cellVal = new SimpleStringProperty(val);
    }

    public String getCellVal() {
        return cellVal.get();
    }

    public void setCellVal(String val) {
    	cellVal.set(val);
    }
} 

