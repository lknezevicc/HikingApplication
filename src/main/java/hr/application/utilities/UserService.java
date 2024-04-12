package hr.application.utilities;

import hr.application.entities.User;
import hr.application.exceptions.UsernameExistsException;
import hr.application.exceptions.UsersFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final String USERS_FILE_PATH = "dat/users.txt";

    public static synchronized boolean checkIfUsernameExists(String username) throws UsernameExistsException {
        try (Connection connection = DatabaseUtility.connectToDatabase();
             PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM USER WHERE USERNAME = ?")) {

            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                throw new UsernameExistsException("User with username " + username + " already exists!");
            }

        } catch (SQLException | IOException e) {
            logger.error("Error while connecting to database or getting user from database!", e);
        }

        return false;
    }

    public static synchronized void writeUserToFile(User user) {
        if (user == null || !ensureUsersFileExists()) return;

        try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE_PATH, true))) {
            pw.println(user.getUsername() + "=" + user.getHashedPassword());
            logger.info("User {} written to file!", user.getUsername());

        } catch (IOException e) {
            logger.error("Error while writing user to file!, [{}]", e.getMessage());
        }
    }

    public static synchronized void deleteUserFromFile(String username) {
        if (username == null || !ensureUsersFileExists()) return;

        try {
            Path usersFilePath = Paths.get(USERS_FILE_PATH);
            List<String> lines = Files.readAllLines(usersFilePath);

            lines = lines.stream()
                    .filter(line -> {
                        String[] userParts = line.split("=");
                        return userParts.length != 2 || !userParts[0].equals(username);
                    })
                    .toList();

            Files.write(usersFilePath, lines);
            logger.info("User {} deleted from file!", username);
        } catch (IOException e) {
            logger.error("Error while deleting user from file!, [{}]", e.getMessage());
        }
    }

    public static synchronized Map<String, String> getUsersFromFile() {
        Map<String, String> users = new HashMap<>();

        if (!ensureUsersFileExists()) return users;

        try {
            Path usersFilePath = Paths.get(USERS_FILE_PATH);
            List<String> lines = Files.readAllLines(usersFilePath);

            lines.forEach(line -> {
                String[] userParts = line.split("=");
                if (userParts.length == 2) users.put(userParts[0], userParts[1]);
            });
        } catch (IOException e) {
            logger.error("Error while reading users from file!", e);
        }

        return users;
    }

    private static boolean ensureUsersFileExists() {
        Path path = Paths.get(USERS_FILE_PATH);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            } catch (IOException e) {
                throw new UsersFileException("Error while creating users file: " + e.getMessage());
            }
        }
        return Files.exists(path);
    }

}
