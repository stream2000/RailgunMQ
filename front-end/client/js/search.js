//scripts needed to operate "search.html"
$(function(){
    var paras = location.search;
    var result = paras.match(/[^\?&]*=[^&]*/g);
    paras = {}; //让paras变成没有内容的json对象
    for (var i in result) {
        var temp = result[i].split('='); //split()将一个字符串分解成一个数组,两次遍历result中的值分别为['itemId','xx']
        paras[temp[0]] = temp[1];
    }
    
    var searchKey = decodeURIComponent(paras.search);
    //update();
	var url="http://192.168.0.3:8081/book-show-service/v1/book-information-by-title/"+searchKey+"?token="+sessionStorage.getItem('token');
    $.ajax({
        type: 'get',
        url: url,
        success: function (datas) {
            if(datas.product.length!=0){

                $('#presentation').empty();
                $('#presentation').hide();
                $('#presentation').append('<div class="pretitle"><h3 class="accompanyItem">图片</h3> '+ 
                                            '<h3 class="accompanyItem">书籍名称</h3>'+
                                            '<h3 class="accompanyItem">上传者</h3>'+
                                            '<h3 class="accompanyItem">价格</h3>'+
                                            '<HR style="FILTER: progid:DXImageTransform.Microsoft.Shadow(color:#987cb9,direction:145,strength:15)" width="90%" color=#987cb9 SIZE=1></div>');
                for(var i=0;i<datas.product.length;i++){
                    $('#presentation').append('<div class="dropdown"><a href= "product_details.html?id='+datas.product[i].id +'" class="pretitle"><img src="'+datas.product[i].image_url+'" class="accompanyImage" />'+
                                                        '<h4 class="accompanyInfo">'+datas.product[i].name+'</h4></a>'+
                                                        '<h4 class="accompanyInfo">'+datas.product[i].uploader_name+'</h4>'+
                                                        '<h4 class="accompanyInfo">'+datas.product[i].price+'</h4></div>');
            
                }
                $('#presentation').show('slow');
                //update();
                $('.breadcrumb-item').css('font-size','16px');
                $(".breadcrumb-item[name='accompany']").css('font-size','20px');

            }
            else{
                $('#presentation').append('<div class="failInfo"><h2>抱歉，没有找到相关信息</h2></div>');
            }
        }
      });
})

function getUrlQueryString(names, urls) {
	urls = urls || window.location.href;
	urls && urls.indexOf("?") > -1 ? urls = urls
			.substring(urls.indexOf("?") + 1) : "";
	var reg = new RegExp("(^|&)" + names + "=([^&]*)(&|$)", "i");
	var r = urls ? urls.match(reg) : window.location.search.substr(1)
			.match(reg);
	if (r != null && r[2] != "")
		return unescape(r[2]);
	return null;
};

$('.breadcrumb-item').click(function(){
    $('.breadcrumb-item').css('font-size','16px');
    $(this).css('font-size','20px');
    update($(this).attr('name'));
})

function update(){
    $('#presentation').empty();
    $('#presentation').hide();

    var dataSet=JSON.parse(sessionStorage.getItem('searchResult'));

    //if(type==='accompany'){
        if(true||dataSet['accompanys'].length)  
        $('#presentation').append('<div class="pretitle"><h3 class="accompanyItem">图片</h3> '+ 
                                        '<h3 class="accompanyItem">书籍名称</h3>'+
                                        '<h3 class="accompanyItem">上传者</h3>'+
                                        '<h3 class="accompanyItem">价格</h3>'+
                                        '<HR style="FILTER: progid:DXImageTransform.Microsoft.Shadow(color:#987cb9,direction:145,strength:15)" width="90%" color=#987cb9 SIZE=1></div>');
        else
        
        /*for(data of dataSet['accompanys']){
            $('#presentation').append('<div class="dropdown"><div><img src="'+data['img']+'" class="accompanyImage" />'+
                                                        '<h4 class="accompanyInfo">'+data['book_name']+'</h4>'+
                                                        '<h4 class="accompanyInfo">'+data['uploader']+'</h4>');
            
        }*/
        for(var i=0;i<5;i++){
            $('#presentation').append('<div class="dropdown"><div class="pretitle"><img src="'+"images/戒烟.jpg"+'" class="accompanyImage" />'+
                                                        '<h4 class="accompanyInfo">'+"追风筝撒大苏打撒旦萨达萨达萨达萨达撒啊大苏打的人"+'</h4>'+
                                                        '<h4 class="accompanyInfo">'+"小符士大夫沙发罗斯福和杀害放假撒法和监控"+'</h4>'+
                                                        '<h4 class="accompanyInfo">'+"124123"+'</h4>');
            
        }
   // }
    $('#presentation').show('slow');
}
