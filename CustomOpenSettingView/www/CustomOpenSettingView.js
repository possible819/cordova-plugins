var exec = require('cordova/exec');

window.OpenSettingView = {
    open: function(sucess, error) {
        exec(sucess, error, 'CustomOpenSettingView', 'open');
    }
};