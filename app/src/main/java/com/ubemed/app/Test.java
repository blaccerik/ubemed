package com.ubemed.app;

import java.io.IOException;

public class Test {

    public static void main(String[] args) throws IOException {
        Process p = Runtime.getRuntime().exec("psql -f d:\\test.sql postgresql://postgres:password@localhost:5432/ubemed");
    }
}
