var exec = require('cordova/exec');

NativeAlert = {
    alert: function(alertType, title, message, buttons, success, error) {
        exec(success, error, 'CustomAlert', 'alert', [alertType, title, message, buttons.reverse()]);
    }
};

(function() {
    exec(null, null, 'CustomAlert', 'init', []);
})();
