import processing.serial.*;
import java.util.ArrayList;

SerialThermalPrint p;
int baudRate = 38400;//9600;300;38400;
boolean m_bPrinterReady = false;
long waitUntil;
Serial gotMsgOn;
ArrayList<Plaque> plaques;

void setup(){
  plaques = new ArrayList<Plaque>();
  plaques.add(new Plaque("Quin Kennedy,David Huerta", "The Cool Space", "Some crazy cool space with lots of stuff going on. Sit down, relax, and have some codez!"));
  plaques.add(new Plaque("Olof Mathe", "The sick zone", "do not enter!"));
  
  try{
      p = new SerialThermalPrint(this);
      println(Serial.list());
      Serial s = new Serial(this, Serial.list()[8], baudRate);
      p.usePrinter(s);
    } catch (Exception e){
      println("exception while setting up printer, entering test mode");
      println(e);
      println(e.getStackTrace());
      p = null;
    }
}

void draw(){
  if (gotMsgOn != null){
    try{
      p.serialEvent(gotMsgOn);
    }catch (Exception e) {
      println(e);
      println(e.getStackTrace());
    }
    gotMsgOn = null;
  }
  if (m_bPrinterReady && waitUntil < millis()){
    m_bPrinterReady = false;
    println("Printing! "+millis());
    if(p.print("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")){
      println("success! ??");
    }
  }
}

public void onPrinterReady(){
  while (plaques.size() > 0 && !plaques.get(0).doPrint(p)){
    plaques.remove(0);
  }
  if (plaques.size() == 0){
    println("DONE!");
  }
}

void serialEvent(Serial port){
  gotMsgOn = port;
}

void keyPressed(){
  gotMsgOn = p.printer;
}
