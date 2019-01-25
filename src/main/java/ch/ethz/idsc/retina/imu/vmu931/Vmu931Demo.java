// code by jph
package ch.ethz.idsc.retina.imu.vmu931;

import java.util.EnumSet;

/* package */ enum Vmu931Demo {
  ;
  public static void main(String[] args) {
    Vmu931Listener vmu931Listener = new Vmu931Recorder(10000);
    vmu931Listener = Vmu931Printout.INSTANCE;
    new Vmu931( //
        "/dev/ttyACM0", //
        EnumSet.of(Vmu931Channel.ACCELEROMETER, Vmu931Channel.GYROSCOPE), //
        Vmu931_DPS._250, //
        Vmu931_G._2, //
        vmu931Listener);
  }
}
