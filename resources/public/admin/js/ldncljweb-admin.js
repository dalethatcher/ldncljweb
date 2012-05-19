$.ajax({
	url : "/admin/posts/name",
	dataType : 'json',
	success : function(data, textStatus, jqXHR) {
		var posts = [];
		
		$.each(data, function(key, post) {
			posts.push('<div>' + $('<div>').text(post).html() + '</div>');
		});
		
		$('#posts-list').append(posts.join(''));
	}
});