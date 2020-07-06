
$(document).ready(function () {

  $("#submit").click(submit);
  console.log($('#booktype input[name="tab_type"]:checked').val());
});

function submit() {
  var book_name = $("#book_name").val();
  $(".notion").remove();
  for (var i = 0; i < book_name.length; i++) {
    if (book_name[i] == '<' || book_name[i] == '>' ||
      book_name[i] == '&' || book_name[i] == '"' ||
      book_name[i] == '\\' || book_name[i] == '/') {
      var notion = $("<div></div>");
      notion.text("书籍名中有非法字符，请重新输入！");
      notion.addClass("notion");
      $("#submit").before(notion);
      $("#book_name").val('');
      return;
    }
  }
  if ($("#input-file-to-destroy").val() == '') {
    var notion = $("<div></div>");
    notion.text("请上传一张图片！");
    notion.addClass("notion");
    $("#submit").before(notion);
    return;
  }
  if ($("#price").val() == '') {
    var notion = $("<div></div>");
    notion.text("请填写一个价格！");
    notion.addClass("notion");
    $("#submit").before(notion);
    return;
  }
  /*var form = document.querySelector("#form");
  var formlist = new FormData(form);*/
  var image_url = "images/pack-" + (Math.floor(Math.random() * 10000) % 12 + 1) + ".jpg";
 
  //formlist.append('uploader_id', uploader_id);
  $.ajax({
    headers: {
      'Bearer':  sessionStorage.getItem('token'),
    },
    type: "POST",
    url: "http://192.168.0.3:8081/upload-service/v1/Upload"+"?token="+sessionStorage.getItem('token'),
    dataType: 'json',
    contentType: 'application/json',
    data: JSON.stringify({
      book_title: $("#book_name").val(),
      book_type: $('#booktype input[name="tab_type"]:checked').val(),
      picture_url: image_url,
      publisher: $("#book_publisher").val(),
      //author;
      describe: $("#detail").val(),
      string_price: $("#price").val(),
      //user_id;
      book_price: parseInt($("#price").val()),
    }),

    success: function () {
      window.location.href = "own_info.html";
    }
  });
}