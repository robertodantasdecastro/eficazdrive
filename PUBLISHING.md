# Publicação / Publishing

PT-BR

1. Inicie o repositório local e faça o primeiro commit:

```bash
git init
git add .
git commit -m "chore: inicializa projeto EficazDrive"
```

2. Adicione o remoto e publique na branch main:

```bash
git remote add origin git@github.com:robertodantasdecastro/eficazdrive.git
git branch -M main
git push -u origin main
```

3. Após alterações (UI, ícone, docs), publique:

```bash
git add .
git commit -m "docs(ui,icon): atualiza README/PUBLISHING e ícone"
git push
```

EN

1. Initialize the local repo and make the first commit:

```bash
git init
git add .
git commit -m "chore: initialize EficazDrive project"
```

2. Add remote and publish on main branch:

```bash
git remote add origin git@github.com:robertodantasdecastro/eficazdrive.git
git branch -M main
git push -u origin main
```

3. After changes (UI, icon, docs), publish:

```bash
git add .
git commit -m "docs(ui,icon): update README/PUBLISHING and icon"
git push
```
