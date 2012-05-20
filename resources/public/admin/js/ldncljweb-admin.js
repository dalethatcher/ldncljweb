function loadPostData(id, title) {
    $('#posts-edit').text('Loading "' + title + '"...');
    $.ajax({
        url: "/admin/posts/" + id,
        dataType: "json",
        success: function(data, textStatus, jqXHR) {
            $('#posts-edit').text(data['body']);
        }
    });
}

$.ajax({
	url : "/admin/posttitles",
	dataType : 'json',
	success : function(data, textStatus, jqXHR) {
		var posts = [];
		
		$.each(data, function(key, post) {
			posts.push('<div data-id="' + post["_id"] + '">' + $('<div>').text(post["title"]).html() + '</div>');
		});
		
		$('#posts-list').append(posts.join(''));
        $('#posts-list div').click(function () {
            var element = $(this);

            $('#posts-list div').removeClass("selected");
            element.addClass("selected");
            loadPostData(element.attr("data-id"), element.text());
        });
	}
});