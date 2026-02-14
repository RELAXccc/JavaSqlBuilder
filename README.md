# JavaSqlBuilder

[![Build Status](https://github.com/RELAXccc/JavaSqlBuilder/actions/workflows/maven.yml/badge.svg)](https://github.com/RELAXccc/JavaSqlBuilder/actions)
![Coverage](.github/badges/jacoco.svg)

Lightweight, fluent SQL builder for Java 21. Build complex queries programmatically with type-safety and multi-dialect support.

## Features
- **Fluent CRUD**: Programmatic `SELECT`, `INSERT`, `UPDATE`, and `DELETE`.
- **Multi-Dialect**: Native support for Oracle, Postgres, H2, DB2, and MSSQL.
- **Modular Expressions**: Deeply nested `WHERE`/`HAVING` clauses with `AND`/`OR` chaining.
- **Type-Safe Joins**: Dedicated join implementations for `INNER`, `LEFT`, `RIGHT`, and `FULL`.
- **Zero-Dependency**: Keeps your project footprint small.

## Usage

### Initialization
```java
// Choose your dialect
SqlDialect dialect = new PostgresDialect();
SelectBuilder select = new SelectBuilder(dialect);
```

### Complex Queries
```java
Query query = select
    .select("u.name", "COUNT(o.id)")
    .from("users", "u")
    .leftJoin("orders", "o", Expression.eq("u.id", Expression.column("o.user_id")))
    .where(Expression.eq("u.status", "active"))
    .groupBy("u.name")
    .having(Expression.gt("COUNT(o.id)", 5))
    .orderBy("u.name").asc()
    .limit(10)
    .build();

System.out.println(query.getStatement()); // SQL string
System.out.println(query.getParameters()); // List of objects
```

### CRUD Operations
```java
// INSERT
new InsertBuilder(dialect).into("users").value("name", "John").build();

// UPDATE
new UpdateBuilder(dialect).table("users").set("status", "active").where(Expression.eq("id", 1)).build();

// DELETE
new DeleteBuilder(dialect).from("users").where(Expression.eq("id", 1)).build();
```

## Architecture
- **Builders**: Orchestrate query assembly, extending `AbstractBuilder`.
- **Dialects**: Isolate DB-specific syntax (e.g., paging).
- **Expressions**: Modular system of `Condition` and `Operand` objects.
- **Joins**: Independent classes for SQL join logic.

## Development
```bash
mvn clean test # Run tests and JaCoCo coverage
```

## License
MIT
