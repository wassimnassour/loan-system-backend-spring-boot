#!/bin/bash

# Script to create 3 users and 5 loans each using the API
BASE_URL="http://localhost:8080"

echo "=========================================="
echo "Creating 3 Users and 5 Loans Each"
echo "=========================================="

# Array to store tokens for each user
declare -a TOKENS

# Create User 1: John Doe
echo -e "\n[1/3] Creating User: John Doe"
REGISTER_RESPONSE=$(curl -s -X POST "${BASE_URL}/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john.doe@example.com",
    "password": "password123",
    "role": "USER"
  }')
echo "Response: $REGISTER_RESPONSE"

# Login User 1
echo "Logging in as John Doe..."
LOGIN_RESPONSE=$(curl -s -X POST "${BASE_URL}/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "password123"
  }')
TOKEN1=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | sed 's/"token":"//')
TOKENS[0]=$TOKEN1
echo "Token obtained for John Doe"

# Create 5 Loans for User 1
echo -e "\nCreating 5 loans for John Doe..."

echo "[Loan 1/5] Personal Loan - Home Renovation"
curl -s -X POST "${BASE_URL}/loans/create" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKENS[0]}" \
  -d '{
    "amount": 15000.00,
    "type": "PERSONAL",
    "interestRate": 5.5,
    "termInMonths": 24,
    "purpose": "Home renovation",
    "notes": "For kitchen and bathroom remodeling"
  }'

echo -e "\n[Loan 2/5] Mortgage Loan"
curl -s -X POST "${BASE_URL}/loans/create" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKENS[0]}" \
  -d '{
    "amount": 250000.00,
    "type": "MORTGAGE",
    "interestRate": 3.75,
    "termInMonths": 360,
    "purpose": "Purchase primary residence",
    "notes": "30-year fixed mortgage"
  }'

echo -e "\n[Loan 3/5] Auto Loan"
curl -s -X POST "${BASE_URL}/loans/create" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKENS[0]}" \
  -d '{
    "amount": 28000.00,
    "type": "AUTO",
    "interestRate": 4.25,
    "termInMonths": 60,
    "purpose": "Purchase new car",
    "notes": "2024 Toyota Camry"
  }'

echo -e "\n[Loan 4/5] Business Loan"
curl -s -X POST "${BASE_URL}/loans/create" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKENS[0]}" \
  -d '{
    "amount": 50000.00,
    "type": "BUSINESS",
    "interestRate": 6.5,
    "termInMonths": 84,
    "purpose": "Expand business operations",
    "notes": "Small business expansion loan"
  }'

echo -e "\n[Loan 5/5] Student Loan"
curl -s -X POST "${BASE_URL}/loans/create" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKENS[0]}" \
  -d '{
    "amount": 20000.00,
    "type": "STUDENT",
    "interestRate": 3.5,
    "termInMonths": 120,
    "purpose": "Graduate school tuition",
    "notes": "MBA program funding"
  }'

# Create User 2: Jane Smith
echo -e "\n\n[2/3] Creating User: Jane Smith"
REGISTER_RESPONSE=$(curl -s -X POST "${BASE_URL}/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Smith",
    "email": "jane.smith@example.com",
    "password": "password123",
    "role": "USER"
  }')
echo "Response: $REGISTER_RESPONSE"

# Login User 2
echo "Logging in as Jane Smith..."
LOGIN_RESPONSE=$(curl -s -X POST "${BASE_URL}/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "jane.smith@example.com",
    "password": "password123"
  }')
TOKEN2=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | sed 's/"token":"//')
TOKENS[1]=$TOKEN2
echo "Token obtained for Jane Smith"

# Create 5 Loans for User 2
echo -e "\nCreating 5 loans for Jane Smith..."

echo "[Loan 1/5] Personal Loan - Debt Consolidation"
curl -s -X POST "${BASE_URL}/loans/create" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKENS[1]}" \
  -d '{
    "amount": 12000.00,
    "type": "PERSONAL",
    "interestRate": 6.0,
    "termInMonths": 36,
    "purpose": "Debt consolidation",
    "notes": "Consolidating credit card debt"
  }'

echo -e "\n[Loan 2/5] Mortgage Loan - Investment Property"
curl -s -X POST "${BASE_URL}/loans/create" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKENS[1]}" \
  -d '{
    "amount": 180000.00,
    "type": "MORTGAGE",
    "interestRate": 4.0,
    "termInMonths": 240,
    "purpose": "Purchase investment property",
    "notes": "Investment property mortgage"
  }'

echo -e "\n[Loan 3/5] Auto Loan - Used Luxury Car"
curl -s -X POST "${BASE_URL}/loans/create" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKENS[1]}" \
  -d '{
    "amount": 35000.00,
    "type": "AUTO",
    "interestRate": 5.0,
    "termInMonths": 48,
    "purpose": "Purchase used luxury car",
    "notes": "2022 BMW 5 Series"
  }'

echo -e "\n[Loan 4/5] Personal Loan - Wedding"
curl -s -X POST "${BASE_URL}/loans/create" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKENS[1]}" \
  -d '{
    "amount": 10000.00,
    "type": "PERSONAL",
    "interestRate": 7.5,
    "termInMonths": 24,
    "purpose": "Wedding expenses",
    "notes": "Personal loan for wedding ceremony"
  }'

echo -e "\n[Loan 5/5] Credit Card Loan"
curl -s -X POST "${BASE_URL}/loans/create" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKENS[1]}" \
  -d '{
    "amount": 5000.00,
    "type": "CREDIT_CARD",
    "interestRate": 15.0,
    "termInMonths": 12,
    "purpose": "Credit card balance transfer",
    "notes": "Balance transfer from high-interest card"
  }'

# Create User 3: Michael Johnson
echo -e "\n\n[3/3] Creating User: Michael Johnson"
REGISTER_RESPONSE=$(curl -s -X POST "${BASE_URL}/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Michael Johnson",
    "email": "michael.johnson@example.com",
    "password": "password123",
    "role": "USER"
  }')
echo "Response: $REGISTER_RESPONSE"

# Login User 3
echo "Logging in as Michael Johnson..."
LOGIN_RESPONSE=$(curl -s -X POST "${BASE_URL}/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "michael.johnson@example.com",
    "password": "password123"
  }')
TOKEN3=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | sed 's/"token":"//')
TOKENS[2]=$TOKEN3
echo "Token obtained for Michael Johnson"

# Create 5 Loans for User 3
echo -e "\nCreating 5 loans for Michael Johnson..."

echo "[Loan 1/5] Business Loan - Restaurant"
curl -s -X POST "${BASE_URL}/loans/create" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKENS[2]}" \
  -d '{
    "amount": 75000.00,
    "type": "BUSINESS",
    "interestRate": 7.0,
    "termInMonths": 60,
    "purpose": "Start new restaurant",
    "notes": "Business startup loan for Italian restaurant"
  }'

echo -e "\n[Loan 2/5] Auto Loan - Electric Vehicle"
curl -s -X POST "${BASE_URL}/loans/create" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKENS[2]}" \
  -d '{
    "amount": 45000.00,
    "type": "AUTO",
    "interestRate": 4.5,
    "termInMonths": 72,
    "purpose": "Purchase electric vehicle",
    "notes": "Tesla Model 3 with green loan discount"
  }'

echo -e "\n[Loan 3/5] Mortgage Loan - Family Home"
curl -s -X POST "${BASE_URL}/loans/create" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKENS[2]}" \
  -d '{
    "amount": 300000.00,
    "type": "MORTGAGE",
    "interestRate": 3.5,
    "termInMonths": 300,
    "purpose": "Purchase family home",
    "notes": "Primary residence with 25-year term"
  }'

echo -e "\n[Loan 4/5] Student Loan - Undergraduate"
curl -s -X POST "${BASE_URL}/loans/create" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKENS[2]}" \
  -d '{
    "amount": 15000.00,
    "type": "STUDENT",
    "interestRate": 2.5,
    "termInMonths": 96,
    "purpose": "Undergraduate education",
    "notes": "4-year computer science program"
  }'

echo -e "\n[Loan 5/5] Personal Loan - Emergency Medical"
curl -s -X POST "${BASE_URL}/loans/create" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKENS[2]}" \
  -d '{
    "amount": 8000.00,
    "type": "PERSONAL",
    "interestRate": 8.0,
    "termInMonths": 18,
    "purpose": "Emergency medical expenses",
    "notes": "Fast-tracked emergency loan for surgery"
  }'

echo -e "\n\n=========================================="
echo "✓ Test Data Creation Complete!"
echo "=========================================="
echo "Created 3 users with 5 loans each (15 loans total)"
echo ""
echo "User Credentials (all use password: password123):"
echo "  1. john.doe@example.com"
echo "  2. jane.smith@example.com"
echo "  3. michael.johnson@example.com"
echo "=========================================="

