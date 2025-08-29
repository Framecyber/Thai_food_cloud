# 🍜 เว็บไซต์ร้านอาหารไทย

## แอปพลิเคชันเว็บสำหรับสั่งอาหารไทยแบบ Fullstack พัฒนาด้วย Angular frontend และ Spring Boot backend

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

<img width="1581" height="645" alt="image" src="https://github.com/user-attachments/assets/05be7d42-5f8e-4a15-a5f4-e8e7096cbf0c" />

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
├── frontend/                     # แอปพลิเคชัน Angular
│   ├── src/
│   │   ├── app/
│   │   │   ├── auth/             # ส่วนที่จัดการการเข้าสู่ระบบ/สมัครสมาชิก
│   │   │   │   ├── login/
│   │   │   │   ├── signup/
│   │   │   │   └── auth-routing.module.ts
│   │   │   ├── pages/            # หน้าเว็บหลักของแอปพลิเคชัน
│   │   │   ├── app-routing.module.ts
│   │   │   └── app.module.ts
│   │   ├── assets/
│   │   └── environments/
│   ├── angular.json
│   └── package.json
└── backend/                      # แอปพลิเคชัน Spring Boot
├── src/main/java/com/thaifoodweb
│   ├── controller/           # REST API endpoints
│   ├── service/              # Business logic
│   ├── repository/           # JPA repositories
│   ├── model/                # JPA entities and DTOs
│   ├── security/             # Security configuration (e.g., Spring Security)
│   └── exception/            # Custom exception handling
├── src/main/resources/
│   ├── application.properties
│   └── data.sql              # Optional: Initial data for the database
└── pom.xml
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
- **บัญชี AWS** (สำหรับ S3, RDS และ CloudFront)

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
npm start
```

Frontend จะพร้อมใช้งานที่: `http://localhost:4200`

# 🍜 ThaiFood API

RESTful API สำหรับจัดการเมนู ออเดอร์ และหมวดหมู่ในระบบร้านอาหารไทย  
รองรับ CRUD และค้นหาข้อมูล

---

## 🔗 Base URLs

- **Menus API** → `http://localhost:8080/api/menus`
- **Categories API** → `http://localhost:8080/api/categories`
- **Orders API** → `http://localhost:8080/api/orders`

---

## 📂 Endpoints

### 📌 Menus (`/api/menus`)

| Method | Endpoint        | คำอธิบาย              |
|--------|-----------------|-----------------------|
| GET    | `/`             | ดึงข้อมูลเมนูทั้งหมด |
| GET    | `/{id}`         | ดึงข้อมูลเมนูตาม ID |
| POST   | `/`             | เพิ่มเมนูใหม่        |
| PUT    | `/{id}`         | แก้ไขเมนูตาม ID      |
| DELETE | `/{id}`         | ลบเมนูตาม ID         |

**ตัวอย่าง Request (POST /api/menus):**
```json
{
  "itemName": "ผัดไทย",
  "description": "ผัดไทยสูตรดั้งเดิม",
  "price": 120.00,
  "imageUrl": null,
  "isAvailable": true
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
| ไก่ทอดสมุนไพร | THB120.00 | ปีกไก่ทอดกรอบ คลุกเคล้าสมุนไพรไทยหอมๆ |
| ผัดไทยกุ้งสด | THB180.00 | เส้นจันท์เหนียวนุ่ม ผัดกับซอสสูตรพิเศษและกุ้งตัวโต | 
| ข้าวเหนียวมะม่วง | THB95.00| ข้าวเหนียวมูนหวานมัน ทานคู่กับมะม่วงน้ำดอกไม้ |
| ชาไทย | THB60.00 | ชาไทยเย็น หอมหวานชื่นใจ |
| หมูสะเต๊ะ | THB100.00| หมูนุ่มหมักเครื่องเทศย่างหอมๆ พร้อมน้ำจิ้มรสเด็ด |
| ไข่เจียวปู | THB10,000.00 | ไข่เจียวฟูๆ ใส่เนื้อปูเต็มคำ จากเจ้แบงค์ในตำนาน |

## 📝 ไลเซนส์

โปรเจคนี้อยู่ภายใต้ MIT License - ดูไฟล์ [LICENSE](LICENSE) สำหรับรายละเอียด


