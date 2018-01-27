// code by jph
package ch.ethz.idsc.retina.demo;

import java.io.File;

public enum DubendorfHangarLog {
  _20180108T152648_5f742add, //
  _20180108T154035_5f742add, //
  _20180108T160752_5f742add, //
  _20180108T162528_5f742add, //
  _20180108T165210_5f742add, //
  _20180112T103859_9e1d3699, //
  _20180112T105400_9e1d3699, //
  _20180112T113153_9e1d3699, //
  _20180112T154355_9e1d3699, //
  ;
  public File file(File directory) {
    return new File(directory, name().substring(1) + ".lcm.00");
  }
}
