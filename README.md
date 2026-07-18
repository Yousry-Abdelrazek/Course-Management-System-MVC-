# Course Management System (Spring Boot MVC)

A full-stack, server-side rendered **Course Management System** built with **Spring Boot 3.4**,
**Thymeleaf**, **Bootstrap 5** (via WebJars), **Spring Data JPA/Hibernate**, and **MySQL**.

This is a monolithic **MVC** web application (not a REST API). All pages are rendered
server-side with Thymeleaf and communicated with through standard HTML forms.

## Features

- **Dashboard** with live statistics (instructors, courses, students, enrollments, active enrollments).
- **Instructors** — CRUD with course-count display, search, pagination, and delete protection
  (cannot delete an instructor that still has courses assigned).
- **Courses** — CRUD with soft-delete (`@SQLRestriction(deleted = false)`), registration
  window (`enrollmentStart` / `enrollmentEnd`), search, pagination, and dependency protection.
- **Students** — CRUD, duplicate-email prevention, and a per-student enrollment list view.
- **Enrollments** — CRUD with grade, progress (0–100), and status (`ACTIVE`, `COMPLETED`,
  `DROPPED`), duplicate-enrollment prevention, and registration-window validation.
- Global exception handling with friendly error pages (`404`, `500`, `error`) and flash
  alert messages for business-rule violations.

## Architecture

Clean, layered architecture following best practices:

```
controller/    -> thin controllers, validation + flash messages only
service/        -> interfaces
service/impl/   -> business logic (the only place rules live)
repository/     -> Spring Data JPA repositories
entity/         -> JPA entities (never exposed to views)
dto/request/    -> incoming data (validated)
dto/response/   -> outgoing data (view models)
mapper/         -> entity <-> DTO conversion
exception/      -> domain + global exceptions
config/         -> WebConfig (interceptors) + DataInitializer (seed data)
```

- Constructor injection only (`final` fields).
- `Optional` handled explicitly with `orElseThrow(ResourceNotFoundException)`.
- DTO pattern + mappers — entities are never passed to the view.
- Repository pattern via Spring Data.

## Technology Stack

| Layer        | Technology                                  |
|--------------|---------------------------------------------|
| Language     | Java 21                                     |
| Framework    | Spring Boot 3.4.2 (Web MVC, Data JPA, Validation) |
| View         | Thymeleaf + Bootstrap 5.3 + Bootstrap Icons (WebJars) |
| Persistence  | Hibernate / Spring Data JPA                 |
| Database     | MySQL 8                                     |
| Build        | Maven                                        |
| Utilities    | Lombok, Bean Validation                     |

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+ (or use the bundled `./mvnw`)
- MySQL 8 running on `localhost:3306` with a `root` user

### Local database

Create the database (the app also does this automatically via `createDatabaseIfNotExist=true`):

```sql
CREATE DATABASE CourseManagementDBMVC;
```

### Configuration

`src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/CourseManagementDBMVC?createDatabaseIfNotExist=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=Yousry@#1234
spring.jpa.hibernate.ddl-auto=update
app.seed-data=true
```

All of these can be overridden with environment variables (`SPRING_DATASOURCE_URL`, etc.).

### Build & Run

```bash
./mvnw clean package
java -jar target/CourseManagementSystemMVC-0.0.1-SNAPSHOT.jar
```

Or use Maven directly:

```bash
./mvnw spring-boot:run
```

Then open <http://localhost:8080> (redirects to `/dashboard`).

> On first startup the `DataInitializer` seeds sample instructors, courses, students, and
> enrollments (controlled by `app.seed-data=true`). Delete the data, set the flag to `false`,
> or change `ddl-auto` to `validate` for a clean run.

## Docker

Build and run the whole stack (MySQL + app) with Docker Compose:

```bash
docker compose up --build
```

The app will be available at <http://localhost:8080>. Data is persisted in a named volume
(`cms_mysql_data`). Override the database password with the `MYSQL_ROOT_PASSWORD` env var.

To build only the image:

```bash
docker build -t course-management-system .
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://<db-host>:3307/CourseManagementDBMVC \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=secret \
  course-management-system
```

## Entity Relationship Diagram (ERD)

```
                          ┌──────────────────────────┐
                          │       INSTRUCTOR         │
                          ├──────────────────────────┤
                          │ PK  id            BIGINT │
                          │     name          VARCHAR│
                          │     email         VARCHAR│  (unique)
                          │     bio           TEXT   │
                          │     created_at    DATETIME│
                          │     updated_at    DATETIME│
                          └──────────┬───────────────┘
                                     │ 1
                                     │
                                     │ *
                          ┌──────────▼───────────────┐
                          │         COURSE           │
                          ├──────────────────────────┤
                          │ PK  id            BIGINT │
                          │ FK  instructor_id BIGINT │─── instructor.id
                          │     title         VARCHAR│
                          │     description   TEXT   │
                          │     enrollment_start DATE│
                          │     enrollment_end   DATE│
                          │     created_at    DATETIME│
                          │     updated_at    DATETIME│
                          │     deleted       BOOLEAN│  (soft delete)
                          └──────────┬───────────────┘
                                     │ 1
                                     │
                          ┌──────────▼───────────────┐        ┌──────────────────────────┐
                          │       ENROLLMENT         │        │         STUDENT          │
                          ├──────────────────────────┤        ├──────────────────────────┤
                          │ PK  id            BIGINT │        │ PK  id            BIGINT │
                          │ FK  course_id     BIGINT │────────│     name          VARCHAR│
                          │ FK  student_id    BIGINT │   ────▶ │     email         VARCHAR│ (unique)
                          │     enrolled_on   DATE   │        │     phone         VARCHAR│
                          │     grade         VARCHAR│        │     created_at    DATETIME│
                          │     progress      INT    │        │     updated_at    DATETIME│
                          │     status        ENUM   │        └──────────────────────────┘
                          └──────────────────────────┘
                          UNIQUE (course_id, student_id)


   Relationship cardinality:
   ────────────────────────────────────────────────────────────────────
     INSTRUCTOR (1) ────< (many) COURSE
     COURSE    (1) ────< (many) ENROLLMENT
     STUDENT   (1) ────< (many) ENROLLMENT
     ENROLLMENT       * --- *  (junction: COURSE *---* STUDENT)
   ────────────────────────────────────────────────────────────────────
```

### Legend

| Entity | Key Notes |
|--------|-----------|
| **Instructor** | Owns many `Course` records. Cannot be deleted while courses are assigned. |
| **Course** | Belongs to one `Instructor`. Soft-deleted via `deleted` flag + `@SQLRestriction(deleted = false)`. Has a registration window (`enrollment_start` / `enrollment_end`). |
| **Student** | Owns many `Enrollment` records. Duplicate emails are rejected. |
| **Enrollment** | Junction entity linking `Course` and `Student`. Unique constraint on `(course_id, student_id)` prevents double enrollment. Tracks `grade`, `progress (0–100)`, and `status`. |

- `EnrollmentStatus` enum: `ACTIVE`, `COMPLETED`, `DROPPED`.
- `Course` is soft-deleted; enrolling into a deleted course is blocked (returns `ResourceNotFoundException`).
- Foreign keys: `course.instructor_id → instructor.id`, `enrollment.course_id → course.id`, `enrollment.student_id → student.id`.

## Project Layout

```
src/main/java/com/codewithyousry/coursemanagementsystemmvc/
    controller/
    service/  service/impl/
    repository/
    entity/  dto/request/  dto/response/  mapper/
    exception/
    config/
src/main/resources/
    application.properties
    templates/
        layouts/main.html          (shared layout via th:replace)
        fragments/                 (sidebar, navbar, alerts, footer, components)
        dashboard.html
        instructors/  courses/  students/  enrollments/
        error/  (404, 500, error)
    static/  (css/app.css, js/app.js)
```

## Notes

- This project intentionally uses **server-side rendered Thymeleaf** (no SPA, no REST controllers).
- No automated tests are included; verification is done by manual smoke-testing the running app.
