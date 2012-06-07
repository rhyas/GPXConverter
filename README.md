GPXConverter
============

Converter to translate Garmin eTrex GPX files into Garmin Edge 200/800 TCX files

Converter was concieved to allow Heart Rate and Cadence data that are supported
by the eTrex 30 to be input in the Strava tracking website. But the converter 
should work for any tracking site that uses the tcx format and for any Garmin
device that supports Cadence/HR Sensors.

Note: When "Yes" is checked for Altimeter, the app plugs in "Garmin Edge 800" so 
strava knows to use the readings in the file. If "No" is checked, the app plugs
in "Garmin Edge 200" which doesn't have an altimeter, and Strava will then use
it's own elevation profile database.

Note: Activity Type and Name don't work. This is a bug in Strava's API, not the
application.