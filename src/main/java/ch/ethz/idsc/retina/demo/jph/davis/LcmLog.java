// code by jph
package ch.ethz.idsc.retina.demo.jph.davis;

import java.io.File;

enum LcmLog {
  SIMPLE(new File("/home/datahaki", "lcmlog-2017-08-13.00")), //
  ;
  public final File file;

  private LcmLog(File file) {
    this.file = file;
  }
}
