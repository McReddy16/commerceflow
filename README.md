

# 🔐 Authentication Module (`authenaction` Branch)

This branch contains the full authentication implementation for CommerceFlow using **JWT tokens**.
It isolates all security logic away from the main application so `main` stays clean and business-focused.

---

## 🎯 Purpose

* Add secure login support
* Protect sensitive APIs
* Validate users using JWT
* Keep authentication isolated inside a dedicated feature branch
* Prepare for controlled PR into `main`

---

# ⚙️ Authentication Features

✔ Login using phone or user credentials
✔ JWT token generation
✔ Token expiration handling
✔ Validation for every secured request
✔ Spring Security filter chain
✔ Swagger Bearer token authentication

---

# 📁 Code Structure

```
src/main/java/com/example/authentication/
    AuthController.java
    AuthService.java
    LoginRequestDTO.java
    LoginResponseDTO.java

src/main/java/com/example/security/
    JwtUtil.java
    JwtAuthenticationFilter.java

src/main/java/com/example/config/security/
    SecurityConfig.java

src/main/java/com/example/config/swagger/
    SwaggerSecurityConfig.java
```

---

# 🧾 JWT Configuration (application.properties)

```properties
# ================================
# JWT CONFIG
# ================================

# Must be minimum 32 characters
jwt.secret=THIS_IS_A_SUPER_SECRET_JWT_KEY_1234567890

# Token validity: 1 hour
jwt.expiration=3600000
```

---

# 🔁 End-to-End Authentication Flow

```
Client (Swagger/Postman)

        |
        | POST /auth/login  
        | Body: { "phone": "..." }
        v

AuthController  --->  AuthService  --->  CustomerRepository
                            |                 (Checks if user exists)
                            |
                            --> JwtUtil.generateToken(phone, userId)
                                           |
                                           --> returns signed JWT token

        |
        v

Client receives response:
{ "token": "<JWT>" }  → must store this

        |
        | Next protected API call:
        | DELETE /api/customers/123
        | Header: Authorization: Bearer <JWT>
        v

Spring Security Filter Chain
        |
        ---> JwtAuthenticationFilter
                - Extracts token from header
                - Validates signature & expiry using JwtUtil
                - If valid → sets user authentication in SecurityContext
                - If invalid/missing → request blocked

        |
        v

SecurityConfig (request authorization rules)
        |
        v

CustomerController.delete(...)
(Executes only if authenticated & allowed)
```

---

# 🔐 Authorization Requirement

For protected APIs, client must send:

```
Authorization: Bearer <JWT_TOKEN>
```

Without token → controller will NOT execute.

---

# 🔑 Key Components

### `AuthController`

* Receives `/auth/login`
* Validates request DTO
* Returns generated token

### `AuthService`

* Checks DB for registered customer
* Builds login response
* Delegates token creation

### `JwtUtil`

* Creates signed JWT token
* Extracts claims
* Verifies expiration
* Validates integrity

### `JwtAuthenticationFilter`

* Reads Authorization header
* Validates token via JwtUtil
* Sets authentication context

### `SecurityConfig`

* Configures Spring Security filter chain
* Decides which APIs require authentication

### `SwaggerSecurityConfig`

* Enables JWT authentication inside Swagger UI header

---

# 🛡 Protected Endpoints Example

```http
DELETE /api/customers/{id}
Header: Authorization: Bearer <JWT>
```

If token is:
❌ Missing
❌ Invalid
❌ Expired

→ Request is rejected before reaching controller.

---

# 🧪 Testing via Postman / Swagger

### 1️⃣ Login

```
POST /auth/login
Body:
{
  "phone": "9876543210"
}
```

Response:

```json
{
  "token": "<JWT-TOKEN>"
}
```

### 2️⃣ Use token to hit protected APIs

```
Authorization: Bearer <JWT_TOKEN>
```

Example:

```
DELETE /api/customers/5
```

---

# 🔁 Branch Usage Workflow

```bash
# switch to this branch
git checkout authenaction

# add updates
git add .

# commit changes
git commit -m "Auth module update"

# push
git push
```

When authentication is fully ready, tested, and reviewed:

```bash
Merge into main via Pull Request
```

---

# 📌 Why Separate Authentication Branch?

✔ Cleaner code base
✔ No security leaks into unfinished main branch
✔ Easy rollback
✔ Safe PR review
✔ Controlled merge strategy


