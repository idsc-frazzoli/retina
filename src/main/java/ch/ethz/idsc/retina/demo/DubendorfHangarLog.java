// code by jph
package ch.ethz.idsc.retina.demo;

import java.io.File;

public enum DubendorfHangarLog implements LogFileInterface {
  /* 2017-12-13 */
  /** lot's of braking */
  _20171213T161500_55710a6b, //
  /** brake distance testing */
  _20171213T162832_55710a6b, //
  /* 2018-01-08 */
  /** linmot failure at 662.75[s] */
  _20180108T152648_5f742add, //
  _20180108T154035_5f742add, //
  /** linmot failure at 571.65[s] */
  _20180108T160752_5f742add, //
  /** linmot failure at 128.25[s] */
  _20180108T162528_5f742add, //
  /** joystick with max torque
   * oval pursuit in reverse */
  _20180108T165210_5f742add, //
  /* 2018-01-12 */
  /** linmot failure at 708.50[s] */
  _20180112T103859_9e1d3699, //
  /** oval pursuit */
  _20180112T105400_9e1d3699, //
  /** linmot failure at 1578.05[s] */
  _20180112T113153_9e1d3699, //
  _20180112T154355_9e1d3699, //
  ;
  @Override
  public File file(File directory) {
    return new File(directory, name().substring(1) + ".lcm.00");
  }

  /** Example: "20180112T105400"
   * 
   * @return file name without commit id and extension */
  @Override
  public String title() {
    int index = name().indexOf('_', 1);
    return name().substring(1, index);
  }
}
