---
description: Create a new admin command handler (//command)
---

## Steps to create a new admin command handler

1. Create a new Java file in `java/net/sf/l2j/gameserver/handler/admincommandhandlers/Admin<Name>.java`
   - Implement `IAdminCommandHandler`
   - Define `ADMIN_COMMANDS` string array with all subcommands
   - Null-check `activeChar` at the start of `useAdminCommand`
   - Check `activeChar.isGM()` or access level as appropriate

2. Register the handler in `AdminCommandHandler.java`:
   - Add `registerAdminCommandHandler(new Admin<Name>());` in the constructor

3. HTML admin dialogs go in `data/html/admin/<feature>.htm`
   - Reference them via `AdminHtmlMessage` or `NpcHtmlMessage`

4. Access level / GM check:
   - Use `AdminCommandHandler.checkLevel(activeChar, level)` or equivalent
