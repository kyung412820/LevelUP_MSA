# Level_UP
<br>

## 🚀 프로젝트 소개
🛍️ **Level_UP** - 게임을 잘하고 싶은 사람들에게 멘토를 매칭해 주는 서비스입니다! 매칭된 사람들끼리의 실시간 채팅, 전체 커뮤니티등 다양한 기능을 함께 제공해 드립니다.

<br>

## 👤 팀원 소개

- **김효중**: Redis, 커뮤니티, 프로젝트 총괄
- **최대현**: 소켓,배포
- **이경훈**: ElasticSearch를 이용한 인기 검색어 조회 기능, 자동완성, 감성분석과 집계를 통한 Top3 선정, 클러스터를 이용한 분산 데이터 처리, 카테고리별 상품 개수 검색, ELK 기반 Log 관리
- **이동건**: Order, Bill, Payments 테이블 관리, 결제흐름 구현, 재고관리(Redis 분산락, 비관적락), 중복결제 생성 개선(Redis Listener TTL발생), 결제승인 재시도 기능
- **정영균**: 스프링 시큐리티

<br>

## 🎯 프로젝트 목표

### 1. JWT 및 스프링 시큐리티 / OAuth 2.0 소셜 로그인
- JWT 및 Spring Security 설정을 통해 인증 및 인가 로직 구현
- OAuth 2.0을 사용하여 소셜 로그인 기능 구현
  
#### 주요 기능  
- **일반 로그인**: email과 비밀번호를 통한 일반 로그인 기능
- **소셜 로그인**: 구글, 네이버 로그인을 통한 소셜 로그인 기능
- **자동 로그인**: 리프레시 토큰을 통한 자동 로그인 기능

#### 로그인 시스템 구성  
- **Spring Security**: 스프링 시큐리티를 통한 로그인 시스템 기본 구성  
- **JWT**: 액세스 토큰과 리프레시 토큰 발급을 위한 JWT
- **OAUTH2**: 소셜 로그인을 위한 OAUTH2 인증 시스템 
- **MySQL**: 회원 정보 저장을 위한 RDB.

<br>

### 2. ElasticSearch를 활용한 검색 서비스  

ElasticSearch는 대용량 데이터를 실시간으로 검색하고 분석할 수 있는 분산형 검색 엔진입니다.  
본 서비스에서는 ElasticSearch를 활용하여 **빠르고 정확한 검색 기능**을 제공합니다.

#### 주요 기능  
- **키워드 검색**: 상품명, 게임 장르, 설명(Contents) 등을 기반으로 검색 가능  
- **자동 완성(Auto-Suggest)**: 입력 중인 검색어에 대한 추천어 제공  
- **필터링 및 정렬**: 가격, 인기순, 최신 등록일 등의 필터 및 정렬 기능  
- **리뷰 감성 분석**: 검색 결과에 포함된 리뷰의 감성 점수를 분석하여 긍정적/부정적 리뷰 제공  

#### 검색 성능 최적화  
- **n-gram 토크나이저 적용**: 한국어 및 영어 검색을 위한 형태소 분석기 적용  
- **검색 인덱스 튜닝**: 불필요한 필드 제외 및 검색 성능 향상을 위한 캐싱 적용  
- **ElasticSearch Query DSL 활용**: 다중 필드 검색 및 적용  

<br>

### 3. 모니터링 서비스  

서비스의 원활한 운영을 위해 **실시간 모니터링 시스템**을 구축하여 장애 예방 및 성능 개선을 지원합니다.

#### 주요 모니터링 항목  
- **로그 모니터링(Log Monitoring)**: Logstash & Filebeat를 활용하여 서버 및 애플리케이션 로그 수집  
- **모니터링 시각화**: Kibana를 활용한 시스템 로그, 데이터 관리 시각화  
- **ElasticSearch 검색 성능 모니터링**: 검색 응답 시간 및 인덱스 크기 모니터링  

#### 모니터링 시스템 구성  
- **Elastic Stack(ELK)**: Elasticsearch + Logstash + Kibana를 이용한 로그 분석  
- **Fleet Server**: 서버 및 애플리케이션 성능 시각화  

<br>

### 4.알림 서비스

- 회원가입 완료시 회원가입 환영메시지가 가입 이메일을 통해 발송.
- 회원정보 변경감지시 로그인된 사용자에게 알림이 가도록 구현.

#### 알림 서비스 시스템 구성

- **Server Sent Event(SSE)**: 클라이언트와 서버의 연결 유지를 위한 표준 기술. 단방향 통신
- **Spring Event**: 알림을 변경 작업과 별개의 스레드로 동작시켜 변경 작업의 속도에 영향을 미치지 않게 하기 위한 스프링 이벤트
- **Redis**: SSE 연결 끊김 발생시 수신 성공한 메시지부터 다시 받아오기 위한 임시 보관용 Redis 캐시
- **MySQL**: 변경메시지와 타겟 유저, 변경시각, 알림 전송 성공 여부를 기록하기 위한 추가 RDB. 관리자만 접근가능.
  
<br>

### 5. 
- 

### 6. 빠르고 안전한 결제 경험 제공
- 카드, 간편결제, 계좌이체 토스페이 지원
- 토스 개발자 문서를 활용하여 빠르게API 연동 가능
- 실 결제 없이 테스트 가능
- 결제 관련 이메일 알림 전송
- 결제 취소 및 환불기능 지원
- 결제 승인 실패 시 재시도 전략 적용
- 안정적인 주문 처리 (트랜잭션 롤백, 재고 검증 및 관리)
- 재고 관리에 대한 악성유저 방지(Redis TTL발생 10분이내 결제되지않으면 HardDelete)


### 7. 
- 

### 8. 
- 
<br>
<br>

## 🏆 **Architecture** 
![image](https://github.com/user-attachments/assets/27a7d8be-d38d-4036-834b-57b93aa53c1b)

## 📝 **와이어프레임**
![와이어프레임](https://github.com/user-attachments/assets/b01ecd9d-afa0-4b53-a06b-6e2cad4ca68f)

## 💬 **ERD**
![ERD](https://github.com/user-attachments/assets/e36a5524-ed83-4636-9b5d-a77596d88c50)
</details>

<br>

|  Level_UP Team Notion |  발표 보고서 |  발표 영상 |
|:------:|:----------------------:|:----------------------:|
| [Notion 보러가기](https://www.notion.so/teamsparta/9-1962dc3ef51480d5b934d27f143c3c41) | [발표 보고서 보러가기](https://www.canva.com/design/DAGaRbld9so/37ehM1xDZDsknpC-fXeebQ/edit?utm_content=DAGaRbld9so&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton) | [발표 영상 보러가기](https://www.youtube.com/watch?v=-8S3XLLW6jA) |

<br>
<br>

## 📚 **기술 스택**

### Back-end
![Java](https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white)
![Spring](https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Spring Boot](https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![Elasticsearch](https://img.shields.io/badge/elasticsearch-47A248?style=for-the-badge&logo=elasticsearch&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-6DZ44F?style=for-the-badge&logo=MongoDB&logoColor=white)

- **Java**: Spring Boot 기반 서버 개발
- **Spring**: 의존성 주입 및 AOP, 트랜잭션 관리 등 다양한 엔터프라이즈 기능을 제공하는 프레임워크
- **Spring Boot**: 빠른 설정 및 간단한 구성을 통해 스프링 기반 애플리케이션을 개발할 수 있도록 돕는 프레임워크
- **Redis**: 캐시 관리 및 분산 락을 통한 동시성 제어
- **MySQL**: AWS RDS에서 제공되는 관계형 데이터베이스
- **Elasticsearch**: 인기 검색어 순위 및 빠른 검색 기능, 감성분석, 집계, 모니터링 기능 제공
- **MongoDB**: 실시간 데이터 처리를 통한 채팅 기능 제공


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

<br>

## 🔧 **성능 개선**

### 1. **레디스 캐싱**: 데이터 캐싱을 통한 빠른 응답 처리

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

<br>
<br>
  
### 2. **Elasticsearch**: 엘라스틱 서치를 이용한 검색 속도 개선

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

<br>
<br>
  
### 3. 


<br>
<br>

### 4. 

<br>

<br>

### 5. 

<br>
<br>

## 🔒 **트러블슈팅**

### 1. **엘라스틱 서치의 사용 이유**
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

<br>

### 2. CustomOAuth2UserService에서 발생한 Exception이 상위로 던져지지 않는 문제

- CustomOAuth2UserService에서 발생한 로그인 실패 관련 커스텀 Exception들이 상위로 넘어가지 못해서 postman과 웹페이지로 표시가 되지 않는 문제가 발생하였다.
 ![Image](https://github.com/user-attachments/assets/27838a73-ede1-4c6c-924e-5b96bfe5319a)
- 어떤 이유로 로그인에 실패했는지 명확하게 사용자에게 알려주기 위해서는 반드시 이 커스텀 Exception이 상위로 던져져야만 한다.
- 이 문제를 해결하기 위해 **어디에서 넘어가지 못했는지 알아보다가 **OAuth2LoginAuthenticationFilter**에서는 OAuth2AuthenticationException만 상위로 넘겨줄 수 있다는 것을 알게 되었다.**
- OAuth2AuthenticationException에 메시지부분에 발생한 Exception의 메시지를 넣어주고, Exception을 받는 핸들러부분에서 메시지를 꺼내 리턴해주면 해결될것이라 판단하였다.
- 해당 방식으로 구조를 변경한 후 정상적으로 익셉션이 상위로 리턴됨을 확인하였다.
 ![Image](https://github.com/user-attachments/assets/71b2ef8d-f6a0-4478-a917-22865edb5326)

<br>

### 3.

<br>

### 4. 


<br>

## 📈 **추가 개선 가능 점**

### 1.  **엘라스틱 서치 개발의 개선**
- 샤드 수가 너무 많으면 오버헤드가 증가하고, 너무 적으면 데이터 검색 속도가 저하된다. 이를 유념하여 조정이 필요하다.
- 데이터의 사용 빈도에 따라 핫(Hot), 웜(Warm), 콜드(Cold) 노드를 구성하여 리소스를 효율적으로 사용해야한다.
- 여러 클러스터로 나누어 데이터를 검색하거나 복자하여 대규모 환경에서도 안정적인 성능을 유지할 수 있도록 개발해야한다.
- 백업을 위한 스냅샷을 정기적으로 생성하도록 설정해야한다.

<br>

### 2. 알림 이메일 발송기능
- 기존에는 알림을 로그인된 사용자에게만 발생했으나, 해킹을 당해 적법한 사용자의 허가 없이 개인정보 변경작업이 일어나는 경우에 로그인이 되어있지 않은 사용자는 이 상황을 인지할 수 없는 문제가 있다는 것이 확인되어 개인정보 변경시에 사용자에게 알림을 보내주어, 사용자가 해킹을 당한 상태라는 것을 인지할 수 있게 만들어야 한다.
<br>

### 3. 

<br>

### 4. 

<br>

## 🤝 **팀원**

| 이름   | 깃허브                                                   |
|--------|---------------------------------------------------------|
| 김효중 | (https://github.com/rlagywnd4) |
| 최대현 | [https://github.com/DeaHyun0911](https://github.com/DeaHyun0911) |
| 이경훈 | [https://github.com/kyung412820](https://github.com/kyung412820) |
| 이동건 | (https://github.com/LeeDong-gun) |
| 정영균 | (https://github.com/lq0920084) |
---

