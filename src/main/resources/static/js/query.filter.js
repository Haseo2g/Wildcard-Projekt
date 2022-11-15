/**
	Method responsible for redirect after filtering
 */
function getQueryParams(idQuery) {
	var params = "";
	$("#filter-table tr").each(function() {
		var id = '#' + $(this).attr('id');
		var result = concatQuery(params, id);
		params += result
	});
	const path = location.pathname.split('/');
	const proj = path[1]
	if (proj == 'query')
		$(location).attr('href', '/query/execute?idQuery=' + idQuery + '&params=' + params);
	else
		$(location).attr('href', '/' + path[1] + '/query/execute?idQuery=' + idQuery + '&params=' + params);
}

/**
	Method responsible for concating each parameter used as filter
 */
function concatQuery(query, id) {
	var result = ""
	if ($(id + '_4').text() == "limit") {
		result = concatLimit(id)
		return result;
	}

	var selected = $(id + '_1').find(":selected").text();
	if (selected == "") {
		return result;
	}
	if (query == "") {
		result = " WHERE "
	} else {
		result += " AND "
	}
	result += decideConcat(id);
	return result;
}

/**
	Decide on which filter the param will progress through
	I.E.: if the object is a boolean, it will concat with either 'obj = true' or 'obj = false' while an int can be filtered with = > < and between
 */
function decideConcat(id) {
	var type = $(id + '_4').text()
	switch (type) {
		case "boolean": return concatBool(id)
		case "text": return concatString(id)
		case "number": return concatNumber(id)
		case "date": return concatDate(id)
		case "datetime": return concatDateTime(id)
		case "limit": return concatLimit(id)
	}
	return "";
}

/**
	Method responsible for concating any text field
 */
function concatString(id) {
	var result = "";
	var obj = id.substring(1);
	var option = $(id + '_1').find(":selected").text();
	var param1 = $(id + '_2').val()
	switch (option) {
		case '=':
			if (param1 != "")
				result = obj + " = '" + param1 + "'";
			break;
		case 'contém':
			if (param1 != "") {
				result = obj + " LIKE '%" + param1 + "%'";
			}
	}
	return result;
}

/**
	Method responsible for concating any text field
 */
function concatNumber(id) {
	var result = "";
	var obj = id.substring(1);
	var option = $(id + '_1').find(":selected").text();
	var param1 = $(id + '_2').val()
	var param2 = $(id + '_3').val()
	switch (option) {
		case 'entre':
			if (param1 != "" && param2 != "" && param2 > param1)
				result = obj + ' BETWEEN ' + param1 + ' AND ' + param2;
			break;
		case '>':
		case '<':
		case '=':
			if (param1 != "") {
				result = obj + ' ' + option + ' ' + param1
			}
	}
	return result;
}

/**
	Method responsible for concating boolean values
 */
function concatBool(id) {
	var result = "";
	var obj = id.substring(1);
	result = obj + ' = '
	if ($(id + '_1').val() != 'empty')
		result += $(id + '_1').val();
	return result;
}

/**
	Method responsible for concating any date and time field
 */
function concatDateTime(id) {
	var result = "";
	var obj = id.substring(1);
	var option = $(id + '_1').find(":selected").text();
	var param1 = $(id + '_2').val().replace("T", " ");
	var param2 = $(id + '_3').val().replace("T", " ");
	switch (option) {
		case 'entre':
			if (param1 != "" && param2 != "" && param2 > param1) {
				param1 += ':00'
				param2 += ':00'
				result = obj + " BETWEEN '" + param1 + "' AND '" + param2 + "'";
			}
			break;
		case '>':
		case '<':
		case '=':
			if (param1 != "") {
				param1 += ':00'
				result = obj + ' ' + option + "'" + param1 + "'"
			}
	}
	return result;
}

/**
	Method responsible for concating date fields without time
 */
function concatDate(id) {
	var result = "";
	var obj = id.substring(1);
	var option = $(id + '_1').find(":selected").text();
	switch (option) {
		case 'entre':
			if (param1 != "" && param2 != "" && (new Date(param2).getTime() > new Date(param1).getTime())) {
				result = obj + " BETWEEN '" + param1 + "' AND '" + param2 + "'";
			}
			break;
		case '>':
		case '<':
		case '=':
			if (param1 != "") {
				result = obj + ' ' + option + ' ' + param1
			}
	}
	return result;
}

/**
	Method responsible for concating limit on query
 */
function concatLimit(id) {
	var result = "";
	var limit = $(id + '_1').val();
	if (limit != "" && limit > 0)
		result = " LIMIT " + ($(id + '_1').val());
	return result;
}

/**
	Method responsible for showing queries tooltips
 */
$(function() {
	$('[data-toggle="tooltip"]').tooltip()
})