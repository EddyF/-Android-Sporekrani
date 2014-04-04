var CalendarPlugin = function() {};

CalendarPlugin.prototype.createEvent = function(title, location, description, startDate, endDate) {
    return cordova.exec(function(arg){}, function(error){}, 'CalendarPlugin', 'createEvent', [title, location, description, startDate, endDate]);
};

PhoneGap.addConstructor(function() {
PhoneGap.addPlugin('CalendarPlugin', new CalendarPlugin());
PluginManager.addService("CalendarPlugin", "rocabee.com.plugin.CalendarPlugin");
});