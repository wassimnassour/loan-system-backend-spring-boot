# Test Data Scripts

This directory contains scripts to create and manage test data for the Loan Backend System.

## Scripts

### 1. `create_test_data.sh`

Creates 3 test users with 5 loans each (15 loans total).

**Usage:**

```bash
./create_test_data.sh
```

**Prerequisites:**

- The application must be running on `http://localhost:8080`
- Database must be initialized with the roles table

**Test Users Created:**
| Name | Email | Password |
|------|-------|----------|
| John Doe | john.doe@example.com | password123 |
| Jane Smith | jane.smith@example.com | password123 |
| Michael Johnson | michael.johnson@example.com | password123 |

**Loans Created per User:**
Each user gets 5 different loans with various types:

- Personal Loans
- Mortgage Loans
- Auto Loans
- Business Loans
- Student Loans
- Credit Card Loans

### 2. `cleanup_test_data.sh`

Cleans up the test data by deleting all loans for test users.

**Usage:**

```bash
./cleanup_test_data.sh
```

**Note:** This script only deletes loans. To fully remove users from the database, you'll need to connect to PostgreSQL directly.

## Complete Database Cleanup

If you need to completely remove test users from the database:

```sql
-- Connect to your PostgreSQL database
psql -h localhost -p 5450 -U my_user -d my_database

-- Delete user-role relationships
DELETE FROM user_role WHERE user_id IN (SELECT id FROM users WHERE email LIKE '%@example.com');

-- Delete users
DELETE FROM users WHERE email LIKE '%@example.com';
```

## Quick Start

1. Start your application:

```bash
mvn spring-boot:run
```

2. Run the test data script:

```bash
./create_test_data.sh
```

3. Access the application and login with any test user credentials

4. When done testing, clean up:

```bash
./cleanup_test_data.sh
```

## Tips

- Run `create_test_data.sh` after a fresh database setup to have consistent test data
- The script is idempotent - running it multiple times will create duplicate users/loans (use cleanup first)
- You can modify the scripts to add more users or different loan configurations
