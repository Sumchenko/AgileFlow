package ru.sfedu.agileflow.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import ru.sfedu.agileflow.models.Sprint;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SprintCSV {
    private static final String FILE_PATH = "src/main/resources/dataCSV/sprints.csv";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final ProjectCSV projectCsvDAO = new ProjectCSV();

    public SprintCSV() {
        initializeCsvFile();
    }

    private void initializeCsvFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
                String[] headers = {"id", "startDate", "endDate", "project_id"};
                writer.writeNext(headers);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void create(Sprint sprint) {
        List<Sprint> sprints = findAll();
        sprint.setId(sprints.isEmpty() ? 1 : sprints.get(sprints.size() - 1).getId() + 1);
        sprints.add(sprint);
        writeAll(sprints);
    }

    public Sprint findById(int id) {
        return findAll().stream()
                .filter(sprint -> sprint.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Sprint> findAll() {
        List<Sprint> sprints = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(FILE_PATH))) {
            reader.readNext(); // Пропускаем заголовки
            String[] line;
            while ((line = reader.readNext()) != null) {
                Sprint sprint = new Sprint();
                sprint.setId(Integer.parseInt(line[0]));
                sprint.setStartDate(dateFormat.parse(line[1]));
                sprint.setEndDate(dateFormat.parse(line[2]));
                int projectId = Integer.parseInt(line[3]);
                sprint.setProject(projectCsvDAO.findById(projectId));
                sprints.add(sprint);
            }
        } catch (IOException | CsvValidationException | java.text.ParseException e) {
            e.printStackTrace();
        }
        return sprints;
    }

    public void update(Sprint sprint) {
        List<Sprint> sprints = findAll();
        sprints.removeIf(s -> s.getId() == sprint.getId());
        sprints.add(sprint);
        writeAll(sprints);
    }

    public void delete(int id) {
        List<Sprint> sprints = findAll();
        sprints.removeIf(sprint -> sprint.getId() == id);
        writeAll(sprints);
    }

    private void writeAll(List<Sprint> sprints) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_PATH))) {
            String[] headers = {"id", "startDate", "endDate", "project_id"};
            writer.writeNext(headers);
            for (Sprint sprint : sprints) {
                String[] line = {
                        String.valueOf(sprint.getId()),
                        dateFormat.format(sprint.getStartDate()),
                        dateFormat.format(sprint.getEndDate()),
                        String.valueOf(sprint.getProject().getId())
                };
                writer.writeNext(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
