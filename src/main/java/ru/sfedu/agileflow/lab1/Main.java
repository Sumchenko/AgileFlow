//package ru.sfedu.agileflow.lab1;
//
//import org.apache.log4j.Logger;
//import ru.sfedu.agileflow.constants.Constants;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.Scanner;
//import java.util.function.Supplier;
//
///**
// * Класс для предоставления интерфейса командной строки для получения служебной информации о базе данных.
// */
//public class Main {
//    private static final Logger log = Logger.getLogger(Main.class);
//    private static final HibernateDataProvider provider = new HibernateDataProvider();
//
//    /**
//     * Главный метод для запуска CLI.
//     * @param args Аргументы командной строки (не используются)
//     */
//    public static void main(String[] args) {
//        String methodName = "main";
//        log.info(String.format(Constants.LOG_METHOD_START, methodName));
//
//        try (Scanner scanner = new Scanner(System.in)) {
//            while (true) {
//                displayMenu();
//                log.info("main [1] Ожидание ввода пользователя");
//                String choice = scanner.nextLine().trim();
//                log.debug(String.format(Constants.LOG_DB_DEBUG, methodName, "Выбор пользователя: " + choice));
//
//                String result;
//                switch (choice) {
//                    case "1":
//                        result = executeAndLog("getDatabaseName", provider::getDatabaseName);
//                        break;
//                    case "2":
//                        result = executeAndLog("getDatabaseSize", provider::getDatabaseSize);
//                        break;
//                    case "3":
//                        result = executeAndLog("getTableNames", provider::getTableNames);
//                        break;
//                    case "4":
//                        result = executeAndLog("getServerVersion", provider::getServerVersion);
//                        break;
//                    case "5":
//                        log.info("main [2] Завершение работы программы");
//                        log.info(String.format(Constants.LOG_METHOD_END, methodName));
//                        return;
//                    default:
//                        result = "Неверный выбор. Пожалуйста, выберите число от 1 до 5.";
//                        log.warn("main [2] Неверный ввод пользователя: " + choice);
//                }
//                System.out.println(result);
//            }
//        } catch (Exception e) {
//            log.error(String.format(Constants.LOG_ERROR, methodName, "Ошибка при выполнении CLI: " + e.getMessage()), e);
//            throw new RuntimeException("Ошибка при выполнении CLI", e);
//        }
//    }
//
//    /**
//     * Отображает меню CLI.
//     */
//    private static void displayMenu() {
//        String menu = """
//            \n=== Информация о базе данных ===
//            1. Получить имя базы данных
//            2. Получить размер базы данных
//            3. Получить список таблиц
//            4. Получить версию сервера
//            5. Выход
//            Выберите опцию (1-5): """;
//        System.out.print(menu);
//        log.debug("displayMenu [1] Меню отображено");
//    }
//
//    /**
//     * Выполняет запрос к провайдеру, логирует результат и возвращает строковое представление.
//     * @param methodName Название метода для логирования
//     * @param supplier Функция, выполняющая запрос к провайдеру
//     * @param <T> Тип возвращаемого результата
//     * @return Строковое представление результата или ошибки
//     */
//    private static <T> String executeAndLog(String methodName, Supplier<T> supplier) {
//        log.info(String.format(Constants.LOG_METHOD_START, methodName));
//        log.info(String.format("%s [1] Выполнение запроса", methodName));
//
//        try {
//            T result = supplier.get();
//            String message;
//            if (result instanceof Optional) {
//                Optional<?> optional = (Optional<?>) result;
//                message = optional.isPresent() ? optional.get().toString() : "Результат не возвращён";
//            } else if (result instanceof List) {
//                List<?> list = (List<?>) result;
//                message = list.isEmpty() ? "Список пуст" : list.toString();
//            } else {
//                message = result != null ? result.toString() : "Результат не возвращён";
//            }
//            log.info(String.format("%s [2] Успешно: %s", methodName, message));
//            log.info(String.format(Constants.LOG_METHOD_END, methodName));
//            return formatResult(methodName, message);
//        } catch (Exception e) {
//            String errorMessage = "Ошибка: " + e.getMessage();
//            log.error(String.format(Constants.LOG_ERROR, methodName, errorMessage), e);
//            log.info(String.format(Constants.LOG_METHOD_END, methodName));
//            return errorMessage;
//        }
//    }
//
//    /**
//     * Форматирует результат для вывода в CLI.
//     * @param methodName Название метода
//     * @param result Результат выполнения
//     * @return Отформатированная строка
//     */
//    private static String formatResult(String methodName, String result) {
//        return switch (methodName) {
//            case "getDatabaseName" -> "Имя базы данных: " + result;
//            case "getDatabaseSize" -> "Размер базы данных: " + result + " MB";
//            case "getTableNames" -> "Список таблиц: " + result;
//            case "getServerVersion" -> "Версия сервера: " + result;
//            default -> result;
//        };
//    }
//}