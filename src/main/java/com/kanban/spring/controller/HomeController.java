package com.kanban.spring.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/")
    public String showKanbanBoard(Model model) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode tasks = null;

        ArrayList<JsonNode> openNodes = new ArrayList<>();
        ArrayList<JsonNode> inProgressNodes = new ArrayList<>();
        ArrayList<JsonNode> completedNodes = new ArrayList<>();

        try {
            tasks = objectMapper.readTree(Paths.get("src/main/resources/tasks.json").toFile());

            for (JsonNode node : tasks) { // eine for-each Schleife; tasks ist ein Array aus JsonNode Objekten
                String status = node.get("status").asText();
                String title = node.get("title").asText();

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

            logger.info("offene Aufgaben: " + openNodes.toString()); // logged alle offenen Aufgaben
            logger.info("in Bearbeitung: " + inProgressNodes.toString());
            logger.info("bearbeitete Aufgaben: " + completedNodes.toString());

            } catch (IOException e) {
            e.printStackTrace();
        }
        List<Task> openTasks = objectMapper.convertValue(openNodes, new TypeReference<List<Task>>() {});
        model.addAttribute("openTasks", openTasks);
        List<Task> inProgressTasks = objectMapper.convertValue(inProgressNodes, new TypeReference<List<Task>>() {});
        model.addAttribute("inProgressTasks", inProgressTasks);
        List<Task> completedTasks = objectMapper.convertValue(completedNodes, new TypeReference<List<Task>>() {});
        model.addAttribute("completedTasks", completedTasks);

        return "index"; // Index-Seite wird geladen
    }
}
