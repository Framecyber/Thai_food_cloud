# 🍜 เว็บไซต์ร้านอาหารไทย

แอปพลิเคชันเว็บสำหรับสั่งอาหารไทยแบบ Fullstack พัฒนาด้วย Angular frontend และ Spring Boot backend

http://bucketfontporpear.s3-website-us-east-1.amazonaws.com/auth/login

<img width="1581" height="645" alt="image" src="https://github.com/user-attachments/assets/748ab23c-c50e-4df5-a37b-a3c14da2cef3" />

## ✨ ฟีเจอร์หลัก

- ✅ ดูเมนูอาหารไทยด้วย UI ที่สวยงาม
- ✅ ค้นหาเมนูด้วยชื่อแบบ Real-time
- ✅ จัดการเมนูแบบเต็มรูปแบบ (เพิ่ม/แก้ไข/ลบ)
- ✅ อัปโหลดรูปภาพและเก็บบน AWS S3 พร้อม CloudFront CDN
- ✅ รองรับการใช้งานบนมือถือและคอมพิวเตอร์
- ✅ RESTful API ด้วย Spring Boot backend
- ✅ ฐานข้อมูล MySQL บน AWS RDS
- ✅ CI/CD pipeline ด้วย GitHub Actions
- ✅ Load testing ด้วย k6

## 🏗️ สถาปัตยกรรมระบบ

```
Frontend (Angular) ←→ Backend (Spring Boot) ←→ Database (MySQL/RDS)
                              ↓
                         AWS S3 + CloudFront
```

**เทคโนโลยีที่ใช้:**
- **Frontend**: Angular (TypeScript, RxJS, HttpClient)
- **Backend**: Spring Boot (Java 21, Spring Web, Spring Data JPA)
- **Database**: MySQL (AWS RDS)
- **Storage**: AWS S3 สำหรับเก็บรูปภาพอาหาร
- **CDN**: CloudFront สำหรับการกระจายรูปภาพทั่วโลก
- **Deployment**: EC2 Auto Scaling Group หรือ ECS Cluster

## 📂 โครงสร้างโปรเจค

```
thai-food-web/
├── frontend/                 # แอปพลิเคชัน Angular
│   ├── src/
│   │   ├── app/
│   │   ├── assets/
│   │   └── environments/
│   ├── angular.json
│   └── package.json
├── backend/                  # แอปพลิเคชัน Spring Boot
│   ├── src/main/java/
│   │   ├── controller/       # REST controllers
│   │   ├── service/         # Business logic
│   │   ├── repository/      # JPA repositories
│   │   ├── model/           # Entity classes
│   │   ├── dto/             # Data transfer objects
│   │   └── exception/       # การจัดการ Error
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
├── k6/                      # สคริปต์ Load testing
│   └── load-test.js
├── .github/workflows/       # CI/CD pipeline
│   └── deploy.yml
├── docker-compose.yml
├── Dockerfile
└── README.md
```

## 🚀 เริ่มต้นใช้งาน

### ความต้องการเบื้องต้น

- **Java 21** ขึ้นไป
- **Node.js 18+** และ npm
- **MySQL 8.0+** หรือ AWS RDS instance
- **บัญชี AWS** (สำหรับ S3 และ CloudFront)

### 1. Clone Repository

```bash
git clone https://github.com/yourusername/thai-food-web.git
cd thai-food-web
```

### 2. ติดตั้ง Backend (Spring Boot)

1. **กำหนดค่าฐานข้อมูล**

สร้างไฟล์ `backend/src/main/resources/application.properties`:

```properties
server.port=8080
spring.datasource.url=jdbc:mysql://localhost:3306/thai_food
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# การกำหนดค่า AWS S3 (ตัวเลือก)
aws.s3.bucket=your-s3-bucket-name
aws.s3.region=ap-southeast-1
aws.cloudfront.domain=your-cloudfront-domain.cloudfront.net
```

2. **รัน Backend**

```bash
cd backend
./mvnw clean package -DskipTests
./mvnw spring-boot:run
```

Backend API จะพร้อมใช้งานที่: `http://localhost:8080/api/menu`

### 3. ติดตั้ง Frontend (Angular)

1. **ติดตั้ง Dependencies**

```bash
cd frontend
npm install
```

2. **กำหนดค่า Environment**

อัปเดทไฟล์ `frontend/src/environments/environment.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

3. **รัน Frontend**

```bash
ng serve -o
```

Frontend จะพร้อมใช้งานที่: `http://localhost:4200`

## 📡 เอกสาร API

### Base URL
```
http://localhost:8080/api/menu
```

### Endpoints

| Method | Endpoint | คำอธิบาย |
|--------|----------|----------|
| `GET` | `/` | ดึงข้อมูลเมนูทั้งหมด |
| `GET` | `/{id}` | ดึงข้อมูลเมนูตาม ID |
| `GET` | `/search?q={query}` | ค้นหาเมนูตามชื่อ |
| `POST` | `/` | เพิ่มเมนูใหม่ |
| `PUT` | `/{id}` | แก้ไขเมนู |
| `DELETE` | `/{id}` | ลบเมนู |

### ตัวอย่าง Request/Response

**POST** `/api/menu`

```json
{
  "itemName": "ผัดไทย",
  "description": "ผัดไทยกุ้งสด เส้นจันท์ผัดตามแบบดั้งเดิม",
  "price": 120.00,
  "imageUrl": null,
  "isAvailable": true
}
```

**Response:**

```json
{
  "id": 1,
  "itemName": "ผัดไทย",
  "description": "ผัดไทยกุ้งสด เส้นจันท์ผัดตามแบบดั้งเดิม",
  "price": 120.00,
  "imageUrl": "https://your-cloudfront-domain.cloudfront.net/images/pad-thai.jpg",
  "isAvailable": true,
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}

```
## ☁️ การ Deploy บน AWS

### 1. ฐานข้อมูล (RDS)

1. สร้าง MySQL RDS instance
2. อัปเดท security group ให้อนุญาต port 3306
3. สร้างฐานข้อมูล: `CREATE DATABASE thai_food;`

### 2. Storage (S3 + CloudFront)

1. สร้าง S3 bucket สำหรับเก็บรูปภาพอาหาร
2. ตั้งค่า CloudFront distribution
3. กำหนดค่า CORS policy สำหรับ S3 bucket

### 3. แอปพลิเคชัน (EC2/ECS)

**ตัวเลือก A: EC2 พร้อม Auto Scaling**
```bash
# Deploy ผ่าน GitHub Actions (ดู .github/workflows/deploy.yml)
```

**ตัวเลือก B: ECS พร้อม Fargate**
```bash
# Push ไป ECR และ deploy ผ่าน ECS service
```

🔄 CI/CD Pipeline with GitHub Actions
โปรเจคนี้ใช้ GitHub Actions สำหรับ CI/CD pipeline ที่ครบวงจร รองรับการ deploy ทั้ง EC2, ECS และ Elastic Beanstalk
Pipeline Workflow
Code Push → Build → Test → Docker Build → Deploy → Load Test
ไฟล์ GitHub Actions หลัก
📄 .github/workflows/ci-cd.yml
yamlname: CI/CD Pipeline - Thai Food Web

on:
  push:
    branches: [ "main", "develop" ]
  pull_request:
    branches: [ "main" ]

env:
  AWS_REGION: ap-southeast-1
  ECR_REPOSITORY: thai-food-web
  EB_APPLICATION_NAME: thai-food-web
  EB_ENVIRONMENT_NAME: thai-food-web-prod

jobs:
  # 1. Build และ Test
  build-and-test:
    name: Build and Test
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      # Java Setup
      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven

      # Node.js Setup  
      - name: Set up Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '18'
          cache: 'npm'
          cache-dependency-path: frontend/package-lock.json

      # Build Frontend
      - name: Build Angular Frontend
        working-directory: ./frontend
        run: |
          npm ci
          npm run test -- --watch=false --browsers=ChromeHeadless
          npm run build --prod

      # Build Backend
      - name: Build Spring Boot Backend
        working-directory: ./backend
        run: |
          ./mvnw clean compile
          ./mvnw test
          ./mvnw package -DskipTests

      # Upload Artifacts
      - name: Upload Backend JAR
        uses: actions/upload-artifact@v3
        with:
          name: backend-jar
          path: backend/target/*.jar

      - name: Upload Frontend Dist
        uses: actions/upload-artifact@v3
        with:
          name: frontend-dist
          path: frontend/dist/

  # 2. Security Scan
  security-scan:
    name: Security Scan
    runs-on: ubuntu-latest
    needs: build-and-test

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Run Snyk Security Scan
        uses: snyk/actions/maven@master
        env:
          SNYK_TOKEN: ${{ secrets.SNYK_TOKEN }}
        with:
          args: --severity-threshold=medium
          command: test

  # 3. Build Docker Image
  docker-build:
    name: Build Docker Image
    runs-on: ubuntu-latest
    needs: [build-and-test, security-scan]
    if: github.ref == 'refs/heads/main'

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Download Build Artifacts
        uses: actions/download-artifact@v3
        with:
          name: backend-jar
          path: backend/target/

      - name: Download Frontend Artifacts
        uses: actions/download-artifact@v3
        with:
          name: frontend-dist
          path: frontend/dist/

      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: ${{ env.AWS_REGION }}

      - name: Login to Amazon ECR
        id: login-ecr
        uses: aws-actions/amazon-ecr-login@v2

      - name: Build and Push Docker image to ECR
        env:
          ECR_REGISTRY: ${{ steps.login-ecr.outputs.registry }}
          IMAGE_TAG: ${{ github.sha }}
        run: |
          # Build Docker image
          docker build -t $ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG .
          docker build -t $ECR_REGISTRY/$ECR_REPOSITORY:latest .
          
          # Push images
          docker push $ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG
          docker push $ECR_REGISTRY/$ECR_REPOSITORY:latest

  # 4. Deploy to Elastic Beanstalk
  deploy-eb:
    name: Deploy to Elastic Beanstalk
    runs-on: ubuntu-latest
    needs: docker-build
    if: github.ref == 'refs/heads/main'

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Download Backend JAR
        uses: actions/download-artifact@v3
        with:
          name: backend-jar
          path: backend/target/

      - name: Create Deployment Package
        run: |
          cp backend/target/*.jar application.jar
          zip -r deploy.zip application.jar .ebextensions/ .platform/

      - name: Deploy to Elastic Beanstalk
        uses: einaregilsson/beanstalk-deploy@v21
        with:
          aws_access_key: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws_secret_key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          application_name: ${{ env.EB_APPLICATION_NAME }}
          environment_name: ${{ env.EB_ENVIRONMENT_NAME }}
          version_label: v${{ github.sha }}
          region: ${{ env.AWS_REGION }}
          deployment_package: deploy.zip
          wait_for_deployment: true

  # 5. Alternative: Deploy to ECS
  deploy-ecs:
    name: Deploy to ECS (Alternative)
    runs-on: ubuntu-latest
    needs: docker-build
    if: github.ref == 'refs/heads/main' && github.event.inputs.deploy_target == 'ecs'

    steps:
      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: ${{ env.AWS_REGION }}

      - name: Deploy to ECS
        run: |
          # Update ECS service to use new image
          aws ecs update-service \
            --cluster thai-food-cluster \
            --service thai-food-service \
            --force-new-deployment

  # 6. Load Testing
  load-test:
    name: Load Testing with k6
    runs-on: ubuntu-latest
    needs: [deploy-eb]
    if: github.ref == 'refs/heads/main'

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Install k6
        run: |
          sudo gpg -k
          sudo gpg --no-default-keyring --keyring /usr/share/keyrings/k6-archive-keyring.gpg --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
          echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
          sudo apt-get update
          sudo apt-get install k6

      - name: Run Load Tests
        env:
          TARGET_URL: ${{ secrets.EB_ENVIRONMENT_URL || 'http://thai-food-web-prod.ap-southeast-1.elasticbeanstalk.com' }}
        run: |
          k6 run --env TARGET_URL=$TARGET_URL k6/load-test.js

      - name: Upload Load Test Results
        uses: actions/upload-artifact@v3
        if: always()
        with:
          name: load-test-results
          path: k6-results.json

  # 7. Notification
  notify:
    name: Send Notifications
    runs-on: ubuntu-latest
    needs: [deploy-eb, load-test]
    if: always()

    steps:
      - name: Send Slack Notification
        uses: 8398a7/action-slack@v3
        with:
          status: ${{ job.status }}
          channel: '#deployments'
          webhook_url: ${{ secrets.SLACK_WEBHOOK }}
          fields: repo,message,commit,author,action,eventName,ref,workflow
        if: always()

      - name: Send Line Notification (สำหรับทีมไทย)
        run: |
          curl -X POST https://notify-api.line.me/api/notify \
            -H "Authorization: Bearer ${{ secrets.LINE_TOKEN }}" \
            -F "message=🚀 Thai Food Web deployed successfully! 
            Environment: Production
            Commit: ${{ github.sha }}
            Status: ${{ job.status }}"
        if: success()
GitHub Secrets ที่จำเป็น
ตั้งค่า Secrets ใน GitHub Repository Settings:
bash# AWS Configuration
AWS_ACCESS_KEY_ID=AKIA...
AWS_SECRET_ACCESS_KEY=...
AWS_REGION=ap-southeast-1

# Elastic Beanstalk
EB_APPLICATION_NAME=thai-food-web
EB_ENVIRONMENT_NAME=thai-food-web-prod
EB_ENVIRONMENT_URL=http://thai-food-web-prod.ap-southeast-1.elasticbeanstalk.com

# Database
DB_HOST=your-rds-endpoint.amazonaws.com
DB_USERNAME=admin
DB_PASSWORD=your-secure-password

# Notifications
SLACK_WEBHOOK=https://hooks.slack.com/services/...
LINE_TOKEN=your-line-notify-token

# Security Scanning
SNYK_TOKEN=your-snyk-token
Manual Deployment Triggers
สร้างไฟล์ .github/workflows/manual-deploy.yml สำหรับ manual deployment:
yamlname: Manual Deployment

on:
  workflow_dispatch:
    inputs:
      environment:
        description: 'Environment to deploy'
        required: true
        default: 'staging'
        type: choice
        options:
          - staging
          - production
      deploy_target:
        description: 'Deployment target'
        required: true
        default: 'elastic-beanstalk'
        type: choice
        options:
          - elastic-beanstalk
          - ecs
          - ec2
      run_load_test:
        description: 'Run load tests after deployment'
        required: false
        default: true
        type: boolean

jobs:
  deploy:
    name: Manual Deployment
    runs-on: ubuntu-latest
    
    steps:
      - name: Echo Deployment Parameters
        run: |
          echo "Deploying to: ${{ github.event.inputs.environment }}"
          echo "Target: ${{ github.event.inputs.deploy_target }}"
          echo "Load test: ${{ github.event.inputs.run_load_test }}"
🧪 Load Testing
รัน k6 load tests เพื่อให้มั่นใจในประสิทธิภาพของแอปพลิเคชัน:
bash# ติดตั้ง k6
brew install k6  # macOS
# หรือ
sudo apt install k6  # Ubuntu

# รัน load test
k6 run k6/load-test.js
การกำหนดค่า Load Test:

20 virtual users
ระยะเวลา 30 วินาที
เทสต์ endpoint /api/menu

🔧 การแก้ไขปัญหา
ปัญหาที่พบบ่อย
ปัญหาวิธีแก้ไขCORS ถูกบล็อคตรวจสอบ CorsConfig.java ใน backendเชื่อมต่อฐานข้อมูลไม่ได้ตรวจสอบว่า RDS security group อนุญาต port 3306ไม่พบฐานข้อมูลสร้างฐานข้อมูล thai_food ใน RDS ด้วยตนเองAngular เกิด 404 errorตรวจสอบว่า environment.ts ชี้ไป backend API ที่ถูกต้องอัปโหลด S3 ไม่สำเร็จตรวจสอบ AWS credentials และสิทธิ์ของ S3 bucket
คำสั่งสำหรับ Debug
bash# ตรวจสอบ backend logs
./mvnw spring-boot:run --debug

# ตรวจสอบ Angular build
ng build --verbose

# ทดสอบ API endpoints
curl http://localhost:8080/api/menu

# ตรวจสอบ Docker container logs
docker logs thai-food-app
🤝 การมีส่วนร่วม

Fork repository นี้
สร้าง feature branch (git checkout -b feature/amazing-feature)
Commit การเปลี่ยนแปลง (git commit -m 'Add some amazing feature')
Push ไป branch (git push origin feature/amazing-feature)
เปิด Pull Request

📋 ตัวอย่างเมนูอาหารไทย
ชื่อเมนูราคาคำอธิบายผัดไทย฿120เส้นจันท์ผัดกุ้งสด รสชาติดั้งเดิมแกงเขียวหวานไก่฿95แกงเขียวหวานไก่สูตรโบราณส้มตำไทย฿80ส้มตำใส่ถั่วฝักยาว มะเขือเทศต้มยำกุ้ง฿150ต้มยำกุ้งน้ำใส รสเปรี้ยวจี๊ดข้าวผัดกุ้ง฿110ข้าวผัดกุ้งสับปะรดมะม่วงข้าวเหนียว฿85ขนมหวานไทยคลาสสิค
📝 ไลเซนส์
โปรเจคนี้อยู่ภายใต้ MIT License - ดูไฟล์ LICENSE สำหรับรายละเอียด
📞 ติดต่อสนับสนุน
สำหรับการสนับสนุน ติดต่อ your-email@example.com หรือสร้าง issue ใน repository นี้
🌟 Contributors
ขอบคุณผู้ที่มีส่วนร่วมในโปรเจคนี้:

@yourusername - ผู้พัฒนาหลัก
@contributor1 - Frontend Developer
@contributor2 - Backend Developer

📊 สถิติการใช้งาน

⭐ Stars: Show Image
🍴 Forks: Show Image
🐛 Issues: Show Image
📦 Version: Show Image


สร้างด้วย ❤️ สำหรับคนรักอาหารไทย
อย่าลืม Star ⭐ ถ้าคุณชอบโปรเจคนี้นะครับ!
