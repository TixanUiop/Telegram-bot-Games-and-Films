package org.evgeny.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class HttpService {

    public String getJson(String urlString) throws IOException {

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        StringBuilder stringBuilder = new StringBuilder();

        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            while (scanner.hasNext()) {
                stringBuilder.append(scanner.nextLine());
            }
        }
        return stringBuilder.toString();

    }
}
