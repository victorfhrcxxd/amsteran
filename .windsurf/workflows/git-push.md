---
description: Commit and push changes to GitHub
---

## Steps to commit and push to GitHub

1. Stage all changes:
// turbo
```
git add .
```

2. Commit with a descriptive message:
```
git commit -m "feat: <short description of what was added or changed>"
```

3. Push to the remote:
// turbo
```
git push origin main
```

## Commit message convention
- `feat: <description>` — new feature or handler
- `fix: <description>` — bug fix
- `config: <description>` — config property change
- `sql: <description>` — SQL schema change
- `refactor: <description>` — code refactor without behavior change
- `docs: <description>` — documentation or HTML dialog change
