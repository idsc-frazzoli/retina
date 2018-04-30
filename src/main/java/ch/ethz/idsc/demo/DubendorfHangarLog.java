// code by jph
package ch.ethz.idsc.demo;

import java.io.File;

// TODO remove empty on 20180305T151234
public enum DubendorfHangarLog implements LogFileInterface {
  /* 2017-12-13 */
  /** lot's of braking */
  _20171213T161500_55710a6b, //
  /** brake distance testing */
  _20171213T162832_55710a6b, //
  /* 2017-12-18 */
  // gokart.pose.lidar does not contain quality field
  /***************************************************/
  /* 2018-01-08 */
  /** no movement at all
   * linmot failure at 662.75[s]; */
  _20180108T152648_5f742add, //
  /**  */
  _20180108T154035_5f742add, //
  /** linmot failure at 571.65[s]; */
  _20180108T160752_5f742add, //
  /** linmot failure at 128.25[s]; */
  _20180108T162528_5f742add, //
  /** joystick with max torque;
   * 332[s] oval pursuit in reverse */
  _20180108T165210_5f742add, //
  /***************************************************/
  /* 2018-01-12 */
  /** no movement at all
   * 708.50[s] linmot failure */
  _20180112T103859_9e1d3699, //
  /**  */
  _20180112T105400_9e1d3699, //
  /** 565[s] oval pursuit
   * 1578.05[s] linmot failure */
  _20180112T113153_9e1d3699, //
  /** oval pursuit 758.18[s] */
  _20180112T154355_9e1d3699, //
  /***************************************************/
  /* 2018-02-26 */
  /** joystick driving */
  _20180226T150533_ed1c7f0a, //
  /** */
  _20180226T164802_ed1c7f0a, //
  /***************************************************/
  /* 2018-03-05 */
  /** joystick */
  _20180305T114356_9e1dc13c, //
  /** NOT MUCH HAPPENS HERE */
  _20180305T121016_9e1dc13c, //
  /** joystick driving
   * contains LCM_SELF_TEST */
  _20180305T150255_9e1dc13c, //
  /** joystick */
  _20180305T151234_9e1dc13c, //
  /** pure pursuit without davis */
  _20180305T170018_9e1dc13c, //
  /***************************************************/
  /* 2018-03-07 */
  /** joystick slow, with safety barriers for demo */
  _20180307T095540_1cd7a2e2, //
  // TODO comment content
  _20180307T150715_28f09b86, //
  _20180307T151633_28f09b86, //
  _20180307T154859_0cd18c6b, //
  /** autonomous driving shape 8
   * velocity peak ~3.6[m*s^-1] */
  _20180307T165102_6e45300d, //
  /** autonomous driving shape 8 */
  _20180307T171311_fc084dc9, //
  /***************************************************/
  /* 2018-04-09 */
  _20180409T150925_d520ea0d, //
  /***************************************************/
  /* 2018-04-12 */
  _20180412T114245_7e5b46c2, //
  /** stationary gokart in center of hangar
   * pedestrians, bicycles and motorcycles moving around
   * no Davis240C */
  _20180412T163109_7e5b46c2, //
  /** stationary gokart in center of hangar
   * pedestrians and bicycles moving around */
  _20180412T163855_7e5b46c2, //
  /** manual drive with markers on floor for davis240c
   * without cones
   * contains linmot failure */
  _20180412T164740_7e5b46c2, //
  /***************************************************/
  /* 2018-04-18 */
  // TODO DUBENDORF/OFFICE HD obtain log files
  /** joystick driving and single prbs */
  // _20180418T102854_5a650fbf, //
  /** pure pursuit */
  // _20180418T125913_bca165ae, //
  /** multiple prbs */
  // _20180418T132333_bca165ae, //
  /***************************************************/
  /* 2018-04-19 */
  /** pure pursuit with hangar doors closed and then open */
  _20180419T124700_7373f83e, //
  /** pure pursuit with speed controller going in reverse */
  _20180419T150253_7373f83e, //
  /** pure pursuit with speed controller going in reverse */
  _20180419T172115_f80284e9, //
  /** joystick driving along figure 8 */
  // TODO DUBENDORF obtain and document log files
  // _20180419T172918_f80284e9, //
  /***************************************************/
  /* 2018-04-23 */
  // TODO DUBENDORF obtain and document log files
  /***************************************************/
  /* 2018-04-27 */
  /** joystick driving small circles at different speeds
   * TODO investigate min turning radius */
  _20180427T105421_08cf25f5, //
  /** torque signals to rimo for sys id */
  _20180427T121545_22662115, //
  /** torque signals to rimo for sys id */
  _20180427T123334_22662115, //
  /** rimo PI controller with Kp=74 Ki==0
   * exhibits backwards driving
   * log includes internals of PI controller */
  _20180427T125356_22662115, //
  /** slow and medium joystick driving with 2 pedestrians walking
   * gokart localization not successful */
  _20180427T140645_22662115, //
  /** trajectory planning and pure pursuit */
  _20180427T155210_987cb124, //
  /** trajectory planning and pure pursuit */
  _20180427T155709_987cb124, //
  ; //
  @Override
  public File file(File directory) {
    File folder = new File(directory, name().substring(1, 9));
    return new File(folder, name().substring(1) + ".lcm.00");
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
