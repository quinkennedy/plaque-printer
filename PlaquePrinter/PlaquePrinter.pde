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
  Table plaqueData = loadTable("spreadsheet.csv", "header");
  for (TableRow row : plaqueData.rows()) {
    
    String title = row.getString("Project Title");
    String artist = row.getString("Artist(s)");
    String description = row.getString("Short Description (50 Words)");
    if (title.length() > 0 || artist.length() > 0 || description.length() > 0){
      plaques.add(new Plaque(artist, title, description));
    }
    if (plaques.size() > 2){
      break;
    }
    //println(name + " (" + species + ") has an ID of " + id);
  }
  
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
