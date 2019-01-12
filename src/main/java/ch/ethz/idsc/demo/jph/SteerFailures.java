// code by jph
package ch.ethz.idsc.demo.jph;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.gokart.offline.api.LogFile;

/** Instance in all log files:
 * 20180607T140443 \--> 323.837875[s]
 * 20180607T144545 \--> 561.786948[s]
 * 20180607T165423 \--> 29.085299[s]
 * 20180607T170837 \--> 556.908939[s]
 * 20180611T101502 \--> 553.125962[s]
 * 20180611T144759 \--> 146.609942[s]
 * 20180611T150139 \--> 8.32E-4[s]
 * 20180614T092856 \--> 20.875161[s]
 * 20180614T092944 \--> 0.00161[s]
 * 20180614T122925 \--> 28.178607[s]
 * 20180614T142228 \--> 57.514723[s] */
/* package */ enum SteerFailures {
  ;
  public static void main(String[] args) {
    List<LogFile> list = Arrays.asList( //
        GokartLogFile._20180607T140443_e9d47681, //
        GokartLogFile._20180607T144545_e9d47681, //
        GokartLogFile._20180607T165423_e9d47681, //
        GokartLogFile._20180607T170837_e9d47681, //
        GokartLogFile._20180611T101502_851c404d, //
        GokartLogFile._20180611T144759_44b96dd6, //
        GokartLogFile._20180611T150139_872fbbb8, //
        GokartLogFile._20180614T092856_7f9c94c9, //
        GokartLogFile._20180614T092944_7f9c94c9, //
        GokartLogFile._20180614T122925_1fe5ba47, //
        GokartLogFile._20180614T142228_6a2f62c6);
    for (LogFile logFile : list)
      try {
        System.out.println(logFile.getFilename());
        ProduceReport.of(logFile);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
  }
}
