Below is a **clean, professional, GitHub-ready README content specifically for your `authenaction` branch**.
It clearly explains purpose, structure, JWT config, flow, and endpoints.

You can paste this directly into your branch-level README.

---

# 🔐 Authentication Module (`authenaction` Branch)

This branch contains the complete **authentication and security layer** for the CommerceFlow backend.
It introduces JWT-based authentication, request filtering, secure API access, and token validation without impacting the primary business logic on `main`.

---

## 🚀 Purpose of This Branch

* Keep all authentication logic separate from core features
* Allow clean PRs into `main`
* Make the security layer optional and modular
* Provide reusable JWT utilities for future versions

This branch will only be merged into `main` once authentication is fully implemented, tested, and reviewed.

---

# 🔑 Key Features

✔ Login API with username/password request
✔ Secure token generation using JWT
✔ Token expiry validation
✔ Authentication filter for API request interception
✔ Custom JWT claims support
✔ Separate security configuration
✔ Swagger UI protection

---

# 📂 Folder Structure

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

# ⚙️ JWT Configuration (application.properties)

> These configs must exist in the `authenaction` branch only.

```properties
# ================================
# JWT CONFIG
# ================================

# Secret key (minimum 32 characters)
jwt.secret=THIS_IS_A_SUPER_SECRET_JWT_KEY_1234567890

# Token expiration time
# 1 Hour = 3600000 ms
jwt.expiration=3600000
```

---

# 🔄 Authentication Flow

1️⃣ Client sends login credentials
2️⃣ Backend validates user
3️⃣ On success: JWT token is generated
4️⃣ Token is returned in response
5️⃣ Client must add this token in headers for future API calls:

```
Authorization: Bearer <JWT_TOKEN>
```

6️⃣ Filter validates token for every request
7️⃣ If token is invalid or expired → request is blocked

---

# 🔐 Required HTTP Header

```
Authorization: Bearer <JWT_TOKEN>
```

> Without this, protected APIs will not execute.

---

# 🛡️ Protected Endpoints

Any endpoint annotated with:

```
@SecurityRequirement(name = "bearerAuth")
```

requires a valid JWT token.

Example:

```
DELETE /api/customers/{id}
```

---

# 🧠 Main Components Explained

### `AuthController`

* Handles login requests
* Returns JWT token

### `AuthService`

* Validates credentials
* Builds response DTOs

### `JwtUtil`

* Generates tokens
* Extracts claims
* Validates expiry

### `JwtAuthenticationFilter`

* Reads token from header
* Validates request before controller hits

### `SecurityConfig`

* Configures HTTP security
* Applies filter chain
* Defines public vs private endpoints

### `SwaggerSecurityConfig`

* Attaches bearer token support to Swagger UI

---

# 🧪 Testing via Postman / Swagger

1. Hit `/auth/login` with credentials
2. Copy JWT token from response
3. Add token to header in protected endpoints:

```
Authorization: Bearer <JWT_TOKEN>
```

---

# 🔗 Branch Usage

### Switch to branch

```bash
git checkout authenaction
```

### Commit & push security changes

```bash
git add .
git commit -m "Implement authentication module"
git push
```

### Create PR into main when ready

---

# 🤝 Merge Strategy

* Keep `main` clean
* Develop & test in `authenaction`
* Raise PR → code review → merge

---

# 📝 Notes

* JWT must be minimum **32 chars**
* Token expiry customizable
* Future enhancements will include refresh token support

---


