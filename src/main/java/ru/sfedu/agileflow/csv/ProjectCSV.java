package ru.sfedu.agileflow.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import ru.sfedu.agileflow.models.Project;
import ru.sfedu.agileflow.models.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectCSV {
    private static final String FILE_PATH = "src/main/resources/dataCSV/projects.csv";
    private static final String PROJECT_USERS_FILE_PATH = "src/main/resources/dataCSV/project_users.csv";
    private final UserCSV userCsvDAO = new UserCSV();

    public ProjectCSV() {
        initializeCsvFile();
        initializeProjectUsersCsvFile();
    }

    private void initializeCsvFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
                String[] headers = {"id", "name", "description"};
                writer.writeNext(headers);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initializeProjectUsersCsvFile() {
        File file = new File(PROJECT_USERS_FILE_PATH);
        if (!file.exists()) {
            try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
                String[] headers = {"project_id", "user_id"};
                writer.writeNext(headers);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void create(Project project) {
        List<Project> projects = findAll();
        project.setId(projects.isEmpty() ? 1 : projects.get(projects.size() - 1).getId() + 1);
        projects.add(project);
        writeAll(projects);
    }

    public Project findById(int id) {
        Project project = findAll().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
        if (project != null) {
            project.setUsers(getUsersByProject(id));
        }
        return project;
    }

    public List<Project> findAll() {
        List<Project> projects = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(FILE_PATH))) {
            reader.readNext(); // Пропускаем заголовки
            String[] line;
            while ((line = reader.readNext()) != null) {
                Project project = new Project();
                project.setId(Integer.parseInt(line[0]));
                project.setName(line[1]);
                project.setDescription(line[2]);
                project.setUsers(getUsersByProject(project.getId()));
                projects.add(project);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return projects;
    }

    public void update(Project project) {
        List<Project> projects = findAll();
        projects.removeIf(p -> p.getId() == project.getId());
        projects.add(project);
        writeAll(projects);
    }

    public void delete(int id) {
        List<Project> projects = findAll();
        projects.removeIf(project -> project.getId() == id);
        writeAll(projects);

        // Удаляем связи из project_users.csv
        List<String[]> projectUsers = readProjectUsers();
        projectUsers.removeIf(line -> Integer.parseInt(line[0]) == id);
        writeProjectUsers(projectUsers);
    }

    public void addUserToProject(int projectId, int userId) {
        List<String[]> projectUsers = readProjectUsers();
        projectUsers.add(new String[]{String.valueOf(projectId), String.valueOf(userId)});
        writeProjectUsers(projectUsers);
    }

    public void removeUserFromProject(int projectId, int userId) {
        List<String[]> projectUsers = readProjectUsers();
        projectUsers.removeIf(line -> Integer.parseInt(line[0]) == projectId && Integer.parseInt(line[1]) == userId);
        writeProjectUsers(projectUsers);
    }

    public List<User> getUsersByProject(int projectId) {
        List<User> users = new ArrayList<>();
        List<String[]> projectUsers = readProjectUsers();
        for (String[] line : projectUsers) {
            if (Integer.parseInt(line[0]) == projectId) {
                int userId = Integer.parseInt(line[1]);
                User user = userCsvDAO.findById(userId);
                if (user != null) {
                    users.add(user);
                }
            }
        }
        return users;
    }

    public List<Project> getProjectsByUser(int userId) {
        List<Project> projects = new ArrayList<>();
        List<String[]> projectUsers = readProjectUsers();
        for (String[] line : projectUsers) {
            if (Integer.parseInt(line[1]) == userId) {
                int projectId = Integer.parseInt(line[0]);
                Project project = findById(projectId);
                if (project != null) {
                    projects.add(project);
                }
            }
        }
        return projects;
    }

    private void writeAll(List<Project> projects) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_PATH))) {
            String[] headers = {"id", "name", "description"};
            writer.writeNext(headers);
            for (Project project : projects) {
                String[] line = {
                        String.valueOf(project.getId()),
                        project.getName(),
                        project.getDescription()
                };
                writer.writeNext(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String[]> readProjectUsers() {
        List<String[]> projectUsers = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(PROJECT_USERS_FILE_PATH))) {
            reader.readNext(); // Пропускаем заголовки
            String[] line;
            while ((line = reader.readNext()) != null) {
                projectUsers.add(line);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return projectUsers;
    }

    private void writeProjectUsers(List<String[]> projectUsers) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(PROJECT_USERS_FILE_PATH))) {
            String[] headers = {"project_id", "user_id"};
            writer.writeNext(headers);
            writer.writeAll(projectUsers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
