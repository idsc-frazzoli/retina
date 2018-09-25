# ch.ethz.idsc.retina+gokart <a href="https://travis-ci.org/idsc-frazzoli/retina"><img src="https://travis-ci.org/idsc-frazzoli/retina.svg?branch=master" alt="Build Status"></a>

Sensor and actuator interfaces, Gokart software

The repository was developed with the following objectives in mind
* interface sensors without loss of precision or temporal resolution
* interface actuators of gokart using a protocol that is specific to the MicroAutoBox implementation

The code in the repository operates a heavy and fast robot that may endanger living creatures.
We follow best practices and coding standards to protect from avoidable errors.
See [development_guidelines](doc/development_guidelines.md)

## Features

* interfaces to lidars Velodyne VLP-16, HDL-32E, Quanergy Mark8, HOKUYO URG-04LX-UG01
* interfaces to event based camera Davis240C with lossless compression by 4x
* lidar based localization
* simultaneous localization and mapping for event-based vision systems inspired by Weikersdorfer/Hoffmann/Conradt, reliable waypoint extraction and following
* offline processing of log data

## Gallery

<table>
<tr>
<td>

![usecase_gokart](https://user-images.githubusercontent.com/4012178/35968269-a92a3b46-0cc3-11e8-8d5e-1276762cdc36.png)

[Trajectory pursuit](https://www.youtube.com/watch?v=XgmS8CP6gqw)

<td>

![planning_obstacles](https://user-images.githubusercontent.com/4012178/40268689-2af06cd4-5b72-11e8-95cf-d94edfdc3dd1.png)

[Static obstacles](https://www.youtube.com/watch?v=xLZeKFeAokM)

<td>

![visioneventbased](https://user-images.githubusercontent.com/4012178/45996325-21d77680-c09c-11e8-9d0a-ffdd4dfba62b.png)

[Event-based SLAM](https://www.youtube.com/watch?v=NKylhRHbnGA), [Fig. 8](https://www.youtube.com/watch?v=NpCwG_32Cr8)

</tr>
</table>

## Architecture

Source file size distribution

![retina](https://user-images.githubusercontent.com/4012178/45996769-a4146a80-c09d-11e8-87c4-acf9db7fcc5f.png)

We use `LCM` for message interchange.
All messages are encoded using a single type `BinaryBlob`.
The byte order of the binary data is `little endian` since the encoding is native on most architectures.

* [Video on Gokart Actuators](https://www.youtube.com/watch?v=t3oAqQlWoyo)
* [Video of Testing Software](https://www.youtube.com/watch?v=Oh9SyG4Lgm8)

## GOKART

![gokart_operation](https://user-images.githubusercontent.com/4012178/45913998-85fcff00-be3e-11e8-872a-11aa84f4dba0.png)

### Actuation

<table>
  <tr><th>Priority<th>Module<th>Purpose<th>Rimo<th>Steer<th>Linmot<th>Misc</tr>
  <tr><td>Hardware<td>SteerBatteryCharger<td>prevent overcharging of battery<th><th>X<th><th></tr>
  <tr><td>Hardware<td>LinmotFireFighter<td>prevent brake hardware damage<th><th><th>X<th></tr>
  <tr><td>Emergency<td>LinmotCoolingModule<td>no acceleration while temperature of linmot critical<th>X<th><th><th></tr>
  <tr><td>Emergency<td>MiscEmergencyModule<td>no acceleration with steering battery low<th>X<th><th><th></tr>
  <tr><td>Emergency<td>SteerEmergencyModule<td>no acceleration with uncalibrated steering<th>X<th><th><th></tr>
  <tr><td>Emergency<td>Vlp16ClearanceModule<td>no acceleration towards a close obstacle<th>X<th><th><th></tr>
  <tr><td>Emergency<td>EmergencyBrakeProvider<td>brake maneuver based on obstacle in path<th><th><th>X<th></tr>
  <tr><td>Emergency<td>LinmotTakeoverModule<td>switch linmot to passive when driver pushes brake pedal<th><th><th>X<th></tr>
  <tr><td>Emergency<td>DeadManSwitchModule<td>brake if driver doesn't use joystick and gokart is above certain speed<th><th><th>X<th></tr>
  <tr><td>Calibration<td>SteerCalibrationProvider<td>execute steering calibration procedure, duration ~5[s]<th><th>X<th><th></tr>
  <tr><td>Calibration<td>LinmotCalibrationProvider<td>execute calibration of brake, duration ~4[s]<th><th><th>X<th></tr>
  <tr><td>Calibration<td>MiscIgnitionProvider<td>ACK of communication problem to microautobox by human operator, duration 0.3[s]<th><th><th><th>X</tr>
  <tr><td>Manual<td>RimoTorqueJoystickModule<td>torque control by joystick<th>X<th><th><th></tr>
  <tr><td>Manual<td>SteerJoystickModule<td>steering control by joystick<th><th>X<th><th></tr>
  <tr><td>Manual<td>LinmotJoystickModule<td>braking by joystick<th><th><th>X<th></tr>
  <tr><td>Testing<td>RimoComponent<td>interaction with motors in GUI<th>X<th><th><th></tr>
  <tr><td>Testing<td>SteerComponent<td>interaction with steering in GUI<th><th>X<th><th></tr>
  <tr><td>Testing<td>LinmotComponent<td>interaction with brake in GUI<th><th><th>X<th></tr>
  <tr><td>Testing<td>MiscComponent<td>interaction with misc in GUI<th><th><th><th>X</tr>
  <tr><td>Safety<td>LinmotSafetyModule<td>no acceleration with brake disabled<th>X<th><th><th></tr>
  <tr><td>Autonomous<td>PurePursuitRimo<td>control of speed during pure pursuit trajectory following<th>X<th><th><th></tr>
  <tr><td>Autonomous<td>PurePursuitSteer<td>steering during pure pursuit<th><th>X<th><th></tr>
  <tr><td>Fallback<td>RimoPutFallback<td>zero torque on motors<th>X<th><th><th></tr>
  <tr><td>Fallback<td>SteerPutFallback<td>zero torque on steering column<th><th>X<th><th></tr>
  <tr><td>Fallback<td>LinmotPutFallback<td>maintain operation in home position<th><th><th>X<th></tr>
  <tr><td>Fallback<td>MiscPutFallback<td>normal operation, all LEDs off<th><th><th><th>X</tr>
</table>

## LIDAR

### Velodyne VLP-16

* point cloud visualization and localization with lidar [video](https://www.youtube.com/watch?v=pykecjwixgg)

### Velodyne HDL-32E

* 3D-point cloud visualization: see [video](https://www.youtube.com/watch?v=abOYEIdBgRs)

distance as 360[deg] panorama

![velodyne distances](https://user-images.githubusercontent.com/4012178/29020149-581e9236-7b61-11e7-81eb-0fc4577b687d.gif)

intensity as 360[deg] panorama

![intensity](https://user-images.githubusercontent.com/4012178/29026760-c29ebbce-7b7d-11e7-9854-9280594cb462.gif)

### Quanergy Mark8

* 3D-point cloud visualization: see [video](https://www.youtube.com/watch?v=DjvEijz14co)

### HOKUYO URG-04LX-UG01

![urg04lx](https://user-images.githubusercontent.com/4012178/29029959-c052da4c-7b89-11e7-8b01-1b4efc3593c0.gif)

our code builds upon the
[urg_library-1.2.0](https://sourceforge.net/projects/urgnetwork/files/urg_library/)

## DVS

### IniLabs DAVIS240C

* [SAE with different temporal windows](https://www.youtube.com/watch?v=NKw27ekIosI)
* [SAE with different bucket size](https://www.youtube.com/watch?v=vuXMG3TnZlM)

Rolling shutter mode

<table>
<tr>
<td>

![05tram](https://user-images.githubusercontent.com/4012178/30553969-2948547a-9ca3-11e7-91e8-159806c7e329.gif)

<td>

![04peds](https://user-images.githubusercontent.com/4012178/30553578-f3429ce2-9ca1-11e7-8870-85078c8aa96c.gif)

<td>

![00scene](https://user-images.githubusercontent.com/4012178/30553889-e59c0a5a-9ca2-11e7-8cc3-08de77e21e5e.gif)

</tr>
</table>

Global shutter mode

<table>
<tr>
<td>

![dvs_2500](https://user-images.githubusercontent.com/4012178/34606522-075a20ec-f210-11e7-966a-49384b048809.gif)

2.5[ms]

<td>

![dvs_5000](https://user-images.githubusercontent.com/4012178/34606520-073c7d08-f210-11e7-8ee2-1a35173bbade.gif)

5[ms]

</tr>
</table>

Events only

<table>
<tr>
<td>

![dvs_noaps_1000](https://user-images.githubusercontent.com/4012178/34684372-2eb4b200-f4a5-11e7-891e-74c2123a3bfe.gif)

1[ms]

<td>

![dvs_noaps_2500](https://user-images.githubusercontent.com/4012178/34684373-2eca8ee0-f4a5-11e7-9f70-f41d4722edf7.gif)

2.5[ms]

<td>

![dvs_noaps_5000](https://user-images.githubusercontent.com/4012178/34684374-2ee3aaba-f4a5-11e7-9ac6-72b7ac502793.gif)

5[ms]

</tr>
</table>



AEDAT 2.0, and AEDAT 3.1

* parsing and visualization
* conversion to text+png format as used by the Robotics and Perception Group at UZH
* loss-less compression of DVS events by the factor of 2
* compression of raw APS data by factor 8 (where the ADC values are reduced from 10 bit to 8 bit)

### Device Settings

Quote from Luca/iniLabs:
* *Two parameters that are intended to control framerate:* `APS.Exposure` and `APS.FrameDelay`
* `APS.RowSettle` *is used to tell the ADC how many cycles to delay before reading a pixel value, and due to the ADC we're using, it takes at least three cycles for the value of the current pixel to be output by the ADC, so an absolute minimum value there is 3. Better 5-8, to allow the value to settle. Indeed changing this affects the framerate, as it directly changes how much time you spend reading a pixel, but anything lower than 3 gets you the wrong pixel, and usually under 5-6 gives you degraded image quality.*

We observed that in *global shutter mode*, during signal image capture the stream of events is suppressed. Whereas, in *rolling shutter mode* the events are more evenly distributed.

## streaming DAT files

![hdr](https://user-images.githubusercontent.com/4012178/27771907-a3bbcef4-5f58-11e7-8b0e-3dfb0cb0ecaf.gif)

## streaming DAVIS recordings

![shapes_6dof](https://user-images.githubusercontent.com/4012178/27771912-cb58ebb8-5f58-11e7-9566-79f3fbc5d9ba.gif)

## generating DVS from video sequence

![cat_final](https://user-images.githubusercontent.com/4012178/27771885-0eadb2aa-5f58-11e7-9f4d-78a57e610f56.gif)

## synthetic signal generation 

<table><tr>
<td>

![synth2](https://user-images.githubusercontent.com/4012178/27772611-32cc2e92-5f66-11e7-9d1f-ff15c42d54be.gif)

<td>

![synth1](https://user-images.githubusercontent.com/4012178/27772610-32af593e-5f66-11e7-8c29-64611f6ca3e6.gif)

</tr></table>

## Integration

Due to the rapid development of the code base, `retina` is not yet available as a maven artifact.
Instead, download the project and run `mvn install` on your machine.
Subsequently, you can use the project on your machine as

    <dependency>
      <groupId>ch.ethz.idsc</groupId>
      <artifactId>retina</artifactId>
      <version>0.0.1</version>
    </dependency>
