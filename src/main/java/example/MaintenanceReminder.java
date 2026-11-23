package example;

import java.time.LocalDate;

/** Domain decision kept separate so a teacher can test it without a network. */
public final class MaintenanceReminder {
    private MaintenanceReminder() {}

    public static boolean isDue(LocalDate inspectionDate, LocalDate today) {
        return !inspectionDate.isAfter(today.plusDays(7));
    }
}
