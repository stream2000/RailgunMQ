var own_pic = "images/miss.jpg";
$(document).ready(function () {
  $("#login").click(login);
  $("#search").on('keydown', function (e) {
    if (e.keyCode == 13) {
      if ($('#search').val()) {
        window.location.href = 'search.html?search=' + $('#search').val();
      }
      return false;
    }
  });
  if (sessionStorage.getItem('token')) {
    $('#login').addClass('is_login');
    $('#login').after('<div class="user logined"><div id="nav-icon"><img src="' + own_pic + '"></div></div>');
    $('#nav-icon').after('<div class="dropdown-logout logined" align="center" id="logout"><h4 class="logout">注销</h4></div>');
    $('#nav-icon').after('<div class="dropdown-notify logined" align="center" id="notify"><h4 class="notify">系统消息</h4></div>');
    $('#nav-icon').after('<div class="dropdown-userInfo logined" align="center" id="user_info"><h4 class="userInfo">我的信息</h4></div>');
    $("#logout").click(logout);
    $("#user_info").click(function () {
      //通过cookie获取当前用户id
      window.location.href = "own_info.html"; //！！！！！data需添加，此处get上传者的id
    });
    $('#notify').hover(function () {
      createBox();
    }, function () {
      $('.msg_box').remove();
    });
  };



  /*axios.get('api/banner/init',{
    headers:{
              'Authorization':'Bearer' + sessionStorage.getItem('token')
          }
  }
  ).then(function(response){
  response=response.data;
  
  own_pic=response.own_pic;
  
   
  })
  .catch(function(error){console.log(error);});*/

});


function login() {
  var login_div = document.createElement('iframe');
  var login_close = document.createElement('i');
  var blocker = document.createElement('div');
  login_div.src = "page-login.html";
  login_div.classList.add('login', 'new');
  login_close.classList.add('fa', 'fa-close', 'fa-2x', 'login_close', 'new');
  blocker.classList.add('box', 'new');
  var body = document.getElementById('container');
  body.insertBefore(login_div, body.childNodes[0]);
  body.insertBefore(login_close, body.childNodes[0]);
  body.insertBefore(blocker, body.childNodes[0]);
  $('html,body').css('overflow', 'hidden')
  $(".login_close").click(login_complete);

}

function login_complete() {
  $(".new").remove();
  $('html,body').css('overflow', '');

   $('#login').addClass('is_login');
  $('#login').after('<div class="user logined"><div id="nav-icon"><img src="'+own_pic+'"></div></div>');
  $('#nav-icon').after('<div class="dropdown-logout logined" align="center" id="logout"><h4 class="logout">注销</h4></div>');
  $('#nav-icon').after('<div class="dropdown-notify logined" align="center" id="notify"><h4 class="notify">系统消息</h4></div>');
  $('#nav-icon').after('<div class="dropdown-userInfo logined" align="center" id="user_info"><h4 class="userInfo">我的信息</h4></div>');
  $("#logout").click(logout);
  $("#user_info").click(function() {
    //通过cookie获取当前用户id
    window.location.href="own_info.html";//！！！！！data需添加，此处get上传者的id
  });
$('#notify').hover(function(){createBox();},function(){$('.msg_box').remove();});


}

function logout() {
  sessionStorage.removeItem('token');
  $(".logined").remove();
  $('#login').removeClass('is_login');
  location.reload();
}




function createBox() {
  var msg_box = document.createElement('div');
  msg_box.classList.add('msg_box');
  msg_box.id = 'msg_box';
  msg_box.innerHTML = msg_box.innerHTML + '<i class="fa fa-caret-left" id="msg_arrow" aria-hidden="true"></i>';
  $('#nav-icon').after(msg_box);
  $('#msg_box').animate({
    opacity: 1,
    width: '180px',
    height: '176px'
  });

  var msgs = document.createElement('ul');
  msgs.innerHTML = '<li>您的粉丝达到了2人!</li><hr><li>您的举报得到了回复</li><hr><li>新私信</li>';
  msg_box.appendChild(msgs);
}