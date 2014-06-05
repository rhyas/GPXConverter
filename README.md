
GPXConverter
============

Converter to upload GPX data with HR/Cadence/Temp/Elevation into 
Strava, Garmin Connect, & RideWithGPS.

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

Activity names only seem to work with RideWithGPS at the moment. Neither Strava
nor Garmin Connect will pull the name out of the TCX file it seems. So you will 
have to go in and rename your rides post-upload if you wish.
