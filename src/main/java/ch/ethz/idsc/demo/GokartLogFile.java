// code by jph
package ch.ethz.idsc.demo;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.gokart.core.fuse.SpeedLimitSafetyModule;
import ch.ethz.idsc.gokart.dev.linmot.LinmotFireFighter;
import ch.ethz.idsc.gokart.offline.api.LogFile;

/** enumeration of all significant gokart log files */
public enum GokartLogFile implements LogFile {
  /* 2017-12-07 */
  /** joystick driving */
  _20171207T105632_59f9bc78,
  /** joystick driving with 1[m/s] */
  _20171207T121043_59f9bc78,
  /** joystick driving with up to 6[m/s] */
  _20171207T133222_59f9bc78,
  /** joystick driving with up to 6[m/s] */
  _20171207T134930_59f9bc78,
  /***************************************************/
  /* 2017-12-08 */
  /** joystick driving */
  _20171208T102427_a03fba2d,
  /** joystick driving with up to 4[m/s] */
  _20171208T110839_a03fba2d,
  /** joystick driving */
  _20171208T115237_a03fba2d,
  /***************************************************/
  /* 2017-12-13 */
  /** lot's of braking */
  _20171213T161500_55710a6b,
  /** brake distance testing */
  _20171213T162832_55710a6b,
  /***************************************************/
  /* 2017-12-18 */
  // gokart.pose.lidar does not contain quality field
  /** moving gokart */
  _20171218T121006_9b56b71b,
  /** pure pursuit oval up to 2[m/s] */
  _20171218T130515_4794c081,
  /** pure pursuit oval */
  _20171218T133833_2eb2bfb0,
  /** pure pursuit oval up to 2[m/s] */
  _20171218T135141_2eb2bfb0,
  /***************************************************/
  /* 2018-01-08 */
  /** no movement at all
   * linmot failure at 662.75[s]; */
  _20180108T152648_5f742add,
  /**  */
  _20180108T154035_5f742add,
  /** linmot failure at 571.65[s]; */
  _20180108T160752_5f742add,
  /** linmot failure at 128.25[s]; */
  _20180108T162528_5f742add,
  /** joystick with max torque;
   * 332[s] oval pursuit in reverse */
  _20180108T165210_5f742add,
  /***************************************************/
  /* 2018-01-12 */
  /** no movement at all
   * 708.50[s] linmot failure */
  _20180112T103859_9e1d3699,
  /**  */
  _20180112T105400_9e1d3699,
  /** 565[s] oval pursuit
   * 1578.05[s] linmot failure */
  _20180112T113153_9e1d3699,
  /** oval pursuit 758.18[s] */
  _20180112T154355_9e1d3699,
  /***************************************************/
  /* 2018-02-26 */
  /** joystick driving */
  _20180226T150533_ed1c7f0a,
  /** joystick driving */
  _20180226T164802_ed1c7f0a,
  /***************************************************/
  /* 2018-03-05 */
  /** joystick */
  _20180305T114356_9e1dc13c,
  /** NOT MUCH HAPPENS HERE */
  _20180305T121016_9e1dc13c,
  /** joystick driving
   * contains LCM_SELF_TEST */
  _20180305T150255_9e1dc13c,
  /** joystick */
  _20180305T151234_9e1dc13c,
  /** pure pursuit without davis */
  _20180305T170018_9e1dc13c,
  /***************************************************/
  /* 2018-03-07 */
  /** joystick slow, with safety barriers for demo */
  _20180307T095540_1cd7a2e2,
  /** pure pursuit with localization failure */
  _20180307T150715_28f09b86,
  /** pure pursuit of oval shape */
  _20180307T151633_28f09b86,
  /** pure pursuit of figure 8 */
  _20180307T154859_0cd18c6b,
  /** autonomous driving shape 8
   * velocity peak ~3.6[m*s^-1] */
  _20180307T165102_6e45300d,
  /** autonomous driving shape 8 */
  _20180307T171311_fc084dc9,
  /***************************************************/
  /* 2018-03-08 */
  /** no driving */
  _20180308T145954_45b9eed8,
  /** pure pursuit figure 8 */
  _20180308T151702_45b9eed8,
  /** pure pursuit figure 8 */
  _20180308T165333_1ab0c811,
  /***************************************************/
  /* 2018-03-10 DEMO DAY */
  /** pure pursuit figure 8 */
  _20180310T102810_1ab0c811,
  /** pure pursuit figure 8 */
  _20180310T110029_1ab0c811,
  /** pure pursuit figure 8 */
  _20180310T112508_1ab0c811,
  /** pure pursuit figure 8 */
  _20180310T120222_1ab0c811,
  /** pure pursuit figure 8 */
  _20180310T130740_1ab0c811,
  /** pure pursuit figure 8 */
  _20180310T132414_1ab0c811,
  /** pure pursuit figure 8 */
  _20180310T135043_1ab0c811,
  /** pure pursuit figure 8 */
  _20180310T142811_1ab0c811,
  /** not moving */
  _20180310T151418_1ab0c811,
  /** not moving */
  _20180310T152653_1ab0c811,
  /** pure pursuit figure 8 */
  _20180310T153409_1ab0c811,
  /** pure pursuit figure 8 */
  _20180310T160324_1ab0c811,
  /***************************************************/
  /* 2018-03-19 */
  /** pure pursuit figure 8 */
  _20180319T133213_1ab0c811,
  /***************************************************/
  /* 2018-03-22 */
  /** pure pursuit figure 8 */
  _20180322T120834_d520ea0d,
  /** fast pure pursuit figure 8 with escapes */
  _20180322T124329_d520ea0d,
  /***************************************************/
  /* 2018-04-09 */
  _20180409T150925_d520ea0d,
  /***************************************************/
  /* 2018-04-12 */
  _20180412T114245_7e5b46c2,
  /** stationary gokart in center of hangar
   * {46.9657[m], 48.4280[m], 1.1587704741}
   * pedestrians, bicycles and motorcycles moving around
   * no Davis240C */
  _20180412T163109_7e5b46c2,
  /** stationary gokart in center of hangar
   * {46.9249[m], 48.6060[m], 1.1602311755}
   * pedestrians and bicycles moving around */
  _20180412T163855_7e5b46c2,
  /** manual drive with markers on floor for davis240c
   * without cones
   * contains linmot failure */
  _20180412T164740_7e5b46c2,
  /***************************************************/
  /* 2018-04-18 */
  /** joystick driving and single prbs */
  _20180418T102854_5a650fbf,
  /** pure pursuit */
  _20180418T125913_bca165ae,
  /** multiple prbs */
  _20180418T132333_bca165ae,
  /***************************************************/
  /* 2018-04-19 */
  /** pure pursuit with hangar doors closed and then open */
  _20180419T124700_7373f83e,
  /** pure pursuit with speed controller going in reverse */
  _20180419T150253_7373f83e,
  /** pure pursuit with speed controller going in reverse */
  _20180419T172115_f80284e9,
  /** joystick driving along figure 8 */
  _20180419T172918_f80284e9,
  /***************************************************/
  /* 2018-04-23 */
  /** pure pursuit with controller driving backwards */
  _20180423T181849_633cc6e6,
  /***************************************************/
  /* 2018-04-27 */
  /** joystick driving small circles at different speeds
   * used to approx. min turning radius 2.45[m] */
  _20180427T105421_08cf25f5,
  /** torque signals to rimo for sys id */
  _20180427T121545_22662115,
  /** torque signals to rimo for sys id */
  _20180427T123334_22662115,
  /** rimo PI controller with Kp=74 Ki==0
   * exhibits backwards driving
   * log includes internals of PI controller
   * channel rimo.controller.pi is encoded in BIG_ENDIAN */
  _20180427T125356_22662115,
  /** slow and medium joystick driving with 2 pedestrians walking
   * gokart localization not successful */
  _20180427T140645_22662115,
  /** trajectory planning and pure pursuit */
  _20180427T155210_987cb124,
  /** trajectory planning and pure pursuit */
  _20180427T155709_987cb124,
  /***************************************************/
  /* 2018-04-30 */
  /** pure pursuit at different speeds with pedestrians walking
   * PI-controller with Kp==20 and Ki==0 */
  _20180430T104113_a5291af9,
  /** slow driving for recording improved map with hangar doors open+closed
   * at the end: driving fast circles */
  _20180430T141530_a5291af9,
  /** joystick driving along oval with markers for dvs */
  _20180430T153747_0e959fc6,
  /** no driving */
  _20180430T161506_0e959fc6,
  /** taking pictures with APS for Davis240C calibration
   * no driving */
  _20180430T163141_0e959fc6,
  /** joystick driving with camera calibrated
   * localization not working */
  _20180430T164704_0e959fc6,
  /***************************************************/
  /* 2018-05-03 */
  /** slow pure pursuit oval with Kp==30, Ki==0 */
  _20180503T094457_ce8724ba,
  /** pure pursuit oval
   * initial localization failure
   * last round success */
  _20180503T103916_836189cd,
  /** motion planning with driving speeds up to 4[m/s] */
  _20180503T140722_16144bb6,
  /** motion planning with driving speeds up to 3[m/s] */
  _20180503T150559_16144bb6,
  /** motion planning
   * pure pursuit figure 8 */
  _20180503T151615_16144bb6,
  /** pure pursuit figure 8 with Kp==40, Ki==0
   * first at slow speed then up to 4[m/s]
   * pedestrians, bicycle, static obstacles
   * speed controller allows very minor backwards motion */
  _20180503T160522_16144bb6,
  /***************************************************/
  /* 2018-05-07 */
  /** pure pursuit figure 8 */
  _20180507T090040_28e21174,
  /** pure pursuit figure 8 with speed then up to 5[m/s] */
  _20180507T132022_588d4c6c,
  /** pure pursuit figure 8 with speed then up to 5[m/s]
   * exhibits backwards driving */
  _20180507T135949_588d4c6c,
  /** pure pursuit figure 8 with davis camera calibrated */
  _20180507T143002_588d4c6c,
  /** trajectory planning
   * several ovals until planner stop */
  _20180507T152619_8d5acc24,
  /** trajectory planning few ovals */
  _20180507T153455_8d5acc24,
  /** stationary gokart single pedestrian walking at various distances */
  _20180507T160755_8d5acc24,
  /** driving by joystick fast */
  _20180507T161322_8d5acc24,
  /***************************************************/
  /* 2018-05-09 */
  /** joystick driving */
  _20180509T120343_8d5acc24,
  /***************************************************/
  /* 2018-05-14 */
  /** slow joystick driving to establish
   * min torque to set gokart in motion */
  _20180514T101430_3a743349,
  /** slow joystick driving to establish
   * min torque to set gokart in motion */
  _20180514T105947_eda6fc3d,
  /** record waypoints for duct-tape track */
  _20180514T151138_767e5417,
  /** pure pursuit of duct-tape track */
  _20180514T153139_767e5417,
  /** pure pursuit of duct-tape track
   * second half includes pedestrians */
  _20180514T155248_767e5417,
  /***************************************************/
  /* 2018-05-17 */
  /** joystick driving up to 5[m/s]
   * with consistent localization */
  _20180517T152605_c1876fc4,
  /** figure eight up to 3[m/s] */
  _20180517T153517_c1876fc4,
  /** path planning with static obstacles */
  _20180517T161015_294bf075,
  /** path planning with static obstacles */
  _20180517T161714_294bf075,
  /** path planning with static obstacles, several pure pursuit
   * failures due to large curvature */
  _20180517T162431_294bf075,
  /** path planning with static obstacles, several failures
   * because gokart reaches end of trajectory before replanning */
  _20180517T163528_294bf075,
  /** path planning with static obstacles, several pure pursuit
   * failures due to large curvature, gokart cuts through center */
  _20180517T172517_a7130894,
  /** path planning with static obstacles
   * start from point away from waypoints */
  _20180517T174022_a7130894,
  /***************************************************/
  /* 2018-05-22 */
  /** pure pursuit figure 8 with speeds up to 6[m/s] */
  _20180522T111414_6806b8fd,
  /** oval pure pursuit slow */
  _20180522T114650_6806b8fd,
  /** trajectory planning with moving obstacles */
  _20180522T135700_2da7e1f5,
  /** trajectory planning with moving obstacles */
  _20180522T140901_2da7e1f5,
  /** joystick driving with 1000[ARMS] */
  _20180522T144106_2da7e1f5,
  /** trajectory planning */
  _20180522T145910_2da7e1f5,
  /** trajectory planning with moving obstacles */
  _20180522T150418_2da7e1f5,
  /***************************************************/
  /* 2018-05-24 */
  /** different rimo P/PI controller values
   * pure pursuit figure 8 */
  _20180524T142610_0c5750cc,
  /** trajectory planning, pure pursuit figure 8 */
  _20180524T163738_65ff8506,
  /** pure pursuit figure 8
   * involves localization failure */
  _20180524T164438_65ff8506,
  /** trajectory planning oval waypoints, no obstacles */
  _20180524T165519_65ff8506,
  /** trajectory planning oval waypoints, no obstacles */
  _20180524T165934_65ff8506,
  /** trajectory planning oval waypoints, shifting obstacles */
  _20180524T175331_f5b40700,
  /***************************************************/
  /* 2018-05-28 */
  /** joystick driving and parking */
  _20180528T092757_3d02888c,
  /** slow figure eight */
  _20180528T114144_3d02888c,
  /** slow joystick driving */
  _20180528T115153_3d02888c,
  /** joystick driving on wet surface */
  _20180528T130940_3d02888c,
  /** joystick driving */
  _20180528T160809_3d02888c,
  /** joystick driving */
  _20180528T161753_bb8cdede,
  /** autonomous figure eight with tent at side */
  _20180528T163638_bb8cdede,
  /***************************************************/
  /* 2018-05-29 */
  /** joystick driving
   * autonomous figure eight */
  _20180529T113303_bb8cdede,
  /** trajectory planning with changing obstacle locations */
  _20180529T121927_701d9748,
  /***************************************************/
  /* 2018-05-31 */
  /** autonomous figure eight */
  _20180531T144153_701d9748,
  /** joystick driving with stops for map building
   * with two large tents and bus tent in place */
  _20180531T162851_701d9748,
  /** joystick driving with stops for map building
   * with two large tents and bus tent in place */
  _20180531T171521_701d9748,
  /***************************************************/
  /* 2018-06-04 */
  /** autonomous driving figure eight with tents */
  _20180604T092030_270dd1ab,
  /** collecting waypoints */
  _20180604T100452_a2e94332,
  /** waypoint following */
  _20180604T102303_a2e94332,
  /** joystick driving fast and slow */
  _20180604T104509_a2e94332,
  /** joystick driving as car in a city */
  _20180604T105353_a2e94332,
  /** pedestrians walking with stationary gokart */
  _20180604T120216_a2e94332,
  /** pure pursuit figure eight reverse */
  _20180604T122138_d2234286,
  /** pure pursuit figure eight reverse
   * localization issues at certain configuration */
  _20180604T132432_d2234286,
  /** testing brake and linmot with stationary gokart */
  _20180604T140448_d2234286,
  /** testing brake and linmot with stationary gokart
   * {@link LinmotFireFighter} switches off linmot */
  _20180604T142924_77ab1670,
  /** pure pursuit figure eight slow driving
   * with pedestriants around */
  _20180604T150508_15e65bba,
  /** pure pursuit figure eight */
  _20180604T153602_15e65bba,
  /***************************************************/
  /* 2018-06-07 */
  /** emergency brake testing */
  _20180607T095321_e5ca0ea5,
  /** emergency brake testing */
  _20180607T122743_edd2e720,
  /** emergency brake testing */
  _20180607T124405_edd2e720,
  /** gokart investigation
   * steering failure 323.837875[s] */
  _20180607T140443_e9d47681,
  /** gokart investigation */
  _20180607T142738_e9d47681,
  /** current measurement of steering
   * steering failure at 561.786948[s] */
  _20180607T144545_e9d47681,
  /** steering failure at 29.085299[s] */
  _20180607T165423_e9d47681,
  /** gokart investigation */
  _20180607T165530_e9d47681,
  /** pure pursuit figure eight reverse
   * including emergency brake testing
   * steering failure at 556.908939[s] */
  _20180607T170837_e9d47681,
  /***************************************************/
  /* 2018-06-11 */
  /** autonomous figure eight */
  _20180611T095800_851c404d,
  /** trajectory planning, but incomplete because of virtual obstacle
   * pure pursuit figure eight including emergency stops,
   * localization failure after emergency stop,
   * steering failure at 553.125962[s] */
  _20180611T101502_851c404d,
  /** trajectory planning, but incomplete because of virtual obstacle */
  _20180611T143955_44b96dd6,
  /** trajectory planning successful: complete loop, proximity to waypoints
   * aborted because of steering system failure at 146.609942[s] */
  _20180611T144759_44b96dd6,
  /** steering failure at 8.32E-4[s] */
  _20180611T150139_872fbbb8,
  /***************************************************/
  /* 2018-06-14 */
  /** steering failure at 20.875161[s] */
  _20180614T092856_7f9c94c9,
  /** steering failure at 0.00161[s] */
  _20180614T092944_7f9c94c9,
  /** steering failure at 28.178607[s] */
  _20180614T122925_1fe5ba47,
  /** steer operation failure at 57.514723[s]
   * during calibration procedure */
  _20180614T142228_6a2f62c6,
  /***************************************************/
  /* 2018-06-18 */
  /* investigation of steering system anomaly */
  /***************************************************/
  /* 2018-06-21 */
  /** pure pursuit figure eight with localization failure
   * after emergency stop */
  _20180621T085525_2876053b,
  /** pure pursuit figure eight with driving in reverse */
  _20180621T093117_2876053b,
  /** trajectory planning with changing obstacles */
  _20180621T095326_707a71e0,
  /** pure pursuit figure eight with localization failure */
  _20180621T120115_707a71e0,
  /** pure pursuit figure eight with driving in reverse */
  _20180621T125013_2b01cac5,
  /***************************************************/
  /* 2018-06-25 */
  /* visitors from local school */
  /** pure pursuit figure 8 with reverse driving */
  _20180625T084608_ab61da0c,
  /** pure pursuit figure 8 with reverse driving */
  _20180625T100400_52c7710a,
  /** demo for school: pure pursuit figure 8
   * with reverse driving and several emergency stops */
  _20180625T101902_52c7710a,
  /** driving with joystick, u-turn challenge */
  _20180625T113242_52c7710a,
  /** driving with joystick, u-turn challenge */
  _20180625T141009_52c7710a,
  /** parallel parking challenge */
  _20180625T142644_52c7710a,
  /** parallel parking challenge but steering failure after single attempt */
  _20180625T144316_52c7710a,
  /***************************************************/
  /* 2018-06-28 */
  /* driving with new steering battery lithium polymer */
  /** parallel parking challenge */
  _20180628T160026_275d4082,
  /** parallel parking challenge */
  _20180628T172648_275d4082,
  /** pure pursuit figure 8 including reverse driving */
  _20180628T173900_275d4082,
  /***************************************************/
  /* 2018-07-02 */
  /** pure pursuit forward and reverse */
  _20180702T102850_4e2a5674,
  /** parallel parking */
  _20180702T112912_4e2a5674,
  /** race track, fence */
  _20180702T133612_4e2a5674,
  /** workshop, pure pursuit, trajectory planning */
  _20180702T144949_4e2a5674,
  /** trajectory planning with spherical goal region */
  _20180702T153457_4e2a5674,
  /** race track */
  _20180702T154417_4e2a5674,
  /** pure pursuit figure eight with emergency stops
   * and localization failure */
  _20180702T170032_4e2a5674,
  /** pedestrians in front of stationary gokart */
  _20180702T174738_4e2a5674,
  /** race track */
  _20180702T180041_4e2a5674,
  /***************************************************/
  /* 2018-07-05 */
  /** manual driving on white track (from 2018-07-02) */
  _20180705T101944_b01c2886,
  /** manual driving along blue track */
  _20180705T105112_b01c2886,
  /** manual driving along blue track with water shed on the floor */
  _20180705T110648_b01c2886,
  /** manual driving along blue track */
  _20180705T145317_b01c2886,
  /** manual driving along blue track */
  _20180705T151140_b01c2886,
  /** manual driving along blue track with steering failure */
  _20180705T154948_b01c2886,
  /***************************************************/
  /* 2018-07-09 */
  /** two aerotain balloons present
   * manual driving */
  _20180709T114750_7838f4d6,
  /** manual driving */
  _20180709T145359_c3f2f23c,
  /** autonomous driving */
  _20180709T151010_c3f2f23c,
  /** autonomous driving */
  _20180709T160438_c3f2f23c,
  /** autonomous driving */
  _20180709T162136_c3f2f23c,
  /***************************************************/
  /* 2018-07-16 */
  // micro autobox not responding due to can
  /***************************************************/
  /* 2018-07-19 */
  /** stationary gokart parked in front of the container */
  _20180719T155247_c3f2f23c,
  /** manual driving */
  _20180719T160007_c3f2f23c,
  /***************************************************/
  /* 2018-07-23 */
  /** autonomous figure 8 but localization failure
   * due to many aerotain balloons present */
  _20180723T133113_c3f2f23c,
  /** manual driving */
  _20180723T151006_c3f2f23c,
  /** manual driving */
  _20180723T152836_c3f2f23c,
  /***************************************************/
  /* 2018-08-08 */
  // no davis240c
  /** manual driving */
  _20180808T100727_61778dc4,
  /** manual driving */
  _20180808T121708_61778dc4,
  /** manual driving */
  _20180808T150227_61778dc4,
  /** manual driving */
  _20180808T155907_61778dc4,
  /***************************************************/
  /* 2018-08-13 */
  // davis240c on
  /** collecting lidar data for localization map update */
  _20180813T115544_26cfbbca,
  /** slow pure pursuit figure 8 reverse */
  _20180813T131746_2c569ed8,
  /** slow pure pursuit figure 8 reverse */
  _20180813T134234_2c569ed8,
  /** pure pursuit figure 8 reverse with varying speeds
   * with davis240c but upside down */
  _20180813T151442_2c569ed8,
  /** pure pursuit figure 8 reverse with varying speeds
   * davis240c with correct orientation */
  _20180813T165630_2c569ed8,
  /***************************************************/
  /* 2018-08-14 */
  /** autonomous figure 8 reverse with varying speeds
   * along stripes for davis240c */
  _20180814T111143_2c569ed8,
  /** autonomous figure 8 forward with varying speeds
   * along stripes for davis240c */
  _20180814T112249_2c569ed8,
  /** manual figure 8 forward with varying speeds
   * along stripes for davis240c */
  _20180814T112843_2c569ed8,
  /** manual driving, davis240c off */
  _20180814T145725_2c569ed8,
  /** autonomous figure 8 reverse with varying speeds
   * along stripes for davis240c */
  _20180814T170407_2c569ed8,
  /** autonomous figure 8 reverse
   * pi-controller experiments resulting in backwards motion */
  _20180814T173757_2c569ed8,
  /** autonomous figure 8 reverse
   * pi-controller experiments resulting in backwards motion */
  _20180814T174943_2c569ed8,
  /** manual driving, davis240c on */
  _20180814T175821_2c569ed8,
  /***************************************************/
  /* 2018-08-20 */
  /** joystick driving, precursor for track red */
  _20180820T135013_568f9954,
  /** designing and testing track red
   * several stops for map building */
  _20180820T140707_568f9954,
  /** track red, [jh mg yn jh mg yn]
   * last driving stopped early */
  _20180820T143852_568f9954,
  /** track red, [jh mg yn jh yn] */
  _20180820T165637_568f9954,
  /***************************************************/
  /* 2018-08-23 system identification */
  /** manual driving track red, [mh, az, mh, az, az] */
  _20180823T115959_2693c28e,
  /** manual driving track red, [mh, az] */
  _20180823T134242_2693c28e,
  /** slow manual driving */
  _20180823T135656_2693c28e,
  /** pure pursuit figure eight reverse
   * forward/reverse with emergency stops */
  _20180823T152218_2693c28e,
  /** pure pursuit figure eight reverse
   * forward/reverse with emergency stops */
  _20180823T162001_2693c28e,
  /** manual driving track red, [mh, az, jh] */
  _20180823T163738_2693c28e,
  /***************************************************/
  /* 2018-08-27 system identification */
  /** track azure driving [jh, yn, mg, az] */
  _20180827T150209_db899976,
  /** slow manual driving along track
   * with frequent stops for gathering map */
  _20180827T155655_db899976,
  /** track azure driving [jh, az, yn, mg, az, yn, yn] */
  _20180827T170643_db899976,
  /** track azure driving [] */
  _20180827T175941_db899976,
  /***************************************************/
  /* 2018-08-30 */
  /** manual driving track azure */
  _20180830T101537_db899976,
  /** manual driving track azure */
  _20180830T111749_db899976,
  /** recording with silicon eye
   * while manual driving track azure
   * no Davis240c */
  _20180830T123206_db899976,
  /** recording with silicon eye
   * while manual driving track azure */
  _20180830T134222_db899976,
  /** testing new "anti-windup" controller
   * along figure eight reverse */
  _20180830T141843_21b2e8ae,
  /** testing new "anti-windup" controller
   * along figure eight reverse */
  _20180830T150618_21b2e8ae,
  /** testing new "anti-windup" controller
   * along figure eight reverse */
  _20180830T151854_21b2e8ae,
  /** slam and waypoint following based on davis240c */
  _20180830T160739_30e51fa2,
  /***************************************************/
  /* 2018-09-04 demoday */
  /** pure pursuit figure 8 reverse */
  _20180904T113548_c4fc6532,
  /** planning around changing obstacles
   * using bayesian mapping in separate thread */
  _20180904T115211_d1142faa,
  /** davis240c based waypoint following without lidar pose */
  _20180904T133123_e84a7b76,
  /** davis240c based waypoint following using lidar pose */
  _20180904T133718_e84a7b76,
  /** davis240c based waypoint following without lidar pose */
  _20180904T134507_e84a7b76,
  /** planning around changing obstacles */
  _20180904T150939_e84a7b76,
  /** driving on a straight line forward and reverse */
  _20180904T165639_b00c893a,
  /** planning around changing obstacles */
  _20180904T171628_b00c893a,
  /** planning around changing obstacles */
  _20180904T172719_b00c893a,
  /** planning around changing obstacles
   * visitors near the container and tables */
  _20180904T183437_b00c893a,
  /***************************************************/
  /* 2018-09-06 */
  /** event-based driving using davis240c */
  _20180906T134345_6cd480f5,
  /** event-based driving using davis240c */
  _20180906T134939_6cd480f5,
  /** event-based driving using davis240c */
  _20180906T140237_6cd480f5,
  /** event-based driving using davis240c, no localization */
  _20180906T141146_6cd480f5,
  /** trajectory planning */
  _20180906T165741_cf500306,
  /***************************************************/
  /* 2018-09-12 */
  /** measurement of front wheel inclination
   * using laser distance measurement */
  _20180912T110653_76cef406,
  /** slow joystick driving to collect mapping information
   * with vehicle and house tents */
  _20180912T142434_76cef406,
  /** planning a xyavt trajectory, stationary */
  _20180912T172503_c2615078,
  /** planning a xyavt trajectory, stationary */
  _20180912T172801_c2615078,
  /** planning a xyavt trajectory, stationary */
  _20180912T173408_c2615078,
  /** planning a xyavt trajectory, driving along */
  _20180912T174110_c2615078,
  /** planning a xyavt trajectory, driving along */
  _20180912T175114_c2615078,
  /** planning a xyavt trajectory, driving along */
  _20180912T180817_c2615078,
  /***************************************************/
  /* 2018-09-13 */
  /** testing if linmot maintains position even when in non-operational mode */
  _20180913T110848_c2615078,
  /** event-based marker following with davis240c */
  _20180913T115450_52e28a08,
  /** event-based marker following with davis240c */
  _20180913T120042_52e28a08,
  /** event-based marker following with davis240c */
  _20180913T121258_52e28a08,
  /** visitor joystick driving; trajectory planning */
  _20180913T143824_b0c36115,
  /** xyavt trajectory planning */
  _20180913T155444_b0c36115,
  /** several xyavt trajectory planning, driving along */
  _20180913T160707_b0c36115,
  /** event-based marker following with davis240c along circle */
  _20180913T164921_34b3470d,
  /** event-based marker following with davis240c along eight */
  _20180913T172039_34b3470d,
  /** event-based marker following with davis240c straight */
  _20180913T174600_34b3470d,
  /** system id track plastic [mh yn jh mh yn jh] */
  _20180913T183146_34b3470d,
  /***************************************************/
  /* 2018-09-17 */
  /** event-based marker following with davis240c */
  _20180917T110859_f47a58b9,
  /** event-based marker following with davis240c */
  _20180917T112522_f47a58b9,
  /** event-based marker following with davis240c */
  _20180917T113048_f47a58b9,
  /** event-based marker following with davis240c */
  _20180917T114847_f47a58b9,
  /** event-based marker following with davis240c */
  _20180917T142635_f47a58b9,
  /** event-based marker following with davis240c */
  _20180917T143904_f47a58b9,
  /** event-based marker following with davis240c */
  _20180917T144839_f47a58b9,
  /** event-based marker following with davis240c */
  _20180917T152855_f47a58b9,
  /** event-based marker following with davis240c */
  _20180917T154236_f47a58b9,
  /** event-based marker following with davis240c */
  _20180917T163109_f47a58b9,
  /** event-based marker following with davis240c
   * with markers parallel to driving direction */
  _20180917T163954_f47a58b9,
  /** event-based marker following with davis240c
   * with markers shape X */
  _20180917T165800_f47a58b9,
  /** event-based marker following with davis240c
   * with markers shape X */
  _20180917T170914_f47a58b9,
  /** event-based marker following with davis240c
   * with markers parallel to driving direction and placed to the sides */
  _20180917T172353_f47a58b9,
  /***************************************************/
  /* 2018-09-20 */
  /** event-based marker following with davis240c */
  _20180920T102409_53622489,
  /** event-based marker following with davis240c */
  _20180920T103907_53622489,
  /** event-based marker following with davis240c */
  _20180920T104534_53622489,
  /** event-based marker following with davis240c */
  _20180920T105332_53622489,
  /** event-based marker following with davis240c */
  _20180920T110838_53622489,
  /** event-based marker following with davis240c */
  _20180920T111706_53622489,
  /** event-based marker following with davis240c */
  _20180920T112831_53622489,
  /** event-based marker following with davis240c */
  _20180920T113529_53622489,
  /** event-based marker following with davis240c
   * long pause before beginning and after ending */
  _20180920T145559_53622489,
  /***************************************************/
  /* 2018-09-24 */
  /** event-based marker following with davis240c */
  _20180924T104243_820c1ac4,
  /** event-based marker following with davis240c */
  _20180924T105358_820c1ac4,
  /** event-based marker following with davis240c */
  _20180924T110653_820c1ac4,
  /** event-based marker following with davis240c */
  _20180924T141613_820c1ac4,
  /** event-based marker following with davis240c */
  _20180924T151741_820c1ac4,
  /** event-based marker following with davis240c
   * lidar based localization has trouble */
  _20180924T153005_820c1ac4,
  /** system identification */
  _20180924T162502_820c1ac4,
  /** system identification */
  _20180924T165429_820c1ac4,
  /***************************************************/
  /* 2018-09-27 */
  /** event-based marker following with davis240c */
  _20180927T120119_6581b630,
  /** event-based marker following with davis240c */
  _20180927T121713_6581b630,
  /** event-based marker following with davis240c */
  _20180927T123336_6581b630,
  /** torque vectoring */
  _20180927T143750_44599876,
  /** event-based marker following with davis240c */
  _20180927T145943_44599876,
  /** torque vectoring, without davis240c */
  _20180927T162555_44599876,
  /***************************************************/
  /* 2018-10-01 */
  /** torque vectoring */
  _20181001T142708_91a61e15,
  /** demo for visitor: figure 8, trajectory planning, emergency stops */
  _20181001T144009_91a61e15,
  /** torque vectoring */
  _20181001T163228_e654a9ad,
  /** trajectory planning, manual driving */
  _20181001T183434_e654a9ad,
  /***************************************************/
  /* 2018-10-03 */
  /** torque vectoring */
  _20181003T120312_e654a9ad,
  /** manual driving */
  _20181003T144503_6d8de4be,
  /** silicon eye recording while driving along markers
   * aedat protocol */
  _20181003T155915_f6edefe8,
  /** manual driving */
  _20181003T172231_f6edefe8,
  /***************************************************/
  /* 2018-10-05 */
  /** event-based marker following using davis */
  _20181005T104920_c0176eef,
  /** event-based marker following */
  _20181005T110121_c0176eef,
  /** event-based marker following */
  _20181005T112611_1cb189b4,
  /** manual driving along markers */
  _20181005T135151_1cb189b4,
  /** event-based marker following */
  _20181005T145807_1cb189b4,
  /** driving with silicon eye and davis on */
  _20181005T154321_1cb189b4,
  /** driving with silicon eye and davis on */
  _20181005T161158_1cb189b4,
  /** event-based marker following */
  _20181005T181350_1cb189b4,
  /** event-based marker following */
  _20181005T183532_1cb189b4,
  /** event-based marker following */
  _20181005T184013_1cb189b4,
  /** event-based marker following */
  _20181005T184043_1cb189b4,
  /***************************************************/
  /* 2018-10-08 */
  /** autonomous driving, trajectory following
   * test of {@link SpeedLimitSafetyModule} */
  _20181008T114035_0faeff06,
  /** manual driving track orange, torque vectoring comparison */
  _20181008T160558_88f26d5f,
  /** manual driving track orange, torque vectoring comparison */
  _20181008T161549_88f26d5f,
  /** manual driving track orange, torque vectoring comparison */
  _20181008T165907_69109edf,
  /** manual driving track orange, torque vectoring comparison */
  _20181008T175943_786ab990,
  /** manual driving track orange, torque vectoring comparison */
  _20181008T183011_786ab990,
  /***************************************************/
  /* 2018-10-10 demo */
  /** figure 8, trajectory planning */
  _20181010T143615_786ab990,
  /** manual driving track orange */
  _20181010T151121_786ab990,
  /** manual driving track orange, torque vectoring comparison */
  _20181010T160308_786ab990,
  /** manual driving */
  _20181010T163911_786ab990,
  /** manual driving */
  _20181010T171643_786ab990,
  /***************************************************/
  /* 2018-10-11 */
  /** manual driving using torque vectoring */
  _20181011T115610_66b8cfdb,
  /** manual driving using torque vectoring */
  _20181011T121025_66b8cfdb,
  /** manual driving using torque vectoring */
  _20181011T123004_4a8286b3,
  /** manual driving */
  _20181011T152524_4a8286b3,
  /** manual driving using torque vectoring */
  _20181011T171350_96e04760,
  /** manual driving using torque vectoring */
  _20181011T172043_96e04760,
  /** slow manual driving */
  _20181011T172612_96e04760,
  /** manual driving using torque vectoring */
  _20181011T173058_96e04760,
  /** manual driving using torque vectoring
   * autonomous driving figure 8 reverse */
  _20181011T173747_96e04760,
  /***************************************************/
  /* 2018-10-15 */
  /** demo with figure 8, trajectory following, torque vectoring */
  _20181015T140629_f273889f,
  /** figure 8, and trajectory following */
  _20181015T153805_f273889f,
  /** figure 8 */
  _20181015T160852_f273889f,
  /** davis240c event-based waypoint following */
  _20181015T171909_a9bf87af,
  /***************************************************/
  /* 2018-10-18 */
  /** figure 8, manual driving */
  _20181018T134353_a9bf87af,
  /** trajectory planning with spherical goal region */
  _20181018T140542_1a649e65,
  /***************************************************/
  /* 2018-10-22 */
  /** testing normalized torque vectoring */
  _20181022T104121_e2d44216,
  /** demo for visitors: figure 8, trajectory planning */
  _20181022T113943_e2d44216,
  /** lookup table rimo rate controller figure 8 */
  _20181022T143102_f6163877,
  /***************************************************/
  /* 2018-10-25 */
  /** braking at different press levels */
  _20181025T112636_34e647c3,
  /** braking at different press levels */
  _20181025T114229_6b1a19b1,
  /** braking at different press levels */
  _20181025T131025_6b1a19b1,
  /** braking at different press levels */
  _20181025T131901_6b1a19b1,
  /** braking at different press levels */
  _20181025T133400_6b1a19b1,
  /** demo for student: autonomous figure 8,
   * manual driving, trajectory planning */
  _20181025T153139_6b1a19b1,
  /** trajectory planning, manual driving */
  _20181025T160134_6b1a19b1,
  /** trajectory planning */
  _20181025T161638_6b1a19b1,
  /** trajectory planning */
  _20181025T162632_6b1a19b1,
  /** trajectory planning */
  _20181025T165405_6b1a19b1,
  /** trajectory planning, manual driving */
  _20181025T183141_bcbf8a93,
  /***************************************************/
  /* 2018-10-29 */
  /** slow manual driving */
  _20181029T132801_e004ef97,
  /** recording effect of fog machine */
  _20181029T134619_e004ef97,
  /** trajectory planning */
  _20181029T152509_e004ef97,
  /** trajectory planning */
  _20181029T153536_e004ef97,
  /** trajectory planning */
  _20181029T160947_e004ef97,
  /** trajectory planning */
  _20181029T170443_e004ef97,
  /** trajectory planning, figure eight, reverse */
  _20181029T175711_e004ef97,
  /** manual driving */
  _20181029T191539_e004ef97,
  /** manual driving */
  _20181029T193004_e004ef97,
  /** manual driving */
  _20181029T194801_e004ef97,
  /***************************************************/
  /* 2018-10-30 */
  /** manual driving */
  _20181030T171745_e004ef97,
  /** trajectory planning */
  _20181030T173005_e004ef97,
  /** trajectory planning */
  _20181030T173307_e004ef97,
  /***************************************************/
  /* 2018-11-01 */
  /** trajectory planning */
  _20181101T115317_e004ef97,
  /** trajectory planning */
  _20181101T122932_e004ef97,
  /** trajectory planning */
  _20181101T123219_e004ef97,
  /** trajectory planning */
  _20181101T150501_e004ef97,
  /** trajectory planning */
  _20181101T161155_e004ef97,
  /***************************************************/
  /* 2018-11-06 */
  /** trajectory planning */
  _20181106T144827_e004ef97,
  /** trajectory planning */
  _20181106T165850_e004ef97,
  /** trajectory planning */
  _20181106T173049_e004ef97,
  /***************************************************/
  /* 2018-11-09 */
  /** slow mpc */
  _20181109T110020_33c24ec8,
  /** slow mpc */
  _20181109T110837_33c24ec8,
  /** slow mpc */
  _20181109T115340_33c24ec8,
  /** slow mpc */
  _20181109T135439_c6eb9ac8,
  /** slow mpc */
  _20181109T135845_c6eb9ac8,
  /** slow mpc */
  _20181109T140126_c6eb9ac8,
  /** slow mpc wobbly track */
  _20181109T152146_c04d20dd,
  /** mpc curvy track */
  _20181109T152823_c04d20dd,
  /** mpc curvy track */
  _20181109T154810_c04d20dd,
  /** mpc curvy track */
  _20181109T155424_c04d20dd,
  /** mpc oval track */
  _20181109T155803_c04d20dd,
  /** mpc oval track */
  _20181109T160019_c04d20dd,
  /***************************************************/
  /* 2018-11-12 */
  /** slow manual driving */
  _20181112T140527_6e6f094b,
  /** slow manual driving */
  _20181112T144415_1a22f701,
  /** mpc driving narrow circles */
  _20181112T144729_1a22f701,
  /** mpc driving medium circles */
  _20181112T145009_1a22f701,
  /** mpc oval track */
  _20181112T145912_1a22f701,
  /** mpc something */
  _20181112T152435_1a22f701,
  /** mpc curvy track */
  _20181112T152833_1a22f701,
  /** mpc curvy track */
  _20181112T153332_1a22f701,
  /** mpc curvy track */
  _20181112T154058_1a22f701,
  /** mpc curvy track */
  _20181112T154408_1a22f701,
  /** mpc curvy track */
  _20181112T164438_1a22f701,
  /** mpc curvy track */
  _20181112T164719_1a22f701,
  /** mpc curvy track, manual driving */
  _20181112T170810_1a22f701,
  /** mpc curvy track up to 5[m/s] */
  _20181112T171453_1a22f701,
  /***************************************************/
  /* 2018-11-15 */
  // no driving
  /***************************************************/
  /* 2018-11-20 */
  /** brief mpc and manual driving */
  _20181120T153650_3e86cf88,
  /** mpc curvy track */
  _20181120T160108_1bd00b36,
  /** mpc curvy track */
  _20181120T160653_1bd00b36,
  /** brief mpc and manual driving */
  _20181120T161355_1bd00b36,
  /** mpc curvy track */
  _20181120T161932_1bd00b36,
  /** manual driving */
  _20181120T162403_1bd00b36,
  /** mpc curvy track, trajectory planning */
  _20181120T165942_b52778b7,
  /** brief mpc and manual driving */
  _20181120T191027_b52778b7,
  /** brief mpc and manual driving */
  _20181120T192304_b52778b7,
  /***************************************************/
  /* 2018-11-26 */
  /** trajectory planning */
  _20181126T162819_536ef97b,
  /** trajectory planning */
  _20181126T164756_536ef97b,
  /** trajectory planning */
  _20181126T165929_536ef97b,
  /** mpc */
  _20181126T171927_536ef97b,
  /** trajectory planning */
  _20181126T174937_18384a74,
  /***************************************************/
  /* 2018-11-27 */
  /** trajectory planning */
  _20181127T143921_35b19382,
  /** autonomous figure 8, manual driving */
  _20181127T162138_35b19382,
  /** trajectory planning */
  _20181127T172938_35b19382,
  /***************************************************/
  /* 2018-11-28 demo day */
  /** trajectory planning */
  _20181128T112109_35b19382,
  /** trajectory planning */
  _20181128T130954_35b19382,
  /** trajectory planning */
  _20181128T134352_35b19382,
  /** trajectory planning, demo */
  _20181128T145801_35b19382,
  /** manual driving, torque vectoring */
  _20181128T160330_35b19382,
  /***************************************************/
  /* 2018-12-03 old car tires on track */
  /** manual driving with tires on track */
  _20181203T135247_70097ce1,
  /** manual driving with tires on track
   * localization mostly intact */
  _20181203T141504_70097ce1,
  /** manual driving with tires on track */
  _20181203T142514_70097ce1,
  /** manual driving, testing ranked put providers */
  _20181203T165931_1649aef7,
  /** manual driving up to 7[m/s], odometry uses gyro */
  _20181203T184122_3309d8c4,
  /** manual driving up to 7[m/s], odometry uses gyro */
  _20181203T185927_3309d8c4,
  /***************************************************/
  /* 2018-12-06 */
  /** slow manual driving between tires
   * trajectory planning */
  _20181206T110202_3309d8c4,
  /** fast manual driving between tires */
  _20181206T122251_3309d8c4,
  /** slow manual driving between tires
   * trajectory planning */
  _20181206T135020_d5c027d3,
  /** fast manual driving between tires
   * with changing topology */
  _20181206T160846_d5c027d3,
  /***************************************************/
  /* 2018-12-11 */
  /** torque vectoring */
  _20181211T141643_f8690659,
  /** torque vectoring, trajectory planning */
  _20181211T143841_f8690659,
  /** torque vectoring */
  _20181211T153939_f8690659,
  /** torque vectoring */
  _20181211T155230_f8690659,
  /***************************************************/
  /* 2018-12-13 */
  /** demo manual driving, trajectory planning */
  _20181213T100655_add1a7bf,
  /** demo manual driving, trajectory planning */
  _20181213T133900_a04ee685,
  /** manual driving with throttle (short) */
  _20181213T141826_cc82dcb3,
  /** manual driving with throttle, localization failure */
  _20181213T143648_cc82dcb3,
  /** manual driving with throttle, localization failure */
  _20181213T154338_6728a721,
  /***************************************************/
  /* 2018-12-15 */
  /** manual driving */
  _20181215T081653_9ca96533,
  /***************************************************/
  /* 2018-12-18 */
  /** manual driving */
  _20181218T114300_3161d85d,
  /** manual driving */
  _20181218T120906_b554b6a9,
  /** manual driving */
  _20181218T122901_b554b6a9,
  /** manual driving slow */
  _20181218T141322_b554b6a9,
  /** manual driving slow */
  _20181218T142316_b554b6a9,
  /** manual driving slow */
  _20181218T144337_b554b6a9,
  /** manual driving, trajectory planning */
  _20181218T150139_b554b6a9,
  /** manual driving */
  _20181218T172046_7d7bfcfb,
  /***************************************************/
  /* 2018-12-20 */
  /** manual driving; localization failure at end */
  _20181220T113627_6ad0d1f8,
  /** manual driving */
  _20181220T115137_6ad0d1f8,
  /** manual driving */
  _20181220T134721_2a2cab5b,
  /** manual driving; mpc */
  _20181220T135558_2a2cab5b,
  /** manual driving; mpc */
  _20181220T144124_2a2cab5b,
  /** manual driving; mpc */
  _20181220T151446_6d16b8f5,
  /** manual driving; mpc */
  _20181220T152212_6d16b8f5,
  /** manual driving; mpc */
  _20181220T152848_2287c884,
  /***************************************************/
  /* 2018-12-28 */
  /** manual driving */
  _20181228T084736_8f8316ab,
  /** manual driving; mpc */
  _20181228T085118_8f8316ab,
  /** manual driving */
  _20181228T155256_41aeb417,
  /** manual driving; mpc */
  _20181228T160225_41aeb417,
  /***************************************************/
  /* 2019-01-10 */
  /** manual driving */
  _20190110T143855_41aeb417,
  /** manual driving; autobox failure? */
  _20190110T145219_41aeb417,
  /** autobox failure? */
  _20190110T150234_41aeb417,
  /** manual driving; localization not initialized */
  _20190110T150643_41aeb417,
  /** manual driving; localization not initialized */
  _20190110T173502_41aeb417,
  /***************************************************/
  /* 2019-01-14 */
  /** manual driving */
  _20190114T110212_9f0070a9,
  /** manual driving */
  _20190114T142213_83a9d595,
  /***************************************************/
  /* 2019-01-16 */
  /** manual driving */
  _20190116T095357_1df9eb42,
  /** manual driving */
  _20190116T095915_1df9eb42,
  /** manual driving */
  _20190116T100232_1df9eb42,
  /** manual driving */
  _20190116T113307_e6cad884,
  /** manual driving */
  _20190116T114807_e6cad884,
  /***************************************************/
  /* 2019-01-17 power conserving torque vectoring */
  /** manual driving */
  _20190117T144922_f882e5e2,
  /** manual driving */
  _20190117T153934_e746c6d4,
  /** manual driving, mpc */
  _20190117T154746_58bbb64d,
  /** manual driving, mpc */
  _20190117T162143_5412c078,
  /** manual driving, mpc */
  _20190117T162719_5412c078,
  /** manual driving, mpc */
  _20190117T163814_5412c078,
  /** manual driving, mpc */
  _20190117T164057_5412c078,
  /***************************************************/
  /* 2019-01-21 autonomous driving without holding pressing the dead-man switch */
  /** manual driving */
  _20190121T115509_3e45ef97,
  /** manual driving, mpc */
  _20190121T121032_3e45ef97,
  /** manual driving, mpc */
  _20190121T132318_4c60fa65,
  /** manual driving, mpc */
  _20190121T140815_49bf477f,
  /** manual driving, mpc */
  _20190121T141848_49bf477f,
  /** manual driving, mpc */
  _20190121T142535_49bf477f,
  /** */
  _20190121T151918_3969d7aa,
  /** */
  _20190121T153427_3969d7aa,
  /** */
  _20190121T155506_3969d7aa,
  /***************************************************/
  /* 2019-01-25 new sensor VMU931 */
  /** manual driving data collection vmu931 */
  _20190125T105720_ecbd24e3,
  /** manual driving data collection vmu931 */
  _20190125T133925_e5eb6f95,
  /** autonomous driving, vmu931 */
  _20190125T134537_e5eb6f95,
  /** mpc */
  _20190125T150258_c2df0d09,
  /***************************************************/
  /* 2019-01-28 */
  /** track recon and pure pursuit of center line */
  _20190128T133144_6f6e3dee,
  /** manual driving data collection vmu931 */
  _20190128T134321_6f6e3dee,
  /** manual driving, pure pursuit */
  _20190128T141006_6f6e3dee,
  /** slow driving */
  _20190128T145748_0811fd85,
  /** faster driving */
  _20190128T150120_6f6e3dee,
  /** faster driving */
  _20190128T151045_0811fd85,
  /** faster driving */
  _20190128T151537_0811fd85,
  /** short driving without localization */
  _20190128T152117_0811fd85,
  /** slow driving */
  _20190128T154652_0811fd85,
  /** slow driving */
  _20190128T155203_0811fd85,
  /** slow driving */
  _20190128T162344_380dac0f,
  /** slow driving */
  _20190128T165650_f558b2d4,
  /** pure pursuit forward and reverse up to 6[m/s] */
  _20190128T171650_f558b2d4,
  /***************************************************/
  /* 2019-01-31 */
  /** mpc */
  _20190131T105100_cb553d2c,
  /** mpc */
  _20190131T110019_cb553d2c,
  /** mpc */
  _20190131T115309_cb553d2c,
  /** mpc */
  _20190131T151301_76c01162,
  /** mpc */
  _20190131T152406_76c01162,
  /** pure pursuit, manual driving */
  _20190131T160439_9b9dc0ca,
  /** manual driving */
  _20190131T161655_9b9dc0ca,
  /***************************************************/
  /* 2019-02-04 */
  /** pushing gokart manually to test track recon module */
  _20190204T092436_a07a8452,
  /** pure pursuit demo for visitor group */
  _20190204T104424_a07a8452,
  /** manual driving, mpc */
  _20190204T125814_ee446e1c,
  /** mpc */
  _20190204T130517_ee446e1c,
  /** manual driving, mpc */
  _20190204T135550_2393d562,
  /** mpc */
  _20190204T140137_2393d562,
  /** mpc */
  _20190204T143908_e9fccdaa,
  /** mpc */
  _20190204T144259_e9fccdaa,
  /** mpc */
  _20190204T145011_e9fccdaa,
  /** pure pursuit */
  _20190204T145317_e9fccdaa,
  /** mpc */
  _20190204T145612_e9fccdaa,
  /** mpc */
  _20190204T150553_e9fccdaa,
  /** mpc */
  _20190204T151113_e9fccdaa,
  /** mpc */
  _20190204T154239_e9fccdaa,
  /** mpc */
  _20190204T155454_e9fccdaa,
  /** mpc */
  _20190204T160247_e9fccdaa,
  /** mpc */
  _20190204T161100_e9fccdaa,
  /** pure pursuit */
  _20190204T172205_e9fccdaa,
  /** mpc, pure pursuit, manual driving */
  _20190204T185052_fdc1b4b9,
  /***************************************************/
  /* 2019-02-08 */
  /** vmu931 at new location, manual driving, mpc */
  _20190208T145312_dac9d0fb,
  /***************************************************/
  /* 2019-02-11 */
  /** manual driving for collecting brake data */
  _20190211T100755_8b7a8047,
  /** manual driving for collecting brake data, testing velocity estimation */
  _20190211T113051_e3c6742e,
  /** testing adv conversion, no driving */
  _20190211T120203_e3c6742e,
  /** testing non-threaded abstract module, pure pursuit, manual driving */
  _20190211T130907_ad3cc9bb,
  /** manual driving, mpc */
  _20190211T133154_eb380e2c,
  /** manual driving, mpc */
  _20190211T134942_eb380e2c,
  /** slow manual driving */
  _20190211T142219_fa03c034,
  /** slow manual driving */
  _20190211T143219_fa03c034,
  /** slow manual driving */
  _20190211T143539_fa03c034,
  /** mpc with localization failure */
  _20190211T144141_fa03c034,
  /** mpc */
  _20190211T151908_6e0a6d48,
  /** manual driving, mpc, log file ends abruptly */
  _20190211T155100_0946f9fb,
  /** mpc */
  _20190211T155641_0946f9fb,
  /** mpc */
  _20190211T161417_0946f9fb,
  /** manual driving, mpc */
  _20190211T161759_0946f9fb,
  /** manual driving, mpc */
  _20190211T162033_0946f9fb,
  /** manual driving, mpc */
  _20190211T162359_0946f9fb,
  /** manual driving, mpc brief */
  _20190211T162923_0946f9fb,
  /** mpc */
  _20190211T163243_0946f9fb,
  /** manual driving, mpc */
  _20190211T172719_e8f75c95,
  /***************************************************/
  /* 2019-02-14 */
  /** mpc */
  _20190214T141704_24d542f6,
  /** mpc */
  _20190214T144808_24d542f6,
  /** manual driving */
  _20190214T154227_a943ac32,
  /** mpc */
  _20190214T154813_a943ac32,
  /** mpc */
  _20190214T171441_ad628347,
  /** mpc */
  _20190214T173709_380160a9,
  /***************************************************/
  /* 2019-02-15 */
  /** mpc */
  _20190215T113351_380160a9,
  /** manual driving */
  _20190215T135130_380160a9,
  /** mpc */
  _20190215T135406_380160a9,
  /** manual driving */
  _20190215T143502_380160a9,
  /** mpc */
  _20190215T144054_380160a9,
  /** mpc */
  _20190215T144349_380160a9,
  /** mpc */
  _20190215T150920_380160a9,
  /** mpc */
  _20190215T152744_380160a9,
  /** mpc */
  _20190215T154532_380160a9,
  /***************************************************/
  /* 2019-03-04 */
  /** slow manual driving */
  _20190304T151610_380160a9,
  /** fast manual driving, localization failure */
  _20190304T181143_e8ec1e35,
  /***************************************************/
  /* 2019-03-07 */
  /** slow manual driving */
  _20190307T105648_e8ec1e35,
  /** track recon and pure pursuit */
  _20190307T110429_e8ec1e35,
  /** power steering test, slow and fast manual driving */
  _20190307T161708_086ad351,
  /***************************************************/
  /* 2019-03-08 */
  /** manual driving fast straight */
  _20190308T145103_ad48d4dd,
  /** manual driving fast straight */
  _20190308T145247_ad48d4dd,
  /** manual driving fast curves */
  _20190308T162915_ad48d4dd,
  /** manual driving fast curves */
  _20190308T163555_ad48d4dd,
  /***************************************************/
  /* 2019-03-09 */
  /** manual driving tokyo drift with localization failure */
  _20190309T115037_d527c4a8,
  /** manual driving tokyo drift */
  _20190309T134051_7b231074,
  /** manual driving tokyo drift with localization failure */
  _20190309T135141_a9dcd8dc,
  /** swissloop drift doughnuts */
  _20190309T160311_a9dcd8dc,
  /** slow manual driving */
  _20190309T171059_ed0b19fb,
  /** slow manual driving */
  _20190309T172802_ed0b19fb,
  /** slow manual driving */
  _20190309T173356_ed0b19fb,
  /** mpc dynamic model */
  _20190309T174349_ed0b19fb,
  /** mpc dynamic model */
  _20190309T181745_ed0b19fb,
  /** mpc dynamic model */
  _20190309T191245_885223b3,
  /** swiss loop, mpc dynamic model */
  _20190309T201432_885223b3,
  /** swiss loop, mpc dynamic model */
  _20190309T212714_c5c7a266,
  /***************************************************/
  /* 2019-03-10 */
  /** mpc */
  _20190310T151604_3e1a6ce8,
  /** mpc */
  _20190310T171601_6b2a14e5,
  /** mpc */
  _20190310T173552_6b2a14e5,
  /** mpc */
  _20190310T175648_6b2a14e5,
  /** mpc */
  _20190310T195505_67e7ec8d,
  /** mpc */
  _20190310T203926_67e7ec8d,
  /** swiss loop drift */
  _20190310T220933_67e7ec8d,
  /***************************************************/
  /* 2019-03-11 */
  /** swiss loop drift */
  _20190311T102715_67e7ec8d,
  /** trajectory planning */
  _20190311T134617_35a37055,
  /** swiss loop drift */
  _20190311T143335_35a37055,
  /** slow manual driving */
  _20190311T153709_cec49cde,
  /** mpc simple track */
  _20190311T164018_da0bb9b9,
  /** mpc challenging track
   * manual driving
   * [abrupt log termination] */
  _20190311T173809_da0bb9b9,
  /***************************************************/
  /* 2019-03-14
   * dust proof wall was installed */
  /** manual driving, no localization */
  _20190314T113402_396b512b,
  /** manual driving, no localization */
  _20190314T114500_086ad351,
  /** manual driving, no localization */
  _20190314T115655_cec49cde,
  /** slow manual driving collecting data for map update */
  _20190314T135434_cec49cde,
  /** mpc wet floor */
  _20190314T143253_382aafe0,
  /** power steering proportional to angle */
  _20190314T150851_c3c5a0db,
  /** mpc */
  _20190314T154544_c3c5a0db,
  /** mpc
   * [abrupt log termination] */
  _20190314T174957_c3c5a0db,
  /** mpc
   * [abrupt log termination] */
  _20190314T175917_c3c5a0db,
  /***************************************************/
  /* 2019-03-18 */
  /** mpc */
  _20190318T111729_2f26ff8b,
  /** mpc challenging track */
  _20190318T114748_eace07e4,
  /** manual driving challenging track */
  _20190318T120437_eace07e4,
  /** slow and fast manual driving */
  _20190318T141147_9a9329f5,
  /** mpc and slow manual driving */
  _20190318T141608_9a9329f5,
  /** mpc and manual driving */
  _20190318T142605_9a9329f5,
  /***************************************************/
  /* 2019-03-21 */
  /** slow manual driving */
  _20190321T111128_19a9d2bb,
  /** power steering test
   * localization inactive */
  _20190321T114452_79067bba,
  /** data collection for mapping challenge
   * with moving obstacles and various speeds
   * [abrupt log termination] */
  _20190321T144129_140b9727,
  /** power steering test */
  _20190321T152149_140b9727,
  /** pure pursuit */
  _20190321T170225_140b9727,
  /** pure pursuit */
  _20190321T174352_b8d4b66a,
  /***************************************************/
  /* 2019-03-25
   * davis 240c is not detected */
  /** testing gokart pose event v2 */
  _20190325T083437_be8261a9,
  /** steer torque to angle experiment */
  _20190325T112618_cfdd7814,
  /** short driving */
  _20190325T134326_583c4e95,
  /** manual and mpc driving */
  _20190325T135103_583c4e95,
  /** manual and mpc driving */
  _20190325T140608_583c4e95,
  /** fast manual driving */
  _20190325T141033_583c4e95,
  /** test of pid trajectory following */
  _20190325T144606_47d5c461,
  /** mpc with changing topology */
  _20190325T163400_f5272f99,
  /** slow mpc */
  _20190325T170745_61639f01,
  /** slow manual driving */
  _20190325T171337_61639f01,
  /** mpc with changing map */
  _20190325T172211_61639f01,
  /** mpc with changing map
   * [abrupt log termination] */
  _20190325T172919_61639f01,
  /** mpc */
  _20190325T175100_61639f01,
  /***************************************************/
  /* 2019-03-28 */
  /** brief manual driving */
  _20190328T112543_3b0b20b0,
  /** brief manual driving */
  _20190328T144659_e6dada55,
  /** slow manual driving */
  _20190328T145152_e6dada55,
  /** trajectory planning */
  _20190328T155002_ad28d651,
  /** trajectory planning */
  _20190328T162227_e084a72b,
  /** kinematic mpc
   * [abrupt log termination] */
  _20190328T163118_558748f1,
  /** manual driving with localization failure az, jg */
  _20190328T164433_ad28d651,
  /** mpc az jg
   * manual driving jh */
  _20190328T165416_558748f1,
  /***************************************************/
  /* 2019-03-29 */
  /** manual driving, mpc
   * [abrupt log termination] */
  _20190329T140240_459f8e9a,
  /** mpc, manual driving */
  _20190329T141035_459f8e9a,
  /** mpc, localization failure */
  _20190329T142259_459f8e9a,
  /** mpc, localization failure */
  _20190329T143349_558748f1,
  /** slow manual driving to scan map
   * mpc, localization failure */
  _20190329T144049_558748f1,
  /** mpc, localization failure */
  _20190329T151406_558748f1,
  /** mpc, localization failure */
  _20190329T152003_558748f1,
  /***************************************************/
  /* 2019-04-01 */
  /** slow manual driving, mpc */
  _20190401T101109_411917b6,
  /** trajectory planning with moving obstacles */
  _20190401T115537_411917b6,
  /***************************************************/
  /* 2019-04-04 */
  /** manual and mpc drive */
  _20190404T133714_39258d17,
  /** mpc */
  _20190404T143912_39258d17,
  /** mostly manual drive but without localization
   * start at {26.0749605[m], 30.1878686[m], -1.1216625} */
  _20190404T154558_eb6eadfe,
  /***************************************************/
  /* 2019-04-08 */
  /** mpc with challenging track */
  _20190408T103556_eb6eadfe,
  /** mh thesis benchmarking: dynamic kinematic */
  _20190408T104821_eb6eadfe, //
  /** slow manual driving with localization failure */
  _20190408T111457_eb6eadfe,
  /** fast manual driving with localization failure */
  _20190408T123841_eb6eadfe,
  /** mh thesis benchmarking: human centerline kinematic */
  _20190408T124954_eb6eadfe,
  /***************************************************/
  /* 2019-04-15 */
  /* steer battery was found discharged */
  /***************************************************/
  /* 2019-04-18 */
  /** geodesic pursuit */
  _20190418T141321_b6a70baf,
  /** geodesic pursuit, slow manual driving */
  _20190418T142758_b6a70baf,
  /** testing steering but without localization */
  _20190418T144403_b6a70baf,
  /** testing steering but without localization
   * start at {28.9864287[m], 27.5183134[m], -1.1886032} */
  _20190418T145229_b6a70baf,
  /** geodesic pursuit */
  _20190418T155607_b6a70baf,
  /** geodesic pursuit, drift in accelerometer */
  _20190418T160410_b6a70baf,
  /** geodesic pursuit */
  _20190418T160707_b6a70baf,
  /** geodesic pursuit */
  _20190418T161148_b6a70baf,
  /***************************************************/
  /* 2019-04-24 */
  /** geodesic pursuit */
  _20190424T130849_b486c368,
  /** geodesic pursuit */
  _20190424T131353_aa45eece,
  /** geodesic pursuit */
  _20190424T144517_f0590cf6,
  /** geodesic pursuit */
  _20190424T144844_f0590cf6,
  /** geodesic pursuit */
  _20190424T155214_3262e93a,
  /** geodesic pursuit */
  _20190424T161815_3262e93a,
  /** geodesic pursuit */
  _20190424T162520_3262e93a,
  /***************************************************/
  /* 2019-04-25 */
  /** with HighPowerSteerPid, each 2 times slow, 2 times fast
   * pure pursuit
   * geodesic pursuit, minimal distance [m] 3, 4, 5, 6, 7 */
  _20190425T133500_7cf20bb2,
  /** geodesic pursuit, minimal distance [m] 3, 6, with default SteerPositionControl, each slow and fast */
  _20190425T135709_7cf20bb2,
  /** fast manual drive */
  _20190425T140358_7cf20bb2,
  /** fast manual drive */
  _20190425T141045_7cf20bb2,
  /***************************************************/
  /* 2019-04-29 */
  /** slow pursuit */
  _20190429T111737_37db3fce,
  /** slow pursuit */
  _20190429T141452_70dfcdda,
  /** slow pursuit */
  _20190429T142046_70dfcdda,
  /** slow pursuit */
  _20190429T154513_3c0dfa28,
  /** slow pursuit */
  _20190429T163418_25103376,
  /***************************************************/
  /* 2019-05-02 */
  /** geodesic pursuit */
  _20190502T105037_bdbf8063,
  /** geodesic pursuit */
  _20190502T110217_bdbf8063,
  /** fast manual driving */
  _20190502T113816_923429be,
  /** geodesic pursuit */
  _20190502T115238_3839b112,
  /** geodesic pursuit */
  _20190502T133418_3839b112,
  /** power steering test */
  _20190502T135355_622e8d75,
  /***************************************************/
  /* 2019-05-06 */
  /** tuning of power steering
   * [abrupt log termination] */
  _20190506T101748_99afbf25,
  /** tuning of power steering */
  _20190506T102339_99afbf25,
  /** racing with and without power steering */
  _20190506T134816_99afbf25,
  /** manual driving, pursuit */
  _20190506T141135_99afbf25,
  /** pursuit */
  _20190506T151117_dd899454,
  /** pd control trajectory pursuit */
  _20190506T162944_9c0e80e3,
  /** pd control trajectory pursuit */
  _20190506T165554_9c0e80e3,
  /***************************************************/
  /* 2019-05-07 */
  /** test for demo with occupancy grid mapping */
  _20190507T091621_c817d5db,
  /** test for demo with sight lines mapping */
  _20190507T092002_c817d5db,
  /** demo trajectory planning
   * data collection for steering mapping */
  _20190507T094525_c817d5db,
  /** fast manual driving */
  _20190507T122145_06a2a5f4,
  /** mpc */
  _20190507T142359_06a2a5f4,
  /** mpc, localization failure
   * TODO investigate: system block? */
  _20190507T155931_06a2a5f4,
  /** short mpc, manual driving */
  _20190507T161525_06a2a5f4,
  /***************************************************/
  /* 2019-05-09 */
  /** demo of trajectory planner and mpc */
  _20190509T102655_56443d8b,
  /** steering static force test */
  _20190509T113253_dced107a,
  /** steering static force test */
  _20190509T121107_dced107a,
  /** steering static force test */
  _20190509T123210_dced107a,
  /** steering static force test */
  _20190509T124117_dced107a,
  /** steering static force test [no] */
  _20190509T124910_dced107a,
  /***************************************************/
  /* 2019-05-13 */
  /** manual driving */
  _20190513T112548_ac099b4a,
  /** manual driving and mpc */
  _20190513T114553_0026208f,
  /** mpc */
  _20190513T150620_0026208f,
  /** mpc */
  _20190513T151627_0026208f,
  /** mpc */
  _20190513T185714_0026208f,
  /***************************************************/
  /* 2019-05-14 */
  /** mpc */
  _20190514T090101_ce869540,
  /** mpc with localization failure */
  _20190514T092148_ce869540,
  /** mpc with camera team
   * 1st part: start set incorrectly
   * 2nd part: good performance */
  _20190514T102650_ce869540,
  /** mpc with camera team */
  _20190514T103657_ce869540,
  /** mpc with camera team
   * good performance, localization failure */
  _20190514T105746_ce869540,
  /** mpc with camera team
   * good performance */
  _20190514T111152_ce869540,
  /** mpc */
  _20190514T141121_b942849a,
  /** mpc localization failure */
  _20190514T143608_b942849a,
  /** steer static force test
   * using pid with autonomous button pressed */
  _20190514T152853_b942849a,
  /** manual drive with power steering */
  _20190514T162756_577662b6,
  /** manual drive with power steering */
  _20190514T164612_577662b6,
  /***************************************************/
  ;
  // ---
  @Override // from LogFile
  public String getFilename() {
    return name().substring(1) + ".lcm.00";
  }

  @Override // from LogFile
  public String getTitle() {
    return name().substring(1, 16);
  }

  /** @param startInclusive
   * @param endInclusive
   * @return */
  public static List<GokartLogFile> range(GokartLogFile startInclusive, GokartLogFile endInclusive) {
    return Arrays.asList(values()) //
        .subList(startInclusive.ordinal(), endInclusive.ordinal() + 1);
  }
}
