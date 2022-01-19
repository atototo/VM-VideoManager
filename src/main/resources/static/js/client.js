
$(function () {
   //공통 사용 변수 정의
   const TOKEN_ACCESS_KEY = "jwtAccessToken";
   const TOKEN_REFRESH_KEY = "jwtRefreshToken";
   const $registerBtn = $('#registerBtn');
   const $modifyBtn = $(`#modifyBtn`);
   const $modifyUserModal = $('#modifyUserModal');
   const $deleteBtn = $(`#deleteBtn`);
   const $deleteUserModal =  $('#deleteUserModal');
   const $response = $("#response");
   const $login = $("#login");
   const $inputUserName = $("#inputUserName");
   const $inputPassword = $("#inputPassword");
   const $userInfo = $("#userInfo").hide();
   const $videoInfo = $("#videoInfo");
   const $fileUploadInfo = $("#fileUploadInfo");

   // function  =============================================================
   /** 엑세스 토큰 조회 **/
   function getJwtAccessToken() {
      return localStorage.getItem(TOKEN_ACCESS_KEY);
   }

   /** 리프레쉬 토큰 조회 **/
   function getJwtRefreshToken() {
      return localStorage.getItem(TOKEN_REFRESH_KEY);
   }

   /** JWT 토큰 세팅 ㅣ localStorage **/
   function setJwtToken(token) {
      localStorage.setItem(TOKEN_ACCESS_KEY, token.access_token);
      localStorage.setItem(TOKEN_REFRESH_KEY, token.refresh_token);
   }
   /** JWT 토큰 제거 ㅣ localStorage **/
   function removeJwtToken() {
      localStorage.removeItem(TOKEN_ACCESS_KEY);
      localStorage.removeItem(TOKEN_REFRESH_KEY);
   }

   /**
    * 로그인 시도
    * @param loginData
    */
   function doLogin(loginData) {
      $.ajax({
         url: "/api/authenticate",
         type: "POST",
         data: JSON.stringify(loginData),
         contentType: "application/json; charset=utf-8",
         dataType: "json",
         success: function (data, textStatus, jqXHR) {
            console.log(JSON.stringify(data));
            setJwtToken(data);
            console.log(data.access_token);
            console.log(data.refresh_token);
            $login.hide();
            $registerBtn.hide();
            showUserInformation();
         },
         error: function (jqXHR, textStatus, errorThrown){

            console.log(JSON.stringify(jqXHR))
            console.log("tmpJson  readyState :: " +jqXHR.readyState);
            console.log("tmpJson  status :: " +jqXHR.status);
            console.log("tmpJson  statusText :: " +jqXHR.statusText );
            console.log("tmpJson  message :: " +jqXHR.responseJSON.message );

            if(jqXHR.status === 401){
               alert("알 수 없는 사용자입니다");
            } else {
              alert(jqXHR.responseJSON.message);
            }



         }
      });
   }

   function doLogout() {
      removeJwtToken();
      $login.show();
      $inputUserName.val("");
      $inputPassword.val("");
      $userInfo
         .hide()
         .find("#userInfoBody").empty();
      $modifyUserModal
          .find("#modifyUserModal").empty();
      $deleteUserModal
          .find("#deleteUserModal").empty();

      $modifyBtn.hide();
      $deleteBtn.hide();
      $registerBtn.show();

      initIndex();

   }


   function createAuthorizationTokenHeader() {
      var token = getJwtAccessToken();
      if (token) {
         return {"Authorization": "Bearer " + token};
      } else {
         return {};
      }
   }

   /**
    * 사용자 정보 비디오 목록 조회
    */
   function showUserInformation() {

      $userInfo
          .hide()
          .find("#userInfoBody").empty();

      $.ajax({
         url: "/api/user",
         type: "GET",
         contentType: "application/json; charset=utf-8",
         dataType: "json",
         headers: createAuthorizationTokenHeader(),
         success: function (data, textStatus, jqXHR) {
            var $userInfoBody = $userInfo.find("#userInfoBody");

            $userInfoBody.append($("<div>").text("Username: " + data.username));
            $userInfoBody.append($("<div>").text("Phone: " + data.phone));
            $userInfoBody.append($("<div>").text("Email: " + data.email)).attr("onclick","showVideo();");

            var $authorityList = $("<ul>");
            var userFlag = true;
            data.authorities.forEach(function (authorityItem) {
               $authorityList.append($("<li>").text(authorityItem.name));
               if(authorityItem.name ==='ROLE_ADMIN') userFlag = false;
            });
            console.log(userFlag);
            //권한 정보에 user 일 경우에만 파일 등록 버튼 보이도록
            if(userFlag) $fileUploadInfo.show();

            var $authorities = $("<div>").text("Authorities:");
            $authorities.append($authorityList);

            $userInfoBody.append($authorities);
            $userInfo.show();


            console.log(data.videos);
            showVideoList(data);


            console.log("내 정보 조회 값 확인 : " + JSON.stringify(data));
            showModifyInformation(data);
            showDeleteUser(data);
         }
      });
   }

   /**
    * 비디오저장 목록 생성
    * @param data
    */
   function showVideoList(data){

      let insertTr = ""; // 변수 선언
      data.videos.forEach(function (videoItem) {
         console.log(videoItem);
         // 동적으로 리스트 추가
         var videoId ="video_"+data.username+"_" +videoItem.id;
         insertTr += "<tr>";
         insertTr += "<td>" + videoItem.name + "</td>";
         insertTr += "<td>" + videoItem.uploadDate+ "</td>";
         insertTr += "<td>";
         insertTr += '<button type="button" id='+videoId+'>재생</button>';
         insertTr += "</td>";
         insertTr += "</tr>";

         //이벤트 리스터 등록
         $(document).on("click", "#"+videoId, function() {
            showVideo(videoId, videoItem.name);
         });

      });


      $("#responseVideoList").html(insertTr);
   }

   //사용자 선택에 따라 재생 동영상을 불러 옴
   function movieDialog(str) {
      //선택한 버튼의 동영상 경로를 불러옴
      $("#movie_src").attr("src", $(str).attr("value"));
      //동영상을 다시 load 함
      $("#a_video").load();
      //load한 동영상을 재생
      document.getElementById("a_video").play();
   }

   /** 비디오 재생**/
   function showVideo(id, name) {
      $videoInfo.show();
      var player = videojs('my_video');
      player.ready(function() {
         this.src({
            src: "/video-stream/"+name+"/token/"+getJwtAccessToken(),
            type : "video/mp4"
         });

      });
   }

   /**
    * 내 정보 수정 모달 데이터 세팅
    * @param data
    */
   function showModifyInformation(data){
      $("#modifyBtn").show();
      $("#modifyUserId").val(data.id);
      $("#modifyUserName").val(data.username);
      $("#modifyUserEmail").val(data.email);
      $("#modifyUserPhone").val(data.phone);
   }

   /**
    * 회원 탈퇴 확인 창 모달 세팅
    * @param data
    */
   function showDeleteUser(data){
      $("#deleteBtn").show();
      $("#deleteUserId").val(data.id);
      $("#deleteUserName").val(data.username);
   }

   /**
    * 에러 내용 확인 창
    * @param statusCode
    * @param message
    */
   function showResponse(statusCode, message) {
      $response
         .empty()
         .text(
            "status code: "
            + statusCode + "\n-------------------------\n"
            + (typeof message === "object" ? JSON.stringify(message) : message)
         );
   }



   // 클릭 이벤트 리스터 =============================================================
   /* 로그인 이벤트 */
   $("#loginForm").submit(function (event) {
      event.preventDefault();

      var $form = $(this);
      var formData = {
         username: $form.find('input[name="username"]').val(),
         password: $form.find('input[name="password"]').val()
      };

      doLogin(formData);
   });

   /* 로그아웃 이벤트 */
   $("#logoutButton").click(doLogout);

   /* 회원 등록 이벤트 */
   $("#registerUserBtn").click(function () {
      const data = $("form[name=registerForm]").serializeObject();
      console.log(data);

      $.ajax({
         url: "/api/register",
         type: "POST",
         data: JSON.stringify(data),
         contentType: "application/json; charset=utf-8",
         dataType: "json",
         headers: createAuthorizationTokenHeader(),
         success: function (res) {
            $('#registerModal')
               .modal("hide");
             alert(res.message);
            location.reload();
         },
         error: function (jqXHR, textStatus, errorThrown) {
            showResponse(jqXHR.status, jqXHR.responseJSON.message)
            alert(jqXHR.responseJSON.message);
            console.log(jqXHR);
            console.log(textStatus);
            console.log(errorThrown);

         }
      });
   });

   /** 내 정보 수정 모달 **/
   $modifyBtn.click(function () {
      $modifyUserModal
         .modal("show");
   });

   /** 회원 탈퇴 확인 모달 **/
   $deleteBtn.click(function () {
      $deleteUserModal
         .modal("show");
   });

   /** 회원정보 수정 이벤트 **/
   $("#modifyUserBtn").click(function () {
      const data = $("form[name=modifyUserForm]").serializeObject();
      console.log(data);

      $.ajax({
         url: "/api/modify-user",
         type: "PUT",
         data: JSON.stringify(data),
         contentType: "application/json; charset=utf-8",
         dataType: "json",
         headers: createAuthorizationTokenHeader(),
         success: function (data, textStatus, jqXHR) {
            console.log(data);
             alert("회원 정보 수정에 성공하였습니다.");
            setJwtToken(data);
                $modifyUserModal
                  .modal("hide")
                  .find("#modifyUserModal").empty();
                  showUserInformation();
            }, error: function (jqXHR, textStatus, errorThrown) {
            showResponse(jqXHR.status, jqXHR.responseJSON.message)
            alert(jqXHR.responseJSON.message);
            console.log(jqXHR);
            console.log(textStatus);
            console.log(errorThrown);

         }
      });
   });

   /** 회원 탈퇴 이벤트 **/
   $("#deleteUserBtn").click(function (){
      const data = $("form[name=deleteUserForm]").serializeObject();
      console.log(data);

      $.ajax({
         url: "/api/delete-user",
         type: "DELETE",
         data: JSON.stringify(data),
         contentType: "application/json; charset=utf-8",
         dataType: "json",
         headers: createAuthorizationTokenHeader(),
         success: function (data, textStatus, jqXHR) {
            console.log(data);
            alert(data.message);
            setJwtToken(data);
            $deleteUserModal
                .modal("hide")
                .find("#modifyUserModal").empty();
            doLogout();
         }, error: function (jqXHR, textStatus, errorThrown) {
            showResponse(jqXHR.status, jqXHR.responseJSON.message)
            alert(jqXHR.responseJSON.message);
            console.log(jqXHR);
            console.log(textStatus);
            console.log(errorThrown);

         }
      });
   });

   /*파일 등록 이벤트 */
   $("#btn_upload").click(function () {
      var formData = new FormData();
      var inputFile = $("input[name='upload_file']");
      console.log(inputFile);

      formData.append('file', $("input[name=upload_file]")[0].files[0])

      console.log(formData);
      $.ajax({
         url: "/api/file-upload",
         type: "POST",
         data: formData,
         contentType: false,
         processData: false,
         headers: createAuthorizationTokenHeader(),
         success: function (data, textStatus, jqXHR) {
            console.log(data);
            alert(data.message);
            $("#upload_file").val("");
            showUserInformation();
         }, error: function (jqXHR, textStatus, errorThrown) {
            showResponse(jqXHR.status, jqXHR.responseJSON.message);
            alert(responseJSON.message);
         }
      });

   })

   /** 화면 비우기 **/
   function initIndex() {
      //파일 폼 비우기기
      $("#upload_file").val("");
     //파일 리스트 비우기
      $("#responseVideoList").empty();

      //비디오 영역 닫기
      $videoInfo.hide();

      //파일업로드 영역 닫기
      $fileUploadInfo.hide();
   }

   /*회원 등록 모달 */
   $("#registerBtn").click(function () {
      $('#registerModal')
         .modal("show");
   });

   // form to json =============================================================

   jQuery.fn.serializeObject = function() {
      var obj = null;
      try {
         if (this[0].tagName && this[0].tagName.toUpperCase() == "FORM") {
            var arr = this.serializeArray();
            if (arr) {
               obj = {};
               jQuery.each(arr, function() {
                  console.log( this.value);
                  obj[this.name] = this.value;
               });
            }//if ( arr ) {
         }
      } catch (e) {
         alert(e.message);
      } finally {
      }

      return obj;
   };

   // INITIAL CALLS =============================================================
   if (getJwtAccessToken()) {
      $login.hide();
      $registerBtn.hide();
      showUserInformation();
   }

});
