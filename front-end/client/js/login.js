$(document).ready(function(){

});


var login=new Vue({
    el:'#forms',
    data:{
      user_id:'',
      password:'',
    },
    methods:{
        confirm: function () {
            var encoded = btoa(this.user_id + ':' + this.password);
            if (this.user_id == '' || this.password == '') {
                $(".notion").remove();
                var notion = $("<div></div>");
                notion.text("输入栏不能为空!");
                notion.addClass("notion");
                $("#confirm").before(notion);
                this.user_id = '';
                this.password = '';
            }
        
           
                
        $.ajax({
            type: 'post',
            dataType: 'json',
            contentType: 'application/json',
            url: 'http://192.168.0.3:8081/auth-service/v1/User/login',
            data: JSON.stringify({ "Account": this.user_id, "Pwd": this.password }),
            /*headers:{
                'Authorization':'Basic '+encoded
            },*/
            success:function(data){
                console.log(data);
               
                sessionStorage.setItem('token',data.token);
                var id_display=$("<div></div>");
                id_display.html("<h1>登录成功!</h1>");
                id_display.addClass("id_display");
                $("#forms").append(id_display);
            },
            error: function () {
                var id_display = $("<div></div>");
                id_display.html("<h1>登录失败!</h1>");
                id_display.addClass("id_display");
                $("#forms").append(id_display);
                
            }
        })
           
    },
    }
    
});




  