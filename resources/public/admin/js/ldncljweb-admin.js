$.ajax({
	url : "/admin/posts/titles",
	dataType : 'json',
	success : function(data, textStatus, jqXHR) {
		var posts = [];
		
		$.each(data, function(key, post) {
			posts.push('<div>' + $('<div>').text(post["title"]).html() + '</div>');
		});
		
		$('#posts-list').append(posts.join(''));
	}
});