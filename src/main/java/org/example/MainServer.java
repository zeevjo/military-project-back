package org.example;

import com.sun.net.httpserver.HttpServer;
import org.example.controllers.AssignController;
import org.example.controllers.HistoryController;
import org.example.controllers.InventoryController;
import org.example.controllers.LoginController;
import org.example.router.Router;
import org.example.utils.Properties;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainServer {

    private static final Logger logger = LogManager.getLogger(MainServer.class);
    private static final String WEB_SITE_PATH = "src/main/resources/static";
    private static final String BASE_URL = Properties.get("BASE_URL");
    private static final int PORT = Properties.get("PORT", 8081);
    private static final int POOL_SIZE = 10;
    private static final int BACK_LOG = 0;

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), BACK_LOG);
        server.setExecutor(Executors.newFixedThreadPool(POOL_SIZE));

        AssignController assignController = new AssignController();
        InventoryController inventoryController = new InventoryController();
        LoginController loginController = new LoginController();
        StaticFileHandler staticFileHandler = new StaticFileHandler(WEB_SITE_PATH);
        HistoryController  historyController = new HistoryController();

        Router router = new Router();
        router.get("/", staticFileHandler::handle);

        router.get("/api/stock", inventoryController::getAllInventory);
        router.get("/api/stock/item",inventoryController::getItemDetails);
        router.post("/api/login", loginController::login);

        router.post("/api/assign", assignController::assign);

        router.get("/api/history", historyController::getHistory);
        router.wrap(server);
        logger.info("Server is running on {}:{}", BASE_URL, PORT);
        server.start();
    }
}