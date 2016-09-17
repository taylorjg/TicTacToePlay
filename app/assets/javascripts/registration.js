let $form;
let $username;
let $password;
let $password2;
let $submit;

$(document).ready(() => {

    $form = $('form');
    $username = $('#username', $form);
    $password = $('#password', $form);
    $password2 = $('#password2', $form);
    $submit = $('#registerButton', $form);

    checkSubmitButton();

    function checkSubmitButton() {
        const formIsValid = $form.find('.has-success').length === 3;
        $submit.prop('disabled', !formIsValid);
    }

    function validateControl($control) {
        if ($control.val()) {
            fieldHasSuccess($control);
        }
        else {
            fieldHasError($control);
        }
        checkSubmitButton();
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
            checkSubmitButton();
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
            }
            else {
                fieldHasError($password);
                fieldHasError($password2);
            }
        }

        checkSubmitButton();
    }

    $username.blur(function() { validateControl($(this)); });
    $password.blur(function() { comparePasswords($password); });
    $password2.blur(function() { comparePasswords($password2); });
});
