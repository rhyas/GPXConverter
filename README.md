NOTE: As of July 1. Srava decided to be jerks and eliminated their API. Thus, this
no longer works. I will update it at some point. Maybe. If you're unhappy with this,
please, by all means, complain to Strava for their bad decision.

GPXConverter
============

Converter to upload GPX data with HR/Cadence into Strava & RideWithGPS.

(Known Compatible with Garmin eTrex & Mio devices)

To use on windows, make sure Java 1.7 is installed and simply download/save 

https://github.com/rhyas/GPXConverter/blob/master/dist/GPXConverter.jar?raw=true

and double click it to run it.

Converter was concieved to allow Heart Rate and Cadence data that are supported
by the eTrex 30 to be input in the Strava tracking website. But the converter 
should work for any Garmin device that supports Cadence/HR Sensors. Mio support
was added as well. (Thanks dvdeurse for the clues!)

Notes: 

When "Yes" is checked for Altimeter, the app plugs in "Garmin Edge 800" so 
strava knows to use the readings in the file. If "No" is checked, the app plugs
in "Garmin Edge 200" which doesn't have an altimeter, and Strava will then use
it's own elevation profile database.

The Save Password option will store the password cleartext in the registry. Don't
use it if you don't want that.

Login/Password information is specific to the services you check to upload to. So
if you check both Strava and RideWithGPS, make sure your login info matches for 
both sites.

Activity Type and Name don't work for Strava. This is a bug in Strava's API, not 
the application.
