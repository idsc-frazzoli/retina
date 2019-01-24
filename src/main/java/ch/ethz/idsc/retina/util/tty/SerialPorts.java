// code by jph
package ch.ethz.idsc.retina.util.tty;

import com.fazecast.jSerialComm.SerialPort;

public enum SerialPorts {
  ;
  private static final int NO_SLEEP = 0;

  /** for devices that do not require port parameter configuration such as VMU931
   * 
   * @param port
   * @return */
  public static SerialPort create(String port) {
    SerialPort serialPort = SerialPort.getCommPort(port);
    boolean success = serialPort.openPort(NO_SLEEP);
    if (!success)
      throw new RuntimeException("cannot open port: " + port);
    return serialPort;
  }

  /** for devices such as seesaw
   * 
   * @param port
   * @param baudRate for example 9600
   * @param dataBits for example 8
   * @return */
  public static SerialPort create(String port, int baudRate, int dataBits) {
    SerialPort serialPort = SerialPort.getCommPort(port);
    serialPort.setComPortParameters(baudRate, dataBits, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
    serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
    boolean success = serialPort.openPort();
    if (!success)
      throw new RuntimeException("cannot open port: " + port);
    return serialPort;
  }
}
