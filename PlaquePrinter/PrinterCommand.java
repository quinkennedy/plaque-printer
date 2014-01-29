public class PrinterCommand{
  String data = null;
  SerialThermalPrint.TextFormat format;
  public PrinterCommand(String d){
    data = d;
  }
  public PrinterCommand(SerialThermalPrint.TextFormat f){
    format = f;
  }
  
  public void doPrint(SerialThermalPrint p){
    if (data != null){
      p.print(data);
    } else {
      p.setControl(format);
    }
  }
}
