$('#post-save').button().click(function () {
	id = $('#post-id').val();
	
	if (id.length == 0) {
	    $.ajax({
	        url: "/admin/posts/new",
	    	type: "POST",
	    	data: {
	    		"title": $('#post-title').val(),
	    		"date": $('#post-date').val(),
	    		"body": $('#post-edit-area').val()
	    	},
	    	success: function(data) {
	    		loadPosts();
	    	}
	    });
		
	}
	else {
	    $.ajax({
	        url: "/admin/posts/" + id,
	    	type: "PUT",
	    	data: {
	    		"title": $('#post-title').val(),
	    		"date": $('#post-date').val(),
	    		"body": $('#post-edit-area').val()
	    	},
	    	success: function(data) {
	    		loadPosts();
	    	}
	    });
	}
	
	clearPostFields();
});

function clearPostFields() {
    $('#post-id').val('');
    $('#post-title').val('');
    $('#post-date').val('');
    $('#post-edit-area').val('');

    $('#posts-list div').removeClass('selected');
}

$('#post-new').button().click(function() {
	clearPostFields();
});

$('#post-delete').button().click(function() {
	id = $('#post-id').val();
	clearPostFields();
	
	if (id.length > 0) {
		$.ajax({
			url: "/admin/posts/" + id,
			type: "DELETE",
			success: function(data) {
				loadPosts();
			}
		});
	}
});

function loadPostData(id, title) {
    $('#post-edit-area').val('Loading "' + title + '"...');
    $.ajax({
        url: "/admin/posts/" + id,
        dataType: "json",
        success: function(data, textStatus, jqXHR) {
            $('#post-id').val(data['_id']);
            $('#post-date').val(data['date']);
            $('#post-title').val(data['title']);
            $('#post-edit-area').val(data['body']);
        }
    });
}

function loadPosts() {
	$('#posts-list').html('');
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
}

loadPosts();