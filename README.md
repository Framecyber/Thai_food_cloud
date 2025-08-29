# Thai_food_cloud

🍜 Thai Food Shop Web

Fullstack Thai food ordering web application

Frontend: Angular

Backend: Spring Boot

Database: MySQL (AWS RDS)

Storage: AWS S3 + CloudFront CDN

✨ Features

✅ View Thai food menu items

✅ Search menu by name

✅ CRUD (Add / Edit / Delete) menu items

✅ Store food images on AWS S3 (served via CloudFront CDN)

✅ Backend with Spring Boot + MySQL (AWS RDS)

✅ Frontend with Angular

🏗 System Architecture

Frontend → Angular (TypeScript, RxJS, HttpClient)

Backend → Spring Boot (Java 21, Spring Web, Spring Data JPA)

Database → MySQL (AWS RDS)

Storage → AWS S3 for food images

CDN → CloudFront for global image distribution

Deployment Options → EC2 Auto Scaling Group or ECS Cluster

📂 Project Structure
/ (repo root)
├── frontend/        # Angular app
└── backend/         # Spring Boot app
    ├── controller/  # REST controllers
    ├── service/     # Business logic
    ├── repository/  # JPA repositories
    ├── model/       # Entities
    ├── dto/         # Data transfer objects
    ├── exception/   # Error handling
    └── resources/application.properties

⚙️ Backend Configuration

📄 backend/src/main/resources/application.properties

server.port=8080
spring.datasource.url=jdbc:mysql://<RDS_ENDPOINT>:3306/thai_food
spring.datasource.username=admin
spring.datasource.password=YOUR_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

🚀 Running Locally
▶️ Backend (Spring Boot)
cd backend
./mvnw clean package -DskipTests
./mvnw spring-boot:run


API → http://localhost:8080/api/menu

▶️ Frontend (Angular)
cd frontend
npm install
ng serve -o


App → http://localhost:4200

📡 API Endpoints

Base URL: http://localhost:8080/api/menu

Method	Endpoint	Description
GET	/	Get all menu items
GET	/{id}	Get menu item by ID
GET	/search?q=pad	Search menu by name
POST	/	Add new menu item
PUT	/{id}	Update menu item
DELETE	/{id}	Delete menu item

Example POST JSON

{
  "itemName": "Green Curry",
  "description": "แกงเขียวหวานไก่",
  "price": 95.00,
  "imageUrl": null,
  "isAvailable": true
}

🛠 Tech Stack

🎨 Frontend: Angular (TypeScript, RxJS, HttpClient)

⚡ Backend: Java 21, Spring Boot 3.5.x, JPA, Hibernate

🗄 Database: MySQL (AWS RDS)

☁️ Cloud: AWS S3 (image storage), CloudFront (CDN)

🔄 CI/CD: GitHub Actions, Docker, Kubernetes/EC2

🚢 Deployment

Frontend → ng build → Deploy to S3 + CloudFront or Nginx

Backend → Build Docker image → Deploy to Kubernetes / ECS / EC2 / Elastic Beanstalk

Database → AWS RDS (Multi-AZ recommended for production)

⚡️ CI/CD with GitHub Actions

📄 .github/workflows/deploy.yml

name: CI-CD Deploy ThaiFood Web

on:
  push:
    branches: [ "main" ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      # Build Angular
      - name: Build Angular Frontend
        working-directory: ./frontend
        run: |
          npm install
          npm run build --prod

      # Build Spring Boot
      - name: Build Spring Boot Backend
        working-directory: ./backend
        run: |
          ./mvnw clean package -DskipTests

      # Build Docker Image
      - name: Build Docker image
        run: |
          docker build -t thai-food-app .

      # Push to AWS ECR
      - name: Configure AWS Credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: ap-southeast-1

      - name: Login to Amazon ECR
        run: |
          aws ecr get-login-password --region ap-southeast-1 | docker login --username AWS --password-stdin <your_account_id>.dkr.ecr.ap-southeast-1.amazonaws.com

      - name: Push Docker image to ECR
        run: |
          docker tag thai-food-app:latest <your_account_id>.dkr.ecr.ap-southeast-1.amazonaws.com/thai-food-app:latest
          docker push <your_account_id>.dkr.ecr.ap-southeast-1.amazonaws.com/thai-food-app:latest

  deploy:
    runs-on: ubuntu-latest
    needs: build
    steps:
      - name: Deploy to EC2
        uses: appleboy/ssh-action@v0.1.10
        with:
          host: ${{ secrets.EC2_HOST }}
          username: ubuntu
          key: ${{ secrets.EC2_SSH_KEY }}
          script: |
            docker pull <your_account_id>.dkr.ecr.ap-southeast-1.amazonaws.com/thai-food-app:latest
            docker stop thai-food-app || true
            docker rm thai-food-app || true
            docker run -d -p 80:8080 --name thai-food-app <your_account_id>.dkr.ecr.ap-southeast-1.amazonaws.com/thai-food-app:latest

  load-test:
    runs-on: ubuntu-latest
    needs: deploy
    steps:
      - uses: actions/checkout@v3
      - name: Run k6 Load Test
        uses: grafana/k6-action@v0.2.0
        with:
          filename: k6/load-test.js

🧪 Load Testing (k6)

📄 k6/load-test.js

import http from "k6/http";
import { check, sleep } from "k6";

export const options = {
  vus: 20,           // 20 virtual users
  duration: "30s",   // run for 30 seconds
};

export default function () {
  let res = http.get("http://your-ec2-domain-or-ip/api/menu");
  check(res, { "status was 200": (r) => r.status === 200 });
  sleep(1);
}


▶️ Run locally:

k6 run k6/load-test.js

🔧 Troubleshooting

🚫 CORS blocked → check CorsConfig.java

🛑 DB connection error → ensure RDS security group allows port 3306

❓ Unknown database → manually create thai_food DB in RDS

⚠️ Angular 404 → verify environment.ts points to correct backend API

Unknown database → manually create thai_food DB in RDS

Angular 404 → verify environment.ts points to correct backend API
