
GPXConverter
============

Converter to upload GPX data with HR/Cadence/Temp/Elevation into Strava & 
RideWithGPS.

Known Compatible with Garmin eTrex, Oregon, & Mio devices

To use on windows, make sure Java 1.7 is installed and simply download/save:

https://github.com/rhyas/GPXConverter/blob/master/dist/GPXConverter.jar?raw=true

and double click it to run it.

Converter was concieved to allow Heart Rate and Cadence data that are supported
by the eTrex 30 to be input in the Strava tracking website. But the converter 
should work for any Garmin device that supports Cadence/HR Sensors. Mio support
was added as well. (Thanks dvdeurse for the clues!) 

Notes: 

When "Yes" is checked for Altimeter, the app plugs in "Garmin Edge 800" so 
strava knows to use the readings in the file from the GPS. If "No" is checked, 
the app plugs in "Garmin Edge 200" which doesn't have an altimeter, and Strava 
will then use it's own elevation profile database. 

The Save Password option will store the password cleartext in the registry. Don't
use it if you don't want that.

Login/Password information is specific to the services you check to upload to. So
if you check both Strava and RideWithGPS, make sure your login info matches for 
both sites.

Activity Type and Name don't work for Strava. They never worked in the Strava API,
and now they have no generally available API. 
