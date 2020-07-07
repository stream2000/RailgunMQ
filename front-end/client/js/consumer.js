var vue = new Vue({
  el: '#products',
  data: {
    messages: [
    ],
    connections: [],
    mode: "normal",
    clientName: "",
    topic: "",
    connectionPos: 0,
    url: "http://localhost:8080"

  },
  methods: {
    ack: function (id, event) {
      console.log(event.currentTarget);
      event.currentTarget.disabled = true;
      $.ajax({
        url: /*this.url+*/ '/spring2/consumer/send',
        data: {
          "name":GetQueryString("id"),
          "id": id,
          "topic":vue.topic,
          "isSuccess":true
        },
        method: 'POST',
        success: function (data) {

          if(data)
          {
            var messages = JSON.parse(sessionStorage.getItem(GetQueryString("id")));
            console.log("messages: "+messages);
            for (var i = 0; i < messages.length; i++) {
              if (messages[i].messageId == id) {
                var newMessage = {
                  "messageId":id,
                  "messageContent":messages[i].messageContent,
                  "isAcked":true
                }
                messages.splice(i,1,newMessage);
                renderForm(messages);
                sessionStorage.setItem(GetQueryString("id"),JSON.stringify(messages));
              }
            }
          }
          
         
        },
        error: function (error) {
          console.log(error);
        }
      })
    },
    rename: function () {
      var newName = prompt("Please input the new name", "");
      this.clientName = newName;
      if (newName != "" && newName != null) {
        $.ajax({
          url: /*this.url +*/ '/spring2/consumer/setChannelName',
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
                "role":"consumer",
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
      }
    },
    disconnect: function () {
      this.topic = "";
      this.clientName = ""
      $.ajax({
        url: /*this.url+*/ '/spring2/consumer/disconnect',
        method: 'GET',
        data:{"name":GetQueryString("id")},
        success: function (data) {
          console.log(data);
          var connections = JSON.parse(localStorage.getItem('connections'));
          connections.splice(vue.connectionPos, 1);
          localStorage.setItem('connections', JSON.stringify(connections));
          window.location = "index.html"
        },
        error: function (error) {
          console.log(error);
        }
      })
    },
    refresh: function () {
      location.reload();
    },
    clear:function(){
      var messages = JSON.parse(sessionStorage.getItem( GetQueryString("id")));
      for (var i = 0; i < messages.length; i++) {
        if (messages[i].isAcked) {
          messages.splice(i,1);
        }
      }
      renderForm(messages);
      sessionStorage.setItem( GetQueryString("id"),JSON.stringify(messages));
     
    }

  },
  mounted: function () {
    var self = this;
    var id = GetQueryString("id");
    console.log("id: ", id);
    var connections = JSON.parse(localStorage.getItem('connections'));

    for (var i = 0; i < connections.length; i++) {
      if (connections[i].connectionId == id) {
        this.clientName = connections[i].connectionName;
        this.topic = connections[i].topic;
        this.connectionPos = i;
      }
    }

    setInterval(function () {
      $.ajax({
        url: /*this.url+*/ '/spring2/consumer/getMessages',
        method: 'POST',
        data:{"name":GetQueryString("id"),"maxTime":1000},
        success: function (data) {
          
          var messages = JSON.parse(sessionStorage.getItem(id));
          if (messages != null ) {
            if(data.length != 0)
            {
              for(var i = 0; i<data.length;i++)
              {
                console.log(data.data);
                var message = {
                  "messageId": data[i].id,
                  "messageContent": data[i].data,
                  "isAcked":false
                }
                messages.push(message);
                renderForm(messages);
                sessionStorage.setItem(id,JSON.stringify(messages));
              }
              
            }
            
          } else if(messages == null ){
            sessionStorage.setItem(id, JSON.stringify(data));
          }
         
        },
        error: function (error) {
          console.log(error);
        }
      })
    }, 500)

  }
});

function renderForm(messages){
  if(messages.length == 0)
  {
    vue.messages = "[]";
  }
  for(var i = 0;i<messages.length;i++)
  {
    vue.$set(vue.messages,i,messages[i]);
  }
}

function GetQueryString(name) {
  var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
  var r = window.location.search.substr(1).match(reg); //search,查询？后面的参数，并匹配正则
  if (r != null)
    return unescape(r[2]);
  return null;
}