---
description: Create a new custom PvP event (TvT/CTF/DM/LM/KTB style)
---

## Steps to create a new custom event

1. Create a new package: `java/net/sf/l2j/gameserver/model/entity/events/<eventname>/`

2. Create the main event class `<Name>Event.java`:
   - Use static state fields with synchronized access
   - Implement start/stop/register/unregister methods
   - Use `ThreadPoolManager.getInstance().scheduleGeneral(...)` for timed phases
   - Send packets via `ExShowScreenMessage` for announcements

3. Create `<Name>EventTeleporter.java` if needed for custom NPC teleport dialogs
   - HTML dialogs go in `data/html/events/<eventname>/`

4. Register the event config in `config/events/<eventname>.properties`
   - Load in `Config.java` under the events section

5. Register startup in `GameServer.java` if the event needs a persistent manager

6. Add the event to `VoicedEvent.java` if players join via `.event` command

7. Add forced unregistration in `OfflineFarmManager.java` if players in the event
   should be ejected when entering offline farm mode
