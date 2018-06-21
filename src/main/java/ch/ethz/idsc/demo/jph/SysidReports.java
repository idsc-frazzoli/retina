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
enum SysidReports {
  ;
  public static void main(String[] args) {
    List<LogFile> list = Arrays.asList( //
        GokartLogFile._20180418T102854_5a650fbf, //
        GokartLogFile._20180418T132333_bca165ae, //
        GokartLogFile._20180427T121545_22662115, //
        GokartLogFile._20180427T123334_22662115);
    for (LogFile logFile : list)
      try {
        System.out.println(logFile.getFilename());
        ProduceReport.of(logFile);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
  }
}
