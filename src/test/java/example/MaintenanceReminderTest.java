package example;

import java.time.LocalDate;

public final class MaintenanceReminderTest {
    public static void main(String[] args) {
        boolean due = MaintenanceReminder.isDue(LocalDate.parse("2026-10-28"), LocalDate.parse("2026-10-23"));
        if (!due) throw new AssertionError("an inspection within seven days must be due");
        boolean later = MaintenanceReminder.isDue(LocalDate.parse("2026-11-01"), LocalDate.parse("2026-10-23"));
        if (later) throw new AssertionError("an inspection beyond seven days must not be due");
        System.out.println("maintenance reminder decision: passed");
    }
}
