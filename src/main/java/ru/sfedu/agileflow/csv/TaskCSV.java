package ru.sfedu.agileflow.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import ru.sfedu.agileflow.models.Task;
import ru.sfedu.agileflow.models.TaskStatus;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TaskCSV {
    private static final String FILE_PATH = "src/main/resources/dataCSV/tasks.csv";
    private final SprintCSV sprintCsvDAO = new SprintCSV();
    private final UserCSV userCsvDAO = new UserCSV();

    public TaskCSV() {
        initializeCsvFile();
    }

    private void initializeCsvFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
                String[] headers = {"id", "title", "description", "status", "priority", "sprint_id", "assigned_user_id"};
                writer.writeNext(headers);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void create(Task task) {
        List<Task> tasks = findAll();
        task.setId(tasks.isEmpty() ? 1 : tasks.get(tasks.size() - 1).getId() + 1);
        tasks.add(task);
        writeAll(tasks);
    }

    public Task findById(int id) {
        return findAll().stream()
                .filter(task -> task.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Task> findAll() {
        List<Task> tasks = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(FILE_PATH))) {
            reader.readNext(); // Пропускаем заголовки
            String[] line;
            while ((line = reader.readNext()) != null) {
                Task task = new Task();
                task.setId(Integer.parseInt(line[0]));
                task.setTitle(line[1]);
                task.setDescription(line[2]);
                task.setStatus(TaskStatus.valueOf(line[3]));
                task.setPriority(Integer.parseInt(line[4]));
                int sprintId = Integer.parseInt(line[5]);
                task.setSprint(sprintCsvDAO.findById(sprintId));
                if (!line[6].isEmpty()) {
                    int userId = Integer.parseInt(line[6]);
                    task.setAssignedUser(userCsvDAO.findById(userId));
                }
                tasks.add(task);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public void update(Task task) {
        List<Task> tasks = findAll();
        tasks.removeIf(t -> t.getId() == task.getId());
        tasks.add(task);
        writeAll(tasks);
    }

    public void delete(int id) {
        List<Task> tasks = findAll();
        tasks.removeIf(task -> task.getId() == id);
        writeAll(tasks);
    }

    private void writeAll(List<Task> tasks) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_PATH))) {
            String[] headers = {"id", "title", "description", "status", "priority", "sprint_id", "assigned_user_id"};
            writer.writeNext(headers);
            for (Task task : tasks) {
                String[] line = {
                        String.valueOf(task.getId()),
                        task.getTitle(),
                        task.getDescription(),
                        task.getStatus().toString(),
                        String.valueOf(task.getPriority()),
                        String.valueOf(task.getSprint().getId()),
                        task.getAssignedUser() != null ? String.valueOf(task.getAssignedUser().getId()) : ""
                };
                writer.writeNext(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
