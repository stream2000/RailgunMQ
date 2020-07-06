function login() {
    var form = document.getElementById('loginForm');
    var formData = new FormData(form);
    var encoded = btoa(formData.get('user_id') + ':' + formData.get('password'));
    $.ajax({
        type : 'GET',
        url : '/api/login?role=admin',
        headers: {
            'Authorization': 'Basic ' + encoded
        },
        success : function(data){
            sessionStorage.setItem('admin_id', formData.get('user_id'));
            sessionStorage.setItem('token', data['access_token']);
            window.location = 'reportedUsers.html';
        }
    })
}


history.pushState(null, null, document.URL);
window.addEventListener('popstate', function () {
    history.pushState(null, null, document.URL);
});