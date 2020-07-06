$(document).ready(function(){
    
})

var register=new Vue({
    el:'#forms',
    data:{
      user_name:'',
      password_1:'',
      password_2:'',
      legacy_check:false,
      is_checked:true,
    },
    methods:{
    confirm:function(){
      
        if(this.is_checked)
        {
            $(".notion").remove();
            if(this.password_1 != this.password_2)
            {
                var notion=$("<div></div>");
                notion.text("两次密码输入不一致,请重新输入!");
                notion.addClass("notion");
                $("#register_confirm").before(notion);
                this.password_1='';
                this.password_2='';
                return ;
            }
            else if(!this.legacy_check)
            {
                var notion=$("<div></div>");
                notion.text("请先阅读并同意条款！");
                notion.addClass("notion");
                $("#register_confirm").before(notion);
                return ;
            }
            for(var i=0;i<this.user_name.length;i++)
            {
                if(this.user_name[i]=='<' || this.user_name[i]=='>' 
                    || this.user_name[i]=='&' || this.user_name[i]=='"' 
                    || this.user_name[i]=='\\' || this.user_name[i]=='/')
                    {
                        var notion=$("<div></div>");
                        notion.text("用户名中有非法字符，请重新输入！");
                        notion.addClass("notion");
                        $("#register_confirm").before(notion);
                        this.user_name='';
                        return ;
                    }
            }

            $.ajax({
                type: 'post',
                dataType: 'json',
                contentType: 'application/json',
                url: 'http://192.168.0.3:8081/user-service/v1/User/Account',
                data: JSON.stringify({ "Account": this.user_name, "Pwd": this.password_1 }),
                success: function(response) {
           
                    console.log(response);
                    if (response.succeed)
                    {
                        var id_display=$("<div></div>");
                        id_display.html("<h2>注册成功！</h2><h3>登录时请使用注册时输入的账号</h3>");
                        id_display.addClass("id_display");
                        $("#forms").append(id_display);
                    }
                    else {
                        var text=response.errorMessage;
                        alert(text);
                        this.user_name='';
                    }
                  },
                error: function(error){}

                });
        }    
    },
    }
   
});
  