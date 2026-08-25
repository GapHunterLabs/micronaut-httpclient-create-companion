// Demo data for Micronaut HttpClient Create Companion -- used with
// `./gradlew runIde` to capture the real Marketplace screenshot. Open
// this file, the warning should appear on the HttpClient.create() line.

class OrderService {

    void fetchOrdersUnsafely() {
        // Bypasses dependency injection and creates a connection leak
        // risk -- FLAGGED. The javadoc says this factory is for use
        // outside a Micronaut application only.
        HttpClient client = HttpClient.create(new URL("https://api.example.com"));
        client.toBlocking().retrieve("/orders");
    }
}
