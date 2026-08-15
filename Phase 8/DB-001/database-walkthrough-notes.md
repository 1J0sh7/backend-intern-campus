# Database Walkthrough Notes
## Phase 8 - Database, SQL and Hibernate

---

## 1. PostgreSQL Installation

**What I Did:**
- Downloaded PostgreSQL from https://www.postgresql.org/download/windows/
- Installed with default settings
- Set password: `[YOUR_PASSWORD]`
- Port: 5432

**Verify:**
```bash
psql -U postgres
\q
```

# 2. Database Creation
 #  What I Did:

```bash
CREATE DATABASE customer_db;
```

#  Verify:

```bash
psql -U postgres -d customer_db
3. Tables Created by Hibernate
   
   
   What Happened:

Hibernate automatically created the customers table

Based on @Entity annotations in Customer.java

Used ddl-auto: update
```

# Table Structure:
```
sql
Table "public.customers"
Column  |  Type   | Modifiers
----------+---------+-----------
id       | bigint  | not null
name     | varchar | not null
email    | varchar | not null, unique
phone    | varchar | not null, unique
Indexes:
"customers_pkey" PRIMARY KEY, btree (id)
"customers_email_key" UNIQUE, btree (email)
"customers_phone_key" UNIQUE, btree (phone)

```
# 4. JPA Annotations Used
```
Annotation	| Purpose

   @Entity  	Marks class as a database table
   @Table   	Specifies table name
   @Id	        Primary key
   @GeneratedValue	Auto-generated ID
   @Column(nullable = false)	NOT NULL constraint
   @Column(unique = true)	UNIQUE constraint

```

# 5. Repository Methods
```
   What I Created:

Method	Purpose
findByEmail(String email)	Find customer by email
findByPhone(String phone)	Find customer by phone
existsByEmail(String email)	Check if email exists
existsByPhone(String phone)	Check if phone exists
findByNameContainingIgnoreCase	Search by name (partial, case-insensitive)
findByEmailContainingIgnoreCase	Search by email (partial, case-insensitive)
```
# 6. SQL Logging
```
   Configuration in application-dev.yml:

yaml
jpa:
show-sql: true
properties:
hibernate:
format_sql: true
What I See in Console:

sql
select c1_0.id,c1_0.email,c1_0.name,c1_0.phone from customers c1_0
insert into customers (email, name, phone) values (?, ?, ?)

```



# 13. Key Learnings

    What I Learned	Why It Matters
    PostgreSQL is a relational database	Data persists after app restarts
    JPA maps Java objects to tables	No manual SQL needed
    Hibernate generates SQL automatically	Faster development
    ddl-auto: update creates tables	Automatic schema management
    Repository methods become SQL queries	Clean data access layer
    Constraints prevent bad data	Data integrity