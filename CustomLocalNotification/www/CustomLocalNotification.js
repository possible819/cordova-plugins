var exec = require('cordova/exec');

window.LocalNotification = {
    addNotification: function(title, subtitle, body, hour, minute, success, error) {
        exec(success, error, 'CustomLocalNotification', 'addNotification', [title, subtitle, body, hour, minute]);
    },

    clearNotification: function(success, error) {
        exec(success, error, 'CustomLocalNotification', 'clearNotification', null);
    }
};