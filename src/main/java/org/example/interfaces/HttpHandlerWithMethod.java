package org.example.interfaces;

public interface HttpHandlerWithMethod {
    void handle(com.sun.net.httpserver.HttpExchange exchange) throws java.io.IOException;
}
