# Wanderlust Planner — Travel Blog & AI Itinerary Platform

A full-stack web application combining a community-driven travel blog with an **AI-powered trip itinerary generator**. Built with a **Spring Boot** RESTful backend and a **React** frontend, the platform allows travelers to publish destination stories, bookmark favorite blogs, and generate personalized day-by-day travel plans.

---

## Key Features

### AI Travel Itinerary Planner
- **Personalized Trip Generation:** Creates structured, day-by-day travel itineraries tailored to a destination, total budget, trip duration, and traveler preferences (e.g., local cuisine, architecture, culture).
- **Structured JSON Output:** Uses **Groq Cloud (OpenAI-compatible API)** with strict JSON schema enforcement (`response_format: json_object`) mapped directly to strongly typed Java DTOs via Jackson.
- **Fault Tolerance & Resilience (Resilience4j):**
    - **Rate Limiter:** Protects the AI endpoint from spam and quota exhaustion (5 requests per minute).
    - **Circuit Breaker & Graceful Fallback:** Automatically detects external API timeouts, HTTP errors, or parsing failures and serves a standard fallback itinerary (`"fallback": true`) instead of failing with a `500` error.

### Travel Blog & Community Platform
- **User Authentication & Ownership:** Stateless registration and email-based login secured with **Spring Security** and **JWT (JSON Web Tokens)**, enforcing author-level ownership for editing and deleting posts.
- **Blog CRUD & Pagination:** Full create, read, update, and delete support for travel blogs (`/api/v1/blogs`) with built-in pagination and sorting (`size = 10`, sorted by `createdAt` descending).
- **Favorites System:** Authenticated users can bookmark and unbookmark favorite travel blogs (`POST` / `DELETE /api/v1/blogs/{id}/favorite`).

### 💻 Interactive React Frontend
- **Authentication & Protected Routes:** Seamless login and registration flows with client-side JWT token persistence, automatic `Authorization` header injection, and route guards for authenticated actions.
- **Blog Feed & Rich Post Management:** Paginated travel blog feed, detailed article views, intuitive forms for creating/editing travel stories, and one-click bookmarking for favorite posts.
- **Interactive AI Trip Planner UI:** Dynamic form allowing users to input destination, budget, duration, and custom preference tags, displaying the generated day-by-day itinerary with clear visual indicators when fallback mode is active.
- **Responsive Design & Feedback States:** Mobile-friendly layout with loading spinners, form validation, and user-friendly toast/error notifications.

---

## Tech Stack

### Backend
- **Core:** Java 17+, Spring Boot 3
- **Database & Migrations:** MySQL, Spring Data JPA, Hibernate, **Flyway**
- **Security:** Spring Security, JJWT (Bearer Token Authentication with `@AuthenticationPrincipal`)
- **HTTP Client & AI:** Spring `RestClient`, Groq Cloud API (`openai/gpt-oss-20b`)
- **Resilience:** Resilience4j (`CircuitBreaker`, `RateLimiter`)
- **Validation & Utilities:** Jakarta Bean Validation (`@Valid`), Lombok, Jackson `ObjectMapper`, Maven

### Frontend
- **Core:** React 18+, Vite, JavaScript (ES6+) / TypeScript
- **Routing & State:** React Router DOM, React Hooks (`useState`, `useEffect`, Context API)
- **HTTP & API Communication:** Axios / Fetch API with centralized JWT interceptor & environment-based API URLs
- **Styling & Icons:** Modern CSS / Tailwind CSS, responsive grid/flexbox layouts, Lucide / React Icons

---

## Architecture Overview

```text
├── backend/
│   ├── config/          # Security, JWT filter, and RestClient configurations
│   ├── controller/      # REST API endpoints (AuthController, BlogController, AiRecommendationController)
│   ├── dto/             # Validated Request/Response data transfer objects
│   ├── model/           # JPA entities (User, Blog, Favorites, etc.)
│   ├── repository/      # Spring Data JPA repositories
│   ├── security/        # CustomUserDetailsService & UserPrincipal
│   ├── service/         # BlogService & AiRecommendationService (Resilience4j)
│   └── resources/
│       ├── db/migration # Versioned Flyway SQL migration scripts
│       └── application.properties
└── frontend/
    ├── public/          # Static assets and favicon
    └── src/
        ├── api/         # Centralized HTTP client & endpoint service modules (auth, blogs, ai)
        ├── context/     # AuthContext for managing user session and JWT state
        ├── App.jsx      # Main router configuration and layout wrapper
        └── main.jsx     # Application entry point

Configuration & Environment VariablesSensitive credentials are externalized via environment variables to keep version control secure. Configure the following variables before starting the backend:VariableDescriptionExampleAI_API_KEYGroq Cloud API key for itinerary generationgsk_your_api_key_hereJWT_SECRETSecret key for signing JWT authentication tokensyour_256_bit_hex_secretDB_URLJDBC connection URL for your MySQL databasejdbc:mysql://localhost:3306/travel_blog_dbDB_USERNAMEDatabase usernamerootDB_PASSWORDDatabase passwordyour_db_passwordapplication.properties SetupPropertiesspring.application.name=travel-blog-app

# Database & Flyway
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.locations=classpath:db/migration
spring.jpa.hibernate.ddl-auto=validate

# Security
app.jwt.secret=${JWT_SECRET}
app.jwt.expiration-ms=86400000

# AI Integration (Groq)
ai.api.url=[https://api.groq.com/openai/v1/chat/completions](https://api.groq.com/openai/v1/chat/completions)
ai.api.key=${AI_API_KEY:dummy-key}
ai.api.model=openai/gpt-oss-20b
ai.api.timeout-seconds=20

# Resilience4j
resilience4j.ratelimiter.instances.aiRecommendationLimiter.limit-for-period=5
resilience4j.ratelimiter.instances.aiRecommendationLimiter.limit-refresh-period=1m
resilience4j.ratelimiter.instances.aiRecommendationLimiter.timeout-duration=0s

resilience4j.circuitbreaker.instances.aiRecommendationBreaker.sliding-window-size=6
resilience4j.circuitbreaker.instances.aiRecommendationBreaker.failure-rate-threshold=50
resilience4j.circuitbreaker.instances.aiRecommendationBreaker.wait-duration-in-open-state=30s
🚀 Getting Started1. Clone the RepositoryBashgit clone [https://github.com/Preslava-7/wanderlust-planner.git](https://github.com/Preslava-7/wanderlust-planner.git)
cd wanderlust-planner
2. Run the BackendNavigate to the backend directory:Bashcd backend
Set the required environment variable (AI_API_KEY) in your terminal or IntelliJ Run Configuration:Windows (PowerShell): $env:AI_API_KEY="gsk_your_api_key_here"Linux/macOS: export AI_API_KEY=gsk_your_api_key_hereStart the Spring Boot server (Flyway will automatically apply database migrations on startup):Windows (PowerShell): .\mvnw.cmd spring-boot:runLinux/macOS: ./mvnw spring-boot:runThe API will be available at http://localhost:8080.3. Run the FrontendNavigate to the frontend directory and install dependencies:Bashcd ../frontend
npm install
Create a .env file in the frontend/ root directory to point the client to your Spring Boot API:Фрагмент от кодVITE_API_BASE_URL=http://localhost:8080/api/v1
Start the Vite development server:Bashnpm run dev
The frontend will be accessible at http://localhost:5173.📡 Core API EndpointsAuthenticationMethodEndpointAccessDescriptionPOST/api/v1/auth/registerPublicRegister a new user accountPOST/api/v1/auth/loginPublicAuthenticate user and receive a JWT tokenTravel Blogs (/api/v1/blogs)MethodEndpointAccessDescriptionGET/api/v1/blogsPublicRetrieve paginated blogs (default: 10 per page, sorted by createdAt DESC)GET/api/v1/blogs/{id}PublicRetrieve full details of a specific blog postPOST/api/v1/blogsAuthenticatedCreate a new travel blog postPUT/api/v1/blogs/{id}Authenticated (Author)Update an existing blog postDELETE/api/v1/blogs/{id}Authenticated (Author)Delete a blog postPOST/api/v1/blogs/{id}/favoriteAuthenticatedAdd a blog post to user favoritesDELETE/api/v1/blogs/{id}/favoriteAuthenticatedRemove a blog post from user favoritesAI Itinerary PlannerMethodEndpointAccessDescriptionPOST/api/v1/ai/recommendationAuthenticatedGenerate a personalized day-by-day AI travel itinerarySample AI Request (POST /api/v1/ai/recommendation)Headers: Authorization: Bearer <your_jwt_token>JSON{
  "destination": "Shanghai, China",
  "budget": "800 EUR",
  "duration": 3,
  "preferences": ["local food", "architecture"]
}
Sample AI Response (200 OK)JSON{
  "destination": "Shanghai, China",
  "budget": "800 EUR",
  "duration": 3,
  "summary": "A 3-day exploration of Shanghai's iconic skyline, historic neighborhoods, and authentic street food.",
  "fallback": false,
  "days": [
    {
      "day": 1,
      "theme": "The Bund & Old City",
      "morning": "Walk along The Bund and admire colonial-era architecture",
      "afternoon": "Explore Yu Garden and the surrounding bazaar",
      "evening": "Sample Xiaolongbao (soup dumplings) at a local eatery",
      "estimatedCost": "90 EUR"
    }
  ]
}