// code by jph
package ch.ethz.idsc.retina.dev.linmot;

/** Example interface on datahaki's computer
 * 
 * enx9cebe8143edb Link encap:Ethernet HWaddr 9c:eb:e8:14:3e:db
 * inet addr:192.168.1.1 Bcast:192.168.1.255 Mask:255.255.255.0
 * inet6 addr: fe80::9eeb:e8ff:fe14:3edb/64 Scope:Link
 * UP BROADCAST RUNNING MULTICAST MTU:1500 Metric:1
 * RX packets:466380 errors:0 dropped:0 overruns:0 frame:0
 * TX packets:233412 errors:0 dropped:0 overruns:0 carrier:0
 * collisions:0 txqueuelen:1000
 * RX bytes:643249464 (643.2 MB) TX bytes:17275914 (17.2 MB) */
public enum LinmotSocket {
  ;
  public static final int LOCAL_PORT = 5001;
  public static final String LOCAL_ADDRESS = "192.168.1.1";
  // ---
  public static final int REMOTE_PORT = 5001;
  public static final String REMOTE_ADDRESS = "192.168.1.10";
}
