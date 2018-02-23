// code by jph
package ch.ethz.idsc.gokart.core;

import java.net.InetAddress;

public enum AutoboxDevice {
  ;
  public static final String LOCAL_ADDRESS = "192.168.1.1"; // "192.168.1.1"
  private static final String REMOTE_ADDRESS = "192.168.1.10"; // "192.168.1.10"
  // ---
  public static final InetAddress REMOTE_INET_ADDRESS = getRemoteInetAddress();

  private static InetAddress getRemoteInetAddress() {
    try {
      return InetAddress.getByName(AutoboxDevice.REMOTE_ADDRESS);
    } catch (Exception exception) {
      throw new RuntimeException(exception.getMessage());
    }
  }
}
