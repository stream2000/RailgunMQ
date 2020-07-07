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
      messagePrefix:"",
      messageNumber:0,
      url:"http://localhost:8080"
  
    },
    methods: {
      idGen: function (connectonId) {
        return 'connection' + connectonId;
      },
      rename:function(){
        var newName = prompt("Please input the new name","");
        this.clientName = newName;
        if (newName != "" && newName != null) {
          $.ajax({
            url: this.url+'/producer?topic=',
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
        $(".connButton").show();
        $(".disconnButton").hide();
        $("#consumer").hide();
        $("#producer").hide();
        this.topic="";
        this.clientName=""
        $.ajax({
          url: this.url+'/producer/disconnect',
          method: 'GET',
          success: function (data) {
            console.log(data);
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
      localStorage.getItem('connections');
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