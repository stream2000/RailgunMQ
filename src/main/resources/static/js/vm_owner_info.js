$(document).ready(function () {

  axios({
    method: "GET",
    url: 'http://192.168.0.3:8081/user-show-service/v1/user-show-by-id'+"?token="+sessionStorage.getItem('token'),
    
}).then(function(response) {
    console.log(response);
    response=response.data;
      main.user_id = response.user_id;
      main.user_name = "用户";
      main.money = response.total_money;
      //四个循环初始化四组数据

      for (var j = 0; j < response.upload_products.length; j++) {
        var upload_book = {
          product_id: response.upload_products[j].product_id,
          book_root: 'product_details.html?id=' + response.upload_products[j].product_id,
          image_url: response.upload_products[j].image_url,
          name: response.upload_products[j].name,
          price: response.upload_products[j].price
        };
        main.upload_products.push(upload_book);
      }


      for (var k = 0; k < response.buy_products.length; k++) {
        var buy_book = {
          product_id: response.buy_products[k].product_id,
          book_root: 'product_details.html?id=' + response.buy_products[k].product_id,
          image_url: response.buy_products[k].image_url,
          name: response.buy_products[k].name,
          uploader_name: response.buy_products[k].uploader_name,
          price: response.buy_products[k].price,
          trans_id:response.buy_products[k].order_id,
        };
		    main.trans_id=response.buy_products.order_id;
        main.buy_products.push(buy_book);
      }
})

  /*$.ajax({
    type: 'get',
	
    url: 'http://localhost:8081/user-show-service/v1/user-show-by-id/',
    beforeSend: function(xhr) {
                        xhr.setRequestHeader('Bearer':sessionStorage.getItem('token'));
                    },
    headers:{'Content-Type':'application/json;charset=utf8','Bearer':sessionStorage.getItem('token'},
    success: function (response) {
	console.log(response);
      main.user_id = response.user_id;
      main.user_name = response.user_name;
      main.money = response.total_money;
      //四个循环初始化四组数据

      for (var j = 0; j < response.upload_products.length; j++) {
        var upload_book = {
          product_id: response.upload_products[j].product_id,
          book_root: 'product_details.html?id=' + response.upload_products[j].product_id,
          image_url: response.upload_products[j].image_url,
          name: response.upload_products[j].name,
          price: response.upload_products[j].price
        };
        main.upload_products.push(upload_book);
      }


      for (var k = 0; k < response.buy_products.length; k++) {
        var buy_book = {
          product_id: response.buy_products[k].product_id,
          book_root: 'product_details.html?id=' + response.buy_products[k].product_id,
          image_url: response.buy_products[k].image_url,
          name: response.buy_products[k].name,
          uploader_name: response.buy_products[k].uploader_name,
          price: response.buy_products[k].price,
        };
		main.trans_id=response.buy_products.order_id;
        main.buy_products.push(buy_book);
      }
    },
    error: function (jqXHR, textStatus, errorThrown) {
      console.log(jqXHR);
      console.log(textStatus);
      console.log(errorThrown);
    }

  });*/

    

})
var main = new Vue({
  el: '#main',
  data: {
    user_id: "123",
    user_name: '自己看自己',
    money: '-',
    upload_products: [],
    buy_products: [],
	  trans_id:"",
  },
  methods: {
    remove: function (event) {
      if (confirm('确认删除当前商品吗?')) {
        console.log(event.target.id);
        var book_id = event.target.id;
       var url1='http://192.168.0.3:8081/upload-service/v1/Upload/'+book_id+"?token="+sessionStorage.getItem('token');
       $.ajax({
    
        type: 'DELETE',
        url: url1,
           success: function (response) {
             console.log(response);
 	        event.target.parentNode.parentNode.style.display = 'none';
        },

        });

       
      }
    },
    checkTrans: function (data) {
      var login_div = document.createElement('iframe');
      var login_close = document.createElement('i');
      var blocker = document.createElement('div');
      login_div.src = "check_trans.html?trans_id=" + data
      login_div.classList.add('login', 'new');
      login_close.classList.add('fa', 'fa-close', 'fa-2x', 'login_close', 'new');
      blocker.classList.add('box', 'new');
      var body = document.getElementById('container');
      body.insertBefore(login_div, body.childNodes[0]);
      body.insertBefore(login_close, body.childNodes[0]);
      body.insertBefore(blocker, body.childNodes[0]);
      $('html,body').css('overflow', 'hidden')
      $(".login_close").click(close_iframe);

    },

  }
});


function close_iframe() {
  $(".new").remove();
  $('html,body').css('overflow', '');
}