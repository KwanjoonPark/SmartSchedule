# SmartSchedule - Checkpoint 4 Beta Release

Student: 박관준 (213157)  
Department: Software Engineering, Chonnam National University

SmartSchedule is a Java Swing personal schedule management program developed for the Software System Design project. It supports schedule CRUD operations, validation, overlap detection, search/filter/sort, priorities/categories, and automatic local persistence.

## Requirements
- Java 17 or later

## Run the submitted JAR
```bash
java -jar ssd_ip_cp4_2026f_213157_app.jar
```

Schedule data is automatically stored at:
`~/.smartschedule/schedules.tsv`

## Build from source
Linux/macOS:
```bash
./build.sh
java -jar build/SmartSchedule.jar
```

Manual build:
```bash
mkdir -p build/classes
find src/main/java -name "*.java" > build/sources.txt
javac --release 17 -d build/classes @build/sources.txt
printf 'Manifest-Version: 1.0\nMain-Class: com.smartschedule.SmartScheduleApp\n\n' > build/MANIFEST.MF
jar cfm build/SmartSchedule.jar build/MANIFEST.MF -C build/classes .
```

## Main features
- Add, edit, and delete schedules
- Required-field and time validation
- Schedule time-conflict detection
- Keyword search, category filtering, and sorting
- High / Medium / Low priority levels
- Automatic local save and restore

## Main architecture
- `SmartScheduleApp`: application entry point
- `SmartScheduleFrame`: main Swing UI
- `ScheduleDialog`: add/edit form
- `ScheduleManager`: business logic and usecase implementation
- `ScheduleRepository`: local file persistence
- `Schedule`: schedule domain model

## GitHub
Target repository: https://github.com/KwanjoonPark/SmartSchedule
