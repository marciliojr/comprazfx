module com.marciliojr.comprazfx.comprazfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.google.gson;
    requires okhttp3;
    requires com.fasterxml.jackson.databind;
    requires spring.web;

    opens com.marciliojr.comprazfx to javafx.fxml;
    opens com.marciliojr.comprazfx.model to javafx.base, com.google.gson;

    exports com.marciliojr.comprazfx;
}
