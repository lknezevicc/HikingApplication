package hr.application.utilities;

public class TimeConvert {
    public static String convertToHoursAndMinutesString(int minutes) {
        int hours = minutes / 60;
        int minutesLeft = minutes % 60;

        return String.format("%02d:%02d", hours, minutesLeft);
    }

    public static Integer convertToMinutes(String hoursAndMinutes) {
        String[] parts = hoursAndMinutes.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);

        return hours * 60 + minutes;
    }
}
