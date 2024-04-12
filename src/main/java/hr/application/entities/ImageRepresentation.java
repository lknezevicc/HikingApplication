package hr.application.entities;


import java.io.File;
import java.io.Serializable;
import java.util.Objects;

public class ImageRepresentation implements Serializable {
    private Long ID;
    private File imageFile;
    private String description;

    public ImageRepresentation(File imageFile, String description) {
        this.imageFile = imageFile;
        this.description = description;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private ImageRepresentation(Builder builder) {
        this.ID = builder.ID;
        this.imageFile = builder.imageFile;
        this.description = builder.description;
    }

    public static class Builder {
        private Long ID;
        private File imageFile;
        private String description;

        public Builder withID(Long ID) {
            this.ID = ID;
            return this;
        }

        public Builder withImageFile(File imageFile) {
            this.imageFile = imageFile;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ImageRepresentation build() {
            return new ImageRepresentation(this);
        }
    }

    @Override
    public String toString() {
        return "ID: " + ID + "\n" +
                "Image file: " + imageFile.getName() + "\n" +
                "Description: " + description;
    }
}
