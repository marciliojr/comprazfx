module com.marciliojr.comprazfx.comprazfx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.marciliojr.comprazfx.comprazfx to javafx.fxml;
    exports com.marciliojr.comprazfx.comprazfx;
}