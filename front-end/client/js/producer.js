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
          url: this.url + '/producer?topic=',
          method: 'POST',
          success: function (data) {
            console.log(data);
            if (data === true) {
              alert('add ' + topicName + ' success');
              location.reload();
            } else {
              alert('add the topic ' + topicName + ' fail');
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
        url: this.url + '/producer/disconnect',
        method: 'GET',
        success: function (data) {
          console.log(data);
          var connections = JSON.stringify(localStorage.getItem('connections'));
          connections.splice(this.connectionPos, 1);
          localStorage.setItem('connections', JSON.stringify(connections));
          window.location = "index.html"
        },
        error: function (error) {
          console.log(error);
        }
      })
    },
    push: function () {
      var messages = [];
      var prefix = this.messagePrefix;
      for (var i = 0; i < this.messageNumber; i++) {
        var message = prefix + i;
        messages.push(message);
      }
      console.log(messages);
      $.ajax({
        url: this.url + '/publish',
        method: 'POST',
        data: {

        },
        success: function (data) {
          console.log(data);
          
        },
        error: function (error) {
          console.log(error);
        }
      })
    },
  },
  mounted: function () {
    var self = this;
    var id = GetQueryString("id");
    console.log("id: ", id);
    var connections = JSON.stringify(localStorage.getItem('connections'));

    for (var i = 0; i < connections.length; i++) {
      if (connections[i].connectonId == id) {
        this.clientName = connections[i].connectionName;
        this.topic = connections[i].topic;
        this.connectionPos = i;
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