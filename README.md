# spring-redis-blog-api
**(Educational Project)** A high-performance Blog API built with Spring Boot and Redis. This project serves as a comprehensive guide on architecting a multilingual blogging system capable of handling medium-scale traffic while maintaining operational stability.

## 📖 Introduction
This project is a robust back-end implementation of a blogging service, engineered to balance performance and maintainability. By leveraging **Redis** for distributed caching, the system is optimized for read-heavy workloads common in content platforms.

This project also includes performance testing code. The test scenario benchmarks the system using real-world traffic distribution models. Running this test demonstrates that using a cache leads to higher performance, lower latency, and higher RPS.

## 🚀 Key Features

### ⚡ Performance & Scalability
* **Strategic Caching with Redis**: Mitigates database bottlenecks by caching frequent and computationally expensive requests, significantly improving response times and system throughput.
* **S3-based Media Management**: Decoupled media storage using AWS S3. This ensures a **stateless architecture**, allowing horizontal scaling of application servers without local file dependency.
* **Zipf-based Performance Testing**: Validated system resilience using **Zipfian distribution** (the 80/20 rule) to simulate realistic "hot-content" traffic patterns.

### 🛡️ Enterprise-Grade Cross-Cutting Concerns (AOP)
* **Declarative Validation**: Implements **Annotation-based Validation** using `@Valid` and custom constraints to decouple data integrity checks from business logic.
* **Global Exception Handling**: Centralized error management via `@ControllerAdvice` and AOP, ensuring consistent, user-friendly, and localized error responses across all modules.
* **Non-blocking Observability**: Implements a structured asynchronous logging strategy using **Log4j2** and AOP to track execution flows and performance metrics without impacting request latency.

### 🌐 UX & DX
* **Multilingual Support (i18n)**: Integrated Internationalization for system messages and exception handling, providing a localized experience for a global user base.
* **Automated API Documentation**: Powered by **Swagger (SpringDoc)** for real-time API exploration and testing, facilitating seamless front-end and back-end integration.

### 📈 Operational Reliability
* **Proactive Monitoring & Alerting**: An automated mechanism that detects critical system failures and dispatches immediate notifications to administrators to minimize downtime.

## 🛠 Tech Stack 

### Languages & Frameworks 
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) ![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) ![MyBatis](https://img.shields.io/badge/MyBatis-000000?style=for-the-badge&logo=mybatis&logoColor=white)

### Database, Cache & Storage 
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white) ![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white) ![AWS S3](https://img.shields.io/badge/AWS%20S3-569A31?style=for-the-badge&logo=amazons3&logoColor=white)

### Infrastructure & Tools
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white) ![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)

---

## 🧪 Performance Deep Dive: Zipf Distribution
To prove the system's resilience, we simulated traffic where the probability of accessing the $i$-th most popular post follows the Zipfian law:

$$P(i, N, s) = \frac{1/i^s}{\sum_{n=1}^{N} (1/n^s)}$$

By setting the exponent $s$ to mimic real-world content trends, we verified that the Redis cache successfully intercepted over **90% of requests** for popular content, keeping the PostgreSQL load minimal even during high-traffic spikes.


## ⚙️ Installation



To get started with this project, follow the steps below:



**1. Clone the repository:**

   ```bash
   git clone https://github.com/yourusername/spring-redis-blog-api.git

   cd spring-redis-blog-api
   ```


**2. make `.env` file at classpath**

   ```bash
   SLACK_BOT_KEY={your-key}

   SLACK_CHANNEL_ID={your-channel-id}
   ```

**3. Run all of database container**

   ```bash
   docker compose up -d
   ```

**4. Execute PostgreSQL dabatase and create table**

   First, you need to run console with docker
   ```bash
   docker exec -it blog-postgres psql -U postgres -d blog
   ```

   Then, execute all SQL in `classpath:sql`

**5. Run application with Gradlew**
```bash
./gradlew bootrun
```


## 💬 Usage

Once the application is running, you can use blog api via HTTP request.

You can access the interactive API documentation at:
* **Swagger UI**: `http://localhost:8080/swagger-ui.html`
