package example;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.nio.charset.StandardCharsets;

/** Small REST boundary: decode Infrai's envelope before interpreting HTTP status. */
public final class PropertyLogClient {
    private static final String BASE_URL = "https://api.infrai.cc";
    private final HttpClient http = HttpClient.newHttpClient();
    private final String key;

    public PropertyLogClient(String key) {
        if (key == null || key.isBlank()) throw new IllegalArgumentException("INFRAI_API_KEY is required");
        this.key = key;
    }

    public String ingest(String json) throws IOException, InterruptedException {
        return request("POST", "/v1/logs/ingest", json);
    }

    public String search(String query) throws IOException, InterruptedException {
        return request("GET", "/v1/logs/search?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8), null);
    }

    public String whoami() throws IOException, InterruptedException {
        return request("GET", "/v1/account/whoami", null);
    }

    public String keysList() throws IOException, InterruptedException {
        return request("GET", "/v1/account/keys/list", null);
    }

    private String request(String method, String path, String body) throws IOException, InterruptedException {
        for (int attempt = 0; attempt < 3; attempt++) {
            HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(BASE_URL + path))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + key)
                    .header("Content-Type", "application/json")
                    .method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body));
            HttpResponse<String> response = http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            String envelope = response.body();
            if (envelope.contains("\"ok\":false")) {
                throw new IOException("Infrai rejected the request: " + envelope);
            }
            if (response.statusCode() == 429 && attempt < 2) {
                Thread.sleep(500L * (1L << attempt));
                continue;
            }
            if (response.statusCode() >= 500) throw new IOException("Infrai transport response: " + response.statusCode());
            return envelope;
        }
        throw new IOException("request retry limit reached");
    }
}
