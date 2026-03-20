import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Monitor {
    private final Javalin server;
    private final String PAGES_DIR = "/html";
    private String TRANSACTIONS_URL = "http://localhost:8081/transactions";
    private HttpClient httpClient;
    private List<HashMap<String, Object>> transactions;

    public Monitor() {

        transactions = getTransactions();

        JavalinThymeleaf.init(templateEngine());

        this.server = Javalin.create(config -> {
            config.staticFiles.add(PAGES_DIR, Location.CLASSPATH);})
                .get("/", context -> {
                    context.render("index.html");
                })
                .get("/live-transactions", context -> {
//                    if (simulateLiveTransactions() == null) {
//                        context.status(404).json(Map.of("msg", "Connection Lost"));
//                    }
                    context.json(simulateLiveTransactions());
                })
                .get("/search", context -> {
                    int transactionId = Integer.parseInt(context.queryParam("q"));

                    for (HashMap<String, Object> transaction : getTransactions()) {

                        if (transaction.get("id").equals(transactionId)) {
                            context.status(200).json(transaction);
                            return;
                        }
                    }
                    context.status(404).json(Map.of("msg", "Transactions not found"));
                });
    }

    public static void main(String[] args) {
        Monitor server = new Monitor();
        server.start(8080);
    }

    public void start(int port) {
        this.server.start(port);
    }

    public List<HashMap<String, Object>> simulateLiveTransactions() {
        if (this.transactions.isEmpty()) {
            return new ArrayList<>();
        }
        else if (this.transactions == null) {
            return null;
        }
        HashMap<String, Object> next = transactions.get(0);
        this.transactions.remove(0);
        return List.of(next);
    }

    public List<HashMap<String, Object>> getTransactions() {
        httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        try {
            HttpRequest transactionsRequest = HttpRequest.newBuilder()
                    .uri(URI.create(TRANSACTIONS_URL)).GET().build();

            HttpResponse<String> transactionsResponse = httpClient.send(transactionsRequest, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(transactionsResponse.body(), new TypeReference<List<HashMap<String, Object>>>() {}
            );

        } catch (Exception e) {
            return null;
        }
    }

    private TemplateEngine templateEngine () {
        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("/html/");
        resolver.setSuffix(".html");
        templateEngine.setTemplateResolver(resolver);
        templateEngine.addDialect(new LayoutDialect());
        return templateEngine;
    }
}
