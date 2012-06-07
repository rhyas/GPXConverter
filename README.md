GPXConverter
============

Converter to upload Garmin GPX data with HR/Cadence into Strava.

To use on windows, make sure Java 1.7 is installed and simply download/save 

https://github.com/rhyas/GPXConverter/blob/master/dist/GPXConverter.jar?raw=true

and double click it to run it.

Converter was concieved to allow Heart Rate and Cadence data that are supported
by the eTrex 30 to be input in the Strava tracking website. But the converter 
should work for any Garmin device that supports Cadence/HR Sensors.

Note: When "Yes" is checked for Altimeter, the app plugs in "Garmin Edge 800" so 
strava knows to use the readings in the file. If "No" is checked, the app plugs
in "Garmin Edge 200" which doesn't have an altimeter, and Strava will then use
it's own elevation profile database.

Note: Activity Type and Name don't work. This is a bug in Strava's API, not the
application.