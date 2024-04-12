package hr.application.utilities;

import hr.application.changemanagement.DataChange;
import hr.application.changemanagement.DataChangeTracker;
import hr.application.entities.*;
import hr.application.enums.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class DatabaseUtility {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtility.class);
    private static final String DATABASE_CONFIG_FILE = "database/database.properties";
    private static final DataChangeTracker dataChangeTracker = new DataChangeTracker();

    public static synchronized Connection connectToDatabase() throws SQLException, IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(DATABASE_CONFIG_FILE));
        String url = properties.getProperty("databaseURL");

        return DriverManager.getConnection(url);
    }

    public static void closeConnection(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public static synchronized void saveUser(User user) {
        try (Connection connection = connectToDatabase();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO USER (USERNAME, PASSWORD, ROLE) VALUES (?, ?, ?)")) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getHashedPassword());
            statement.setString(3, user.getRole().name());

            statement.executeUpdate();

            dataChangeTracker.writeChangeToFile(
                    new DataChange.Builder<>()
                    .objectName(user.getUsername())
                    .oldValue("Doesn't exist")
                    .newValue(user)
                    .userRole(UserSession.getInstance().getUser() != null ? UserSession.getInstance().getUser().getRole() : UserRole.ADMIN)
                    .build());

            logger.info("User {} saved to database!", user.getUsername());

        } catch (SQLException | IOException e) {
            logger.error("Error while saving user to database!, [{}]", e.getMessage());
        }
    }

    public static Optional<User> getUser(String username) {
        try (Connection connection = connectToDatabase();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM USER WHERE USERNAME = ?")) {
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(new User.Builder()
                        .withID(resultSet.getLong("ID"))
                        .withUsername(resultSet.getString("USERNAME"))
                        .withHashedPassword(resultSet.getString("PASSWORD"))
                        .withRole(UserRole.valueOf(resultSet.getString("ROLE")))
                        .build());
            }
        } catch (IOException | SQLException e) {
            logger.error("Error while getting user from database!, [{}]", e.getMessage());
        }

        return Optional.empty();
    }

    public static synchronized void saveHikingRoute(HikingRoute route) {
        try (Connection connection = connectToDatabase()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO HIKING_ROUTE (NAME, MOUNTAIN_PEAK, STARTING_POINT, ENDING_POINT, LENGTH, DURATION, DIFFICULTY, DESCRIPTION, AUTHOR) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, route.getName());
            statement.setString(2, route.getMountainPeak());
            statement.setString(3, route.getStartingPoint());
            statement.setString(4, route.getEndingPoint());
            statement.setDouble(5, route.getLength());
            statement.setInt(6, route.getDuration());
            statement.setInt(7, route.getDifficulty());
            statement.setString(8, route.getDescription());
            statement.setString(9, route.getAuthor());

            statement.executeUpdate();
            dataChangeTracker.writeChangeToFile(new DataChange
                    .Builder<>()
                    .objectName(route.getName())
                    .oldValue("Doesn't exist")
                    .newValue(route)
                    .userRole(UserSession.getInstance().getUser().getRole())
                    .build());
            logger.info("Hiking route {} saved to database and linked with {}!", route.getName(), UserSession.getInstance().getUser().getUsername());

        } catch (SQLException | IOException e) {
            logger.error("Error while saving hiking route to database!, [{}]", e.getMessage());
        }
    }

    public static synchronized List<HikingRoute> getHikingRoutes() {
        List<HikingRoute> hikingRoutes = new ArrayList<>();
        try (Connection connection = connectToDatabase();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM HIKING_ROUTE")) {

            makeHikingRoutesList(hikingRoutes, statement);

        } catch (IOException | SQLException e) {
            logger.error("Error while getting hiking routes from database!, [{}]", e.getMessage());
        }

        return hikingRoutes;
    }

    public static synchronized List<HikingRoute> getHikingRoutesByUsername() {
        List<HikingRoute> hikingRoutes = new ArrayList<>();
        try (Connection connection = connectToDatabase();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM HIKING_ROUTE WHERE AUTHOR = ?")) {

            statement.setString(1, UserSession.getInstance().getUser().getUsername());
            makeHikingRoutesList(hikingRoutes, statement);

        } catch (IOException | SQLException e) {
            logger.error("Error while getting hiking routes from database!, [{}]", e.getMessage());
        }

        return hikingRoutes;
    }

    private static synchronized void makeHikingRoutesList(List<HikingRoute> hikingRoutes, PreparedStatement statement) throws SQLException {
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            HikingRoute newHikingRoute = new HikingRoute.Builder()
                    .withID(resultSet.getLong("ID"))
                    .withName(resultSet.getString("NAME"))
                    .withMountainPeak(resultSet.getString("MOUNTAIN_PEAK"))
                    .withStartingPoint(resultSet.getString("STARTING_POINT"))
                    .withEndingPoint(resultSet.getString("ENDING_POINT"))
                    .withLength(resultSet.getDouble("LENGTH"))
                    .withDuration(resultSet.getInt("DURATION"))
                    .withDifficulty(resultSet.getInt("DIFFICULTY"))
                    .withDescription(resultSet.getString("DESCRIPTION"))
                    .withAuthor(resultSet.getString("AUTHOR"))
                    .build();

            hikingRoutes.add(newHikingRoute);
        }
    }

    public static synchronized void deleteHikingRoute(HikingRoute route) {
        try (Connection connection = connectToDatabase();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM HIKING_ROUTE WHERE ID = ?")) {

            statement.setLong(1, route.getID());
            statement.executeUpdate();

            dataChangeTracker.writeChangeToFile(new DataChange
                    .Builder<>()
                    .objectName(route.getName())
                    .oldValue(route)
                    .newValue("Deleted!")
                    .userRole(UserSession.getInstance().getUser().getRole())
                    .build());
            logger.info("Hiking route {} deleted from database!", route.getName());

        } catch (IOException | SQLException e) {
            logger.error("Error while getting hiking routes from database!, [{}]", e.getMessage());
        }
    }

    public static synchronized void editHikingRoute(HikingRoute old, HikingRoute route) {
        try (Connection connection = connectToDatabase();
             PreparedStatement statement = connection.prepareStatement("UPDATE HIKING_ROUTE SET NAME = ?, MOUNTAIN_PEAK = ?, STARTING_POINT = ?, ENDING_POINT = ?, LENGTH = ?, DURATION = ?, DIFFICULTY = ?, DESCRIPTION = ? WHERE ID = ?")) {

            statement.setString(1, route.getName());
            statement.setString(2, route.getMountainPeak());
            statement.setString(3, route.getStartingPoint());
            statement.setString(4, route.getEndingPoint());
            statement.setDouble(5, route.getLength());
            statement.setInt(6, route.getDuration());
            statement.setInt(7, route.getDifficulty());
            statement.setString(8, route.getDescription());
            statement.setLong(9, route.getID());

            statement.executeUpdate();
            dataChangeTracker.writeChangeToFile(new DataChange
                    .Builder<>()
                    .objectName(route.getName())
                    .oldValue(old)
                    .newValue(route)
                    .userRole(UserSession.getInstance().getUser().getRole())
                    .build());
            logger.info("Hiking route {} edited in database!", route.getName());

        } catch (IOException | SQLException e) {
            logger.error("Error while editing hiking route in database!, [{}]", e.getMessage());
        }
    }

    public static synchronized List<User> getUsers() {
        List<User> users = new ArrayList<>();
        try (Connection connection = connectToDatabase();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM USER")) {

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                User newUser = new User.Builder()
                        .withID(resultSet.getLong("ID"))
                        .withUsername(resultSet.getString("USERNAME"))
                        .withHashedPassword(resultSet.getString("PASSWORD"))
                        .withRole(UserRole.valueOf(resultSet.getString("ROLE")))
                        .build();

                users.add(newUser);
            }

        } catch (IOException | SQLException e) {
            logger.error("Error while getting users from database!, [{}]", e.getMessage());
        }

        return users;
    }

    public static synchronized void updateUserRole(User user) {
        try (Connection connection = connectToDatabase();
             PreparedStatement statement = connection.prepareStatement("UPDATE USER SET ROLE = ? WHERE ID = ?")) {

            statement.setString(1, user.getRole().name());
            statement.setLong(2, user.getID());

            statement.executeUpdate();

            dataChangeTracker.writeChangeToFile(new DataChange
                    .Builder<>()
                    .objectName(user.getUsername())
                    .oldValue("Role updated!")
                    .newValue(user)
                    .userRole(UserRole.ADMIN)
                    .build());
            logger.info("User role for {} updated in database!", user.getUsername());

        } catch (IOException | SQLException e) {
            logger.error("Error while updating user in database!, [{}]", e.getMessage());
        }
    }

    public static synchronized void deleteUser(User user) {
        try (Connection connection = connectToDatabase();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM USER WHERE ID = ?")) {

            statement.setLong(1, user.getID());
            statement.executeUpdate();

            dataChangeTracker.writeChangeToFile(new DataChange
                    .Builder<>()
                    .objectName(user.getUsername())
                    .oldValue(user)
                    .newValue("Deleted!")
                    .userRole(UserSession.getInstance().getUser().getRole())
                    .build());
            logger.info("User {} deleted from database!", user.getUsername());

        } catch (IOException | SQLException e) {
            logger.error("Error while deleting user from database!, [{}]", e.getMessage());
        }
    }

    public static synchronized void saveImagesForRoute(List<ImageRepresentation> images) {
        try (Connection connection = connectToDatabase();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO IMAGE (HIKINGROUTE_ID, IMAGE_PATH, DESCRIPTION) VALUES (?, ?, ?)")) {

            Long routeID = getLatestHikingRouteID();
            if (routeID.equals(0L)) {
                logger.error("Error while getting latest hiking route ID from database!");
                return;
            }

            for (ImageRepresentation imageRepresentation : images) {
                statement.setLong(1, routeID);
                statement.setString(2, imageRepresentation.getImageFile().getAbsolutePath());
                statement.setString(3, imageRepresentation.getDescription());

                statement.executeUpdate();
                dataChangeTracker.writeChangeToFile(new DataChange
                        .Builder<>()
                        .objectName(imageRepresentation.getImageFile().getName())
                        .oldValue("Doesn't exist")
                        .newValue(imageRepresentation)
                        .userRole(UserSession.getInstance().getUser().getRole())
                        .build());
            }

        } catch (IOException | SQLException e) {
            logger.error("Error while adding image to database!, [{}]", e.getMessage());
        }
    }

    private static synchronized Long getLatestHikingRouteID() {
        try (Connection connection = connectToDatabase();
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery("SELECT MAX(ID) AS MaxID FROM HIKING_ROUTE");
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }

        } catch (IOException | SQLException e) {
            logger.error("Error while getting latest hiking route ID from database!, [{}]", e.getMessage());
        }

        return 0L;
    }

    public static synchronized List<ImageRepresentation> getImagesForRoute(Long routeID) {
        List<ImageRepresentation> images = new ArrayList<>();
        try (Connection connection = connectToDatabase();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM IMAGE WHERE HIKINGROUTE_ID = ?")) {

            statement.setLong(1, routeID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                ImageRepresentation newImage = new ImageRepresentation.Builder()
                        .withID(resultSet.getLong("ID"))
                        .withImageFile(new File(resultSet.getString("IMAGE_PATH")))
                        .withDescription(resultSet.getString("DESCRIPTION"))
                        .build();

                images.add(newImage);
            }

        } catch (IOException | SQLException e) {
            logger.error("Error while getting images from database!, [{}]", e.getMessage());
        }

        return images;
    }

    public static synchronized void deleteImage(ImageRepresentation image) {
        try (Connection connection = connectToDatabase();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM IMAGE WHERE ID = ?")) {

            statement.setLong(1, image.getID());
            statement.executeUpdate();

            dataChangeTracker.writeChangeToFile(new DataChange
                    .Builder<>()
                    .objectName(image.getImageFile().getName())
                    .oldValue(image)
                    .newValue("Deleted!")
                    .userRole(UserSession.getInstance().getUser().getRole())
                    .build());
            logger.info("Image {} deleted from database!", image.getImageFile().getName());

        } catch (IOException | SQLException e) {
            logger.error("Error while deleting image from database!, [{}]", e.getMessage());
        }
    }

    public static synchronized void updateImagesForRoute(Long routeID, ImageRepresentation image) {
        try (Connection connection = connectToDatabase();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO IMAGE (HIKINGROUTE_ID, IMAGE_PATH, DESCRIPTION) VALUES (?, ?, ?)")) {

            statement.setLong(1, routeID);
            statement.setString(2, image.getImageFile().getAbsolutePath());
            statement.setString(3, image.getDescription());

            statement.executeUpdate();
            dataChangeTracker.writeChangeToFile(new DataChange
                    .Builder<>()
                    .objectName(image.getImageFile().getName())
                    .oldValue("Doesn't exist")
                    .newValue(image)
                    .userRole(UserSession.getInstance().getUser().getRole())
                    .build());
        } catch (IOException | SQLException e) {
            logger.error("Error while adding image to database!, [{}]", e.getMessage());
        }
    }

}
