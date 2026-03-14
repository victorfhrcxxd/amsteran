---
description: Create a new custom singleton manager
---

## Steps to create a new custom singleton manager

1. Create a new Java file in `java/net/sf/l2j/gameserver/instancemanager/custom/<Name>Manager.java`

2. Use the SingletonHolder singleton pattern:
```java
public class <Name>Manager
{
    private static final Logger _log = Logger.getLogger(<Name>Manager.class.getName());

    private <Name>Manager()
    {
        // initialization
        _log.info("<Name>Manager: Loaded.");
    }

    public static <Name>Manager getInstance()
    {
        return SingletonHolder._instance;
    }

    private static class SingletonHolder
    {
        protected static final <Name>Manager _instance = new <Name>Manager();
    }
}
```

3. Register the manager in `GameServer.java`:
   - Add `<Name>Manager.getInstance();` in the startup sequence

4. If the manager needs config:
   - Add a `.properties` file in `config/custom/<feature>.properties`
   - Load values in `Config.java` under a clearly commented section header

5. If the manager needs database:
   - Create the SQL table script in `sql/<feature>.sql`
   - Use lowercase table name and underscore_case columns
