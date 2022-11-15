window.setTimeout(function() {
	$(".alert.lgpage").fadeTo(2000, 0).slideUp(500, function() {
	});
}, 3000);

$(document).ready(function() {
	$('.sidebar').hover(function() {
		$('.sidebar').toggleClass("sidebarExpand");
		$('.content').toggleClass("contentExpand");
		$('.logo').toggleClass("logoExpand");
		$('.nav-items').toggleClass("nav-itemsExpand")
		$('.nav-item').toggleClass("nav-itemExpand");
		$('.navtext').toggleClass("navtextExpand");
		$('.navtext').css("animation", "show 1s")
	});
});

function popup(id) {
	id = '#' + id
	var xhr = new XMLHttpRequest();

	xhr.open($(id).attr('method'), $(id).attr('formaction'));
	xhr.onload = function(event) {
		alert(event.target.response); // raw response
	};
	// or onerror, onabort
	var formData = new FormData(document.forms[0]);

	formData.set("_method", $(id).attr('method'))

	xhr.send(formData);
}
