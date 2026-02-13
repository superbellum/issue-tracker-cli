# Issue Tracker CLI

A **Java 21 Spring Boot CLI application** for tracking system issues, with persistent storage in a JSON file.
Allows creating issues, updating their status, and listing them by status. Fully testable and containerized with Docker.

---

## Table of Contents

1. [Features](#features)
2. [Requirements](#requirements)
3. [Build Instructions](#build-instructions)
4. [Running the Application](#running-the-application)
5. [Docker Usage](#docker-usage)
6. [CLI Commands](#cli-commands)

    * [Create Issue](#create-issue)
    * [Update Issue Status](#update-issue-status)
    * [List Issues](#list-issues)
7. [Configuration](#configuration)
8. [Testing](#testing)
9. [License](#license)

---

## Features

### Use cases:

* Create new issues with description and optional parent issue ID
* Update status of existing issues (`OPEN`, `IN_PROGRESS`, `CLOSED`)
* List issues filtered by status

For testing purposes and simplicity's sake, a persistent JSON file storage has been implemented.

The application has been tested with unit and functional tests, and containerized for easy deployment.

---

## Requirements

* Java 21 JDK (Oracle, Temurin, or compatible)
* Maven 3.8+
* Docker (for containerized usage, optional)

---

## Build Instructions

1. Clone the repository:

```bash
git clone <repository-url>
cd issue-tracker-cli
```

2. Build the project using Maven:

```bash
mvn clean package
```

* This will produce a **jar** file in `target/issue-tracker-cli.jar`

---

## Running the Application

Run the CLI directly using Java:

```bash
java -jar target/issue-tracker-cli.jar <command> [options]
```

Examples:

```bash
# Create an issue
java -jar target/issue-tracker-cli.jar create -d "Fix login bug"

# Update an issue status
java -jar target/issue-tracker-cli.jar update-status --id <issue-id> --status IN_PROGRESS

# List issues by status
java -jar target/issue-tracker-cli.jar list -s OPEN
```

---

## Docker Usage

### Build the Docker image

```bash
docker build -t issue-tracker-cli .
```

### Run the CLI commands in a container

```bash
# Create an issue
docker run --rm issue-tracker-cli create -d "Fix login bug"

# Update an issue status
docker run --rm issue-tracker-cli update-status --id <issue-id> --status IN_PROGRESS

# List issues by status
docker run --rm issue-tracker-cli list -s OPEN
```

### Persistent JSON Storage

To keep `issues.json` between container runs:

```bash
docker run --rm -v $(pwd)/data:/app issue-tracker-cli create -d "Persistent issue"
```

* Host `./data` directory is mapped to `/app` in the container
* All issues will now persist across runs

---

## CLI Commands

### Create Issue

**Command:** `create`

**Description:** Creates a new issue with a description and optional parent ID.

**Options:**

| Option          | Short | Required | Description            |
| --------------- | ----- | -------- | ---------------------- |
| `--description` | `-d`  | Yes      | Issue description text |
| `--parent-id`   | —     | No       | ID of the parent issue |

**Examples:**

```bash
create -d "Fix login bug"
create --description "Child task" --parent-id 123e4567-e89b-12d3-a456-426614174000
```

---

### Update Issue Status

**Command:** `update-status`

**Description:** Updates the status of an existing issue.

**Options:**

| Option     | Required | Description                                  |
| ---------- | -------- | -------------------------------------------- |
| `--id`     | Yes      | ID of the issue to update                    |
| `--status` | Yes      | New status (`OPEN`, `IN_PROGRESS`, `CLOSED`) |

**Example:**

```bash
update-status --id 3b87b9e0-4bb6-42c9-b745-dae9f26b7a5f --status IN_PROGRESS
```

**Error Handling:**

* Passing an invalid status:

```text
Wrong status 'INVALID' used. Available statuses: OPEN, IN_PROGRESS, CLOSED
```

* Passing a non-existent issue ID:

```text
Issue not found: <issue-id>
```

---

### List Issues

**Command:** `list`

**Description:** Lists all issues filtered by status.

**Options:**

| Option     | Short | Required | Description                                           |
| ---------- | ----- | -------- | ----------------------------------------------------- |
| `--status` | `-s`  | Yes      | Status to filter by (`OPEN`, `IN_PROGRESS`, `CLOSED`) |

**Example:**

```bash
list -s OPEN
list --status IN_PROGRESS
```

**Output:**

```
Issue{id='3b87b9e0-4bb6-42c9-b745-dae9f26b7a5f', description='Fix login bug', parentId='null', status='OPEN', createdDate='2026-02-13T12:34:56Z', updatedDate='2026-02-13T12:34:56Z'}
```

---

## Configuration

* JSON storage path can be configured via Spring property:

```properties
issues.storage.path=issues.json
```

* Defaults to `issues.json` in the working directory.

---

## Testing

### Run unit and functional tests

```bash
mvn test
```

* Tests include:

    * Service layer unit tests
    * CLI functional tests
    * Negative edge cases (invalid status, non-existent IDs)

---

[Back to top 🔝](#issue-tracker-cli)
