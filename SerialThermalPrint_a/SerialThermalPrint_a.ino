#include "Adafruit_Thermal.h"
#include "SoftwareSerial.h"
#include "Stream.h"

/**
 * Declare which pins to communicate to the printer over
 */
int printer_RX_Pin = 5; // green wire
int printer_TX_Pin = 6; // yellow wire
const int maxWidth = 384;
const int arrayWidth = 384/8;
const int charsPerLine = 32;
const int maxLines = 5;

/**
 * Initialize the thermal printer
 */
Adafruit_Thermal printer(printer_RX_Pin, printer_TX_Pin);

void setup(){
  printer.begin();
  Serial.begin(38400);//38400 - max//9600 - default//300 - lowest
//  printer.println("Hello");
  printer.timeoutWait();
  Serial.write(1);
}

void loop(){
}

void serialEvent(){
  if (Serial.available() > 0){
    int type, c_type;
    while((type = Serial.read()) < 0);
    c_type = type;
    
    if (c_type == 's'){//string
      int v;
      int chars = 0;
      String ss = "";
      while(true){
        while((v = Serial.read()) < 0);
        if (v == 255){
          //reached end of text
          if (chars > 0){
            printer.println(ss);
          }
          break;
        } else {
          ss += (char)v;
        }
        chars++;
        if (chars == charsPerLine){
          printer.println(ss);
          chars = 0;
        }
      }
    } else if (c_type == 'p'){//picture
//    printer.println("picture");
      printer.printBitmap(&Serial);
      //printer.printBitmap(width, height, &Serial);
    } else if (c_type == 'c'){//control
      while((type = Serial.read()) < 0);
      switch(type){
        case 0:
          printer.setSize('L');
          break;
        case 1:
          printer.setSize('M');
          break;
        case 2:
          printer.doubleHeightOn();
          break;
        case 3:
          printer.doubleHeightOff();
          break;
        case 4:
          printer.justify('L');
          break;
        case 5:
          printer.justify('R');
          break;
        case 6:
          printer.justify('C');
          break;
        case 7:
          printer.inverseOn();
          break;
        case 8:
          printer.inverseOff();
          break;
        case 9:
          printer.boldOn();
          break;
        case 10:
          printer.boldOff();
          break;
        case 11:
          printer.underlineOn();
          break;
        case 12:
          printer.underlineOff();
          break;
        default:
          //printer.println(type);
          break;
      }
    } else {
      //lets ignore it I guess.
      return;
    }
//    printer.timeoutWait();
//    delay(3);
    while(Serial.read() > 0);
    Serial.write(1);
    Serial.flush();
  }
}
