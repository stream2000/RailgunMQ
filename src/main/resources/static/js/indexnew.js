var vue = new Vue({
  el: '#products',
  data: {
    topics: [{
      topicName: "",
      active: false,
      nextSubscription: null
    }],
    mode: "normal",
    addStatus: true,
    url:"http://localhost:8080"

  },
  methods: {
    idGen: function (topicName) {
      return 'topic' + topicName;
    },
    status: function (topicName, active) {
      var status_word = 'the status of ' + topicName + ' is ' + active;
      showBar(topicName,status_word);
    },
    consumer: function(topicName,nextSubscription) {
      if(nextSubscription===null){       
        showBar(topicName,"null");
      }
      else {
        var consumer_word = ' <table class="table table-bordered"><tbody><tr>'+nextSubscription[0]+'</tr> ' 
        for (var i = 0; i < nextSubscription.length; i++) {
          consumer_word = consumer_word + '<tr>' + nextSubscription[i]+"</tr>";
        }
        consumer_word = consumer_word +'</tbody></table>'
        showBar(topicName, consumer_word);
      }

    },
    topicButton: function (topic_id, topic) {
      var topic_word = 'the topic of ' + topic_id + ' is ' + topic;
      showBar(topic_id, topic_word);
    },
    reset: function (topic_id) {
      resetTopic(topic_id);
    },
    del: function (topicName) {
      var id = '#topic' + topicName;
      $.ajax({
        url: /*this.url+*/'/deleteTopic?topic='+topicName,
        method: 'DELETE',
        success: function (data) {
          console.log(data);
          if(data===true){
            $(id).remove();
            alert('delete the topic' + topicName + ' success');
          }
          else{
            alert('delete the topic '+ topicName+' fail');
          }
        },
        error: function (error) {
          console.log(error);
        }
      })

    },
    addTopic: function () {
      console.log("add")
      var topicName = prompt("Please input the topic name","topicName");
      if (topicName != null) {
        $.ajax({
          url: /*this.url+*/'/addTopic?topic='+topicName,
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
    refresh: function () {
      location.reload();

    }
  },
  mounted: function () {
    var self = this;
    $.ajax({
      url: /*self.url+*/'/topics',
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
    })
  }
});


function showBar(topic_id, word) {
  var id = '#topic' + topic_id + ' .newBar';
  console.log($(id).is(":visible"));
  console.log($(id).text());
  if ($(id).is(":visible") && word == $(id).html()) {
    $(id).toggle(200);
    return;
  }

  $(id).html(word);
  if ($(id).is(":hidden")) {
    $(id).toggle(200);
  }
}