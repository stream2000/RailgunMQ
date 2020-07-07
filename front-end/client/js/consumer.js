var vue = new Vue({
    el: '#products',
    data: {
      messages: [{
        messageId: "001",
        messageContent: "message-1"
      },
      {
        messageId: "002",
        messageContent: "message-2"
      },
      {
        messageId: "003",
        messageContent: "message-3"
      },
      ],
      connections:[],
      mode: "normal",
      addStatus: true,
      clientName:"",
      topic:"",
      connectionPos:0,
      url:"http://localhost:8080"
  
    },
    methods: {
      ack:function(id,event){
          
        $.ajax({
            url: this.url+'/producer/ack',
            data:{"id":id},
            method: 'get',
            success: function (data) {
              console.log(data);
              console.log(event.currentTarget);
              event.currentTarget.disabled=true;
              
            },
            error: function (error) {
              console.log(error);
            }
          })
      },
      rename:function(){
        var newName = prompt("Please input the new name","");
        this.clientName = newName;
        if (newName != "" && newName != null) {
          $.ajax({
            url: this.url+'/consumer?topic=',
            method: 'POST',
            success: function (data) {
              console.log(data);
            },
            error: function (error) {
              console.log(error);
            }
          })
        } else {
  
        }
      },
      disconnect:function(){
        this.topic="";
        this.clientName=""
        $.ajax({
          url: this.url+'/producer/disconnect',
          method: 'GET',
          success: function (data) {
            console.log(data);
            var connections = JSON.stringify(localStorage.getItem('connections'));
            connections.splice(this.connectionPos,1);
            localStorage.setItem('connections',JSON.stringify(connections));
            window.location = "index.html"
          },
          error: function (error) {
            console.log(error);
          }
        })
      },
      refresh:function(){
        location.reload();
      }
      
    },
    mounted: function () {
      var self = this;
      var id = GetQueryString("id");
      console.log("id: ",id);
      var connections = JSON.stringify(localStorage.getItem('connections'));
      
      for(var i=0;i<connections.length;i++)
      {
        if(connections[i].connectonId == id)
        {
          this.clientName = connections[i].connectionName;
          this.topic = connections[i].topic;
          this.connectionPos = i;
        }
      }

      /*setInterval(function(){
        $.ajax({
            url: this.url+'/producer/disconnect',
            method: 'GET',
            success: function (data) {
              console.log(data);
              var connections = JSON.stringify(localStorage.getItem('connections'));
              connections.splice(this.connectionPos,1);
              localStorage.setItem('connections',JSON.stringify(connections));
              window.location = "index.html"
            },
            error: function (error) {
              console.log(error);
            }
          })
      },500)*/
      
    }
  });
  
function GetQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg); //search,查询？后面的参数，并匹配正则
    if (r != null)
      return unescape(r[2]);
    return null;
  }