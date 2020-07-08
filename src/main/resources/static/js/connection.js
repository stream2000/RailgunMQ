var vue = new Vue({
    el: '#products',
    data: {
      connects: [],
      mode: "normal",
      addStatus: true,
    },
    methods: {
        disconnect:function(id){
            alert("disconnect!"+id);
            location.reload();
            $.ajax({
              url: '/channel/delete',
              data:{"id":id},
              method: 'get',
              success: function (data) {
                console.log(data);
               
              },
              error: function (error) {
                console.log(error);
              }
            })
          }
        },  
    mounted: function () {
      var self = this;
      $.ajax({
        url: '/channel/getAll',
        method: 'get',
        success: function (data) {
          console.log("data",data);
          if(data.length != 0)
          {
            for(var i=0;i<data.length;i++)
          {
             var connect = {
              channelId:data[i].channelId,
              ConnectionName: data[i].connectionName,
              role: data[i].role,
              topic:data[i].topic
             }
            vue.$set(vue.connects,i,connect);
          }
          }
          
          
        },
        error: function (error) {
          console.log(error);
        }
      })
    }
  });
  
  
  