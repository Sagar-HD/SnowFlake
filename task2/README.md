# Snowflake JDBC Demo

A simple Java application demonstrating JDBC connectivity to Snowflake.

## Prerequisites

### 1. Snowflake Setup
1. Sign up for Snowflake free trial: https://signup.snowflake.com/
2. Log in to Snowsight (Snowflake web interface)
3. Run the SQL commands in `snowflake_setup.sql` to create:
   - Warehouse: NEWS_WH
   - Database: NEWS_DB
   - Schema: NEWS_SCHEMA
   - Table: news (with sample data)
4. Run `SELECT CURRENT_ACCOUNT();` to get your account identifier

### 2. Java Development
- Java 17 or higher
- Maven 3.6+
- IntelliJ IDEA (recommended) or Eclipse

## Setup Steps

### 1. Set Environment Variables

**Option 1: Use full JDBC connection string (recommended)**

**Windows PowerShell:**
```powershell
$env:SNOWFLAKE_CONNECTION_STRING="jdbc:snowflake://MRBABSD-WX18904.snowflakecomputing.com/?user=your_username&password=your_password&warehouse=NEWS_WH&db=NEWS_DB&schema=NEWS_SCHEMA"
```

**Or set permanently:**
```powershell
[System.Environment]::SetEnvironmentVariable('SNOWFLAKE_CONNECTION_STRING', 'jdbc:snowflake://MRBABSD-WX18904.snowflakecomputing.com/?user=your_username&password=your_password&warehouse=NEWS_WH&db=NEWS_DB&schema=NEWS_SCHEMA', 'User')
```

**Option 2: Use individual environment variables**

**Windows PowerShell:**
```powershell
$env:SNOWFLAKE_USER="your_username"
$env:SNOWFLAKE_PASSWORD="your_password"
$env:SNOWFLAKE_ACCOUNT="MRBABSD-WX18904"
$env:SNOWFLAKE_WAREHOUSE="NEWS_WH"
$env:SNOWFLAKE_DATABASE="NEWS_DB"
$env:SNOWFLAKE_SCHEMA="NEWS_SCHEMA"
```

### 2. Build with Maven
```bash
mvn clean compile
```

### 3. Run the Application
```bash
mvn exec:java -Dexec.mainClass="com.sagar.snowflake.SnowflakeJdbcDemo"
```

Or run directly from IntelliJ.

## Project Structure
```
snowflake-jdbc-demo/
├── pom.xml
├── snowflake_setup.sql
├── .env.example
├── README.md
└── src/
    └── main/
        └── java/
            └── com/
                └── sagar/
                    └── snowflake/
                        └── SnowflakeJdbcDemo.java
```

## What This Does
- Connects to Snowflake using JDBC
- Executes a SELECT query on the `news` table
- Displays results in the console

## Next Steps
After this works, we'll implement:
- INSERT operations
- UPDATE operations
- DELETE operations
- Proper backend architecture (Controller-Service-DAO pattern)
