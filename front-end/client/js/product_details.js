var productinfo = new Vue({
  el: '#wrapper-bg',
  data: {
    name: "product_name",
    image_url: "images/pack-7.jpg",
    uploader_name: "李荣浩",
    type: "",
    publisher: "",
    detail: "这是一本新书，因为罗老师讲的太好了，不需要书，所以我想把它卖了。这是一本九成新的书，原本售价100元，现在1.99美元卖出，想要的直接拍别犹豫",
    price: 3,
    id: 0,
  },
  methods: {

  }

});

$(document).ready(function () {
  var paras = location.search;
  var result = paras.match(/[^\?&]*=[^&]*/g);
  paras = {}; //让paras变成没有内容的json对象
  for (var i in result) {
    var temp = result[i].split('='); //split()将一个字符串分解成一个数组,两次遍历result中的值分别为['itemId','xx']
    paras[temp[0]] = temp[1];
  }
  productinfo.id = paras.id;


  $("#buy").on('click', function () {
    window.location.href = "confirm_purchase.html?id=" + productinfo.id;
  });

  var url="http://192.168.0.3:8081/book-show-service/v1/book-information-by-id/"+productinfo.id+"?token="+sessionStorage.getItem('token');
  console.log( sessionStorage.getItem('token'));
  $.ajax({
    type: 'GET',
    url: url,
    success: function (response) {
      console.log(response);
      response = response.product;
      productinfo.name = response.name;
      productinfo.image_url = response.image_url;
      productinfo.uploader_name = response.uploader_name;
      productinfo.detail = response.detail;
      productinfo.price = response.price;
	    productinfo.type = response.type;
		  productinfo.publisher = response.publisher;
    }
  });

});