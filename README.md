# taskui
A server that allows one to oversee tasks.
Supported task operations are:
- view all
- add one
- delete one

## Usage
- remote: https://taskui-741f289ec720.herokuapp.com/tasks
- local: http://0.0.0.0:8080/tasks

For local usage:
- postgresql must be running and must contain the below table
- rabbitmq must be running with a queue named tasks

Local run:
```
./gradlew run
```


## Relational Db, Task Definition
Local postgresql creation
```
psql postgres
# Note: user is only for local db
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

## Other Repos
- [taskpublisher](https://github.com/spacether/taskrunner_taskpublisher)
- [taskrunner](https://github.com/spacether/taskrunner_taskrunner)