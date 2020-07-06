var vue = new Vue({
    el: '#products',
    data: {
      topics: [
        {topic_id:1, topic_name:'topic_test1' },
        {topic_id:2, topic_name:'topic_test2'},
        {topic_id:3, topic_name:'topic_test3'},
        {topic_id:4, topic_name:'topic_test4'},
        {topic_id:5, topic_name:'topic_test5'},
      ],
      messages:[
        {message_id:123, tag:'192.168.1.3', key:'msg1', store_time:'2020/6/11',operation:''},
        {message_id:134, tag:'192.168.2.3', key:'msg2', store_time:'2020/6/13',operation:''},
        {message_id:145, tag:'192.168.3.3', key:'msg3', store_time:'2020/6/14',operation:''},
        {message_id:156, tag:'192.168.4.3', key:'msg4', store_time:'2020/6/14',operation:''},
      ]
      
    },
    methods: {
     changeTopic:function(e){
         alert(e.target.value);
         var new_messages = [
            {message_id:000, tag:'192.168.1.3', key:'msg1', store_time:'2020/6/11',operation:''},
            {message_id:000, tag:'192.168.2.3', key:'msg2', store_time:'2020/6/13',operation:''},
            {message_id:000, tag:'192.168.3.3', key:'msg3', store_time:'2020/6/14',operation:''},
            {message_id:000, tag:'192.168.4.3', key:'msg4', store_time:'2020/6/14',operation:''},
          ]
          for(var i=0; i<this.messages.length && i<new_messages.length;i++)
          {
            this.$set(this.messages,i,new_messages[i]);
          }
         
     }
    }
  })
  