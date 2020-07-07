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
      url:"http://localhost:8080"
  
    },
    methods: {
        disconnect:function(id){
            alert("disconnect!"+id);
            location.reload();
            $.ajax({
              url: /*this.url+*/'/Channel/disconnect',
              method: 'get',
              success: function (data) {
                console.log(data);
                if(data===true){
                  alert('add ' + topicName + ' success');
                  location.reload();
                }
                else{
                  alert('add the topic '+ topicName+' fail');
                }
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
        url: this.url+'/Channel/getAll',
        method: 'get',
        success: function (data) {
          console.log(data);
          if(data===true){
            alert('add ' + topicName + ' success');
            location.reload();
          }
          else{
            alert('add the topic '+ topicName+' fail');
          }
        },
        error: function (error) {
          console.log(error);
        }
      })
    }
  });
  
  
  