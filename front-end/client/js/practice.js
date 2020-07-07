Vue.component('todo-item', {
  props: ['todo'],
  template: '<li>{{ todo.text }}</li>'
});

var app7 = new Vue({
  el: '#app-7',
  data: {
    groceryList: [
      { id: 0, text: '蔬菜' },
      { id: 1, text: '奶酪' },
      { id: 2, text: '随便其它什么人吃的东西' }
    ]
  }
});

var datax={a:1,
user_id:'none'
};

var vm = new Vue({
  el:'#test',
  data:datax





});

datax.user_id='123';


var button=new Vue({
  el:'#test_button',
  data:{
    isbuttondisabled:true
  }
});


var vif_test=new Vue({
  el:'#vif_test',
  data:{
    display:true,
    message:'helo'
  }
});

var input_test=new Vue({
  el:'#input_test',
  data:{
    input_value:'12'
  },
  computed:{
    reverse_value:function(){return this.input_value.split('').reverse().join('');}
  }
});





var watchExampleVM = new Vue({
  el: '#watch-example',
  data: {
    question: '',
    answer: 'I cannot give you an answer until you ask a question!'
  },
  watch: {
    // 如果 `question` 发生改变，这个函数就会运行
    question: function (newQuestion, oldQuestion) {
      this.answer = 'Waiting for you to stop typing...'
      this.debouncedGetAnswer()
    }
  },
  created: function () {
    // `_.debounce` 是一个通过 Lodash 限制操作频率的函数。
    // 在这个例子中，我们希望限制访问 yesno.wtf/api 的频率
    // AJAX 请求直到用户输入完毕才会发出。想要了解更多关于
    // `_.debounce` 函数 (及其近亲 `_.throttle`) 的知识，
    // 请参考：https://lodash.com/docs#debounce
    this.debouncedGetAnswer = _.debounce(this.getAnswer, 500)
  },
  methods: {
    getAnswer: function () {
      if (this.question.indexOf('?') === -1) {
        this.answer = 'Questions usually contain a question mark. ;-)'
        return
      }
      this.answer = 'Thinking...'
      var vm = this
      axios.get('https://yesno.wtf/api')
        .then(function (response) {
          vm.answer = _.capitalize(response.data.answer)
        })
        .catch(function (error) {
          vm.answer = 'Error! Could not reach the API. ' + error
        })
    }
  }
});

var reg=new Vue({
el:'#reg_div',
data:{
name:'',
password:'',
id:''
},
methods:{
submit:function(){
  axios.post('http',{
    name:this.name,
    password:this.password
  })
  .then(function(response){console.log(response);})
  .catch(function(error){console.log(error);});
}

}


});












console.log(vm.data);
console.log(datax);
