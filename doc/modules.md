## Actuation

<table>
  <tr><th>Priority<th>Module<th>Purpose<th>Rimo<th>Steer<th>Linmot<th>Misc</tr>
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
