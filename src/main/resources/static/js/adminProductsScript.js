$(document).ready(function () {
    var errorMessage = $(".error-message").text();
    if (errorMessage.trim() !== "") {
        $("#addProductModal").modal("show");
    }
});

$(".item-list").on("click", ".btn-update", function () {
    var row = $(this).closest("tr");
    var id = row.find(".item-id").text();
    var name = row.find(".item-name").text();
    var description = row.find(".item-description").text();
    var price = row.find(".item-price").text();
    var category = row.find(".item-category").text();

    $("#edit-id").val(id);
    $("#edit-name").val(name);
    $("#edit-description").val(description);
    $("#edit-price").val(price);
    $("#edit-category").val(category);

    $("#editModal").modal("show");
});
