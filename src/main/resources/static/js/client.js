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

   function showUserInformation() {
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
            $userInfoBody.append($("<div>").text("Email: " + data.email));

            var $authorityList = $("<ul>");
            data.authorities.forEach(function (authorityItem) {
               $authorityList.append($("<li>").text(authorityItem.name));
            });
            var $authorities = $("<div>").text("Authorities:");
            $authorities.append($authorityList);

            $userInfoBody.append($authorities);
            $userInfo.show();
            console.log("내 정보 조회 값 확인 : " + JSON.stringify(data));
            showModifyInformation(data);
         }
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
