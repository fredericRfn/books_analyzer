function search() {
	$.getJSON(window.location.href + "search", {url: $("#url").val()}, function(json) {
		$.get(window.location.href + '/characterTemplate.html', function(template) {
			$("#resultsBlock").empty().append(Mustache.to_html(template, json));
			//$("#resultsBlock").empty().append(JSON.stringify(json));
		});
	});	
}