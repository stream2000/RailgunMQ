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
        }
     
    },
    mounted: function () {
      var self = this;
      
    }
  });
  
  
  