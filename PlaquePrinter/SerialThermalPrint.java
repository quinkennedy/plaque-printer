/**
 * Serial Thermal Print
 * A library for communicating via serial to a Thermal Printer controlled by an Arduino
 * http://quinkennedy.github.com/SerialThermalPrint
 *
 * Copyright (c) 2013 Quin Kennedy http://quinkennedy.github.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author      Quin Kennedy http://quinkennedy.github.com
 * @modified    08/05/2013
 * @version     1.0.0 (1)
 */

import java.lang.reflect.Method;
import processing.core.*;
import processing.serial.*;

/**
 * This is a template class and can be used to start a new processing library or
 * tool. Make sure you rename this class as well as the name of the example
 * package 'template' to your own library or tool naming convention.
 * 
 * @example Hello
 * 
 *          (the tag @example followed by the name of an example included in
 *          folder 'examples' will automatically include the example in the
 *          javadoc.)
 * 
 */

public class SerialThermalPrint {
  
  public enum TextFormat{
    LARGE,MEDIUM,DOUBLE_HIGH,SINGLE_HIGH,LEFT,RIGHT,CENTER,INVERSE,NON_INVERSE,BOLD,NO_BOLD,UNDERLINE,NO_UNDERLINE
  }

  // myParent is a reference to the parent sketch
  PApplet myParent;

  public static final int MAX_WIDTH = 384;
  public static final int IMAGE_CHUNK_HEIGHT = 1;
  public static final int CHAR_PER_ROW = 32;
  public static final int STRING_CHUNK_HEIGHT = 1;

  /**
   * How loud to be (mutes debug messages)
   * 
   * @type {Boolean}
   */
  public boolean verbose = false;

  public Serial printer;
  private Method onPrinterReadyMethod;
  private boolean bConnected = false;

  private PImage currImage;
  private int imageRowOffset = 0;
  private boolean printerReady = false;
  private String currString;
    private boolean sawFirst = false;
    private boolean disregardFirst = true;
    private int disregardTimeout = 500;
    private boolean isLarge = false;

  public final static String VERSION = "1.0.0";

  /**
   * a Constructor, usually called in the setup() method in your sketch to
   * initialize and start the library.
   * 
   * @example Hello
   * @param theParent
   */
  public SerialThermalPrint(PApplet theParent) {
    myParent = theParent;
    welcome();
    setupMethods();
  }
  
  public void forceReady(){
    printerReady = true;
  }

  private void welcome() {
    System.out
        .println("Serial Thermal Print 1.0.0 by Quin Kennedy http://quinkennedy.github.com");
  }

  /**
   * return the version of the library.
   * 
   * @return String
   */
  public static String version() {
    return VERSION;
  }

    
    //------------------------------------------------
    private void setupMethods(){
      try {
        onPrinterReadyMethod = myParent.getClass().getMethod("onPrinterReady", new Class[]{});
      } catch (Exception e){
        //we need to be careful not to make people think they have a legitimate error
        System.out.println("I suggest you implement an onPrinterReady() method so you know when you can send data");
      }
    }
    
    public void connect(String a_sSerialDevice, int a_nBaud){
      printer = new Serial(myParent, a_sSerialDevice, a_nBaud);
    }
    
    public void usePrinter(Serial s){
      printer = s;
    }
    
    public static String[] list(){
      return Serial.list();
    }

    private void sendImageChunk(){
        myParent.println("sending image chunk, start row: " + imageRowOffset);
        int rowBits = Math.min(MAX_WIDTH, currImage.width);
        int rowBytes = (int)Math.ceil(rowBits/8);
        int numRows = Math.min(IMAGE_CHUNK_HEIGHT, currImage.height - imageRowOffset);
        byte[] bytes = new byte[rowBytes*numRows + 5];
        int i = 0;
        int j = 0;
        bytes[i++] = 'p';
        bytes[i++] = (byte)rowBits;
        bytes[i++] = (byte)(rowBits >>> 8);
        bytes[i++] = (byte)numRows;
        bytes[i++] = (byte)(numRows >>> 8);
        
        //for each pixel, convert it to a bitmap
        byte currByte = 0;
        int stopPrintRow = imageRowOffset + numRows;
        int pixelOffset = imageRowOffset*currImage.width;
        for(; imageRowOffset < stopPrintRow; imageRowOffset++, pixelOffset += currImage.width){
          for(j = 0; j < rowBits; j++){
            currByte <<= 1;
            currByte |= ((currImage.pixels[j + pixelOffset] & 255) > 120 ? 0 : 1);
            //TODO: remove this debugging line
            currImage.pixels[j + pixelOffset] = ((currByte & 1) == 1 ? 0 : 255);

            if (j%8 == 7){
              //we have filled a character, so lets store it and reset
              bytes[i++] = currByte;
              currByte = 0;
            }
          }
          if (j%8 != 0){
            //we have data in currByte that we need to put into our bytes array
            currByte <<= 8 - (j%8);
            bytes[i++] = currByte;
            currByte = 0;
          }
        }

        printer.write(bytes);

        if (imageRowOffset >= currImage.height){
          myParent.println("finished image");
          currImage = null;
          imageRowOffset = 0;
        }
    }

    private void sendStringChunk(){
      int numChars = Math.min(CHAR_PER_ROW * STRING_CHUNK_HEIGHT, currString.length());
      byte[] bytes = new byte[numChars + 1];
      int i = 0;
      bytes[i++] = 's';
      printer.write("s" + (numChars < currString.length() 
        ? currString.substring(0, numChars)
        : currString));
      printer.write((byte)255);//signals the end of the string
      if (numChars < currString.length()){
        currString = currString.substring(numChars, currString.length());
      } else {
        currString = null;
      }
    }

    public void serialEvent(Serial port){
    myParent.println("got serial event");
      if (port != printer){
        return;
      }
      if (disregardFirst && !sawFirst && myParent.millis() < disregardTimeout){
          sawFirst = true;
          return;
        }
      //byte input = printer.read();
      printer.clear();
      if (currImage != null){
        sendImageChunk();
        return;
      } else if (currString != null){
        sendStringChunk();
        return;
      }

      callOnPrinterReady();
    }

    private void callOnPrinterReady(){
      printerReady = true;
      if (onPrinterReadyMethod != null){
        try {
          onPrinterReadyMethod.invoke(myParent);
        } catch( Exception e ){
          System.err.println(e);
          System.err.println("onPrinterReady invoke failed, disabling :(");
          onPrinterReadyMethod = null;
        }
      }
    }
    
    public boolean setControl(TextFormat textFormat){
      if (!printerReady){
          myParent.println("not ready");
        return false;
      }
      printerReady = false;
      printer.write("c");
      printer.write((byte)textFormat.ordinal());
      return true;
    }

    public boolean print(PImage a_iImage){
      if (!printerReady){
          myParent.println("not ready");
        return false;
      }
      printerReady = false;
      //TODO: copy image instead of using supplied one
      currImage = a_iImage;
      imageRowOffset = 0;
      sendImageChunk();
      return true;
    }

    public boolean print(String a_sText){
      if (!printerReady){
        return false;
      }
      printerReady = false;
      //TODO: copy string
      currString = a_sText;
      sendStringChunk();
      return true;
    }
}

