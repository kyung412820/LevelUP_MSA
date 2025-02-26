# levelup_backend

🛍️ **levelup** - 게임을 잘하고 싶은 사람들에게 멘토를 매칭해 주는 서비스입니다! 매칭된 사람들끼리의 실시간 채팅, 전체 커뮤니티등 다양한 기능을 함께 제공해 드립니다.

**프로젝트 기간**: 2025/02/10 ~ 2025/03/17


## ⭐️ **스파르타 내일배움캠프 플러스 프로젝트** : 8조 ⭐️
![쿠팡 쿠폰 메인 이미지](https://github.com/llRosell/Coupang/blob/dev/%E1%84%8F%E1%85%AE%E1%84%91%E1%85%A1%E1%86%BC%20%E1%84%8F%E1%85%AE%E1%84%91%E1%85%A9%E1%86%AB%20%E1%84%86%E1%85%A6%E1%84%8B%E1%85%B5%E1%86%AB.png?raw=true)


## 👤 팀원 소개

- **김효중**: Redis, 커뮤니티, 프로젝트 총괄
- **최대현**: 소켓,배포
- **이경훈**: ElasticSearch를 이용한 인기 검색어 조회 기능, 자동완성, 감성분석과 집계를 통한 Top3 선정, 클러스터를 이용한 분산 데이터 처리
- **이동건**: 결제, 락
- **정영균**: 스프링 시큐리티


## 🚀 프로젝트 소개

### 🎯 프로젝트 목표

### 1. JWT 및 스프링 시큐리티 / OAuth 2.0 소셜 로그인
- JWT 및 Spring Security 설정을 통해 인증 및 인가 로직 구현
- OAuth 2.0을 사용하여 소셜 로그인 기능 구현

### 2. 데이터 처리 능력 강화
- **동시성 문제 해결**: 다수의 요청을 안정적으로 처리하기 위한 동시성 제어 방법 구현
  - 락을 설정하지 않으면 동시성 이슈가 발생하는지 검증하는 테스트 코드 작성
  - Redis Lock을 사용하여 동시성 이슈 제어
  - 동시성 이슈를 락을 통해 제어하는 테스트 코드 작성

### 3. Cache를 이용한 성능 개선
- 상품 목록 조회 API에 In-memory Cache 적용하여 성능 개선
- 적용 전, 적용 후 성능 테스트

### 4. ElasticSearch를 활용한 검색 서비스
- ElasticSearch를 이용한 인기 검색어 조회 및 상품 검색 기능 구현

### 5. AWS 배포
- 클라우드에서 서버 배포 및 운영 환경 구축
- 자동화된 파이프라인을 통해 안정적이고 신속한 배포 환경 구축
- 관리형 데이터베이스를 사용하여 안정적인 데이터 관리 및 운영 실습


## 🏆 **Architecture** 
![image](https://github.com/user-attachments/assets/27a7d8be-d38d-4036-834b-57b93aa53c1b)

## 📚 **쿠빵 Team Notion 보러가기**
[쿠빵 Team Notion](https://teamsparta.notion.site/8-5c74090342f94d1bae575d1f6888cdc1)

## 📄 **발표 보고서 보러가기**
[발표 보고서](https://www.canva.com/design/DAGaRbld9so/37ehM1xDZDsknpC-fXeebQ/edit?utm_content=DAGaRbld9so&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton)

## 🎬 **발표 영상 보러가기**
[발표 영상](https://www.youtube.com/watch?v=-8S3XLLW6jA)

## 📝 **와이어프레임**
[와이어프레임](https://github.com/user-attachments/assets/e156ae13-944e-4089-848b-73bc4ef7d088)


</details>

## 💬 **ERD**
[![ERD 이미지](https://raw.githubusercontent.com/llRosell/Coupang/refs/heads/dev/ERD.webp)]
</details>


## 📽️ 프로젝트 목표

- **선착순 쿠폰 발급**: 대량 트래픽을 처리하며 빠르고 정확한 쿠폰 발급 서비스를 제공합니다.
- **분산 락을 통한 동시성 제어**: 선착순 쿠폰 발급 시스템을 구현하여 사용자들의 동시 요청을 처리합니다.
- **AWS 환경에서 배포**: EC2, RDS, 로드밸런싱, S3 및 도메인 관리 등 AWS 서비스를 활용하여 안정적이고 확장 가능한 시스템을 구축합니다.

---

## 📚 **기술 스택**

### Back-end
![Java](https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white)
![Spring](https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Spring Boot](https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![Elasticsearch](https://img.shields.io/badge/elasticsearch-47A248?style=for-the-badge&logo=elasticsearch&logoColor=white)

- **Java**: Spring Boot 기반 서버 개발
- **Spring**: 의존성 주입 및 AOP, 트랜잭션 관리 등 다양한 엔터프라이즈 기능을 제공하는 프레임워크
- **Spring Boot**: 빠른 설정 및 간단한 구성을 통해 스프링 기반 애플리케이션을 개발할 수 있도록 돕는 프레임워크
- **Redis**: 캐시 관리 및 분산 락을 통한 동시성 제어
- **MySQL**: AWS RDS에서 제공되는 관계형 데이터베이스
- **Elasticsearch**: 인기 검색어 순위 및 빠른 검색 기능 제공


### AWS
![EC2](https://img.shields.io/badge/amazon_ec2-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white)
![RDS](https://img.shields.io/badge/amazon_rds-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white)
![Loadbalancer](https://img.shields.io/badge/amazon_loadbalancer-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white)
![S3](https://img.shields.io/badge/amazon_s3-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white)
![Route 53](https://img.shields.io/badge/route_53-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white)

- **EC2**: 애플리케이션 서버 운영
- **RDS**: 관계형 데이터베이스 관리
- **로드밸런싱**: 트래픽을 여러 EC2 인스턴스에 분산하여 처리
- **S3**: 이미지 및 기타 파일 저장 관리
- **도메인 관리**: AWS Route 53을 통해 도메인 설정

### Tools
![JMeter](https://img.shields.io/badge/jmeter-F5A500?style=for-the-badge&logo=apachejmeter&logoColor=white)
![Docker](https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Git](https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white)
![GitHub](https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white)

- **JMeter**: 성능 테스트 및 로드 테스트를 통한 시스템 안정성 검증
- **Docker**: 개발 및 배포 환경을 컨테이너화하여 일관성 있는 개발 환경 제공
- **Git**: 버전 관리 시스템
- **GitHub**: GitHub을 사용한 협업 및 코드 관리

---

## 🛒 선착순 쿠폰 발급 서비스

### 동시성 제어 테스트 결과

<tr>
<tr>

### 선착순 쿠폰 발급 서비스

▶️ **데이터 정합성**:
- **분산 락 = 비관적 락 > 낙관적 락**
- 분산 락과 비관적 락은 항상 일관되게 100개의 쿠폰을 발급받음.
- 낙관적 락은 100개 이상의 쿠폰을 발급받는 테스트에서 실패하는 경우가 더 많음.

낙관적 락은 충돌이 자주 발생하는 환경에서는 적합하지 않다는 결론을 얻었습니다.

비관적 락은 많은 스레드가 동시에 몰릴 경우 DB 부하가 높아질 수 있습니다.

### 테스트 결과 분석

선착순 쿠폰 100개를 발급받는 서비스와 같이 짧은 시간에 충돌이 많이 발생하는 경우, **Redis 분산 락**을 사용하는 것이 가장 적합하다는 판단을 내렸습니다. 

#### 주요 결과:
- **분산 락**과 **비관적 락**은 항상 일관되게 100개의 쿠폰을 발급하며, 데이터 정합성을 보장합니다.
- **낙관적 락**은 락을 명확하게 설정하지 않고 버전 관리 방식(버전 어노테이션)을 사용하기 때문에, 동시 스레드 환경에서는 쿠폰 발급 수가 100개를 넘는 상황이 발생할 수 있습니다. 이로 인해 테스트에 실패하는 경우가 많았습니다.

#### 결론:
- **낙관적 락**은 충돌 발생이 잦은 환경에서는 적합하지 않으므로 사용을 피해야 합니다.
- **Redis 분산 락**은 충돌이 많이 발생하는 환경에서 데이터 정합성을 보장하고, 응답 시간이 빠르므로 **선택하는 것이 가장 효율적**입니다.
  
---

## 🏆 **서비스 성능**

### 대용량 트래픽 처리
- **가상 쿠폰 발급 사용자 1000명**, 쿠폰 100건 발급 시 **Redis 분산락** 이용하면 2초 이내

### 상품 조회 속도
- **상품 데이터 50만 건**
- 100스레드 100회 반복, 총 10,000건의 요청,
- jmeter로 성능테스트 시 페이지네이션 상품 조회 속도: **0.5초 이내**

### 검색 속도
- **최초 검색 속도**: 39ms
- **캐싱 이후 검색 속도**: **17ms**

---

## 🔧 **성능 개선**

### 1. 🚀 [최대현] **레디스 캐싱**: 데이터 캐싱을 통한 빠른 응답 처리

<tr>
<tr>

**캐시 미적용**
<img alt="스크린샷 2025-02-06 오후 5 07 38" src="https://github.com/user-attachments/assets/1869bf69-de9a-4c37-8ee8-62ef26ad23e7" />

**캐시 적용**
<img alt="스크린샷 2025-02-06 오후 5 08 19" src="https://github.com/user-attachments/assets/0dd89485-0b61-4f28-b61f-48ec41655c52" />


|        | **평균 응답속도** |
  |--------|-------------|
| 캐시 미적용 | 5초 91ms     |
| 캐시 적용  | 1초 880ms    |

- **최적화 결과**
  - Redis Cache 적용 후 **평균 응답 속도 3초 211ms 향상** ㅇ

<tr>
<tr>
  
### 2. 🔍 [이경훈] **Elasticsearch**: 엘라스틱 서치를 이용한 검색 속도 개선

<tr>
<tr>

![Elasticsearch 성능 비교](https://github.com/user-attachments/assets/0ee0141b-38c5-4f6b-84be-54a31de92d47)


| **검색 방법**                  | **설명**                         | **실행 속도 (ms)** |
  |--------------------------------|--------------------------------|-------------------|
| `getPopularKeywords()`         | 기본적인 검색어 집계            | **39ms**          |
| `getPopularKeywordsOptimized()` | 실행 힌트 적용 (`Map` 방식)     | **33ms**          |
| `getPopularKeywordsFastest()`   | 실행 힌트 + 쿼리 캐싱 적용      | **17ms**          |

- **최적화 결과**
    - 기본 검색 대비 **최대 2.3배 속도 향상**
    - `executionHint(TermsAggregationExecutionHint.Map)` 적용 시 **15% 속도 개선**
    - `requestCache(true)` 적용 후 **50% 추가 속도 개선**
    - 캐싱된 검색어 데이터를 활용하면 **0.1초 이내** 응답 가능

 <tr>
<tr>
  
### 3. 🛣️ [최순우] **부하 분산**: AWS 로드밸런싱을 활용한 부하 분산

## 🚀 **AWS Elastic Beanstalk 배포**
### 🌍 **Route 53을 이용한 도메인 설정**
- AWS Route 53을 활용하여 맞춤형 도메인을 설정하고, 사용자가 쉽고 빠르게 접근할 수 있도록 구성

### 🔒 **로드밸런서(Load Balancer) + HTTPS 적용**
- AWS 로드밸런서를 활용하여 트래픽을 분산시키고, HTTPS 인증서를 적용하여 보안 강화
- Elastic Load Balancing(ELB)와 ACM(AWS Certificate Manager)을 활용한 SSL/TLS 암호화 적용

### 🛠 **EC2 + RDS를 활용한 안정적인 데이터베이스 관리**
- AWS RDS를 사용하여 MySQL을 구성하고, EC2와의 연동을 통해 관리형 데이터베이스 운영 실습
- Multi-AZ 배포를 고려하여 가용성과 안정성을 높이는 방법 적용
- 자동 백업 및 스냅샷 기능을 활용하여 데이터 보호

<tr>
<tr>
  
---

## 🔑 **Social Login OAuth2.0 (Google)**
### 🔗 **Google OAuth 2.0을 활용한 로그인 기능 구현**
- Google OAuth 2.0을 활용하여 사용자가 쉽게 로그인할 수 있도록 지원
- 인증된 사용자 정보를 받아와 서버에서 관리

### 🔐 **JWT 기반 인증 및 인가 시스템 구축**
- OAuth2 Access Token 대신 서버에서 별도의 JWT를 발급하여 인증 및 인가 수행
- JWT를 활용하여 보안성을 높이고, 인증 요청 시 서버 부하 감소

### 📂 **MySQL(RDS) + Redis를 활용한 회원 관리 및 세션 유지**
- 회원 정보를 MySQL(RDS)에 저장하여 관리
- Refresh Token을 Redis에 저장하여 로그인 유지 및 재인증 처리 최적화
- Redis를 활용한 세션 캐싱으로 빠른 인증 처리 가능

### 📌 **아키텍처 개요**
![AWS Elastic Beanstalk 및 OAuth2 소셜 로그인 아키텍처](https://github.com/user-attachments/assets/af478284-df00-47be-8442-51e33d3c2b09)

위 사진 **AWS Elastic Beanstalk 기반의 배포 과정과 OAuth2 소셜 로그인 인증 흐름**을 나타냅니다.
1. 사용자가 Google OAuth2.0을 이용하여 로그인 요청
2. OAuth 인증 후, 서버에서 JWT를 발급하여 관리
3. 사용자 정보는 MySQL(RDS)에 저장, Refresh Token은 Redis를 활용하여 빠르게 처리
4. AWS Elastic Beanstalk을 이용한 서비스 배포 및 관리

이러한 구조를 통해 **보안성과 확장성을 갖춘 웹 서비스**를 운영할 수 있습니다. 🚀

### 4. 📊 [김리은] 1000개의 스레드, 선착순 100개의 쿠폰 발급 동시성 제어 

▶️ **테스트에 걸린 시간**:
- **분산 락**: 2초 722ms
- **낙관적 락**: 3초 488ms
- **비관적 락**: 3초 802ms

**분산 락 적용**
![image](https://github.com/llRosell/Coupang/blob/dev/%E1%84%87%E1%85%AE%E1%86%AB%E1%84%89%E1%85%A1%E1%86%AB%E1%84%85%E1%85%A1%E1%86%A8%201000%E1%84%80%E1%85%A2%202%E1%84%8E%E1%85%A9%20722ms.png?raw=true)
▶️  분산 락 2초722ms


**비관적 락 적용**
![image](https://github.com/llRosell/Coupang/blob/dev/%E1%84%87%E1%85%B5%E1%84%80%E1%85%AA%E1%86%AB%E1%84%8C%E1%85%A5%E1%86%A8%E1%84%85%E1%85%A1%E1%86%A8%201000%E1%84%80%E1%85%A2%203%E1%84%8E%E1%85%A9%20802ms.png?raw=true)
▶️  비관적 락 3초802ms


분산 락이 가장 빠르며, 비관적 락은 상대적으로 느리다는 결과가 나왔습니다. 
동시성 제어를 테스트하기 위해 1000개의 스레드가 동시에 100개의 쿠폰을 발급받는 상황을 시뮬레이션하였고, 그 결과:
- **분산 락**이 가장 빠르게 동작했으며, **비관적 락**은 가장 느렸습니다.

---
## 🔒 **트러블슈팅**

### 1. **SQL 최적화**: 검색 시 사용자가 선택한 조건의 조합에 따른 쿼리 적용 방법 고민
### 2. **동시성 제어**: 선착순 쿠폰 발급에 대한 데이터 정확성 확보
### 3. **AWS 배포 서버 중단 문제**: AWS EC2 인스턴스 중단 대응
### 4. **[이경훈] 엘라스틱 서치의 사용 이유**
   - Mysql로 기존의 30만 이상의 데이터에서 특정 단어가 포함된 데이터를 조회시 속도가 조금 느리다는 판단을 함(4.932초)
![image](https://github.com/user-attachments/assets/3b93d3f6-675f-492a-ba3c-88381c7cbb84)
   -  속도의 개선을 위해서 캐시를 적용하거나 페이징을 통해 카테고리화를 수행하여 속도를 올려봄
   -  **속도를 개선하다보니 많은 현업의 몇 천만 데이터를 관리하기 위해서는 새로운 해답이 필요하다 생각하게 됨**
   -  레디스를 찾다가 엘라스틱 서치라는 것을 알게되어 적용 시작
   -  **Mysql과 다른 역인덱스 구조가 검색의 속도를 비약적으로 빠르게 해준다는 것을 학습**, 적용함
   -  Mysql에서 일부를 처시할 경우 5초가 걸렸지만 엘라스틱 서치의 힌트와 캐시를 적용한 후엔 0.2초가 걸리게 바뀜
   -  최종적으로 엘라스틱 서치를 적용하여 검색 속도를 향상
   -  (다만, mysql도 인덱싱을 잘한 상태라면 적은 데이터 셋에서는 엘라스틱 서치보다 빠를 수 있어, 적재적소에 사용해야한다는 것을 유념)
     ![image](https://github.com/user-attachments/assets/9ec327a9-e366-4f5d-8c62-cd49329768b9)


---

## 📈 **추가 개선 가능 점**

- **대기열 시스템**: 대량의 트래픽이 몰리는 것을 방지하는 대기열 시스템 추가 예정
- **트래픽 이벤트 처리**: 트래픽이 몰릴 이벤트 상품 데이터를 미리 캐싱하여 빠른 처리 지원

### **[이경훈] 엘라스틱 서치 개발의 개선**
- 샤드 수가 너무 많으면 오버헤드가 증가하고, 너무 적으면 데이터 검색 속도가 저하된다. 이를 유념하여 조정이 필요하다.
- 데이터의 사용 빈도에 따라 핫(Hot), 웜(Warm), 콜드(Cold) 노드를 구성하여 리소스를 효율적으로 사용해야한다.
- 여러 클러스터로 나누어 데이터를 검색하거나 복자하여 대규모 환경에서도 안정적인 성능을 유지할 수 있도록 개발해야한다.
- 백업을 위한 스냅샷을 정기적으로 생성하도록 설정해야한다.
---

## 🤝 **팀원**

| 이름   | 깃허브                                                   |
|--------|---------------------------------------------------------|
| 최대현 | [https://github.com/DeaHyun0911](https://github.com/DeaHyun0911) |
| 김리은 | [https://github.com/llRosell](https://github.com/llRosell) |
| 최순우 | [https://github.com/asdd2557](https://github.com/asdd2557) |
| 이경훈 | [https://github.com/kyung412820](https://github.com/kyung412820) |

---

