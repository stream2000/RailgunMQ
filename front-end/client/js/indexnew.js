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
    mode: "normal",
    addStatus: true,
    clientName:"",
    topic:"",
    messagePrefix:"",
    messageNumber:0,
    url:"http://localhost:8080"

  },
  methods: {
    connProducer: function () {
      console.log("add")
      var topicName = prompt("Please input the topic name that you want to connect","");
      this.topic = topicName;
      $("#consumer").hide();
      $("#producer").show();
      if (topicName != null) {
        $.ajax({
          url: this.url+'/addTopic?topic=',
          method: 'POST',
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
      } else {
      }

    },
    connConsumer: function () {
      console.log("add")
      var topicName = prompt("Please input the topic name that you want to subscribe","");
      this.topic = topicName;
      $("#consumer").show();
      $("#producer").hide();
      if (topicName != null) {
        $.ajax({
          url: this.url+'/addTopic?topic=',
          method: 'POST',
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
      } else {
      }

    },
    disconnect:function(){
      
    },
    push:function(){
      var messages = [];
      var prefix = this.messagePrefix;
      for(var i=0; i<this.messageNumber;i++)
      {
        var message = prefix + i;
        messages.push(message);
      }
      console.log(messages);
      $.ajax({
        url: this.url+'/push?topic=',
        method: 'POST',
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
    /*$.ajax({
      url: self.url+'/topics',
      method: 'GET',
      success: function (data) {
        console.log(data);
        for (var i = 0; i < data.length; i++) {
          vue.$set(vue.topics, i, data[i]);
        }
      },
      error: function (error) {
        console.log(error);
      }
    })*/
  }
});


function showBar(topic_id, word) {
  var id = '#topic' + topic_id + ' .newBar';
  console.log($(id).is(":visible"));
  console.log($(id).text());
  if ($(id).is(":visible") && word == $(id).text()) {
    $(id).toggle(200);
    return;
  }

  $(id).text(word);
  if ($(id).is(":hidden")) {
    $(id).toggle(200);
  }
}