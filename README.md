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
`HTML` `BootStrap` `JavaScript` `JQuery` `CSS`
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

