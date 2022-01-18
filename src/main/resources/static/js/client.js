/**
 * Created by stephan on 20.03.16.
 */

$(function () {
   // VARIABLES =============================================================
   var TOKEN_KEY = "jwtToken"
   var $notLoggedIn = $("#notLoggedIn");
   var $loggedIn = $("#loggedIn").hide();
   var $response = $("#response");
   var $login = $("#login");
   var $inputUserName = $("#inputUserName");
   var $inputUserEmail = $("#inputUserEmail");
   var $userInfo = $("#userInfo").hide();
   var $videoInfo = $("#userInfo");

   // FUNCTIONS =============================================================
   function getJwtToken() {
      return localStorage.getItem(TOKEN_KEY);
   }

   function setJwtToken(token) {
      localStorage.setItem(TOKEN_KEY, token);
   }

   function removeJwtToken() {
      localStorage.removeItem(TOKEN_KEY);
   }

   function doLogin(loginData) {
      $.ajax({
         url: "/api/authenticate",
         type: "POST",
         data: JSON.stringify(loginData),
         contentType: "application/json; charset=utf-8",
         dataType: "json",
         success: function (data, textStatus, jqXHR) {
            setJwtToken(data.id_token);
            $login.hide();
            $notLoggedIn.hide();
            $('#registerBtn').hide();
            showTokenInformation()
            showUserInformation();
         },
         error: function (jqXHR, textStatus, errorThrown) {
            if (jqXHR.status === 401) {
               $('#loginErrorModal')
                  .modal("show")
                  .find(".modal-body")
                  .empty()
                  .html("<p>" + jqXHR.responseJSON.message + "</p>");
            } else {
               throw new Error("an unexpected error occured: " + errorThrown);
            }
         }
      });
   }

   function doLogout() {
      removeJwtToken();
      $login.show();
      $inputUserName.val("");
      $inputUserEmail.val("");
      $userInfo
         .hide()
         .find("#userInfoBody").empty();
      $loggedIn
         .hide()
         .attr("title", "")
         .empty();
      $notLoggedIn.show();
      $(`#modifyBtn`).hide();
      $('#registerBtn').show();



   }

   function createAuthorizationTokenHeader() {
      var token = getJwtToken();
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
            data.authorities.forEach(function (authorityItem) {
               $authorityList.append($("<li>").text(authorityItem.name));
            });
            var $authorities = $("<div>").text("Authorities:");
            $authorities.append($authorityList);

            $userInfoBody.append($authorities);
            $userInfo.show();


            console.log(data.videos);
            showVideoList(data);
           //  let insertTr = ""; // 변수 선언
           // data.videos.forEach(function (videoItem) {
           //    console.log(videoItem);
           //    // 동적으로 리스트 추가
           //    var videoId ="video_"+data.username+"_" +videoItem.id;
           //    // insertTr += "<tr onclick='showVideo("+videoItem.id+", "+data.username+")' style='cursor:pointer;'>"; // body 에 남겨둔 예시처럼 데이터 삽입
           //    insertTr += "<tr>"; // body 에 남겨둔 예시처럼 데이터 삽입
           //       insertTr += "<td>" + videoItem.name + "</td>"; // body 에 남겨둔 예시처럼 데이터 삽입
           //       insertTr += "<td>" + videoItem.uploadDate+ "</td>";
           //       insertTr += "<td>";
           //          insertTr += '<button type="button" onclick="showVideo('+videoItem.id+');">재생</button>';
           //       insertTr += "</td>";
           //    insertTr += "</tr>";
           //
           //  });
           //    $("#responseVideoList").html(insertTr);

            console.log("내 정보 조회 값 확인 : " + JSON.stringify(data));
            showModifyInformation(data);
         }
      });
   }

   function showVideoList(data){

      // var col=document.getElementById('responseVideoList');

      let insertTr = ""; // 변수 선언
      data.videos.forEach(function (videoItem) {
         console.log(videoItem);
         // 동적으로 리스트 추가
         var videoId ="video_"+data.username+"_" +videoItem.id;
         // insertTr += "<tr onclick='showVideo("+videoItem.id+", "+data.username+")' style='cursor:pointer;'>"; // body 에 남겨둔 예시처럼 데이터 삽입
         insertTr += "<tr>"; // body 에 남겨둔 예시처럼 데이터 삽입
         insertTr += "<td>" + videoItem.name + "</td>"; // body 에 남겨둔 예시처럼 데이터 삽입
         insertTr += "<td>" + videoItem.uploadDate+ "</td>";
         insertTr += "<td>";
         insertTr += '<button type="button" id='+videoId+'>재생</button>';
         insertTr += "</td>";
         insertTr += "</tr>";
         //두 번째 버튼 이벤트
         $(document).on("click", "#"+videoId, function() {
            showVideo(videoId, videoItem.name);
         });

      });
      // col.innerHTML=insertTr;






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

   function showVideo(id, name) {
      videojs.Hls.xhr.beforeRequest = function(options) {
         console.log("토큰 정보 해야대 들어와써???????");
         options.headers = options.headers || {};
         options.headers.Authorization = getJwtToken();
         options.uri = options.uri + "?Authorization="+getJwtToken();
         console.log('options', options)
         return options;
      };

      var player = videojs('my_video');
      player.ready(function() {
         this.src({
            src: "/video-stream/"+name,
            // headers: createAuthorizationTokenHeader(),
            type : "video/mp4"
         });

      });

      // var
      // video = $('#videoInfoBody video')[0];
      // video.src = "/video-stream/"+name;
      // video.load();
      // video.play();
   }

   $("#video_user_1").click(function () {
      console.log(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
   })


   $("input[id^='video_']").click(function () {
      console.log("테이블 로우클릭??   ")

   });

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

   function showTokenInformation() {
      $loggedIn
         .text("Token: " + getJwtToken())
         .attr("title", "Token: " + getJwtToken())
         .show();
   }

   function showResponse(statusCode, message) {
      $response
         .empty()
         .text(
            "status code: "
            + statusCode + "\n-------------------------\n"
            + (typeof message === "object" ? JSON.stringify(message) : message)
         );
   }



   // REGISTER EVENT LISTENERS =============================================================
   $("#loginForm").submit(function (event) {
      event.preventDefault();

      var $form = $(this);
      var formData = {
         username: $form.find('input[name="username"]').val(),
         password: $form.find('input[name="password"]').val()
      };

      doLogin(formData);
   });

   $("#logoutButton").click(doLogout);

   $("#exampleServiceBtn").click(function () {
      $.ajax({
         url: "/api/person",
         type: "GET",
         contentType: "application/json; charset=utf-8",
         dataType: "json",
         headers: createAuthorizationTokenHeader(),
         success: function (data, textStatus, jqXHR) {
            showResponse(jqXHR.status, JSON.stringify(data));
         },
         error: function (jqXHR, textStatus, errorThrown) {
            showResponse(jqXHR.status, jqXHR.responseJSON.message)
         }
      });
   });

   $("#adminServiceBtn").click(function () {
      $.ajax({
         url: "/api/hiddenmessage",
         type: "GET",
         contentType: "application/json; charset=utf-8",
         dataType: "json",
         headers: createAuthorizationTokenHeader(),
         success: function (data, textStatus, jqXHR) {
            showResponse(jqXHR.status, data);
         },
         error: function (jqXHR, textStatus, errorThrown) {
            showResponse(jqXHR.status, jqXHR.responseJSON.message)
         }
      });
   });

   /**
    * 회원 등록 이벤트
    */
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
   $(`#modifyBtn`).click(function () {
      $('#modifyUserModal')
         .modal("show");
   });

   /**
    * 회원정보 수정 이벤트
    */
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
            if (textStatus === '200') {
               alert("회원 정보 수정에 성공하였습니다.");
               setJwtToken(data.id_token);
               $('#modifyUserModal')
                  .modal("hide")
                  .find("#modifyUserModal").empty();
               window.location.href = "/";
            } else {
               alert("회원 정보 수정에 실패                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              하였습니다.");
            }
          }, error: function (jqXHR, textStatus, errorThrown) {
            showResponse(jqXHR.status, jqXHR.responseJSON.message)
         }
      });
   });

   $("#btn_upload").click(function () {
      var formData = new FormData();
      var inputFile = $("input[name='upload_file']");
      console.log(inputFile);
      // var file = inputFile[0].files;
      formData.append('key1', 'value1');
      formData.append('key2', 'value2');

      // for(var i=0; i <files.length;i++) {
         formData.append('file', $("input[name=upload_file]")[0].files[0])

      // }
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
            alert('성공?');
            showUserInformation();
         }, error: function (jqXHR, textStatus, errorThrown) {
            showResponse(jqXHR.status, jqXHR.responseJSON.message)
         }
      });

   })

   $loggedIn.click(function () {
      $loggedIn
         .toggleClass("text-hidden")
         .toggleClass("text-shown");
   });



   $("#registerBtn").click(function () {
      // $("#registerModal").modal();

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
   if (getJwtToken()) {
      $login.hide();
      $notLoggedIn.hide();
      showTokenInformation();
      showUserInformation();
   }

});
