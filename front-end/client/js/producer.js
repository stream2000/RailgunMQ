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
      producers:[
        {client_id:123, client_addr:'192.168.1.0', language:'en', version:'1.0'},
        {client_id:134, client_addr:'192.168.2.0', language:'en', version:'1.0'},
        {client_id:145, client_addr:'192.168.3.0', language:'zh', version:'2.0'},
        {client_id:156, client_addr:'192.168.4.0', language:'zh', version:'3.0'},
      ]
      
    },
    methods: {
     changeTopic:function(e){
         alert(e.target.value);
         var new_producers = [
            {client_id:000, client_addr:'192.168.1.0', language:'en', version:'1.0'},
            {client_id:000, client_addr:'192.168.2.0', language:'en', version:'1.0'},
            {client_id:000, client_addr:'192.168.3.0', language:'zh', version:'2.0'},
            {client_id:000, client_addr:'192.168.4.0', language:'zh', version:'3.0'},
          ]
          for(var i=0; i<this.producers.length && i<new_producers.length;i++)
          {
            this.$set(this.producers,i,new_producers[i]);
          }
         
     }
    }
  })
  