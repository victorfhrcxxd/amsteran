---
description: Create a new voiced command handler (.command)
---

## Steps to create a new voiced command handler

1. Create a new Java file in `java/net/sf/l2j/gameserver/handler/voicedcommandhandlers/Voiced<Name>.java`
   - Implement `IVoicedCommandHandler`
   - Define `VOICED_COMMANDS` string array with all subcommands
   - Null-check `activeChar` at the start of `useVoicedCommand`
   - Use `NpcHtmlMessage` to send HTML dialogs if needed

2. Register the handler in `VoicedCommandHandler.java`:
   - Add `registerVoicedCommandHandler(new Voiced<Name>());` in the constructor

3. If the feature needs config:
   - Add properties to the relevant file under `config/custom/`
   - Load them in `Config.java` under the appropriate section

4. If the feature needs HTML dialogs:
   - Create HTML files in `data/html/<feature>/`
   - Reference them via `NpcHtmlMessage`

5. If the feature needs a manager:
   - Create it in `instancemanager/custom/<Name>Manager.java`
   - Follow the SingletonHolder singleton pattern
   - Register it in `GameServer.java`
