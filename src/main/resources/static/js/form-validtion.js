(function () {
    'use strict';

    // Bootstrap 커스텀 폼 유효성 검사
    window.addEventListener('load', function () {
        var forms = document.querySelectorAll('.needs-validation');

        Array.prototype.forEach.call(forms, function (form) {
            form.addEventListener('submit', function (event) {
                if (form.checkValidity() === false) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            }, false);
        });
    }, false);
}());