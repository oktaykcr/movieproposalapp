var form = $('#search-form');
var searchInput = $('#search-input');


var searchByName = function(){
    form.attr("action", "/movies/search/" + searchInput.val() + "/1");
}

searchInput.on("keyup", searchByName);

searchInput.on('keypress', function (event) {
    var regex = new RegExp("^[a-zA-Z0-9 ]+$");
    var key = String.fromCharCode(!event.charCode ? event.which : event.charCode);
    if (!regex.test(key)) {
        event.preventDefault();
        return false;
    }
});