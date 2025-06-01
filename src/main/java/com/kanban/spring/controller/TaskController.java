package com.kanban.spring.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanban.spring.entities.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Controller
public class TaskController {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private JsonNode tasks;
    private final String jsonPath = "src/main/resources/tasks.json";

    public TaskController() {
        loadTasks();
    }

    private void loadTasks() {
        try {
            tasks = objectMapper.readTree(Paths.get(jsonPath).toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/task/{id}")
    public String showTaskPage(@PathVariable String id, Model model) {
        model.addAttribute("taskId", id);

        for (JsonNode node : tasks) { // eine for-each Schleife; tasks ist ein Array aus JsonNode Objekten
            String jsonId = node.get("id").asText();
            String title = node.get("title").asText();
            String description = node.get("description").asText();
            String status = node.get("status").asText();

            if(Objects.equals(jsonId, id)) {
                logger.info(status + " " + jsonId);
                model.addAttribute("taskTitle", title);
                model.addAttribute("taskDescription", description);
                model.addAttribute("taskStatus", status);
            } else {
                logger.info("kein Status" + " " + title);
            }
        }

        return "task"; // Stellt sicher, dass "task.html" in src/main/resources/templates/ liegt
    }

    @PostMapping("/save") // RequestParam kann man nur nutzen, wenn name-Attribut in html vorhanden ist
    public String save(@RequestParam String taskId,
                           @RequestParam String taskTitle,
                           @RequestParam String taskDescription,
                           @RequestParam String taskStatus) {
        // Hier kommt die Logik der Methode
        logger.info("Aufgabe gespeichert! ID: " + taskId + ", Titel: " + taskTitle + ", Beschreibung: " + taskDescription + ", Status: " + taskStatus);
        if(!taskId.isEmpty()) {
//            if() {
//                // wenn die Aufgabe vorhanden ist, dann soll das passende JSON Objekt bearbeitet werden
//            } else {
//                // wenn die Aufgabe noch nicht vorhanden ist, soll sie angelegt werden
//            }
            // JSON-Datei laden
            File file = new File(jsonPath);
            List<Task> tasks;
            try {
                tasks = objectMapper.readValue(file, new TypeReference<List<Task>>() {});

                // ID für neue Aufgabe setzen
                Task newTask = new Task();
                newTask.setId(Long.parseLong(taskId)); // String taskId wird zu einem Long umgewandelt
                newTask.setTitle(taskTitle);
                newTask.setDescription(taskDescription);
                newTask.setStatus(taskStatus);

                // Neue Aufgabe zur Liste hinzufügen
                tasks.add(newTask);

                // JSON-Datei aktualisieren
                objectMapper.writeValue(file, tasks);
                logger.info(taskId + " wurde gespeichert");

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {

        }
        return "redirect:/";
    }

    @PostMapping("/cancel")
    public String cancel() {
        // Hier kommt die Logik der Methode
        logger.info("Zurück zum Board");
        return "redirect:/"; 
    }
}


