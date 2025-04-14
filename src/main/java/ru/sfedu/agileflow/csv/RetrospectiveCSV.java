package ru.sfedu.agileflow.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import ru.sfedu.agileflow.models.Retrospective;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RetrospectiveCSV {
    private static final String FILE_PATH = "src/main/resources/dataCSV/retrospectives.csv";
    private final SprintCSV sprintCsvDAO = new SprintCSV();

    public RetrospectiveCSV() {
        initializeCsvFile();
    }

    private void initializeCsvFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
                String[] headers = {"id", "sprint_id", "summary", "improvements", "positives"};
                writer.writeNext(headers);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void create(Retrospective retrospective) {
        List<Retrospective> retrospectives = findAll();
        retrospective.setId(retrospectives.isEmpty() ? 1 : retrospectives.get(retrospectives.size() - 1).getId() + 1);
        retrospectives.add(retrospective);
        writeAll(retrospectives);
    }

    public Retrospective findById(int id) {
        return findAll().stream()
                .filter(retrospective -> retrospective.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Retrospective> findAll() {
        List<Retrospective> retrospectives = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(FILE_PATH))) {
            reader.readNext(); // Пропускаем заголовки
            String[] line;
            while ((line = reader.readNext()) != null) {
                Retrospective retrospective = new Retrospective();
                retrospective.setId(Integer.parseInt(line[0]));
                int sprintId = Integer.parseInt(line[1]);
                retrospective.setSprint(sprintCsvDAO.findById(sprintId));
                retrospective.setSummary(line[2]);
                retrospective.setImprovements(Arrays.asList(line[3].split(",")));
                retrospective.setPositives(Arrays.asList(line[4].split(",")));
                retrospectives.add(retrospective);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return retrospectives;
    }

    public void update(Retrospective retrospective) {
        List<Retrospective> retrospectives = findAll();
        retrospectives.removeIf(r -> r.getId() == retrospective.getId());
        retrospectives.add(retrospective);
        writeAll(retrospectives);
    }

    public void delete(int id) {
        List<Retrospective> retrospectives = findAll();
        retrospectives.removeIf(retrospective -> retrospective.getId() == id);
        writeAll(retrospectives);
    }

    private void writeAll(List<Retrospective> retrospectives) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_PATH))) {
            String[] headers = {"id", "sprint_id", "summary", "improvements", "positives"};
            writer.writeNext(headers);
            for (Retrospective retrospective : retrospectives) {
                String[] line = {
                        String.valueOf(retrospective.getId()),
                        String.valueOf(retrospective.getSprint().getId()),
                        retrospective.getSummary(),
                        String.join(",", retrospective.getImprovements()),
                        String.join(",", retrospective.getPositives())
                };
                writer.writeNext(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}