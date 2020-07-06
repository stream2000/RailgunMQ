$(function () {

    //元素
    var oFileInput = $("#input-file-to-destroy");				//选择文件按钮

    var oFileList_parent = $("#fileList_parent");	//表格
    var oFileList = $(".fileList");					//表格tbody
    var oFileBtn = $("#fileBtn");					//上传按钮

    var flieList = [];								//数据，为一个复合数组
    var sizeObj = [];								//存放每个文件大小的数组，用来比较去重

    //点击选择文件按钮选文件
    oFileInput.on("change", function () {
        analysisList(this.files);
    })

    function analysisList(obj) {
        if (obj.length < 1) {
            return false;
        }

        for (var i = 0; i < obj.length; i++) {

            var fileObj = obj[i];
            var size = fileObj.size;
            /*var name = fileObj.name;
            var type = fileType(name);


            if (sizeObj.indexOf(size) != -1) {
                alert('“' + name + '”已经选择，不能重复上传');
                continue;
            }

            var itemArr = [fileObj, name, size, type];
            flieList.push(itemArr);

            sizeObj.push(size);*/
            loadFile(fileObj, i, obj.length);

        }


    };


    //生成列表
    function createList() {
        oFileList.empty();
        for (var i = 0; i < flieList.length; i++) {

            var fileData = flieList[i];
            var objData = fileData[0];
            var name = fileData[1];
            var size = fileData[2];
            var type = fileData[3];
            var volume = bytesToSize(size);
            var artist = fileData[4];

            if(!artist){
                artist = '未知';
            }

            var oTr = $("<tr></tr>");
            var str = '<td><cite title="' + name + '">' + name + '</cite></td>';
            str += '<td>' + type + '</td>';
            str += '<td>' + volume + '</td>';
            str += '<td contentEditable="true">' + artist +'</td>';
            str += '<td>';
            str += '<div class="progressParent">';
            str += '<p class="progress"></p>';
            str += '<span class="progressNum">0%</span>';
            str += '</div>';
            str += '</td>';
            str += '<td><a href="#" class="operation">删除</a></td>';
            oTr.html(str);
            oTr.appendTo(oFileList);
        }
    }

    //删除表格行
    oFileList.on("click", "a.operation", function () {
        var oTr = $(this).parents("tr");
        var index = oTr.index();
        console.log(index);
        oTr.remove();
        flieList.splice(index, 1);
        sizeObj.splice(index, 1);

        //console.log(flieList);
        //console.log(sizeObj);

    });


    //上传
    oFileBtn.on("click", function () {
        oFileBtn.off();
        var tr = oFileList.find("tr");
        var successNum = 0;
        var table = document.getElementById('fileList_parent');
        oFileList.off();
        oFileList.find("a.operation").text("等待上传");


        for (var i = 0; i < tr.length; i++) {
            var author = table.rows[i + 1].cells[3].innerHTML;
            uploadFn(tr.eq(i), i, author, '/');
        }

        function uploadFn(obj, i, author, url) {

            var formData = new FormData();
            var arrNow = flieList[i];

            var result = arrNow[0];
            formData.append("accompFile", result);

            var name = arrNow[1];
            formData.append("fileName", name);

            formData.append("author", author);

            var progress = obj.find(".progress");			//上传进度背景元素
            var progressNum = obj.find(".progressNum");		//上传进度元素文字
            var oOperation = obj.find("a.operation");		//按钮

            oOperation.text("正在上传");
            oOperation.off();

            var request = $.ajax({
                type: "POST",
                url: 'api/Admin/uploadAccomp',
                data: formData,
                processData: false,
                contentType: false,
                headers: {
                    'Authorization': 'Bearer ' + sessionStorage.getItem('token')
                },

                xhr: function () {
                    var xhr = $.ajaxSettings.xhr();
                    if (onprogress && xhr.upload) {
                        xhr.upload.addEventListener("progress", onprogress, false);
                        return xhr;
                    }
                },

                success: function () {
                    oOperation.text("成功");
                    oOperation.css('color', 'green');
                    successNum++;
                    console.log(successNum);
                    if (successNum == tr.length) {
                       window.location = '/accomp_upload.html';
                    }
                },

                
                error: function () {
                    oOperation.text("重传");
                    oOperation.on("click", function () {
                        request.abort();	
                        uploadFn(obj, i);
                    });
                }

            });


            function onprogress(evt) {
                var loaded = evt.loaded;	
                var tot = evt.total;		
                var per = Math.floor(100 * loaded / tot); 
                progressNum.html(per + "%");
                progress.css("width", per + "%");
            }

        }


    });
    function loadFile(file, i, length) {
            var url = file.urn || file.name;

        ID3.loadTags(url, function() {
            var tags = ID3.getAllTags(url);
            addFileInfo(file, tags, i, length);
        }, {
            tags: ["title","artist","album","picture"],
            dataReader: ID3.FileAPIReader(file)
        });
    }

    function addFileInfo(file, tags, i, length){
        var name = file.name;
        var size = file.size;
        var type = fileType(name);

        if (sizeObj.indexOf(size) != -1) {
            alert('“' + name + '”已经选择，不能重复上传');
            return;
        }
        var itemArr = [file, name, size, type, tags.artist];
        flieList.push(itemArr);
        sizeObj.push(size);
        if(i == length - 1){
            createList()
            oFileList_parent.show();
            oFileBtn.show();
        }
    }

})


//字节大小转换，参数为b
function bytesToSize(bytes) {
    var sizes = ['Bytes', 'KB', 'MB'];
    if (bytes == 0) return 'n/a';
    var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
    return (bytes / Math.pow(1024, i)).toFixed(1) + ' ' + sizes[i];
};

//通过文件名，返回文件的后缀名
function fileType(name) {
    var nameArr = name.split(".");
    return nameArr[nameArr.length - 1].toLowerCase();
}
