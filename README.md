# VM - Video Manager

<br/>

> * 포트는 8000 을 사용합니다. (application.yml 에서 수정 가능.)

> * DB 서버의 경우 GCP를 이용하였습니다. (application.yml 에서 확인 가능.)

> * H2 DB는 테스트 케이스 용도로 사용되었습니다.

> * SpringSecurity, JWT 를 적용하여 AccessToken, RefreshToken을 이용하여 로그인을 구현했습니다.

> * Advice를 이용하여 Exception을 공통적으로 관리 할 수 있도록 구현했습니다.

> * SPA로 화면이 동작 될 수 있도록 구현했습니다.


## SPEC & Tech
### Front-End
`ThymeLeaf` `BootStrap` `JavaScript` `JQuery` `CSS`
### Back-End
`Java 11` `spring-boot` `MariaDB` `JPA` `Junit`  <br/><br/>
`lombok` `Validation` `Advice` `JWT`<br/><br/>
`SpringSecurity`, `h2 (gradle dependency)`
### DB Server
`GCP (Google Cloud Platform)`


## 프로젝트 기능 목록 - API 문서
| 기능 | 메소드 | 주소 |
| :--- | :---: | ---: |
| 메인화면 렌더링 | GET | /
| 로그인 | POST | /api/authenticate|
| 사용자조회| GET | /api/user|
| 사용자등록 | POST |/api/register|
| 사용자수정| PUT | /api/modify-user|
| 사용자삭제| DELETE |/api/delete-user|
| 비디오등록|POST | /api/file-upload|
| 비디오재생|GET |/video-stream/{fileName}/token/{token}|
|토큰갱신요청|POST|/api/reissue|


## 프로젝트 실행

### 01) application.yml 설정

* src > main > resources > application.yml에 원하는 포트 설정을 아래와 같은 방식으로 작성해서 이용하시면 됩니다.
  기본 포트 `8080`
* 웹 url : (URL http://localhost:8080/)
* 카카오 API 연동시 원하는 uri와 발급받은 Key를 해당하는 곳에 작성 후 이용하시면 됩니다.

```{.no-highlight}
spring:
  port: 8080
```

* DB 연동 종류 선택 별도의 DB가 없을 경우 H2DB 설정 그대로 이용하시면 됩니다.
* 다른 DB로 연동을 원할 경우 하단에 첨부된 코드의 주석 처리 된 부분을 알맞는 URL로 변경 후 이용하면 됩니다

```{.no-highlight}
#   maria db
#  jpa:
#    hibernate:
#      ddl-auto: none
#  datasource:
#    driver-class-name: org.mariadb.jdbc.Driver
#    url: jdbc:mariadb://34.64.196.39:3306/video-manage?characterEncoding=UTF-8&serverTimezone=UTC;MVCC=TRUE
#    username: root
#    password: 1234
#
#  devtools:
#    restart:
#      enabled: true

# 로컬 디비 테스트 용도 (h2db)
jpa:
database-platform: org.hibernate.dialect.H2Dialect
hibernate:
ddl-auto: create
h2:
console:
enabled: true
datasource:
hikari:
jdbc-url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
driver-class-name: org.h2.Driver
username: sa
password:
```

### 02) 실행 시 default 계정 생성
* main>java>com>lab>vm>VmWebApplication.java 에 default 계정이 생성 될 수 있도로 구현 해 두었습니다 
* 변경을 원할 경우 하단의 코드를 수정하면 됩니다.
* 관리자 : ID = admin / PW = admin
* 일반사용자 : ID = user / PW = user


```{.no-highlight}
  /**
    * 스프링 부트는 run() 이라는 콜백 메소드를 가진 CommendLineRunner라는 인터페이스 제공
    * run() 메소드는 Spring application context의 초기화가 완료된(모든 Baan이 초기화된) 후에 실행되므로
    * 이 안에 원하는 로직을 작성하면 된다.
      */
      @Bean
      public CommandLineRunner run(UserService userService) {
      return (String[] args) -> {


			var admin = RegisterDto.builder()
//            .activated(true)
              .username("admin")
              .password("admin")
              .passwordConfirm("admin")
              .phone("01011112222")
              .email("admin@admin.com")
              .build();

			userService.registerUser(admin);

			var user  = RegisterDto.builder()
//            .activated(true)
              .username("user")
              .password("user")
              .passwordConfirm("user")
              .phone("01011113333")
              .email("user@user.com")
              .build();

			userService.registerUser(user);


		};
	}

```
