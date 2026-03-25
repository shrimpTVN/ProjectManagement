# Project Management Application (JavaFX)

JavaFX desktop app for managing projects, tasks, notifications, and chat. The app follows a layered MVC-style structure (Controller → Service → DAO → DB) with centralized session/context, background chat listener, and global error handling.

## Purpose & goals
- Provide a desktop workspace to manage projects/tasks with roles and status history.
- Keep collaborators informed via in-app notifications and chat-style comments.
- Favor responsive UX by offloading IO to background threads and pooling workers.
- Allow future extensibility through PF4J plugins and a clear controller/service/DAO split.

## Feature highlights
- Auth/login with per-session context and role-aware UI visibility.
- Project and task CRUD, assignment, and status timeline display.
- Notification center driven by server events plus comment/chat messaging over sockets.
- Reusable FXML components (Header, SideBar, ProjectCard, TaskDetail subviews) and scene routing via `SceneManager`.
- Basic plugin hosting: plugins loaded from `plugins/` through PF4J.

## Notable implementation details
- Global exception handling (`GlobalExceptionHandler`) is registered at startup; uncaught exceptions log thread names and full stack traces, and surface an in-UI alert with error code + message.
- Threading model: JavaFX Application Thread for UI; `AsyncExecutor` cached thread pool (named `AppWorkerThread-*`, with uncaught handler attached) for short tasks; `ChatClientService` runs dedicated background listeners and marshals UI updates via `Platform.runLater`.
- Session and caches: `UserSession` + `AppContext` hold the logged-in user and project list; `AppContext.refreshProjects()` guards against missing session data.
- Networking: chat/notification client uses UTF-8 JSON over sockets, queues incoming messages/notifications, and processes them on background tasks before updating UI.
- Persistence: DAOs use JDBC with a configurable `DatabaseConnection` (`MySQLDatabaseConnection`), schema and seed SQL stored under `src/main/resources/Database`.

## Tech stack
- Java 21, JavaFX 21 (controls + FXML)
- Maven build; OpenJFX Maven plugin for runtime
- MySQL 8 (JDBC via mysql-connector-j)
- Gson for message/notification payloads
- PF4J for optional plugin hosting
- JUnit 5 for tests

## Key capabilities
- Authentication and session tracking
- Project & task management with role awareness and status history
- Real-time-ish chat/comments and notifications over socket
- UI built from FXML components (scenes under `src/main/resources/scenes` and reusable components under `src/main/resources/components`)
- Centralized exception handling surfaced in-UI

## Architecture (modules & responsibilities)
- `controllers/`: JavaFX controllers per scene/component (e.g., `SceneManager`, `ProjectListController`, `TaskDetailController`). They orchestrate UI actions and call services.
- `services/`: Business logic (e.g., `ProjectService`, `TasklistService`, `NotificationService`, `LoginService`).
- `daos/`: DB access (e.g., `TaskDAO`, `ProjectDAO`), returning models/DTOs.
- `models/` & `dtos/`: Domain entities (Project, Task, User, Notification, Comment, StatusUpdating, etc.) and view-specific DTOs (`PersonalTaskDTO`).
- `core/`: Cross-cutting infrastructure (`AppContext` for session-bound caches, `async/AsyncExecutor` worker pool, chat service, plugin host/loader, database connection abstraction).
- `exceptions/`: Central error model (`AppException`, `ErrorCode`) and `GlobalExceptionHandler` wired to `Thread.setDefaultUncaughtExceptionHandler`.
- `authentication/`: Role and visibility helpers (`RoleValidator`, `VisibleManer`).
- `ui/`, `utils/`: UI helpers and utilities (e.g., `MySQLDatabaseConnection`).
- Resources: `resources/scenes` (screens), `resources/components` (reusable FXML), `resources/css`, `resources/image`, `resources/Database` (DDL/DML scripts).

## Threading model
- UI work stays on the JavaFX Application Thread.
- Worker pool: `core.async.AsyncExecutor` (cached thread pool with named threads and global error handler) for short-lived async work (DB calls, background tasks).
- Chat listener: `core.service.chat.ChatClientService` keeps a dedicated socket connection, consumes incoming messages/notifications on background tasks, and marshals UI updates via `Platform.runLater`.

## Error handling
- `GlobalExceptionHandler.registerDefaultHandler()` is invoked at app start (see `ProjectManagementApplication.start`).
- All uncaught exceptions are logged with thread name and full stack trace; a JavaFX alert shows error code, message, and stack trace content.

## Database
- MySQL schemas and seed scripts live in `src/main/resources/Database` (`DB_schema.sql`, `task.sql`, `project.sql`, etc.).
- Connection defaults in `utils/MySQLDatabaseConnection`:
  - DB: `mydb`, user: `root`, password: `123456`, URL: `jdbc:mysql://localhost:3306/mydb`.
  - Update these values to match your local environment.
- DAOs use plain JDBC with prepared statements. Status history uses window functions (e.g., `TaskDAO.getStatusHistory`).

## Entry points & navigation
- `ProjectManagementApplication` (JavaFX `Application`) is the main entry; it initializes global exception handling and loads `AuthWrapper.fxml` via `SceneManager`.
- `SceneManager` centralizes stage and scene switching.
- `AppContext` caches user session data and project list per logged-in user.
- Chat/notification setup is in `ChatClientService` (default host `127.0.0.1`, port `8080`).

## Project layout (excerpt)
```
src/main/java/com/app/src/
  ProjectManagementApplication.java
  controllers/        # JavaFX controllers per screen/component
  services/           # Business logic per domain
  daos/               # JDBC data access
  models/, dtos/      # Entities + view DTOs
  core/               # Context, async executor, chat, plugins
  exceptions/         # AppException, ErrorCode, GlobalExceptionHandler
  utils/              # MySQL connection, helpers
src/main/resources/
  scenes/             # FXML screens
  components/         # Reusable UI parts (Header, ProjectCard, ...)
  css/, image/        # Styling and assets
  Database/           # SQL schema and seed scripts
```

## Running locally
Prerequisites: JDK 21, Maven 3.9+, MySQL 8 running with a database matching `utils/MySQLDatabaseConnection` settings.

1) Apply schema/seed scripts from `src/main/resources/Database` to your MySQL instance.
2) Adjust DB credentials in `utils/MySQLDatabaseConnection` if needed.
3) From the project root (`C:\code\ProjectManagement\Src`), run:

```powershell
mvn clean javafx:run
```

Optional: run tests with `mvn test`.

## Notes
- Plugins (PF4J) are loaded via `core.PluginLoader`/`core.PluginHost` if present in `plugins/`.
- Chat/notification server configuration can be customized via `ChatClientService.connect(host, port)` before invoking `connectDefault()`.
- The worker pool (`AsyncExecutor`) is shut down and chat socket is disconnected in `ProjectManagementApplication.stop()` to close resources gracefully.

