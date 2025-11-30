# Plateplanner
This is a comprehensive, full-stack application built on a Microservices Architecture. PlatePlanner allows users to manage their recipes (CRUD) and leverages the Google Gemini API to provide smart, personalized feedback and analysis on recipes, including a premium tier for image-to-recipe intelligence.

The entire application stack is secured using Keycloak (OAuth 2.0) and is fully Dockerized for streamlined deployment.

### 🔗 Deployed Link
👉 [https://plateplanner-okec.onrender.com/](https://plateplanner-okec.onrender.com/)

<img width="1919" height="865" alt="Screenshot 2025-11-30 205535" src="https://github.com/user-attachments/assets/fe6afb3f-d725-48d8-b95e-debbb10650c5" />

<img width="1919" height="860" alt="Screenshot 2025-11-30 204931" src="https://github.com/user-attachments/assets/d8bdffff-3f39-4ca4-8699-78b7dc6f7a49" />

## 🚀  Key Technologies & Stack
- Backend: Java 21, Spring Boot, Spring Cloud (Eureka, Gateway, Security)
- Database: MongoDB, MongoDB GridFS (for premium image storage)
- Frontend: ReactJS (with MUI/Material-UI)
- Authentication: Keycloak (OAuth 2.0, JWT)
- AI: Google Gemini API (for recipe analysis)
- Payments: Razorpay (Integrated via Webhooks)
- DevOps/Deployment: Docker, Render

## ✨  Features
- Full Recipe Management (CRUD): Users can add, view, update, and delete their recipes via secure REST endpoints.
- Personalized AI Recommendations: On-demand analysis for any stored recipe, providing nutritional advice, complexity assessment, and improvement suggestions, powered by the Gemini API.
- Visual AI Intelligence (Premium): Premium feature that allows users to upload recipe photos. The dedicated service analyzes the image using Gemini and extracts key details and recommendations.
- Premium Monetization: Integrated Razorpay for seamless payment processing, unlocking premium features via a successful webhook confirmation.
- Microservices Architecture: Uses 7+ services to ensure high scalability, modularity, and independent service deployment.

## 📐 Microservices Architecture
The application is structured around several independent services coordinated by the Eureka Server and accessed via the API Gateway.

| Service Name              | Description           | Key Functions                                                                            |
| ------------------------- | --------------------- | ---------------------------------------------------------------------------------------- |
| EurekaServer              | Service Discovery     | Registers and tracks the location of all microservices.                                  |
| ApiGateway                | Routing & Security    | Routes all incoming requests to the correct service and performs initial JWT validation. |
| UserService               | User Profiles         | Manages user data in MongoDB (including the critical premium status).                    |
| RecipeService             | Core CRUD             | Handles all recipe creation, viewing, updating, and deleting.                            |
| AIService                 | Text-to-AI            | Fetches recipe data and calls the Gemini API for text-based recommendations.             |
| PaymentService            | Transactions          | Handles Razorpay order creation and processes webhooks to update user status.            |
| RecipeIntelligenceService | Image-to-AI           | Handles image upload (GridFS) and calls the multimodal Gemini API for visual analysis.   |

## 📡 API Endpoints (via API Gateway)

| Method  | Endpoint                             | Microservice     | Description                                                 | 
| ------- | ------------------------------------ | ---------------- | ----------------------------------------------------------- |
| GET     | /api/users/me                        | UserService      | Get current user's details (including premium status).      |
| POST    | /api/recipes/add                     | RecipeService    | Create a new recipe.                                        |
| GET     | /api/recipes/all                     | RecipeService    | Get all recipes for the authenticated user.                 |
| POST    | /api/recommendations/generate/{id}   | AIService        | Generate AI analysis for a specific recipe ID.              |
| POST    | /api/payments/create-order           | PaymentService   | Initiate a Razorpay payment order.                          |
| POST    | /api/ri/premium/images               | RI Service       | Upload a recipe image (Premium feature).                    |
| POST    | /api/ri/premium/recommend/{imageId}  | RI Service       | Generate AI analysis based on an uploaded image.            |

## 📦  Installation & Setup

Prerequisites
- Docker / Docker Compose
- Java SDK (21+)
- MongoDB Instance
- Keycloak Server (or a local instance via Docker)
- Google Gemini API Key
- Razorpay API Keys

Backend (Microservices)
1. Clone the Repository:
```bash
git clone https://github.com/SanskrutiNijai/plateplanner.git
cd plateplanner
```

2. Configure Environment Variables:
Update the ```.env``` or ```application.yml``` files for each microservice with your Keycloak, MongoDB, Gemini, and Razorpay credentials.

3. Run with Docker Compose (Recommended):
```docker-compose up --build```
This command will build and run all 7 services and Keycloak/MongoDB.

Frontend (React)

1. Install Dependencies:
```
cd frontend
npm install
```

2. Configure ```.env```:
Update ```VITE_RECIPE_API```, ```VITE_AI_API```, ```VITE_AUTH_CLIENT_ID```, etc., to point to your API Gateway and Keycloak endpoints.

3. Run Development Server:
```
npm run dev
```
Visit ```http://localhost:5173``` (or the port specified by Vite).
