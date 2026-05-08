
$(function() {
    cropper = '';
    let $confirmBtn = $("#confirm-button");
    let $resetBtn = $("#reset-button");
    let $cutBtn = $("#cut-button");
    let $saveBtn = $("#save-button");
    let $newclubImage = $("#new-club-image");
    let $currentclubImage = $("#current-club-image");
    let $resultImage = $("#cropped-new-club-image");
    let $clubImage = $("#clubImage");

    $newclubImage.hide();
    $cutBtn.hide();
    $resetBtn.hide();
    $confirmBtn.hide();
    $saveBtn.hide();

    $("#club-image-file").change(function(e) {
    if (e.target.files.length === 1) {
    const reader = new FileReader();
    reader.onload = e => {
    if (e.target.result) {
    if (!e.target.result.startsWith("data:image")) {
    alert("이미지 파일을 선택하세요.");
    return;
}

    let img = document.createElement("img");
    img.id = 'new-club';
    img.src = e.target.result;
    img.setAttribute('width', '100%');

    $newclubImage.html(img);
    $newclubImage.show();
    $currentclubImage.hide();

    let $newImage = $(img);
    $newImage.cropper({aspectRatio: 13/2});
    cropper = $newImage.data('cropper');

    $cutBtn.show();
    $confirmBtn.hide();
    $resetBtn.show();
}
};

    reader.readAsDataURL(e.target.files[0]);
}
});

    $resetBtn.click(function() {
    $currentclubImage.show();
    $newclubImage.hide();
    $resultImage.hide();
    $resetBtn.hide();
    $cutBtn.hide();
    $confirmBtn.hide();
    $saveBtn.hide();
    $clubImage.val('');
});

    $cutBtn.click(function () {
    let dataUrl = cropper.getCroppedCanvas().toDataURL();

    if (dataUrl.length > 1000 * 1024) {
    alert("이미지 파일이 너무 큽니다. 1024000 보다 작은 파일을 사용하세요. 현재 이미지 사이즈 " + dataUrl.length);
    return;
}

    let newImage = document.createElement("img");
    newImage.id = "cropped-new-club-image";
    newImage.src = dataUrl;
    newImage.width = 640;
    $resultImage.html(newImage);
    $resultImage.show();
    $confirmBtn.show();

    $confirmBtn.click(function () {
    $newclubImage.html(newImage);
    $cutBtn.hide();
    $confirmBtn.hide();
    $clubImage.val(dataUrl);
    $saveBtn.show();
});
});

    $saveBtn.click(function() {
    $("#imageForm").submit();
})
});

    $(function() {
    cropper = '';
    let $confirmBtn = $("#confirm-button");
    let $resetBtn = $("#reset-button");
    let $cutBtn = $("#cut-button");
    let $saveBtn = $("#save-button");
    let $newclubImage = $("#new-club-image");
    let $currentclubImage = $("#current-club-image");
    let $resultImage = $("#cropped-new-club-image");
    let $clubImage = $("#clubImage");

    $newclubImage.hide();
    $cutBtn.hide();
    $resetBtn.hide();
    $confirmBtn.hide();
    $saveBtn.hide();

    $("#club-image-file").change(function(e) {
    if (e.target.files.length === 1) {
    const reader = new FileReader();
    reader.onload = e => {
    if (e.target.result) {
    if (!e.target.result.startsWith("data:image")) {
    alert("이미지 파일을 선택하세요.");
    return;
}

    let img = document.createElement("img");
    img.id = 'new-club';
    img.src = e.target.result;
    img.setAttribute('width', '100%');

    $newclubImage.html(img);
    $newclubImage.show();
    $currentclubImage.hide();

    let $newImage = $(img);
    $newImage.cropper({aspectRatio: 13/2});
    cropper = $newImage.data('cropper');

    $cutBtn.show();
    $confirmBtn.hide();
    $resetBtn.show();
}
};

    reader.readAsDataURL(e.target.files[0]);
}
});

    $resetBtn.click(function() {
    $currentclubImage.show();
    $newclubImage.hide();
    $resultImage.hide();
    $resetBtn.hide();
    $cutBtn.hide();
    $confirmBtn.hide();
    $saveBtn.hide();
    $clubImage.val('');
});

    $cutBtn.click(function () {
    let dataUrl = cropper.getCroppedCanvas().toDataURL();

    if (dataUrl.length > 1000 * 1024) {
    alert("이미지 파일이 너무 큽니다. 1024000 보다 작은 파일을 사용하세요. 현재 이미지 사이즈 " + dataUrl.length);
    return;
}

    let newImage = document.createElement("img");
    newImage.id = "cropped-new-club-image";
    newImage.src = dataUrl;
    newImage.width = 640;
    $resultImage.html(newImage);
    $resultImage.show();
    $confirmBtn.show();

    $confirmBtn.click(function () {
    $newclubImage.html(newImage);
    $cutBtn.hide();
    $confirmBtn.hide();
    $clubImage.val(dataUrl);
    $saveBtn.show();
});
});

    $saveBtn.click(function() {
    $("#imageForm").submit();
})
});