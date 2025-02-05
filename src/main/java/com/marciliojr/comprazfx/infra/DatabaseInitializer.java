package com.marciliojr.comprazfx.infra;

import java.io.File;

public class DatabaseInitializer {
    public static void init() {
        File databaseDir = new File("./database");
        if (!databaseDir.exists()) {
            databaseDir.mkdirs();
        }
    }
}
