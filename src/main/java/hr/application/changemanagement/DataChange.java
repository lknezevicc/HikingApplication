package hr.application.changemanagement;

import hr.application.entities.Timestamp;
import hr.application.enums.UserRole;

import java.io.Serializable;

public class DataChange <T extends Serializable, U extends Serializable> implements Serializable {
    private String objectName;
    private T oldValue;
    private U newValue;
    private Timestamp timestamp;
    private UserRole userRole;

    public DataChange() {}

    public DataChange(String objectName, T oldValue, U newValue, UserRole userRole) {
        this.objectName = objectName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.timestamp = new Timestamp();
        this.userRole = userRole;
    }

    public static class Builder<T extends Serializable, U extends Serializable> {
        private String objectName;
        private T oldValue;
        private U newValue;
        private UserRole userRole;

        public Builder<T, U> objectName(String objectName) {
            this.objectName = objectName;
            return this;
        }

        public Builder<T, U> oldValue(T oldValue) {
            this.oldValue = oldValue;
            return this;
        }

        public Builder<T, U> newValue(U newValue) {
            this.newValue = newValue;
            return this;
        }

        public Builder<T, U> userRole(UserRole userRole) {
            this.userRole = userRole;
            return this;
        }

        public DataChange<T, U> build() {
            DataChange<T, U> dataChange = new DataChange<>();
            dataChange.setObjectName(this.objectName);
            dataChange.setOldValue(this.oldValue);
            dataChange.setNewValue(this.newValue);
            dataChange.setUserRole(this.userRole);
            dataChange.setTimestamp(new Timestamp());
            return dataChange;
        }
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public T getOldValue() {
        return oldValue;
    }

    public void setOldValue(T oldValue) {
        this.oldValue = oldValue;
    }

    public U getNewValue() {
        return newValue;
    }

    public void setNewValue(U newValue) {
        this.newValue = newValue;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

}