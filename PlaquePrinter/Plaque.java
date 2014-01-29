import java.util.ArrayList;

public class Plaque{
  String[] names;
  String title;
  String description;
  ArrayList<PrinterCommand> commands;
  public Plaque(String names, String title, String description){
    //normalization commands
    commands = new ArrayList<PrinterCommand>();
    commands.add(new PrinterCommand(SerialThermalPrint.TextFormat.SINGLE_HIGH));
    commands.add(new PrinterCommand(SerialThermalPrint.TextFormat.LEFT));
    commands.add(new PrinterCommand(SerialThermalPrint.TextFormat.NO_BOLD));
    commands.add(new PrinterCommand(SerialThermalPrint.TextFormat.NON_INVERSE));
    commands.add(new PrinterCommand(SerialThermalPrint.TextFormat.NO_UNDERLINE));
    commands.add(new PrinterCommand(SerialThermalPrint.TextFormat.MEDIUM));
    
    //artist names
    if (names != null && names.length() > 0){
      String[] sArr = names.split(",");
      commands.add(new PrinterCommand(SerialThermalPrint.TextFormat.INVERSE));
      commands.add(new PrinterCommand(SerialThermalPrint.TextFormat.BOLD));
      commands.add(new PrinterCommand(sArr.length > 1 ? "ARTISTS" : "ARTIST"));
      commands.add(new PrinterCommand(SerialThermalPrint.TextFormat.NON_INVERSE));
      commands.add(new PrinterCommand(SerialThermalPrint.TextFormat.NO_BOLD));
      commands.add(new PrinterCommand(SerialThermalPrint.TextFormat.LARGE));
      for(int i = 0; i < sArr.length; i++){
        commands.add(new PrinterCommand(sArr[i]));
      }
      commands.add(new PrinterCommand(SerialThermalPrint.TextFormat.MEDIUM));
    }
    commands.add(new PrinterCommand(SerialThermalPrint.TextFormat.INVERSE));
    commands.add(new PrinterCommand(SerialThermalPrint.TextFormat.BOLD));
    commands.add(new PrinterCommand("TITLE"));
    commands.add(new PrinterCommand(SerialThermalPrint.TextFormat.NON_INVERSE));
    commands.add(new PrinterCommand(SerialThermalPrint.TextFormat.NO_BOLD));
    commands.add(new PrinterCommand(SerialThermalPrint.TextFormat.DOUBLE_HIGH));
    commands.add(new PrinterCommand(SerialThermalPrint.TextFormat.LARGE));
    commands.add(new PrinterCommand(title));
    commands.add(new PrinterCommand(SerialThermalPrint.TextFormat.SINGLE_HIGH));
    commands.add(new PrinterCommand(SerialThermalPrint.TextFormat.MEDIUM));
    commands.add(new PrinterCommand(""));
    commands.add(new PrinterCommand(description));
  }
  
  public boolean doPrint(SerialThermalPrint p){
    if (commands.size() == 0){
      return false;
    }
    commands.remove(0).doPrint(p);
    System.out.println("printed command");
    return true;
  }
}
