# user + db creation

```
psql postgres
CREATE USER taskrunner_readwriter WITH ENCRYPTED PASSWORD 'xfdz8t-mds-V';
CREATE DATABASE taskrunner_db WITH OWNER taskrunner_readwriter;
\c taskrunner_db taskrunner_readwriter
create table tasks (
  name varchar(255) NOT NULL PRIMARY KEY,
  filename varchar(255) NOT NULL,
  minute SMALLINT,
  hour SMALLINT
);

INSERT INTO tasks (name, filename, minute, hour) VALUES ('everyMin5sTask', 'FiveSecondTask.kt', null, null);
INSERT INTO tasks (name, filename, minute, hour) VALUES ('twoPast10sTask', 'TenSecondTask.kt', 2, null);
```

## Running
```
./gradlew run
```
then browse to http://0.0.0.0:8080/tasks
