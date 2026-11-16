# Task 2 – Data Processor UML Class Diagram

## Problem

Design the architecture of a **dynamically reconfigurable data processor** for a pipeline. The processor can switch at runtime between:

- **Modes:** Dump (drop data), Passthrough (insert), Validate (validate, then insert)
- **Databases:** Postgres, Redis, Elastic

New modes and databases will be added in the future, so the design must be easy to extend.

## Diagram

📄 [data-processor-uml.pdf](data-processor-uml.pdf) · Source: [data-processor-uml.puml](data-processor-uml.puml)

## Design

### Patterns used

- **Strategy (modes):** `ProcessingMode` interface implemented by `DumpMode`, `PassthroughMode` and `ValidateMode`.
- **Strategy (databases):** `Database` interface (`connect`, `insert`, `validate`) implemented by `PostgresDatabase`, `RedisDatabase` and `ElasticDatabase`.
- **Factory / Registry:** `ModeFactory` and `DatabaseFactory` map each identifier enum to an implementation.

Because a mode receives the database as a dependency, modes and databases combine freely: **3 + 3 classes instead of 3 × 3**.

### Runtime flow

```
configure(modeId, dbId)
  → currentMode     = modeFactory.getMode(modeId)
  → currentDatabase = databaseFactory.getDatabase(dbId)
  → currentDatabase.connect()

process(dataPoint)
  → currentMode.process(dataPoint, currentDatabase)
```

### SOLID

| Principle | How it is applied |
|-----------|-------------------|
| Single Responsibility | The processor only delegates; modes define behaviour; databases handle storage |
| Open/Closed | A new mode or database is a new class plus a factory registration, with no changes to existing code |
| Liskov Substitution | Any `ProcessingMode` / `Database` implementation is interchangeable |
| Interface Segregation | Two small, focused interfaces |
| Dependency Inversion | `DataProcessor` depends on abstractions; factories are injected through the constructor |

## Reflection

An alternative is **constructor injection**: create each mode with its database already inside, so `DumpMode` never sees a database at all. That keeps the mode interface smaller, but the mode has to be rebuilt whenever only the database changes. Both approaches are valid; I chose method injection so that the two dimensions can change independently.

## How to render

Paste the `.puml` file into [planttext.com](https://www.planttext.com/) or use the PlantUML plugin in IntelliJ / VS Code.