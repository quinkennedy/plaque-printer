plaque-printer
==============

Arduino sketch tested on an UNO

receipt printer from Adafruit:
* green = pin 5
* yellow = pin 6

Processing sketch connects to Arduino via Serial interface, so Serial index may need to be edited

### Importing data
* Data requires these three columns (others will be ignored): `Project Title`, `Artist(s)`, `Short Description (50 Words)`
* Go to google [spreadsheet](https://docs.google.com/spreadsheet/ccc?key=0At9qbh_TY6Q5dGp5S1lRTlRrV2J4NzBJUGZ0VF9lNUE#gid=1) and `File -> Download as -> Comma separated values (.csv, current sheet)`
* Put the downloaded .csv file in the data directory of the Processing app and rename the .csv file to `spreadsheet.csv`