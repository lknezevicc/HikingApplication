package hr.application.entities;

import java.io.Serializable;

public class HikingRoute implements Serializable {
    private Long ID;
    private String name;
    private String mountainPeak;
    private String startingPoint;
    private String endingPoint;
    private Double length;
    private Integer duration;
    private Integer difficulty;
    private String description;
    private String author;

    public HikingRoute(Builder builder) {
        this.ID = builder.ID;
        this.name = builder.name;
        this.mountainPeak = builder.mountainPeak;
        this.startingPoint = builder.startingPoint;
        this.endingPoint = builder.endingPoint;
        this.length = builder.length;
        this.duration = builder.duration;
        this.difficulty = builder.difficulty;
        this.description = builder.description;
        this.author = builder.author;
    }

    public static class Builder {
        private Long ID;
        private String name;
        private String mountainPeak;
        private String startingPoint;
        private String endingPoint;
        private Double length;
        private Integer duration;
        private Integer difficulty;
        private String description;
        private String author;

        public Builder() {}

        public Builder withID(Long ID) {
            this.ID = ID;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withMountainPeak(String mountainPeak) {
            this.mountainPeak = mountainPeak;
            return this;
        }

        public Builder withStartingPoint(String startingPoint) {
            this.startingPoint = startingPoint;
            return this;
        }

        public Builder withEndingPoint(String endingPoint) {
            this.endingPoint = endingPoint;
            return this;
        }

        public Builder withLength(Double length) {
            this.length = length;
            return this;
        }

        public Builder withDuration(Integer duration) {
            this.duration = duration;
            return this;
        }

        public Builder withDifficulty(Integer difficulty) {
            this.difficulty = difficulty;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withAuthor(String author) {
            this.author = author;
            return this;
        }

        public HikingRoute build() {
            return new HikingRoute(this);
        }
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMountainPeak() {
        return mountainPeak;
    }

    public void setMountainPeak(String mountainPeak) {
        this.mountainPeak = mountainPeak;
    }

    public String getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(String startingPoint) {
        this.startingPoint = startingPoint;
    }

    public String getEndingPoint() {
        return endingPoint;
    }

    public void setEndingPoint(String endingPoint) {
        this.endingPoint = endingPoint;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "ID: " + ID + "\n" +
                "Name: " + name + "\n" +
                "Mountain peak: " + mountainPeak + "\n" +
                "Starting point: " + startingPoint + "\n" +
                "Ending point: " + endingPoint + "\n" +
                "Length: " + length + "\n" +
                "Duration: " + duration + "\n" +
                "Difficulty: " + difficulty + "\n" +
                "Description: " + description + "\n";
    }
}
