$(document).ready(() => {

    const $form = $('form');
    const $username = $('#username', $form);
    const $password = $('#password', $form);
    const $password2 = $('#password2', $form);
    const $passwordMismatchError = $('#passwordMismatchError', $form);

    function validateControl($control) {
        if ($control.val()) {
            fieldHasSuccess($control);
        }
        else {
            fieldHasError($control);
        }
    }

    function fieldHasSuccess($control) {
        const $div = $control.closest('div');
        const $tick = $div.find('.glyphicon-ok');
        const $cross = $div.find('.glyphicon-remove');
        $div.removeClass('has-error').addClass('has-feedback has-success');
        $tick.removeClass('hidden').show();
        $cross.hide();
    }

    function fieldHasError($control) {
        const $div = $control.closest('div');
        const $tick = $div.find('.glyphicon-ok');
        const $cross = $div.find('.glyphicon-remove');
        $div.removeClass('has-success').addClass('has-feedback has-error');
        $cross.removeClass('hidden').show();
        $tick.hide();
    }

    function comparePasswords($control) {

        const val = $control.val();

        if (!val) {
            fieldHasError($control);
            return;
        }

        const password = $password.val();
        const password2 = $password2.val();

        if ($control === $password && password && !password2) {
            fieldHasSuccess($password);
        }
        else {
            if (password === password2) {
                fieldHasSuccess($password);
                fieldHasSuccess($password2);
                hidePasswordMismatchError();
            }
            else {
                fieldHasError($password);
                fieldHasError($password2);
                showPasswordMismatchError();
            }
        }
    }

    function showPasswordMismatchError() {
        $passwordMismatchError.removeClass('hidden').show();
    }

    function hidePasswordMismatchError() {
        $passwordMismatchError.hide();
    }

    $username.blur(function() { validateControl($(this)); });
    $password.blur(function() { comparePasswords($password); });
    $password2.blur(function() { comparePasswords($password2); });
});
