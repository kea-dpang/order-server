# DPANG ORDER SERVER

## 🌐 프로젝트 개요

이 프로젝트는 주문 서비스를 지원하는 마이크로서비스로서, 사용자의 주문, 환불, 취소 등 주문 관련 기능을 제공합니다.

이를 통해 사용자의 주문 관련 작업을 효율적으로 관리하고, 사용자 경험을 향상시키는데 중점을 두고 있습니다.

## 🔀 프로젝트 아키텍처

아래의 시퀀스 다이어그램은 본 프로젝트의 주요 컴포넌트인 Spring Cloud Gateway, 주문 서비스, 그리고 타 서비스 간의 상호작용을 보여줍니다.

```mermaid
sequenceDiagram
    participant Client as Client
    participant Gateway as Spring Cloud Gateway
    participant OrderService as Order Service
    participant MySQL as MySQL
    participant OtherService as Other Service
    
    Client ->> Gateway: 요청 전송
    Gateway ->> OrderService: 요청 전달 <br> (X-DPANG-CLIENT-ID, X-DPANG-CLIENT-ROLE 헤더 추가)
    OrderService ->> OrderService: 해당 요청 권한 식별

    opt 요청에 대한 권한이 있는 경우
        OrderService ->> MySQL: 데이터 요청
        MySQL -->> OrderService: 데이터 응답

        opt 타 서비스의 데이터가 필요한 경우
            OrderService ->> OtherService: API 요청 <br> (X-DPANG-SERVICE-NAME 헤더 추가)
            OtherService ->> OtherService: 요청에 대한 처리
            OtherService -->> OrderService: 처리된 API 응답
        end

        OrderService ->> OrderService: 응답 처리
        OrderService -->> Gateway: 응답 전송
        Gateway -->> Client: 최종 응답 전달
    end

    opt 요청에 대한 권한이 없는 경우
        OrderService -->> Gateway: 사용자 권한 없음 응답
        Gateway -->> Client: 사용자 권한 없음 응답
    end

    opt 인증 실패한 경우
        Gateway -->> Client: 인증 실패 응답
    end

```

이 시퀀스 다이어그램을 통해 볼 수 있듯이, 모든 클라이언트 요청은 먼저 Spring Cloud Gateway를 통해 전달됩니다.

Gateway는 클라이언트의 요청에 대한 토큰을 분석하고, 사용자의 ID와 Role 정보를 추출하여
'X-DPANG-CLIENT-ID'와 'X-DPANG-CLIENT-ROLE'이라는 사용자 정의 헤더에 추가하여 주문 서비스에 전달합니다.

주문 서비스는 해당 요청에 대한 권한을 식별하고, 권한이 있는 경우에만 요청을 처리합니다.

권한이 있는 경우, 주문 서비스는 MySQL 데이터베이스에서 필요한 데이터를 요청하고, 그 데이터를 다시 클라이언트에게 반환합니다.

만약 해당 요청에 대한 데이터가 다른 서비스에서 필요한 경우, 주문 서비스는 해당 서비스에 API 요청을 전달하고, 그 처리 결과를 다시 클라이언트에게 반환합니다.

만약 해당 요청에 대한 권한이 없는 경우, 주문 서비스는 클라이언트에게 '사용자 권한 없음' 응답을 반환하며

해당 요청에 대한 인증이 실패한 경우, Gateway는 클라이언트에게 '인증 실패' 응답을 반환합니다.

## 🗃️ 데이터베이스 구조

주문 서비스에서는 주문과 관련된 정보를 관리하기 위해 MySQL 데이터베이스를 사용하고 있습니다.

아래의 ERD는 주문 서비스에서 사용하는 데이터베이스의 구조를 보여줍니다.

```mermaid
erDiagram
    orders {
        bigint order_id PK "주문 번호"
        bigint user_id "사용자 ID"
        varchar(255) delivery_request "배송 요청사항"
        int delivery_fee "배송비"
        int product_payment_amount "상품 결제 금액"
        datetime(6) order_date "주문 날짜"
        datetime(6) updated_at "변경 날짜"
    }

    order_recipients {
        bigint order_id FK "주문 번호"
        varchar(255) receiver_name "받는 사람"
        varchar(255) receiver_phone_number "받는 사람 전화번호"
        varchar(255) receiver_zip_code "받는 사람 우편번호"
        varchar(255) receiver_address "받는 사람 주소"
        varchar(255) receiver_detail_address "받는 사람 상세 주소"
    }

    order_details {
        bigint order_detail_id PK "주문 상세 ID"
        bigint order_id FK "주문 ID"
        bigint item_id FK "상품 ID"
        int quantity "수량"
        int purchase_price "구매 가격"
        enum status "주문 상태"
    }

    order_refunds {
        bigint refund_id PK "환불 ID"
        bigint order_detail_id FK "주문 상세 ID"
        enum refund_reason "환불 사유"
        varchar(255) note "비고"
        datetime(6) refund_request_date "환불 요청 날짜"
        datetime(6) refund_complete_date "환불 완료 날짜"
        int refund_amount "환불 금액"
        enum refund_status "환불 상태"
    }

    order_recalls {
        bigint recall_id PK "회수 ID"
        bigint refund_id FK "환불 ID"
        varchar(255) retriever_name "회수자 명"
        varchar(255) retriever_phone_number "회수자 연락처"
        varchar(255) retriever_address "회수자 주소"
        varchar(255) retrieval_message "회수 메시지"
    }

    order_cancels {
        bigint cancel_id PK "취소 ID"
        bigint order_detail_id FK "주문 상세 ID"
        datetime(6) cancel_request_date "취소 요청 날짜"
        datetime(6) cancel_complete_date "취소 완료 날짜"
        int refund_amount "환불 금액"
    }

    orders ||--|{ order_details: ""
    order_details ||--o| order_refunds: ""
    orders ||--|| order_recipients: ""
    order_details ||--o| order_cancels: ""
    order_refunds ||--|| order_recalls: ""
    user ||--|{ orders: ""
    item ||--|| order_details: ""

```

## ✅ 프로젝트 실행

해당 프로젝트를 추가로 개발 혹은 실행시켜보고 싶으신 경우 아래의 절차에 따라 진행해주세요

#### 1. `secret.yml` 생성

```commandline
cd ./src/main/resources
touch secret.yml
```

#### 2. `secret.yml` 작성

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://{YOUR_DB_HOST}:{YOUR_DB_PORT}/{YOUR_DB_NAME}
    username: { YOUR_DB_USERNAME }
    password: { YOUR_DB_PASSWORD }

  application:
    name: order-server

eureka:
  instance:
    prefer-ip-address: true

  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://{YOUR_EUREKA_SERVER_IP}:{YOUR_EUREKA_SERVER_PORT}/eureka/

```

#### 3. 프로젝트 실행

```commandline
./gradlew bootrun
```

**참고) 프로젝트가 실행 중인 환경에서 아래 URL을 통해 API 명세서를 확인할 수 있습니다**

```commandline
http://localhost:8080/swagger-ui/index.html
```