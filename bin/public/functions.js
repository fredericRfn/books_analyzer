function search() {
	var title= $("#title").val(); 
	var author= $("#author").val();
	var character= $("#character").val();
	var url= $("#url").val();
	var urlCompleta="https://books-analyzer.herokuapp.com/" + "search"+"?title="+title+"&author="+author+"&character="+character+"&url="+url;
	console.log(urlCompleta);
	$.ajax({
	    url: urlCompleta,
	    headers: {"Access-Control-Allow-Origin" : "*"},
	    dataType: 'json',
	    cache: 'false',
	    type: 'GET',
	    success: function(json){
	    	$.get(window.location.href + '/characterTemplate.html', function(template) {
	   			$("#resultsBlock").empty().append(Mustache.to_html(template, json));
	    	});
	    }
	});
}