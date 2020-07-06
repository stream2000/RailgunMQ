var rM = document.getElementById('reportedMusics');
var rU = document.getElementById('reportedUsers');
var HrM = document.getElementById('handledReportedMusics');
var HrU = document.getElementById('handledReportedUsers')

window.onload = init();

function addReportedUser(reporter_id, user_id, name, detail, isHandled) {
    if (!rU && !HrU) {
        return;
    }
    var rowLength, newRow;
    if (isHandled) {
        rowLength = HrU.rows.length;
        newRow = HrU.insertRow(rowLength);
    } else {
        rowLength = rU.rows.length;
        newRow = rU.insertRow(rowLength);
    }
    newRow.insertCell(0).innerHTML = rowLength;
    newRow.insertCell(1).innerHTML = reporter_id;
    newRow.insertCell(2).innerHTML = user_id;
    newRow.insertCell(3).innerHTML = name;
    newRow.insertCell(4).innerHTML = detail;
    newRow.insertCell(5).innerHTML = 3;
    if (isHandled) {
        newRow.insertCell(6).innerHTML = "<span style='color: green'>已处理</span>";
    } else {
        rU.rows[rowLength].cells[5].contentEditable = true;
        newRow.insertCell(6).innerHTML = '<button onclick="limitUser(this)" type="button" class="user_showtoast btn btn-rounded btn-bordered btn-danger btn-sm waves-effect waves-light">封禁</button>';
        updateReportedUsers();
        updateReportNum();
    }
}

function addHandledUserReport(index) {
    var numHUR = sessionStorage.getItem('numHandledUserReports');
    if (numHUR) {
        sessionStorage.setItem('numHandledUserReports', parseInt(numHUR) + 1);
    } else {
        numHUR = 0;
        sessionStorage.setItem('numHandledUserReports', 1);
    }
    sessionStorage.setItem('user_reporter_id' + numHUR, rU.rows[index].cells[1].innerHTML);
    sessionStorage.setItem('user_id' + numHUR, rU.rows[index].cells[2].innerHTML);
    sessionStorage.setItem('user_name' + numHUR, rU.rows[index].cells[3].innerHTML);
    sessionStorage.setItem('user_detail' + numHUR, rU.rows[index].cells[4].innerHTML);
}

function removeReportedUser(index) {
    addHandledUserReport(index)
    rU.deleteRow(index);
    reOrderIndex(rU);
    updateReportedUsers();
    updateReportNum();
}

function addReportedMusic(reporter_id, music_id, name, uploader, detail, isHandled) {
    if (!rM && !HrM) {
        return;
    }
    var rowLength, newRow;
    if (isHandled) {
        rowLength = HrM.rows.length;
        newRow = HrM.insertRow(rowLength);
    } else {
        rowLength = rM.rows.length;
        newRow = rM.insertRow(rowLength);
    }
    newRow.insertCell(0).innerHTML = rowLength;
    newRow.insertCell(1).innerHTML = reporter_id;
    newRow.insertCell(2).innerHTML = music_id;
    newRow.insertCell(3).innerHTML = name;
    newRow.insertCell(4).innerHTML = uploader;
    newRow.insertCell(5).innerHTML = detail;
    if (isHandled) {
        newRow.insertCell(6).innerHTML = "<span style='color: green'>已处理</span>";
    } else {
        newRow.insertCell(6).innerHTML = "<button  onclick=\"removeMusic(this)\" type=\"button\" class=\"music_showtoast btn btn-rounded btn-bordered btn-danger btn-sm waves-effect waves-light\">删除</button>";
        updateReportedMusics();
        updateReportNum();
    }
}

function addHandledMusicReport(index) {
    var numHMR = sessionStorage.getItem('numHandledMusicReports');
    if (numHMR) {
        sessionStorage.setItem('numHandledMusicReports', parseInt(numHMR) + 1);
    } else {
        numHMR = 0;
        sessionStorage.setItem('numHandledMusicReports', 1);
    }
    sessionStorage.setItem('music_reporter_id' + numHMR, rM.rows[index].cells[1].innerHTML);
    sessionStorage.setItem('music_id' + numHMR, rM.rows[index].cells[2].innerHTML);
    sessionStorage.setItem('music_name' + numHMR, rM.rows[index].cells[3].innerHTML);
    sessionStorage.setItem('uploader' + numHMR, rM.rows[index].cells[4].innerHTML);
    sessionStorage.setItem('music_detail' + numHMR, rM.rows[index].cells[5].innerHTML);
}

function removeReportedMusic(index) {
    addHandledMusicReport(index);
    rM.deleteRow(index);
    reOrderIndex(rM);
    updateReportedMusics();
    updateReportNum();
}

function reOrderIndex(table) {
    var rowLength = table.rows.length;
    for (var i = 1; i < rowLength; i++) {
        table.rows[i].cells[0].innerHTML = i;
    }
}

function updateHandledUserReports() {
    var nHUR = sessionStorage.getItem('numHandledUserReports');
    if (nHUR && HrU) {
        for (var i = 0; i < nHUR; i++) {
            addReportedUser(sessionStorage.getItem('user_reporter_id' + i),
                sessionStorage.getItem('user_id' + i),
                sessionStorage.getItem('user_name' + i),
                sessionStorage.getItem('user_detail' + i),
                true)
        }
    }
}

function updateHandledMusicReports() {
    var nHMR = sessionStorage.getItem('numHandledMusicReports');
    if (nHMR && HrM) {
        for (var i = 0; i < nHMR; i++) {
            addReportedMusic(sessionStorage.getItem('music_reporter_id' + i),
                sessionStorage.getItem('music_id' + i),
                sessionStorage.getItem('music_name' + i),
                sessionStorage.getItem('uploader' + i),
                sessionStorage.getItem('music_detail' + i),
                true)
        }
    }
}

function updateReportedUsers() {
    var numReportedUsers = rU.rows.length - 1;
    sessionStorage.setItem('numReportedUsers', numReportedUsers);
    //document.getElementById('numRU').innerHTML = numReportedUsers;
}

function updateReportedMusics() {
    var numReportedMusics = rM.rows.length - 1;
    sessionStorage.setItem('numReportedMusics', numReportedMusics);
    //document.getElementById('numRM').innerHTML = numReportedMusics;
}

function updateReportNum() {
    var numRU = sessionStorage.getItem('numReportedUsers');
    var numRM = sessionStorage.getItem('numReportedMusics');
    var newNum = parseInt(numRM) + parseInt(numRU);
    if (numRM && numRU && newNum > 0) {
        document.getElementById('numReports').innerHTML = newNum;
    }
}

function removeMusic(btn) {
    var rowIndex = btn.parentNode.parentNode.rowIndex;
    var data = {
        'music_id': parseInt(rM.rows[rowIndex].cells[2].innerHTML),
        'admin_id': parseInt(sessionStorage.getItem('admin_id')),
        'details': rM.rows[rowIndex].cells[5].innerHTML
    };
    removeReportedMusic(rowIndex);
    $.ajax({
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        url: '/api/Admin/deleteMusic',
        headers: {
            'Authorization': 'Bearer ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (data) {

        }
    })
}

function limitUser(btn) {
    var rowIndex = btn.parentNode.parentNode.rowIndex;
    var data = {
        'user_id': parseInt(rU.rows[rowIndex].cells[2].innerHTML),
        'admin_id': parseInt(sessionStorage.getItem('admin_id')),
        'days': parseInt(rU.rows[rowIndex].cells[5].innerHTML),
        'details': rU.rows[rowIndex].cells[4].innerHTML
    };
    removeReportedUser(rowIndex);
    $.ajax({
        type: 'POST',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        url: '/api/Admin/deleteUser',
        headers: {
            'Authorization': 'Bearer ' + sessionStorage.getItem('token')
        },
        data: JSON.stringify(data),
        success: function (data) {

        }
    })
}

function requestReports() {
    $.ajax({
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        url: 'api/Admin/reports',
        headers: {
            'Authorization': 'Bearer ' + sessionStorage.getItem('token')
        },
        success: function (data) {
            var userReports = data['userReports'];
            var musicReports = data['musicReports'];
            for (var i = 0; i < userReports.length && rU; i++) {
                var temp = userReports[i];
                addReportedUser(temp['reporter_id'], temp['reported_id'], temp['user_name'], temp['detail'], false);
            }
            for (var i = 0; i < musicReports.length && rM; i++) {
                var temp = musicReports[i];
                addReportedMusic(temp['reporter_id'], temp['reported_id'], temp['music_name'], temp['author'], temp['detail'], false);
            }
            sessionStorage.setItem('numReportedUsers', userReports.length);
            sessionStorage.setItem('numReportedMusics', musicReports.length);
            updateReportNum();
        }
    })
}

function logout() {
    sessionStorage.setItem('token', null);
    window.location = 'admin_login.html';
}

function init() {
    requestReports();
    updateHandledMusicReports();
    updateHandledUserReports();
}