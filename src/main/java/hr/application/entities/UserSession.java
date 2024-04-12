package hr.application.entities;

import hr.application.enums.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserSession {
    private static final Logger logger = LoggerFactory.getLogger(UserSession.class);
    private static volatile UserSession instance;
    private User user;
    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            synchronized (UserSession.class) {
                if (instance == null) {
                    instance = new UserSession();
                }
            }
        }

        return instance;
    }

    public void setUser(User user) {
        if (instance == null) {
            logger.error("UserSession not initialized!");
            return;
        }
        this.user = user;
    }

    public void cleanUserSession() {
        user = null;
        instance = null;
    }

    public User getUser() {
        return user;
    }

    public boolean isAdmin() {
        return user.getRole().equals(UserRole.ADMIN);
    }
}