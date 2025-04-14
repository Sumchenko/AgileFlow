package ru.sfedu.agileflow.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import ru.sfedu.agileflow.models.User;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class UserCSV {
    private static final String FILE_PATH = "src/main/resources/dataCSV/users.csv";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public UserCSV() {
        initializeCsvFile();
    }

    private void initializeCsvFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
                String[] headers = {"id", "name", "email", "bio", "isActive", "lastLogin", "dateJoined"};
                writer.writeNext(headers);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void create(User user) {
        List<User> users = findAll();
        user.setId(users.isEmpty() ? 1 : users.get(users.size() - 1).getId() + 1);
        users.add(user);
        writeAll(users);
    }

    public User findById(int id) {
        return findAll().stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public User findByEmail(String email) {
        return findAll().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(FILE_PATH))) {
            reader.readNext(); // Пропускаем заголовки
            String[] line;
            while ((line = reader.readNext()) != null) {
                User user = new User();
                user.setId(Integer.parseInt(line[0]));
                user.setName(line[1]);
                user.setEmail(line[2]);
                user.setBio(line[3]);
                user.setActive(Boolean.parseBoolean(line[4]));
                user.setLastLogin(line[5].isEmpty() ? null : dateFormat.parse(line[5]));
                user.setDateJoined(dateFormat.parse(line[6]));
                users.add(user);
            }
        } catch (IOException | CsvValidationException | java.text.ParseException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void update(User user) {
        List<User> users = findAll();
        users.removeIf(u -> u.getId() == user.getId());
        users.add(user);
        writeAll(users);
    }

    public void delete(int id) {
        List<User> users = findAll();
        users.removeIf(user -> user.getId() == id);
        writeAll(users);
    }

    private void writeAll(List<User> users) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(FILE_PATH))) {
            String[] headers = {"id", "name", "email", "bio", "isActive", "lastLogin", "dateJoined"};
            writer.writeNext(headers);
            for (User user : users) {
                String[] line = {
                        String.valueOf(user.getId()),
                        user.getName(),
                        user.getEmail(),
                        user.getBio(),
                        String.valueOf(user.isActive()),
                        user.getLastLogin() != null ? dateFormat.format(user.getLastLogin()) : "",
                        dateFormat.format(user.getDateJoined())
                };
                writer.writeNext(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
