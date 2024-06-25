$(document).ready(function () {
    var errorMessage = $(".error-message").text();
    if (errorMessage.trim() !== "") {
        $("#billModal").modal("show");
    }
    $(document).on('submit', '.remove-from-bill-form', function (event) {
        event.preventDefault();

        var form = $(this);
        var url = form.attr('action');
        var data = form.serialize();

        $('#billModal').addClass('loading');

        $.post(url, data)
            .done(function (response) {
                $('#billModal .modal-body').html($(response).find('.modal-body').html());
            })
            .fail(function () {
                alert('Failed to remove item from bill.');
            })
            .always(function () {

                $('#billModal').removeClass('loading');
            });
    });

    $('#billModal').on('hide.bs.modal', function (e) {

        if ($('#billModal').hasClass('loading')) {
            e.preventDefault();
        }
    });

});