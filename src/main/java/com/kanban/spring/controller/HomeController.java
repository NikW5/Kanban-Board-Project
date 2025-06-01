package com.kanban.spring.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanban.spring.entities.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller // Kennzeichnet diese Klasse als Spring MVC Controller
public class HomeController { // verlinkt mit unserer Homepage
    // Logger für Debugging und Statusmeldungen, brauch man um loggen zu können
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    // ObjectMapper zur Verarbeitung von JSON-Daten
    private final ObjectMapper objectMapper = new ObjectMapper();
    // Variable für die JSON-Daten, hier speichern wir unsere JSON Daten rein
    private JsonNode tasks;
    // Pfad zur JSON-Datei mit den Aufgaben
    private final String jsonPath = "src/main/resources/tasks.json";

//    // Konstruktor der Klasse, ruft die Methode zum Laden der JSON-Daten auf
//    public HomeController() {
//        loadTasks();
//    }
//
//    // Lädt die Aufgaben aus der JSON-Datei beim Start der Anwendung
//    private void loadTasks() {
//        try {
//            tasks = objectMapper.readTree(Paths.get(jsonPath).toFile());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @GetMapping("/") // Endpoint für die Hauptseite ("/")
    public String showKanbanBoard(Model model) {

        try {
            tasks = objectMapper.readTree(Paths.get(jsonPath).toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Listen zur Kategorisierung der Aufgaben nach Status
        ArrayList<JsonNode> openNodes = new ArrayList<>();
        ArrayList<JsonNode> inProgressNodes = new ArrayList<>();
        ArrayList<JsonNode> completedNodes = new ArrayList<>();

        // Durchläuft jede Aufgabe in der JSON-Datei
        for (JsonNode node : tasks) { // eine for-each Schleife; tasks ist ein Array aus JsonNode Objekten
            String status = node.get("status").asText();
            String title = node.get("title").asText();

            // Aufgaben werden basierend auf ihrem Status kategorisiert
            if(Objects.equals(status, "offen")) {
                logger.info(status + " " + title);
                openNodes.add(node);
            } else if (Objects.equals(status, "in Bearbeitung")) {
                logger.info(status + " " + title);
                inProgressNodes.add(node);
            } else if (Objects.equals(status, "bearbeitet")) {
                logger.info(status + " " + title);
                completedNodes.add(node);
            } else {
                logger.info("kein Status" + " " + title);
            }
        }

        // Loggt die sortierten Listen mit Aufgaben
        logger.info("offene Aufgaben: " + openNodes.toString()); // logged alle offenen Aufgaben
        logger.info("in Bearbeitung: " + inProgressNodes.toString());
        logger.info("bearbeitete Aufgaben: " + completedNodes.toString());

        // Wandelt die JSON-Daten in Task-Objekte um und übergibt sie an das Model, Verknüpfung zwischen Controller und Thymeleaf, Attribute für jede Aufgabenart anlegen und befüllen und dann in thymeleaf nutzen
        List<Task> openTasks = objectMapper.convertValue(openNodes, new TypeReference<List<Task>>() {});
        model.addAttribute("openTasks", openTasks);
        List<Task> inProgressTasks = objectMapper.convertValue(inProgressNodes, new TypeReference<List<Task>>() {});
        model.addAttribute("inProgressTasks", inProgressTasks);
        List<Task> completedTasks = objectMapper.convertValue(completedNodes, new TypeReference<List<Task>>() {});
        model.addAttribute("completedTasks", completedTasks);

        return "index"; // Index-Seite wird geladen
    }

    @PostMapping("/createTask") // POST-Endpoint zum Erstellen eines neuen Tasks
    public String createTask() {
        // Loggt, dass die Methode aufgerufen wurde
        logger.info("Methode wurde aufgerufen!");
        int taskId = tasks.size() + 1; // Generiert eine neue ID für die Aufgabe (einfacher Ansatz basierend auf der Anzahl)
        return "redirect:/task/" + taskId; // bringt uns zur neuen Task mit neu erstellter ID
    }

}
