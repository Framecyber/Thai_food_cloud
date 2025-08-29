# 🍜 Thai Food Shop Web

A fullstack Thai food ordering web application built with Angular frontend and Spring Boot backend.

[![CI/CD Pipeline](https://github.com/yourusername/thai-food-web/actions/workflows/deploy.yml/badge.svg)](https://github.com/yourusername/thai-food-web/actions/workflows/deploy.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## ✨ Features

- ✅ View Thai food menu items with beautiful UI
- ✅ Search menu by name with real-time filtering
- ✅ Full CRUD operations (Add/Edit/Delete) for menu items
- ✅ Image upload and storage on AWS S3 with CloudFront CDN
- ✅ Responsive design for mobile and desktop
- ✅ RESTful API with Spring Boot backend
- ✅ MySQL database with AWS RDS hosting
- ✅ CI/CD pipeline with GitHub Actions
- ✅ Load testing with k6

## 🏗️ System Architecture

```
Frontend (Angular) ←→ Backend (Spring Boot) ←→ Database (MySQL/RDS)
                              ↓
                         AWS S3 + CloudFront
```

**Tech Stack:**
- **Frontend**: Angular (TypeScript, RxJS, HttpClient)
- **Backend**: Spring Boot (Java 21, Spring Web, Spring Data JPA)
- **Database**: MySQL (AWS RDS)
- **Storage**: AWS S3 for food images
- **CDN**: CloudFront for global image distribution
- **Deployment**: EC2 Auto Scaling Group or ECS Cluster

## 📂 Project Structure

```
thai-food-web/
├── frontend/                 # Angular application
│   ├── src/
│   │   ├── app/
│   │   ├── assets/
│   │   └── environments/
│   ├── angular.json
│   └── package.json
├── backend/                  # Spring Boot application
│   ├── src/main/java/
│   │   ├── controller/       # REST controllers
│   │   ├── service/         # Business logic
│   │   ├── repository/      # JPA repositories
│   │   ├── model/           # Entity classes
│   │   ├── dto/             # Data transfer objects
│   │   └── exception/       # Error handling
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
├── k6/                      # Load testing scripts
│   └── load-test.js
├── .github/workflows/       # CI/CD pipeline
│   └── deploy.yml
├── docker-compose.yml
├── Dockerfile
└── README.md
```

## 🚀 Quick Start

### Prerequisites

- **Java 21** or higher
- **Node.js 18+** and npm
- **MySQL 8.0+** or AWS RDS instance
- **AWS Account** (for S3 and CloudFront)

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/thai-food-web.git
cd thai-food-web
```

### 2. Backend Setup (Spring Boot)

1. **Configure Database**

Create `backend/src/main/resources/application.properties`:

```properties
server.port=8080
spring.datasource.url=jdbc:mysql://localhost:3306/thai_food
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# AWS S3 Configuration (Optional)
aws.s3.bucket=your-s3-bucket-name
aws.s3.region=ap-southeast-1
aws.cloudfront.domain=your-cloudfront-domain.cloudfront.net
```

2. **Run the Backend**

```bash
cd backend
./mvnw clean package -DskipTests
./mvnw spring-boot:run
```

Backend API will be available at: `http://localhost:8080/api/menu`

### 3. Frontend Setup (Angular)

1. **Install Dependencies**

```bash
cd frontend
npm install
```

2. **Configure Environment**

Update `frontend/src/environments/environment.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

3. **Run the Frontend**

```bash
ng serve -o
```

Frontend will be available at: `http://localhost:4200`

## 📡 API Documentation

### Base URL
```
http://localhost:8080/api/menu
```

### Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/` | Get all menu items |
| `GET` | `/{id}` | Get menu item by ID |
| `GET` | `/search?q={query}` | Search menu by name |
| `POST` | `/` | Add new menu item |
| `PUT` | `/{id}` | Update menu item |
| `DELETE` | `/{id}` | Delete menu item |

### Example Request/Response

**POST** `/api/menu`

```json
{
  "itemName": "Pad Thai",
  "description": "ผัดไทยกุ้งสด Traditional Thai stir-fried noodles",
  "price": 120.00,
  "imageUrl": null,
  "isAvailable": true
}
```

**Response:**

```json
{
  "id": 1,
  "itemName": "Pad Thai",
  "description": "ผัดไทยกุ้งสด Traditional Thai stir-fried noodles",
  "price": 120.00,
  "imageUrl": "https://your-cloudfront-domain.cloudfront.net/images/pad-thai.jpg",
  "isAvailable": true,
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

## 🐳 Docker Deployment

### Using Docker Compose

```bash
docker-compose up -d
```

### Manual Docker Build

```bash
# Build the application
docker build -t thai-food-app .

# Run the container
docker run -d -p 8080:8080 --name thai-food-app thai-food-app
```

## ☁️ AWS Deployment

### 1. Database (RDS)

1. Create MySQL RDS instance
2. Update security group to allow port 3306
3. Create database: `CREATE DATABASE thai_food;`

### 2. Storage (S3 + CloudFront)

1. Create S3 bucket for food images
2. Set up CloudFront distribution
3. Configure CORS policy for S3 bucket

### 3. Application (EC2/ECS)

**Option A: EC2 with Auto Scaling**
```bash
# Deploy using GitHub Actions (see .github/workflows/deploy.yml)
```

**Option B: ECS with Fargate**
```bash
# Push to ECR and deploy via ECS service
```

## 🔄 CI/CD Pipeline

The project includes automated CI/CD with GitHub Actions:

1. **Build**: Compile Angular frontend and Spring Boot backend
2. **Test**: Run unit tests and integration tests
3. **Docker**: Build and push Docker image to ECR
4. **Deploy**: Deploy to EC2 or ECS cluster
5. **Load Test**: Run k6 performance tests

### Required GitHub Secrets

```bash
AWS_ACCESS_KEY_ID=your_aws_access_key
AWS_SECRET_ACCESS_KEY=your_aws_secret_key
EC2_HOST=your_ec2_public_ip
EC2_SSH_KEY=your_private_key_content
```

## 🧪 Load Testing

Run k6 load tests to ensure application performance:

```bash
# Install k6
brew install k6  # macOS
# or
sudo apt install k6  # Ubuntu

# Run load test
k6 run k6/load-test.js
```

**Load Test Configuration:**
- 20 virtual users
- 30 seconds duration
- Targets `/api/menu` endpoint

## 🔧 Troubleshooting

### Common Issues

| Problem | Solution |
|---------|----------|
| **CORS blocked** | Check `CorsConfig.java` in backend |
| **Database connection error** | Ensure RDS security group allows port 3306 |
| **Unknown database error** | Manually create `thai_food` database in RDS |
| **Angular 404 errors** | Verify `environment.ts` points to correct backend API |
| **S3 upload fails** | Check AWS credentials and S3 bucket permissions |

### Debug Commands

```bash
# Check backend logs
./mvnw spring-boot:run --debug

# Check Angular build
ng build --verbose

# Test API endpoints
curl http://localhost:8080/api/menu

# Check Docker container logs
docker logs thai-food-app
```

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support, email your-email@example.com or create an issue in this repository.

---

**Made with ❤️ for Thai food lovers**
