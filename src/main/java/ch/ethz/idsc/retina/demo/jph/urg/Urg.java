// code by jph
package ch.ethz.idsc.retina.demo.jph.urg;

import java.io.File;

enum Urg {
  LOG05(new File("/media/datahaki/media/ethz/urg04lx/", "urh20170809T162447.txt")), //
  LOG06(new File("/media/datahaki/media/ethz/urg04lx/", "urh20170809T163714.txt")), //
  ;
  public final File file;

  private Urg(File file) {
    this.file = file;
  }
}
