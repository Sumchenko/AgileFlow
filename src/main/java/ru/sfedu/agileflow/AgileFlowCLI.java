package ru.sfedu.agileflow;

import ru.sfedu.agileflow.dao.*;
import ru.sfedu.agileflow.models.*;
import ru.sfedu.agileflow.models.TaskStatus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Основной класс для управления проектами AgileFlow через консольное меню.
 */
public class AgileFlowCLI {

    private static User currentUser = null; // Текущий пользователь (сессия)
    private final UserDAO userDAO = new UserDAO();
    private final ProjectDAO projectDAO = new ProjectDAO();
    private final ProjectUserDAO projectUserDAO = new ProjectUserDAO();
    private final SprintDAO sprintDAO = new SprintDAO();
    private final TaskDAO taskDAO = new TaskDAO();
    private final RetrospectiveDAO retrospectiveDAO = new RetrospectiveDAO();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Точка входа приложения.
     */
    public static void main(String[] args) {
        AgileFlowCLI cli = new AgileFlowCLI();
        cli.run();
    }

    /**
     * Запуск консольного меню.
     */
    public void run() {
        while (true) {
            if (currentUser == null) {
                showMainMenu();
            } else {
                showLoggedInMenu();
            }
        }
    }

    /**
     * Отображение главного меню (до входа в систему).
     */
    private void showMainMenu() {
        System.out.println("\n=== AgileFlow ===");
        System.out.println("1. Зарегистрировать пользователя");
        System.out.println("2. Войти в систему");
        System.out.println("3. Выход");
        System.out.print("Выберите опцию: ");

        int choice = readIntInput();
        switch (choice) {
            case 1 -> registerUser();
            case 2 -> loginUser();
            case 3 -> System.exit(0);
            default -> System.out.println("Неверный выбор. Попробуйте снова.");
        }
    }

    /**
     * Отображение меню для авторизованного пользователя.
     */
    private void showLoggedInMenu() {
        System.out.println("\n=== AgileFlow (Добро пожаловать, " + currentUser.getName() + ") ===");
        System.out.println("1. Управление пользователями");
        System.out.println("2. Управление проектами");
        System.out.println("3. Управление спринтами");
        System.out.println("4. Управление задачами");
        System.out.println("5. Управление ретроспективами");
        System.out.println("6. Выйти из системы");
        System.out.println("7. Выход из приложения");
        System.out.print("Выберите опцию: ");

        int choice = readIntInput();
        switch (choice) {
            case 1 -> manageUsers();
            case 2 -> manageProjects();
            case 3 -> manageSprints();
            case 4 -> manageTasks();
            case 5 -> manageRetrospectives();
            case 6 -> logoutUser();
            case 7 -> System.exit(0);
            default -> System.out.println("Неверный выбор. Попробуйте снова.");
        }
    }

    /**
     * Чтение числового ввода от пользователя.
     */
    private int readIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1; // Неверный ввод
        }
    }

    /**
     * Чтение строки от пользователя.
     */
    private String readStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    /**
     * Проверка, авторизован ли пользователь.
     */
    private void checkLoggedIn() {
        if (currentUser == null) {
            throw new IllegalStateException("Вы должны войти в систему.");
        }
    }

    /**
     * Проверка доступа к проекту.
     */
    private void checkProjectAccess(int projectId) {
        List<Project> userProjects = projectUserDAO.getProjectsByUser(currentUser.getId());
        if (userProjects.stream().noneMatch(p -> p.getId() == projectId)) {
            throw new IllegalStateException("У вас нет доступа к проекту с ID " + projectId);
        }
    }

    // === Методы для управления пользователями ===

    private void registerUser() {
        String name = readStringInput("Введите имя пользователя: ");
        String email = readStringInput("Введите email пользователя: ");
        String bio = readStringInput("Введите биографию пользователя (или оставьте пустым): ");

        if (userDAO.findByEmail(email) != null) {
            System.out.println("Пользователь с email " + email + " уже существует.");
            return;
        }

        User user = new User(name, email, bio, true, null, new Date());
        userDAO.create(user);
        System.out.println("Пользователь успешно зарегистрирован. ID: " + user.getId());
    }

    private void loginUser() {
        if (currentUser != null) {
            System.out.println("Вы уже вошли как " + currentUser.getEmail() + ". Сначала выйдите из системы.");
            return;
        }

        String email = readStringInput("Введите email: ");
        User user = userDAO.findByEmail(email);
        if (user == null) {
            System.out.println("Пользователь с email " + email + " не найден. Зарегистрируйтесь.");
            return;
        }

        user.setLastLogin(new Date());
        userDAO.update(user);
        currentUser = user;
        System.out.println("Успешный вход. Добро пожаловать, " + user.getName() + "!");
    }

    private void logoutUser() {
        if (currentUser == null) {
            System.out.println("Вы не вошли в систему.");
            return;
        }

        System.out.println("Выход выполнен. До свидания, " + currentUser.getName() + "!");
        currentUser = null;
    }

    private void manageUsers() {
        while (true) {
            System.out.println("\n=== Управление пользователями ===");
            System.out.println("1. Создать пользователя");
            System.out.println("2. Список пользователей");
            System.out.println("3. Обновить пользователя");
            System.out.println("4. Удалить пользователя");
            System.out.println("5. Назад");
            System.out.print("Выберите опцию: ");

            int choice = readIntInput();
            switch (choice) {
                case 1 -> createUser();
                case 2 -> listUsers();
                case 3 -> updateUser();
                case 4 -> deleteUser();
                case 5 -> {
                    return;
                }
                default -> System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private void createUser() {
        checkLoggedIn();
        String name = readStringInput("Введите имя пользователя: ");
        String email = readStringInput("Введите email пользователя: ");
        String bio = readStringInput("Введите биографию пользователя (или оставьте пустым): ");

        if (userDAO.findByEmail(email) != null) {
            System.out.println("Пользователь с email " + email + " уже существует.");
            return;
        }

        User user = new User(name, email, bio, true, null, new Date());
        userDAO.create(user);
        System.out.println("Пользователь создан. ID: " + user.getId());
    }

    private void listUsers() {
        checkLoggedIn();
        List<User> users = userDAO.findAll();
        if (users.isEmpty()) {
            System.out.println("Пользователи не найдены.");
        } else {
            users.forEach(user -> System.out.println("ID: " + user.getId() + ", Имя: " + user.getName() + ", Email: " + user.getEmail()));
        }
    }

    private void updateUser() {
        checkLoggedIn();
        int id = Integer.parseInt(readStringInput("Введите ID пользователя: "));
        User user = userDAO.findById(id);
        if (user == null) {
            System.out.println("Пользователь с ID " + id + " не найден.");
            return;
        }

        String name = readStringInput("Введите новое имя (или оставьте пустым): ");
        String email = readStringInput("Введите новый email (или оставьте пустым): ");
        String bio = readStringInput("Введите новую биографию (или оставьте пустым): ");
        String activeInput = readStringInput("Введите статус активности (true/false, или оставьте пустым): ");

        if (!name.isEmpty()) user.setName(name);
        if (!email.isEmpty()) user.setEmail(email);
        if (!bio.isEmpty()) user.setBio(bio);
        if (!activeInput.isEmpty()) user.setActive(Boolean.parseBoolean(activeInput));

        userDAO.update(user);
        System.out.println("Пользователь обновлен.");
    }

    private void deleteUser() {
        checkLoggedIn();
        int id = Integer.parseInt(readStringInput("Введите ID пользователя: "));
        User user = userDAO.findById(id);
        if (user == null) {
            System.out.println("Пользователь с ID " + id + " не найден.");
            return;
        }

        userDAO.delete(id);
        System.out.println("Пользователь удален.");
    }

    // === Методы для управления проектами ===

    private void manageProjects() {
        while (true) {
            System.out.println("\n=== Управление проектами ===");
            System.out.println("1. Создать проект");
            System.out.println("2. Список проектов");
            System.out.println("3. Обновить проект");
            System.out.println("4. Удалить проект");
            System.out.println("5. Добавить пользователя в проект");
            System.out.println("6. Удалить пользователя из проекта");
            System.out.println("7. Список пользователей проекта");
            System.out.println("8. Назад");
            System.out.print("Выберите опцию: ");

            int choice = readIntInput();
            switch (choice) {
                case 1 -> createProject();
                case 2 -> listProjects();
                case 3 -> updateProject();
                case 4 -> deleteProject();
                case 5 -> addUserToProject();
                case 6 -> removeUserFromProject();
                case 7 -> listProjectUsers();
                case 8 -> {
                    return;
                }
                default -> System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private void createProject() {
        checkLoggedIn();
        String name = readStringInput("Введите название проекта: ");
        String description = readStringInput("Введите описание проекта (или оставьте пустым): ");

        Project project = new Project(name, description);
        projectDAO.create(project);
        projectUserDAO.addUserToProject(project.getId(), currentUser.getId());
        System.out.println("Проект создан. ID: " + project.getId());
    }

    private void listProjects() {
        checkLoggedIn();
        List<Project> projects = projectUserDAO.getProjectsByUser(currentUser.getId());
        if (projects.isEmpty()) {
            System.out.println("Проекты не найдены.");
        } else {
            projects.forEach(project -> System.out.println("ID: " + project.getId() + ", Название: " + project.getName()));
        }
    }

    private void updateProject() {
        checkLoggedIn();
        int id = Integer.parseInt(readStringInput("Введите ID проекта: "));
        checkProjectAccess(id);
        Project project = projectDAO.findById(id);
        if (project == null) {
            System.out.println("Проект с ID " + id + " не найден.");
            return;
        }

        String name = readStringInput("Введите новое название (или оставьте пустым): ");
        String description = readStringInput("Введите новое описание (или оставьте пустым): ");

        if (!name.isEmpty()) project.setName(name);
        if (!description.isEmpty()) project.setDescription(description);

        projectDAO.update(project);
        System.out.println("Проект обновлен.");
    }

    private void deleteProject() {
        checkLoggedIn();
        int id = Integer.parseInt(readStringInput("Введите ID проекта: "));
        checkProjectAccess(id);
        Project project = projectDAO.findById(id);
        if (project == null) {
            System.out.println("Проект с ID " + id + " не найден.");
            return;
        }

        projectDAO.delete(id);
        System.out.println("Проект удален (включая все связанные спринты, задачи и ретроспективы).");
    }

    private void addUserToProject() {
        checkLoggedIn();
        int projectId = Integer.parseInt(readStringInput("Введите ID проекта: "));
        checkProjectAccess(projectId);
        if (projectDAO.findById(projectId) == null) {
            System.out.println("Проект с ID " + projectId + " не найден.");
            return;
        }
        int userId = Integer.parseInt(readStringInput("Введите ID пользователя: "));
        if (userDAO.findById(userId) == null) {
            System.out.println("Пользователь с ID " + userId + " не найден.");
            return;
        }

        projectUserDAO.addUserToProject(projectId, userId);
        System.out.println("Пользователь добавлен в проект.");
    }

    private void removeUserFromProject() {
        checkLoggedIn();
        int projectId = Integer.parseInt(readStringInput("Введите ID проекта: "));
        checkProjectAccess(projectId);
        if (projectDAO.findById(projectId) == null) {
            System.out.println("Проект с ID " + projectId + " не найден.");
            return;
        }
        int userId = Integer.parseInt(readStringInput("Введите ID пользователя: "));
        if (userId == currentUser.getId()) {
            System.out.println("Вы не можете удалить себя из проекта.");
            return;
        }

        projectUserDAO.removeUserFromProject(projectId, userId);
        System.out.println("Пользователь удален из проекта.");
    }

    private void listProjectUsers() {
        checkLoggedIn();
        int projectId = Integer.parseInt(readStringInput("Введите ID проекта: "));
        checkProjectAccess(projectId);
        if (projectDAO.findById(projectId) == null) {
            System.out.println("Проект с ID " + projectId + " не найден.");
            return;
        }

        List<User> users = projectUserDAO.getUsersByProject(projectId);
        if (users.isEmpty()) {
            System.out.println("Пользователи в проекте не найдены.");
        } else {
            users.forEach(user -> System.out.println("ID: " + user.getId() + ", Имя: " + user.getName() + ", Email: " + user.getEmail()));
        }
    }

    // === Методы для управления спринтами ===

    private void manageSprints() {
        while (true) {
            System.out.println("\n=== Управление спринтами ===");
            System.out.println("1. Создать спринт");
            System.out.println("2. Список спринтов");
            System.out.println("3. Обновить спринт");
            System.out.println("4. Удалить спринт");
            System.out.println("5. Назад");
            System.out.print("Выберите опцию: ");

            int choice = readIntInput();
            switch (choice) {
                case 1 -> createSprint();
                case 2 -> listSprints();
                case 3 -> updateSprint();
                case 4 -> deleteSprint();
                case 5 -> {
                    return;
                }
                default -> System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private void createSprint() {
        try {
            checkLoggedIn();
            int projectId = Integer.parseInt(readStringInput("Введите ID проекта: "));
            checkProjectAccess(projectId);
            Project project = projectDAO.findById(projectId);
            if (project == null) {
                System.out.println("Проект с ID " + projectId + " не найден.");
                return;
            }

            String startDate = readStringInput("Введите дату начала (гггг-мм-дд): ");
            String endDate = readStringInput("Введите дату окончания (гггг-мм-дд): ");
            Date start = dateFormat.parse(startDate);
            Date end = dateFormat.parse(endDate);

            Sprint sprint = new Sprint(start, end, project);
            sprintDAO.create(sprint);
            System.out.println("Спринт создан. ID: " + sprint.getId());
        } catch (ParseException e) {
            System.out.println("Ошибка формата даты: " + e.getMessage());
        }
    }

    private void listSprints() {
        checkLoggedIn();
        int projectId = Integer.parseInt(readStringInput("Введите ID проекта: "));
        checkProjectAccess(projectId);
        List<Sprint> sprints = sprintDAO.findAll().stream()
                .filter(s -> s.getProject().getId() == projectId)
                .toList();
        if (sprints.isEmpty()) {
            System.out.println("Спринты не найдены.");
        } else {
            sprints.forEach(sprint -> System.out.println("ID: " + sprint.getId() + ", Начало: " + sprint.getStartDate() + ", Окончание: " + sprint.getEndDate()));
        }
    }

    private void updateSprint() {
        try {
            checkLoggedIn();
            int id = Integer.parseInt(readStringInput("Введите ID спринта: "));
            Sprint sprint = sprintDAO.findById(id);
            if (sprint == null) {
                System.out.println("Спринт с ID " + id + " не найден.");
                return;
            }
            checkProjectAccess(sprint.getProject().getId());

            String startDate = readStringInput("Введите новую дату начала (гггг-мм-дд, или оставьте пустым): ");
            String endDate = readStringInput("Введите новую дату окончания (гггг-мм-дд, или оставьте пустым): ");

            if (!startDate.isEmpty()) sprint.setStartDate(dateFormat.parse(startDate));
            if (!endDate.isEmpty()) sprint.setEndDate(dateFormat.parse(endDate));

            sprintDAO.update(sprint);
            System.out.println("Спринт обновлен.");
        } catch (ParseException e) {
            System.out.println("Ошибка формата даты: " + e.getMessage());
        }
    }

    private void deleteSprint() {
        checkLoggedIn();
        int id = Integer.parseInt(readStringInput("Введите ID спринта: "));
        Sprint sprint = sprintDAO.findById(id);
        if (sprint == null) {
            System.out.println("Спринт с ID " + id + " не найден.");
            return;
        }
        checkProjectAccess(sprint.getProject().getId());

        sprintDAO.delete(id);
        System.out.println("Спринт удален (включая связанные задачи и ретроспективы).");
    }

    // === Методы для управления задачами ===

    private void manageTasks() {
        while (true) {
            System.out.println("\n=== Управление задачами ===");
            System.out.println("1. Создать задачу");
            System.out.println("2. Список задач");
            System.out.println("3. Обновить задачу");
            System.out.println("4. Удалить задачу");
            System.out.println("5. Назначить пользователя на задачу");
            System.out.println("6. Снять пользователя с задачи");
            System.out.println("7. Назад");
            System.out.print("Выберите опцию: ");

            int choice = readIntInput();
            switch (choice) {
                case 1 -> createTask();
                case 2 -> listTasks();
                case 3 -> updateTask();
                case 4 -> deleteTask();
                case 5 -> assignTask();
                case 6 -> unassignTask();
                case 7 -> {
                    return;
                }
                default -> System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private void createTask() {
        checkLoggedIn();
        int sprintId = Integer.parseInt(readStringInput("Введите ID спринта: "));
        Sprint sprint = sprintDAO.findById(sprintId);
        if (sprint == null) {
            System.out.println("Спринт с ID " + sprintId + " не найден.");
            return;
        }
        checkProjectAccess(sprint.getProject().getId());

        String title = readStringInput("Введите название задачи: ");
        String description = readStringInput("Введите описание задачи (или оставьте пустым): ");
        int priority = Integer.parseInt(readStringInput("Введите приоритет задачи (по умолчанию 1): "));

        Task task = new Task(title, description, TaskStatus.TODO, priority, sprint, null);
        taskDAO.create(task);
        System.out.println("Задача создана. ID: " + task.getId());
    }

    private void listTasks() {
        checkLoggedIn();
        int sprintId = Integer.parseInt(readStringInput("Введите ID спринта: "));
        Sprint sprint = sprintDAO.findById(sprintId);
        if (sprint == null) {
            System.out.println("Спринт с ID " + sprintId + " не найден.");
            return;
        }
        checkProjectAccess(sprint.getProject().getId());

        List<Task> tasks = taskDAO.findAll().stream()
                .filter(t -> t.getSprint().getId() == sprintId)
                .toList();
        if (tasks.isEmpty()) {
            System.out.println("Задачи не найдены.");
        } else {
            tasks.forEach(task -> System.out.println("ID: " + task.getId() + ", Название: " + task.getTitle() +
                    ", Статус: " + task.getStatus() + ", Приоритет: " + task.getPriority() +
                    (task.getAssignedUser() != null ? ", Назначен: " + task.getAssignedUser().getName() : "")));
        }
    }

    private void updateTask() {
        checkLoggedIn();
        int id = Integer.parseInt(readStringInput("Введите ID задачи: "));
        Task task = taskDAO.findById(id);
        if (task == null) {
            System.out.println("Задача с ID " + id + " не найдена.");
            return;
        }
        checkProjectAccess(task.getSprint().getProject().getId());

        String title = readStringInput("Введите новое название (или оставьте пустым): ");
        String description = readStringInput("Введите новое описание (или оставьте пустым): ");
        String status = readStringInput("Введите новый статус (TODO, IN_PROGRESS, DONE, или оставьте пустым): ");
        String priority = readStringInput("Введите новый приоритет (или оставьте пустым): ");

        if (!title.isEmpty()) task.setTitle(title);
        if (!description.isEmpty()) task.setDescription(description);
        if (!status.isEmpty()) task.setStatus(TaskStatus.valueOf(status));
        if (!priority.isEmpty()) task.setPriority(Integer.parseInt(priority));

        taskDAO.update(task);
        System.out.println("Задача обновлена.");
    }

    private void deleteTask() {
        checkLoggedIn();
        int id = Integer.parseInt(readStringInput("Введите ID задачи: "));
        Task task = taskDAO.findById(id);
        if (task == null) {
            System.out.println("Задача с ID " + id + " не найдена.");
            return;
        }
        checkProjectAccess(task.getSprint().getProject().getId());

        taskDAO.delete(id);
        System.out.println("Задача удалена.");
    }

    private void assignTask() {
        checkLoggedIn();
        int taskId = Integer.parseInt(readStringInput("Введите ID задачи: "));
        Task task = taskDAO.findById(taskId);
        if (task == null) {
            System.out.println("Задача с ID " + taskId + " не найдена.");
            return;
        }
        checkProjectAccess(task.getSprint().getProject().getId());

        int userId = Integer.parseInt(readStringInput("Введите ID пользователя: "));
        User user = userDAO.findById(userId);
        if (user == null) {
            System.out.println("Пользователь с ID " + userId + " не найден.");
            return;
        }

        List<User> projectUsers = projectUserDAO.getUsersByProject(task.getSprint().getProject().getId());
        if (projectUsers.stream().noneMatch(u -> u.getId() == userId)) {
            System.out.println("Пользователь с ID " + userId + " не добавлен в проект. Сначала добавьте его.");
            return;
        }

        task.setAssignedUser(user);
        taskDAO.update(task);
        System.out.println("Пользователь назначен на задачу.");
    }

    private void unassignTask() {
        checkLoggedIn();
        int taskId = Integer.parseInt(readStringInput("Введите ID задачи: "));
        Task task = taskDAO.findById(taskId);
        if (task == null) {
            System.out.println("Задача с ID " + taskId + " не найдена.");
            return;
        }
        checkProjectAccess(task.getSprint().getProject().getId());

        task.setAssignedUser(null);
        taskDAO.update(task);
        System.out.println("Пользователь снят с задачи.");
    }

    // === Методы для управления ретроспективами ===

    private void manageRetrospectives() {
        while (true) {
            System.out.println("\n=== Управление ретроспективами ===");
            System.out.println("1. Создать ретроспективу");
            System.out.println("2. Список ретроспектив");
            System.out.println("3. Обновить ретроспективу");
            System.out.println("4. Удалить ретроспективу");
            System.out.println("5. Назад");
            System.out.print("Выберите опцию: ");

            int choice = readIntInput();
            switch (choice) {
                case 1 -> createRetrospective();
                case 2 -> listRetrospectives();
                case 3 -> updateRetrospective();
                case 4 -> deleteRetrospective();
                case 5 -> {
                    return;
                }
                default -> System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private void createRetrospective() {
        checkLoggedIn();
        int sprintId = Integer.parseInt(readStringInput("Введите ID спринта: "));
        Sprint sprint = sprintDAO.findById(sprintId);
        if (sprint == null) {
            System.out.println("Спринт с ID " + sprintId + " не найден.");
            return;
        }
        checkProjectAccess(sprint.getProject().getId());

        String summary = readStringInput("Введите резюме ретроспективы: ");
        String improvements = readStringInput("Введите список улучшений (через запятую, или оставьте пустым): ");
        String positives = readStringInput("Введите список положительных моментов (через запятую, или оставьте пустым): ");

        List<String> improvementsList = improvements.isEmpty() ? List.of() : Arrays.asList(improvements.split(","));
        List<String> positivesList = positives.isEmpty() ? List.of() : Arrays.asList(positives.split(","));
        Retrospective retrospective = new Retrospective(sprint, summary, improvementsList, positivesList);
        retrospectiveDAO.create(retrospective);
        System.out.println("Ретроспектива создана. ID: " + retrospective.getId());
    }

    private void listRetrospectives() {
        checkLoggedIn();
        int projectId = Integer.parseInt(readStringInput("Введите ID проекта: "));
        checkProjectAccess(projectId);
        List<Retrospective> retrospectives = retrospectiveDAO.findAll().stream()
                .filter(r -> r.getSprint().getProject().getId() == projectId)
                .toList();
        if (retrospectives.isEmpty()) {
            System.out.println("Ретроспективы не найдены.");
        } else {
            retrospectives.forEach(r -> System.out.println("ID: " + r.getId() + ", Спринт ID: " + r.getSprint().getId() +
                    ", Резюме: " + r.getSummary()));
        }
    }

    private void updateRetrospective() {
        checkLoggedIn();
        int id = Integer.parseInt(readStringInput("Введите ID ретроспективы: "));
        Retrospective retrospective = retrospectiveDAO.findById(id);
        if (retrospective == null) {
            System.out.println("Ретроспектива с ID " + id + " не найдена.");
            return;
        }
        checkProjectAccess(retrospective.getSprint().getProject().getId());

        String summary = readStringInput("Введите новое резюме (или оставьте пустым): ");
        String improvements = readStringInput("Введите новый список улучшений (через запятую, или оставьте пустым): ");
        String positives = readStringInput("Введите новый список положительных моментов (через запятую, или оставьте пустым): ");

        if (!summary.isEmpty()) retrospective.setSummary(summary);
        if (!improvements.isEmpty()) retrospective.setImprovements(Arrays.asList(improvements.split(",")));
        if (!positives.isEmpty()) retrospective.setPositives(Arrays.asList(positives.split(",")));

        retrospectiveDAO.update(retrospective);
        System.out.println("Ретроспектива обновлена.");
    }

    private void deleteRetrospective() {
        checkLoggedIn();
        int id = Integer.parseInt(readStringInput("Введите ID ретроспективы: "));
        Retrospective retrospective = retrospectiveDAO.findById(id);
        if (retrospective == null) {
            System.out.println("Ретроспектива с ID " + id + " не найдена.");
            return;
        }
        checkProjectAccess(retrospective.getSprint().getProject().getId());

        retrospectiveDAO.delete(id);
        System.out.println("Ретроспектива удалена.");
    }
}