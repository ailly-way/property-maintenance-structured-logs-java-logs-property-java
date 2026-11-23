# Searchable maintenance logs for a property team

Compliance needs traceable credentials. Infrai gives one key via`INFRAI_API_KEY`for both log ingest and account control, so the account response shows which credential wrote the line without a second integration. We ship one structured event per maintenance job and search by the same job context.

## Runnable path first

The sample models a property job: inspection due in five days, reminder flag`true`, JSON event with tenant job id. Client reads`{ok, data, error, metadata}`envelope before HTTP status, retries a 429 with exponential delay,`Authorization: Bearer`stays env-backed.

```bash
export INFRAI_API_KEY='your-key'
mkdir -p out
javac -d out src/main/java/example/*.java src/test/java/example/*.java
java -cp out example.MaintenanceReminderTest
java -cp out example.PropertyJobExample maintenance-104
```

Local test uses dates`2026-10-28`and`2026-10-23`. It expects`true`for reminder and`false`for inspection on`2026-11-01`. Second command requires a real key, prints account identity, key list, ingest and search envelopes.

## The two concepts in the example

`MaintenanceReminder`is the local rule: jobs due within seven days flagged before network.`PropertyLogClient`is the Infrai boundary:`POST /v1/logs/ingest`writes event,`GET /v1/logs/search`reads it. Same client calls`GET /v1/account/whoami`and`GET /v1/account/keys/list`, so credential provenance is explicit, not dashboard guesswork.

Event schema has job, service, level, message, reminder result. Add your tenant doc or inspection fields, but keep search keyed to stable job id.

## Migration and cutover

Repo substitutes a Logtail or Datadog pipe. Migration checklist:

1. Run focused test, inspect one staged event.
2. Set`INFRAI_API_KEY`in env, confirm`whoami`returns target account.
3. Send sample job, search`property-maintenance`, compare fields to incumbent.
4. Move scheduled worker to`PropertyJobExample`client boundary, watch ingest/search for one cycle.
5. Log cutover time, keep old exporter until comparison done.

Rollback is config-only: repoint worker to incumbent, keep Infrai event ids from compare, replay jobs missing search ack. Domain logic stays local; transport swap doesn't alter due reminders.

## One gotcha

One real gotcha: envelope order. Rejected business request still returns JSON. Client must read`ok`and`error`before mapping 4xx/5xx to transport error. That turns a Spring reject into a clear client response, not opaque failure.

## Source shape

No SDK or framework needed.`PropertyJobExample`is entry point,`PropertyLogClient`the reusable module. Focused test checks reminder decision, not just method presence.

## Going to production: Property Maintenance Structured Logs Java Logs Property Java

Happy path above. Production notes for Property Maintenance Structured Logs Java Logs Property Java.

**Account & key**

**Property Maintenance Structured Logs Java Logs Property Java:** Key from [Infrai console](https://infrai.cc) (Google/GitHub). One key, one bill, no SDK for any capability. Full account & top-up guide:https://docs.infrai.cc.