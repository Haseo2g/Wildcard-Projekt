$(document).ready(function() {

	var table = $('#data-table').DataTable({
		colReorder: true,
		dom: "<'row'<'col-sm-3'l><'col-sm-6'B><'col-sm-3'f>>" +
			"<'row'<'col-sm-12'tr>>" +
			"<'row'<'col-sm-5'i><'col-sm-7'p>>",
		buttons: [
			{
				extend: 'csv',
				className: 'btn btn-success',
				text: '<i class="fa fa-file-excel-o bold"></i> CSV',
				exportOptions: {
					columns: ':visible'
				}
			},
			{
				extend: 'excel',
				className: 'btn btn-success',
				text: '<i class="fa fa-file-excel-o bold"></i> XLSX',
				exportOptions: {
					columns: ':visible'
				}
			},
			{
				extend: 'pdfHtml5',
				className: 'btn btn-danger',
				text: '<i class="fa fa-file-pdf-o bold"></i> PDF',
				download: 'open',
				pageSize: getPaperSize(),
				exportOptions: {
					columns: ':visible'
				},
			},
			{
				extend: 'copy',
				className: 'btn btn-primary',
				text: '<i class="fa fa-clone bold"></i> COPIAR',
				exportOptions: {
					columns: ':visible'
				}
			},
			{
				extend: 'print',
				className: 'btn btn-primary',
				text: '<i class="fa fa-clone bold"></i> IMPRIMIR',
				pageSize: getPaperSize(),
				exportOptions: {
					columns: ':visible'
				},
			},
			//			{
			//				extend: 'colvis',
			//				className: 'btn btn-primary',
			//				text: 'Colunas'
			//			}
		],
		pageLength: 0,
		lengthMenu: [[10, 25, 50, 100, 200, 500, 1000, -1], [10, 25, 50, 100, 200, 500, 1000, "Todos"]],
		orderCellsTop: true,
		fixedHeader: true,
		initComplete: function() {
			var api = this.api();

			// For each column
			api
				.columns()
				.eq(0)
				.each(function(colIdx) {
					// Set the header cell to contain the input element
					var cell = $('.filters th').eq(
						$(api.column(colIdx).header()).index()
					);
					var title = $(cell).text();
					$(cell).html('<input type="text" placeholder="' + title + '" />');

					// On every keypress in this input
					$('input',
						$('.filters th')
							.eq($(api.column(colIdx).header()).index()))
						.off('keyup change')
						.on('keyup change', function(e) {
							e.stopPropagation();

							// Get the search value
							$(this).attr('title', $(this).val());
							var regexr = '({search})'; //$(this).parents('th').find('select').val();

							var cursorPosition = this.selectionStart;
							// Search the column for that value
							api
								.column(colIdx)
								.search(
									this.value != ''
										? regexr.replace('{search}', '(((' + this.value + ')))')
										: '',
									this.value != '',
									this.value == ''
								)
								.draw();

							$(this)
								.focus()[0]
								.setSelectionRange(cursorPosition, cursorPosition);
						});
				});
		}
	});
	$('a.toggle-vis').on('click', function(e) {
		e.preventDefault();

		// Get the column API object
		var column = table.column($(this).attr('data-column'));

		// Toggle the visibility
		column.visible(!column.visible());
	});

	$("#data-table thead th input").on('keyup change', function() {
		table.column($(this).parent().index() + ':visible')
			.search(this.value)
			.draw();
	});
});

function getPaperSize() {
	var tableWidth = $('#data-table').width();
	if (tableWidth < 2380) {
		return 'A3';
	} else if (tableWidth < 3408) {
		return 'A2';
	} else if (tableWidth < 7006) {
		return 'A1';
	} else {
		return 'A0';
	}
}