var vue = new Vue({
  el: '#producers',
  data: {
    connections: [],
    addStatus: true,
    clientName: "",
    topic: "",
    connectionPos: 0,
    messagePrefix: "",
    messageNumber: 0,
    ackMessage:"",
    url: "http://localhost:8080"

  },
  methods: {
    idGen: function (connectonId) {
      return 'connection' + connectonId;
    },
    rename: function () {
      var newName = prompt("Please input the new name", "");
      this.clientName = newName;
      if (newName != "" && newName != null) {
        $.ajax({
          url: /*this.url +*/ '/spring/producer/setChannelName',
          method: 'POST',
          data:{
            "id":GetQueryString("id"),
            "name":newName
          },
          success: function (data) {
            console.log(data);
            if (data === true) {
              alert('add ' + newName + ' success');
              var thisConnection = {
                "connectionId":GetQueryString("id"),
                "connectionName":newName,
                "role":"producer",
                "topic":vue.topic
              }
              var connections = JSON.parse(localStorage.getItem('connections'));
              console.log("renamepos: " + vue.connectionPos);
              connections.splice(vue.connectionPos, 1,thisConnection);
              localStorage.setItem('connections', JSON.stringify(connections));
              location.reload();
            }
          },
          error: function (error) {
            console.log(error);
          }
        })
      } else {

      }
    },
    disconnect: function () {
      $(".connButton").show();
      $(".disconnButton").hide();
      $("#consumer").hide();
      $("#producer").hide();
      this.topic = "";
      this.clientName = ""
      $.ajax({
        url: /*this.url + */'/spring/producer/disconnect',
        method: 'GET',
        data:{"id":GetQueryString("id")},
        success: function (data) {
          console.log(data);
          if(data)
          {
            var connections = JSON.parse(localStorage.getItem('connections'));
            connections.splice(vue.connectionPos, 1);
            localStorage.setItem('connections', JSON.stringify(connections));
            window.location = "index.html";
          }
          else {
            window.location = "index.html";
          }
          
        },
        error: function (error) {
          console.log(error);
        }
      })
    },
    push: function () {
      var prefix = this.messagePrefix;
      var text = "";
      if(this.messageNumber<=0)
      {
        alert("error number!");
        return;
      }
      for (var i = 0; i < this.messageNumber; i++) {
        var message = prefix + i;
        $.ajax({
          url: /*this.url +*/ '/spring/producer/publish/string',
          method: 'POST',
          data: {
            "id":GetQueryString("id"),
            "topic":this.topic,
            "content":message
          },
          success: function (data) {
            console.log(data);
            

            $.ajax({
              url: /*this.url + */'/spring/producer/acks',
              method: 'GET',
              data: {
                "id":GetQueryString("id"),
              },
              success: function (data) {
                console.log(data);
                for(var i= 0;i<data.length;i++)
                {
                  text = text + JSON.stringify(data[i]);
                  text = text+"<br>";
                }
               
                vue.ackMessage = text;
                
              },
              error: function (error) {
                console.log(error);
              }
            })

          },
          error: function (error) {
            console.log(error);
          }
        })
      }
      
      

    },
  },
  mounted: function () {
    var self = this;
    var id = GetQueryString("id");
    console.log("id: ", id);
    var connections = JSON.parse(localStorage.getItem('connections'));
    for (var i = 0; i < connections.length; i++) {
      var nowConnection = connections[i];

      if (nowConnection.connectionId == id) {
        this.clientName = nowConnection.connectionName;
        this.topic = nowConnection.topic;
        this.connectionPos = i;
        console.log("pos: " + this.connectionPos);
      }
    }


   


  }
});


function GetQueryString(name) {
  var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
  var r = window.location.search.substr(1).match(reg); //search,查询？后面的参数，并匹配正则
  if (r != null)
    return unescape(r[2]);
  return null;
}