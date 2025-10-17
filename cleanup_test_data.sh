#!/bin/bash

# Script to clean up test data by deleting test users and their loans
BASE_URL="http://localhost:8080"

echo "=========================================="
echo "Cleaning Up Test Data"
echo "=========================================="

# Array of test user credentials
TEST_USERS=(
  "john.doe@example.com"
  "jane.smith@example.com"
  "michael.johnson@example.com"
)

for email in "${TEST_USERS[@]}"; do
  echo -e "\nProcessing user: $email"
  
  # Login as the test user
  LOGIN_RESPONSE=$(curl -s -X POST "${BASE_URL}/auth/login" \
    -H "Content-Type: application/json" \
    -d "{
      \"email\": \"$email\",
      \"password\": \"password123\"
    }")
  
  TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | sed 's/"token":"//')
  
  if [ -n "$TOKEN" ]; then
    echo "  ✓ Logged in successfully"
    
    # Get all loans for this user
    LOANS_RESPONSE=$(curl -s -X GET "${BASE_URL}/loans/my-loans" \
      -H "Authorization: Bearer $TOKEN")
    
    # Extract loan IDs and delete them
    LOAN_IDS=$(echo $LOANS_RESPONSE | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
    
    if [ -n "$LOAN_IDS" ]; then
      for LOAN_ID in $LOAN_IDS; do
        echo "  Deleting loan ID: $LOAN_ID"
        curl -s -X DELETE "${BASE_URL}/loans/$LOAN_ID" \
          -H "Authorization: Bearer $TOKEN" > /dev/null
      done
      echo "  ✓ All loans deleted for $email"
    else
      echo "  No loans found for $email"
    fi
  else
    echo "  ✗ Failed to login (user may not exist)"
  fi
done

echo -e "\n=========================================="
echo "Note: Users themselves are not deleted via API"
echo "To fully clean the database, you may need to:"
echo "  1. Connect to PostgreSQL directly"
echo "  2. Run: DELETE FROM user_role WHERE user_id IN (SELECT id FROM users WHERE email LIKE '%@example.com');"
echo "  3. Run: DELETE FROM users WHERE email LIKE '%@example.com';"
echo "=========================================="

