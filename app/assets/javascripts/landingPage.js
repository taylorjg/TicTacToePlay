$(document).ready(() => {

    const $form = $('form');
    const $username = $('#username', $form);
    const $password = $('#password', $form);

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

    $username.blur(function() { validateControl($(this)); });
    $password.blur(function() { validateControl($(this)); });
});
