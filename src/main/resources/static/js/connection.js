var vue = new Vue({
    el: '#products',
    data: {
      connects: [{
          connectId:"111",
        connectName: "123",
        connectRole: "Producer",
      }],
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
          for(var i=0;i<data.length;i++)
          {
            vue.$set(vue.connects,i,data[i]);
          }
          
        },
        error: function (error) {
          console.log(error);
        }
      })
    }
  });
  
  
  