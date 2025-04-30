package ru.sfedu.agileflow;

import org.apache.log4j.Logger;
import ru.sfedu.agileflow.constants.Constants;
import ru.sfedu.agileflow.dao.*;
import ru.sfedu.agileflow.models.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Класс для реализации командного интерфейса (CLI) приложения AgileFlow.
 */
public class CLIApplication {
    private static final Logger log = Logger.getLogger(CLIApplication.class);
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserDAO userDAO = new UserDAO();
    private static final ProjectDAO projectDAO = new ProjectDAO();
    private static final ProjectUserDAO projectUserDAO = new ProjectUserDAO();
    private static final SprintDAO sprintDAO = new SprintDAO();
    private static final TaskDAO taskDAO = new TaskDAO();
    private static final RetrospectiveDAO retrospectiveDAO = new RetrospectiveDAO();
    private static User currentUser;

    /**
     * Запускает CLI приложение.
     */
    public static void main(String[] args) {
        String methodName = "main";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        try {
            showLoginMenu();
            while (true) {
                showMainMenu();
            }
        } catch (Exception e) {
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            throw new RuntimeException("Ошибка в работе CLI приложения", e);
        } finally {
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
        }
    }

    /**
     * Отображает меню авторизации/регистрации.
     */
    private static void showLoginMenu() {
        String methodName = "showLoginMenu";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        while (currentUser == null) {
            System.out.println("\n=== Меню входа ===");
            System.out.println("1. Войти");
            System.out.println("2. Зарегистрироваться");
            System.out.println("3. Выход");
            System.out.print("Выберите опцию: ");
            String choice = scanner.nextLine();
            log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "Выбрана опция: " + choice));

            switch (choice) {
                case "1" -> login();
                case "2" -> register();
                case "3" -> System.exit(0);
                default -> System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
    }

    /**
     * Выполняет вход пользователя.
     */
    private static void login() {
        String methodName = "login";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        System.out.print("Введите email: ");
        String email = scanner.nextLine();
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "Email: " + email));

        Optional<User> userOpt = userDAO.findByEmail(email);
        if (userOpt.isPresent()) {
            currentUser = userOpt.get();
            currentUser.setLastLogin(new Date());
            userDAO.update(currentUser);
            System.out.println("Добро пожаловать, " + currentUser.getName() + "!");
            log.info("login [1] Пользователь успешно вошел: " + currentUser.getName());
        } else {
            System.out.println("Пользователь с таким email не найден.");
            log.error("login [1] Пользователь с email " + email + " не найден");
        }
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
    }

    /**
     * Регистрирует нового пользователя.
     */
    private static void register() {
        String methodName = "register";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        System.out.print("Введите имя: ");
        String name = scanner.nextLine();
        System.out.print("Введите email: ");
        String email = scanner.nextLine();
        System.out.print("Введите биографию: ");
        String bio = scanner.nextLine();

        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "Регистрация пользователя: " + name + ", " + email));

        User user = new User(name, email, bio, true, new Date());
        try {
            userDAO.create(user);
            currentUser = user;
            System.out.println("Регистрация успешна! Добро пожаловать, " + name + "!");
            log.info("register [1] Пользователь успешно зарегистрирован: " + name);
        } catch (Exception e) {
            System.out.println("Ошибка при регистрации: " + e.getMessage());
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
        }
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
    }

    /**
     * Отображает главное меню.
     */
    private static void showMainMenu() {
        String methodName = "showMainMenu";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        System.out.println("\n=== Главное меню ===");
        System.out.println("1. Просмотреть проекты");
        System.out.println("2. Создать проект");
        System.out.println("3. Выйти");
        System.out.print("Выберите опцию: ");
        String choice = scanner.nextLine();
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "Выбрана опция: " + choice));

        switch (choice) {
            case "1" -> showProjects();
            case "2" -> createProject();
            case "3" -> {
                currentUser = null;
                showLoginMenu();
            }
            default -> System.out.println("Неверный выбор. Попробуйте снова.");
        }
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
    }

    /**
     * Отображает список проектов пользователя.
     */
    private static void showProjects() {
        String methodName = "showProjects";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        List<Project> projects = projectUserDAO.getProjectsByUser(currentUser.getId());
        log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Найдено проектов: " + projects.size()));

        if (projects.isEmpty()) {
            System.out.println("У вас нет проектов.");
        } else {
            System.out.println("\nВаши проекты:");
            for (int i = 0; i < projects.size(); i++) {
                System.out.println((i + 1) + ". " + projects.get(i).getName() + " (" + projects.get(i).getDescription() + ")");
            }
            System.out.print("Выберите проект (номер) или 0 для возврата: ");
            String choice = scanner.nextLine();
            log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "Выбрана опция: " + choice));

            if (!choice.equals("0")) {
                try {
                    int index = Integer.parseInt(choice) - 1;
                    if (index >= 0 && index < projects.size()) {
                        showProjectMenu(projects.get(index));
                    } else {
                        System.out.println("Неверный выбор.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Введите корректный номер.");
                    log.error(String.format(Constants.LOG_ERROR, methodName, "Некорректный ввод: " + choice));
                }
            }
        }
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
    }

    /**
     * Создает новый проект.
     */
    private static void createProject() {
        String methodName = "createProject";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        System.out.print("Введите название проекта: ");
        String name = scanner.nextLine();
        System.out.print("Введите описание проекта: ");
        String description = scanner.nextLine();

        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "Создание проекта: " + name));

        Project project = new Project(name, description);
        try {
            projectDAO.create(project);
            projectUserDAO.addUserToProject(project.getId(), currentUser.getId());
            System.out.println("Проект успешно создан!");
            log.info("createProject [1] Проект успешно создан: " + name);
        } catch (Exception e) {
            System.out.println("Ошибка при создании проекта: " + e.getMessage());
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
        }
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
    }

    /**
     * Отображает меню управления проектом.
     * @param project Проект для управления
     */
    private static void showProjectMenu(Project project) {
        String methodName = "showProjectMenu";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        while (true) {
            System.out.println("\n=== Управление проектом: " + project.getName() + " ===");
            System.out.println("1. Просмотреть пользователей");
            System.out.println("2. Добавить пользователя");
            System.out.println("3. Удалить пользователя");
            System.out.println("4. Просмотреть спринты");
            System.out.println("5. Создать спринт");
            System.out.println("6. Вернуться в главное меню");
            System.out.print("Выберите опцию: ");
            String choice = scanner.nextLine();
            log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "Выбрана опция: " + choice));

            switch (choice) {
                case "1" -> showProjectUsers(project);
                case "2" -> addUserToProject(project);
                case "3" -> removeUserFromProject(project);
                case "4" -> showSprints(project);
                case "5" -> createSprint(project);
                case "6" -> {
                    return;
                }
                default -> System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    /**
     * Отображает пользователей проекта.
     * @param project Проект
     */
    private static void showProjectUsers(Project project) {
        String methodName = "showProjectUsers";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        List<User> users = projectUserDAO.getUsersByProject(project.getId());
        log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Найдено пользователей: " + users.size()));

        if (users.isEmpty()) {
            System.out.println("В проекте нет пользователей.");
        } else {
            System.out.println("\nПользователи проекта:");
            for (User user : users) {
                System.out.println("- " + user.getName() + " (" + user.getEmail() + ")");
            }
        }
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
    }

    /**
     * Добавляет пользователя в проект.
     * @param project Проект
     */
    private static void addUserToProject(Project project) {
        String methodName = "addUserToProject";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        System.out.print("Введите email пользователя: ");
        String email = scanner.nextLine();
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "Email: " + email));

        Optional<User> userOpt = userDAO.findByEmail(email);
        if (userOpt.isPresent()) {
            try {
                projectUserDAO.addUserToProject(project.getId(), userOpt.get().getId());
                System.out.println("Пользователь добавлен в проект!");
                log.info("addUserToProject [1] Пользователь добавлен: " + email);
            } catch (Exception e) {
                System.out.println("Ошибка при добавлении пользователя: " + e.getMessage());
                log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            }
        } else {
            System.out.println("Пользователь с таким email не найден.");
            log.error("addUserToProject [1] Пользователь не найден: " + email);
        }
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
    }

    /**
     * Удаляет пользователя из проекта.
     * @param project Проект
     */
    private static void removeUserFromProject(Project project) {
        String methodName = "removeUserFromProject";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        System.out.print("Введите email пользователя: ");
        String email = scanner.nextLine();
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "Email: " + email));

        Optional<User> userOpt = userDAO.findByEmail(email);
        if (userOpt.isPresent()) {
            try {
                projectUserDAO.removeUserFromProject(project.getId(), userOpt.get().getId());
                System.out.println("Пользователь удален из проекта!");
                log.info("removeUserFromProject [1] Пользователь удален: " + email);
            } catch (Exception e) {
                System.out.println("Ошибка при удалении пользователя: " + e.getMessage());
                log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
            }
        } else {
            System.out.println("Пользователь с таким email не найден.");
            log.error("removeUserFromProject [1] Пользователь не найден: " + email);
        }
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
    }

    /**
     * Отображает спринты проекта.
     * @param project Проект
     */
    private static void showSprints(Project project) {
        String methodName = "showSprints";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        List<Sprint> sprints = sprintDAO.findAll().stream()
                .filter(s -> s.getProject().getId() == project.getId())
                .toList();
        log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Найдено спринтов: " + sprints.size()));

        if (sprints.isEmpty()) {
            System.out.println("В проекте нет спринтов.");
        } else {
            System.out.println("\nСпринты проекта:");
            for (int i = 0; i < sprints.size(); i++) {
                System.out.println((i + 1) + ". Спринт " + sprints.get(i).getId() + " (" + sprints.get(i).getStartDate() + " - " + sprints.get(i).getEndDate() + ")");
            }
            System.out.print("Выберите спринт (номер) или 0 для возврата: ");
            String choice = scanner.nextLine();
            log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "Выбрана опция: " + choice));

            if (!choice.equals("0")) {
                try {
                    int index = Integer.parseInt(choice) - 1;
                    if (index >= 0 && index < sprints.size()) {
                        showSprintMenu(sprints.get(index));
                    } else {
                        System.out.println("Неверный выбор.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Введите корректный номер.");
                    log.error(String.format(Constants.LOG_ERROR, methodName, "Некорректный ввод: " + choice));
                }
            }
        }
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
    }

    /**
     * Создает новый спринт в проекте.
     * @param project Проект
     */
    private static void createSprint(Project project) {
        String methodName = "createSprint";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        System.out.print("Введите дату начала (гггг-мм-дд): ");
        String startDateStr = scanner.nextLine();
        System.out.print("Введите дату окончания (гггг-мм-дд): ");
        String endDateStr = scanner.nextLine();

        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "Создание спринта: " + startDateStr + " - " + endDateStr));

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = sdf.parse(startDateStr);
            Date endDate = sdf.parse(endDateStr);
            Sprint sprint = new Sprint(startDate, endDate, project);
            sprintDAO.create(sprint);
            System.out.println("Спринт успешно создан!");
            log.info("createSprint [1] Спринт успешно создан");
        } catch (ParseException e) {
            System.out.println("Неверный формат даты. Используйте гггг-мм-дд.");
            log.error(String.format(Constants.LOG_ERROR, methodName, "Неверный формат даты: " + e.getMessage()));
        } catch (Exception e) {
            System.out.println("Ошибка при создании спринта: " + e.getMessage());
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
        }
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
    }

    /**
     * Отображает меню управления спринтом.
     * @param sprint Спринт для управления
     */
    private static void showSprintMenu(Sprint sprint) {
        String methodName = "showSprintMenu";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        while (true) {
            System.out.println("\n=== Управление спринтом: " + sprint.getId() + " ===");
            System.out.println("1. Просмотреть задачи");
            System.out.println("2. Создать задачу");
            System.out.println("3. Редактировать задачу");
            System.out.println("4. Просмотреть ретроспективу");
            System.out.println("5. Создать/обновить ретроспективу");
            System.out.println("6. Вернуться в меню проекта");
            System.out.print("Выберите опцию: ");
            String choice = scanner.nextLine();
            log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "Выбрана опция: " + choice));

            switch (choice) {
                case "1" -> showTasks(sprint);
                case "2" -> createTask(sprint);
                case "3" -> editTask(sprint);
                case "4" -> showRetrospective(sprint);
                case "5" -> createOrUpdateRetrospective(sprint);
                case "6" -> {
                    return;
                }
                default -> System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    /**
     * Отображает задачи спринта.
     * @param sprint Спринт
     */
    private static void showTasks(Sprint sprint) {
        String methodName = "showTasks";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        List<Task> tasks = taskDAO.findAll().stream()
                .filter(t -> t.getSprint().getId() == sprint.getId())
                .toList();
        log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Найдено задач: " + tasks.size()));

        if (tasks.isEmpty()) {
            System.out.println("В спринте нет задач.");
        } else {
            System.out.println("\nЗадачи спринта:");
            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                System.out.println((i + 1) + ". " + task.getTitle() + " (Статус: " + task.getStatus() +
                        ", Приоритет: " + task.getPriority() +
                        ", Назначен: " + (task.getAssignedUser() != null ? task.getAssignedUser().getName() : "Никто") + ")");
            }
        }
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
    }

    /**
     * Создает новую задачу в спринте.
     * @param sprint Спринт
     */
    private static void createTask(Sprint sprint) {
        String methodName = "createTask";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        System.out.print("Введите название задачи: ");
        String title = scanner.nextLine();
        System.out.print("Введите описание задачи: ");
        String description = scanner.nextLine();
        System.out.print("Введите приоритет (1-5): ");
        String priorityStr = scanner.nextLine();
        System.out.print("Введите статус (TO_DO, IN_PROGRESS, DONE): ");
        String statusStr = scanner.nextLine();
        System.out.print("Введите email назначенного пользователя (или пусто): ");
        String email = scanner.nextLine();

        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "Создание задачи: " + title));

        try {
            int priority = Integer.parseInt(priorityStr);
            TaskStatus status = TaskStatus.valueOf(statusStr.toUpperCase());
            User assignedUser = null;
            if (!email.isEmpty()) {
                Optional<User> userOpt = userDAO.findByEmail(email);
                if (userOpt.isPresent()) {
                    assignedUser = userOpt.get();
                } else {
                    System.out.println("Пользователь с таким email не найден.");
                    log.error("createTask [1] Пользователь не найден: " + email);
                    return;
                }
            }
            Task task = new Task(title, description, status, priority, sprint, assignedUser);
            taskDAO.create(task);
            System.out.println("Задача успешно создана!");
            log.info("createTask [1] Задача успешно создана: " + title);
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат приоритета.");
            log.error(String.format(Constants.LOG_ERROR, methodName, "Неверный формат приоритета: " + priorityStr));
        } catch (IllegalArgumentException e) {
            System.out.println("Неверный статус задачи.");
            log.error(String.format(Constants.LOG_ERROR, methodName, "Неверный статус: " + statusStr));
        } catch (Exception e) {
            System.out.println("Ошибка при создании задачи: " + e.getMessage());
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
        }
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
    }

    /**
     * Редактирует существующую задачу в спринте.
     * @param sprint Спринт, содержащий задачу
     */
    private static void editTask(Sprint sprint) {
        String methodName = "editTask";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        List<Task> tasks = taskDAO.findAll().stream()
                .filter(t -> t.getSprint().getId() == sprint.getId())
                .toList();
        log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Найдено задач: " + tasks.size()));

        if (tasks.isEmpty()) {
            System.out.println("В спринте нет задач для редактирования.");
            log.info("editTask [1] Нет задач для редактирования");
            return;
        }

        showTasks(sprint);
        System.out.print("Выберите задачу для редактирования (номер) или 0 для возврата: ");
        String choice = scanner.nextLine();
        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "Выбрана опция: " + choice));

        if (choice.equals("0")) {
            log.info(String.format(Constants.LOG_METHOD_END, methodName));
            return;
        }

        try {
            int index = Integer.parseInt(choice) - 1;
            if (index >= 0 && index < tasks.size()) {
                Task task = tasks.get(index);
                System.out.println("Текущие данные задачи:");
                System.out.println("Название: " + task.getTitle());
                System.out.println("Описание: " + task.getDescription());
                System.out.println("Статус: " + task.getStatus());
                System.out.println("Приоритет: " + task.getPriority());
                System.out.println("Назначен: " + (task.getAssignedUser() != null ? task.getAssignedUser().getName() : "Никто"));

                System.out.print("Введите новое название (или Enter для сохранения текущего): ");
                String title = scanner.nextLine();
                if (!title.isEmpty()) {
                    task.setTitle(title);
                }

                System.out.print("Введите новое описание (или Enter для сохранения текущего): ");
                String description = scanner.nextLine();
                if (!description.isEmpty()) {
                    task.setDescription(description);
                }

                System.out.print("Введите новый статус (TO_DO, IN_PROGRESS, DONE, или Enter для сохранения текущего): ");
                String statusStr = scanner.nextLine();
                if (!statusStr.isEmpty()) {
                    task.setStatus(TaskStatus.valueOf(statusStr.toUpperCase()));
                }

                System.out.print("Введите новый приоритет (1-5, или Enter для сохранения текущего): ");
                String priorityStr = scanner.nextLine();
                if (!priorityStr.isEmpty()) {
                    task.setPriority(Integer.parseInt(priorityStr));
                }

                System.out.print("Введите email нового назначенного пользователя (пусто для отсутствия, 'none' для удаления текущего): ");
                String email = scanner.nextLine();
                if (!email.isEmpty()) {
                    if (email.equalsIgnoreCase("none")) {
                        task.setAssignedUser(null);
                    } else {
                        Optional<User> userOpt = userDAO.findByEmail(email);
                        if (userOpt.isPresent()) {
                            task.setAssignedUser(userOpt.get());
                        } else {
                            System.out.println("Пользователь с таким email не найден. Назначение не изменено.");
                            log.error("editTask [2] Пользователь не найден: " + email);
                            return;
                        }
                    }
                }

                taskDAO.update(task);
                System.out.println("Задача успешно обновлена!");
                log.info("editTask [3] Задача успешно обновлена: " + task.getTitle());
            } else {
                System.out.println("Неверный выбор.");
                log.error("editTask [2] Неверный выбор задачи: " + choice);
            }
        } catch (NumberFormatException e) {
            System.out.println("Введите корректный номер.");
            log.error(String.format(Constants.LOG_ERROR, methodName, "Некорректный ввод номера: " + choice));
        } catch (IllegalArgumentException e) {
            System.out.println("Неверный статус задачи.");
            log.error(String.format(Constants.LOG_ERROR, methodName, "Неверный статус: " + e.getMessage()));
        } catch (Exception e) {
            System.out.println("Ошибка при редактировании задачи: " + e.getMessage());
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
        }
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
    }

    /**
     * Отображает ретроспективу спринта.
     * @param sprint Спринт
     */
    private static void showRetrospective(Sprint sprint) {
        String methodName = "showRetrospective";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        Retrospective retrospective = sprint.getRetrospective();
        log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, retrospective != null ? "Ретроспектива найдена" : "Ретроспектива не найдена"));

        if (retrospective == null) {
            System.out.println("Ретроспектива для этого спринта не создана.");
        } else {
            System.out.println("\nРетроспектива спринта " + sprint.getId() + ":");
            System.out.println("Резюме: " + retrospective.getSummary());
            System.out.println("Положительные моменты:");
            for (String positive : retrospective.getPositives()) {
                System.out.println("- " + positive);
            }
            System.out.println("Улучшения:");
            for (String improvement : retrospective.getImprovements()) {
                System.out.println("- " + improvement);
            }
        }
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
    }

    /**
     * Создает или обновляет ретроспективу спринта.
     * @param sprint Спринт
     */
    private static void createOrUpdateRetrospective(Sprint sprint) {
        String methodName = "createOrUpdateRetrospective";
        log.info(String.format(Constants.LOG_METHOD_START, methodName));
        System.out.print("Введите резюме ретроспективы: ");
        String summary = scanner.nextLine();
        System.out.println("Введите положительные моменты (вводите по одному, пустая строка для завершения):");
        List<String> positives = new ArrayList<>();
        while (true) {
            String positive = scanner.nextLine();
            if (positive.isEmpty()) break;
            positives.add(positive);
        }
        System.out.println("Введите улучшения (вводите по одному, пустая строка для завершения):");
        List<String> improvements = new ArrayList<>();
        while (true) {
            String improvement = scanner.nextLine();
            if (improvement.isEmpty()) break;
            improvements.add(improvement);
        }

        log.debug(String.format(Constants.LOG_METHOD_DEBUG, methodName, "Создание/обновление ретроспективы для спринта: " + sprint.getId()));

        try {
            Retrospective retrospective = sprint.getRetrospective();
            if (retrospective == null) {
                retrospective = new Retrospective(sprint, summary, improvements, positives);
                retrospectiveDAO.create(retrospective);
                sprint.setRetrospective(retrospective);
                sprintDAO.update(sprint);
                System.out.println("Ретроспектива успешно создана!");
                log.info("createOrUpdateRetrospective [1] Ретроспектива создана");
            } else {
                retrospective.setSummary(summary);
                retrospective.setPositives(positives);
                retrospective.setImprovements(improvements);
                retrospectiveDAO.update(retrospective);
                System.out.println("Ретроспектива успешно обновлена!");
                log.info("createOrUpdateRetrospective [1] Ретроспектива обновлена");
            }
        } catch (Exception e) {
            System.out.println("Ошибка при создании/обновлении ретроспективы: " + e.getMessage());
            log.error(String.format(Constants.LOG_ERROR, methodName, e.getMessage()));
        }
        log.info(String.format(Constants.LOG_METHOD_END, methodName));
    }
}