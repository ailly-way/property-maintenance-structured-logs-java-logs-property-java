package example;

import java.time.LocalDate;

/** Runnable path: decide, ship one structured event, then search it. */
public final class PropertyJobExample {
    public static void main(String[] args) throws Exception {
        String key = System.getenv("INFRAI_API_KEY");
        PropertyLogClient client = new PropertyLogClient(key);
        String jobId = args.length > 0 ? args[0] : "maintenance-104";
        boolean reminderDue = MaintenanceReminder.isDue(LocalDate.parse("2026-10-28"), LocalDate.parse("2026-10-23"));
        String event = "{\"entries\":[{\"service\":\"property-maintenance\",\"level\":\"info\",\"message\":\"inspection reminder evaluated\",\"job_id\":\"" + jobId + "\",\"reminder_due\":" + reminderDue + "}]}";
        System.out.println("account=" + client.whoami());
        System.out.println("keys=" + client.keysList());
        System.out.println("ingested=" + client.ingest(event));
        System.out.println("search=" + client.search("property-maintenance"));
    }
}
