# 🍜 เว็บไซต์ร้านอาหารไทย

แอปพลิเคชันเว็บสำหรับสั่งอาหารไทยแบบ Fullstack พัฒนาด้วย Angular frontend และ Spring Boot backend

[![CI/CD Pipeline](https://github.com/yourusername/thai-food-web/actions/workflows/deploy.yml/badge.svg)](https://github.com/yourusername/thai-food-web/actions/workflows/deploy.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

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

## 🐳 การ Deploy ด้วย Docker

### ใช้ Docker Compose

```bash
docker-compose up -d
```

### สร้าง Docker แบบ Manual

```bash
# Build แอปพลิเคชัน
docker build -t thai-food-app .

# รัน Container
docker run -d -p 8080:8080 --name thai-food-app thai-food-app
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

## 🔄 CI/CD Pipeline

โปรเจคมี CI/CD อัตโนมัติด้วย GitHub Actions:

1. **Build**: คอมไพล์ Angular frontend และ Spring Boot backend
2. **Test**: รันการทดสอบ unit tests และ integration tests
3. **Docker**: สร้างและ push Docker image ไป ECR
4. **Deploy**: Deploy ไป EC2 หรือ ECS cluster
5. **Load Test**: รันการทดสอบประสิทธิภาพด้วย k6

### GitHub Secrets ที่จำเป็น

```bash
AWS_ACCESS_KEY_ID=your_aws_access_key
AWS_SECRET_ACCESS_KEY=your_aws_secret_key
EC2_HOST=your_ec2_public_ip
EC2_SSH_KEY=your_private_key_content
```

## 🧪 Load Testing

รัน k6 load tests เพื่อให้มั่นใจในประสิทธิภาพของแอปพลิเคชัน:

```bash
# ติดตั้ง k6
brew install k6  # macOS
# หรือ
sudo apt install k6  # Ubuntu

# รัน load test
k6 run k6/load-test.js
```

**การกำหนดค่า Load Test:**
- 20 virtual users
- ระยะเวลา 30 วินาที
- เทสต์ endpoint `/api/menu`

## 🔧 การแก้ไขปัญหา

### ปัญหาที่พบบ่อย

| ปัญหา | วิธีแก้ไข |
|-------|----------|
| **CORS ถูกบล็อค** | ตรวจสอบ `CorsConfig.java` ใน backend |
| **เชื่อมต่อฐานข้อมูลไม่ได้** | ตรวจสอบว่า RDS security group อนุญาต port 3306 |
| **ไม่พบฐานข้อมูล** | สร้างฐานข้อมูล `thai_food` ใน RDS ด้วยตนเอง |
| **Angular เกิด 404 error** | ตรวจสอบว่า `environment.ts` ชี้ไป backend API ที่ถูกต้อง |
| **อัปโหลด S3 ไม่สำเร็จ** | ตรวจสอบ AWS credentials และสิทธิ์ของ S3 bucket |

### คำสั่งสำหรับ Debug

```bash
# ตรวจสอบ backend logs
./mvnw spring-boot:run --debug

# ตรวจสอบ Angular build
ng build --verbose

# ทดสอบ API endpoints
curl http://localhost:8080/api/menu

# ตรวจสอบ Docker container logs
docker logs thai-food-app
```

## 🤝 การมีส่วนร่วม

1. Fork repository นี้
2. สร้าง feature branch (`git checkout -b feature/amazing-feature`)
3. Commit การเปลี่ยนแปลง (`git commit -m 'Add some amazing feature'`)
4. Push ไป branch (`git push origin feature/amazing-feature`)
5. เปิด Pull Request

## 📋 ตัวอย่างเมนูอาหารไทย

| ชื่อเมนู | ราคา | คำอธิบาย |
|---------|------|----------|
| ผัดไทย | ฿120 | เส้นจันท์ผัดกุ้งสด รสชาติดั้งเดิม |
| แกงเขียวหวานไก่ | ฿95 | แกงเขียวหวานไก่สูตรโบราณ |
| ส้มตำไทย | ฿80 | ส้มตำใส่ถั่วฝักยาว มะเขือเทศ |
| ต้มยำกุ้ง | ฿150 | ต้มยำกุ้งน้ำใส รสเปรี้ยวจี๊ด |
| ข้าวผัดกุ้ง | ฿110 | ข้าวผัดกุ้งสับปะรด |
| มะม่วงข้าวเหนียว | ฿85 | ขนมหวานไทยคลาสสิค |

## 📝 ไลเซนส์

โปรเจคนี้อยู่ภายใต้ MIT License - ดูไฟล์ [LICENSE](LICENSE) สำหรับรายละเอียด

## 📞 ติดต่อสนับสนุน

สำหรับการสนับสนุน ติดต่อ your-email@example.com หรือสร้าง issue ใน repository นี้

## 🌟 Contributors

ขอบคุณผู้ที่มีส่วนร่วมในโปรเจคนี้:

- [@yourusername](https://github.com/yourusername) - ผู้พัฒนาหลัก
- [@contributor1](https://github.com/contributor1) - Frontend Developer
- [@contributor2](https://github.com/contributor2) - Backend Developer

## 📊 สถิติการใช้งาน

- ⭐ Stars: ![GitHub Repo stars](https://img.shields.io/github/stars/yourusername/thai-food-web)
- 🍴 Forks: ![GitHub forks](https://img.shields.io/github/forks/yourusername/thai-food-web)
- 🐛 Issues: ![GitHub issues](https://img.shields.io/github/issues/yourusername/thai-food-web)
- 📦 Version: ![GitHub package.json version](https://img.shields.io/github/package-json/v/yourusername/thai-food-web)

---

**สร้างด้วย ❤️ สำหรับคนรักอาหารไทย**

*อย่าลืม Star ⭐ ถ้าคุณชอบโปรเจคนี้นะครับ!*
