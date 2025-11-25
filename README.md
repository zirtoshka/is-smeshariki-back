1. поднять бд
```bash
docker compose up -d postgres
```
2. применить скрипт [schema-postgres.sql](src/main/resources/schema-postgres.sql)
3. применить скрипт [data.sql](src/main/resources/data.sql), который заполнит тестовыми данными
4. применить скрипт [schema-postgres-2.sql](src/main/resources/schema-postgres-2.sql)
5. поднять minio
```bash
docker compose up -d minio
```
6. настроить minio в UI
   - создать bucket `images`
   - создать Access Keys с параметрами из `secret.yaml`
7. поднять бек
```bash
mvn clean package
```
```bash
docker compose up -d --build spring
```
