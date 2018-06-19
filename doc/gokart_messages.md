# Messages

## autobox.linmot.get

Class for decoding: `LinmotGetEvent`
Message contains status of brake.
Total length: 16 bytes

<table>
<tr><th>type   <th>name</tr>
<tr><td>short  <td>status_word</tr>
<tr><td>short  <td>state_variable</tr>
<tr><td>int    <td>actual_position</tr>
<tr><td>int    <td>demand_position</tr>
<tr><td>short  <td>winding_temp1</tr>
<tr><td>short  <td>winding_temp2</tr>
</table>


## autobox.linmot.put

Class for decoding: `LinmotPutEvent`
Message to control brake.
Total length: 12 bytes

<table>
<tr><th>type   <th>name</tr>
<tr><td>short  <td>control_word</tr>
<tr><td>short  <td>motion_cmd_hdr</tr>
<tr><td>short  <td>target_position</tr>
<tr><td>short  <td>max_velocity</tr>
<tr><td>short  <td>acceleration</tr>
<tr><td>short  <td>deceleration</tr>
</table>

## autobox.steer.get

Class for decoding: `SteerGetEvent`
Message contains status of steering.
Total length: 44 bytes

<table>
<tr><th>type   <th>name</tr>
<tr><td>float  <td>motAsp_CANInput</tr>
<tr><td>float  <td>motAsp_Qual</tr>
<tr><td>float  <td>tsuTrq_CANInput</tr>
<tr><td>float  <td>tsuTrq_Qual</tr>
<tr><td>float  <td>refMotTrq_CANInput</tr>
<tr><td>float  <td>estMotTrq_CANInput</tr>
<tr><td>float  <td>estMotTrq_Qual</tr>
<tr><td>float  <td>gcpRelRckPos</tr>
<tr><td>float  <td>gcpRelRckQual</tr>
<tr><td>float  <td>gearRat</tr>
<tr><td>float  <td>halfRckPos</tr>
</table>

## autobox.steer.put

Class for decoding: `SteerPutEvent`
Message to control steering.
Total length: 5 bytes

<table>
<tr><th>type   <th>name</tr>
<tr><td>byte   <td>command</tr>
<tr><td>float  <td>torque</tr>
</table>

## autobox.misc.get

Class for decoding: `MiscGetEvent`
Message with emergency status and steering battery voltage.
Total length: 5 bytes

<table>
<tr><th>type   <th>name</tr>
<tr><td>byte   <td>emergency</tr>
<tr><td>float  <td>batteryAdc</tr>
</table>

## autobox.misc.put

Class for decoding: `MiscPutEvent`
Message to reset actuation
Total length: 6 bytes

<table>
<tr><th>type   <th>name</tr>
<tr><td>byte   <td>resetConnection</tr>
<tr><td>byte   <td>resetRimoL</tr>
<tr><td>byte   <td>resetRimoR</tr>
<tr><td>byte   <td>resetLinmot</tr>
<tr><td>byte   <td>resetSteer</tr>
<tr><td>byte   <td>ledControl</tr>
</table>

## autobox.rimo.get

Class for decoding: `RimoGetEvent`
Message with status about rear-wheel motors.
Total length: 48 bytes

<table>
<tr><th>type   <th>name</tr>
<tr><td>`RimoGetTire`<td>getTireL</tr>
<tr><td>`RimoGetTire`<td>getTireR</tr>
</table>

Message contains of two `RimoGetTire` messages.

<table>
<tr><th>type   <th>name</tr>
<tr><td>short  <td>status_word</tr>
<tr><td>short  <td>actual_rate</tr>
<tr><td>short  <td>rms_motor_current</tr>
<tr><td>short  <td>dc_bus_voltage</tr>
<tr><td>int    <td>error_code</tr>
<tr><td>short  <td>temperature_motor</tr>
<tr><td>short  <td>temperature_heatsink</tr>
<tr><td>`SdoMessage`<td>sdoMessage</tr>
</table>

The type `SdoMessage` is defined as

<table>
<tr><th>type   <th>name</tr>
<tr><td>byte   <td>sdoCommand</tr>
<tr><td>short  <td>mainIndex</tr>
<tr><td>byte   <td>subIndex</tr>
<tr><td>int    <td>sdoData</tr>
</table>

## autobox.rimo.put

Class for decoding: `RimoPutEvent`
Message with commands to rear-wheel motors.
Total length: 30 bytes

<table>
<tr><th>type   <th>name</tr>
<tr><td>`RimoPutTire`<td>putTireL</tr>
<tr><td>`RimoPutTire`<td>putTireR</tr>
</table>

Message contains two `RimoPutTire` messages.

<table>
<tr><th>type   <th>name</tr>
<tr><td>short  <td>command</tr>
<tr><td>short  <td>rate</tr>
<tr><td>short  <td>torque</tr>
<tr><td>byte   <td>trigger</tr>
<tr><td>byte   <td>sdoCommand</tr>
<tr><td>short  <td>mainIndex</tr>
<tr><td>byte   <td>subIndex</tr>
<tr><td>int    <td>sdoData</tr>
</table>

## gokart.status.get

Class for decoding: `GokartStatusEvent`
Message with approximated steering angle after calibration.
Total length: 4 bytes

<table>
<tr><th>type   <th>name</tr>
<tr><td>float  <td>steerColumnEncoder</tr>
</table>

## gokart.pose.lidar

Class for decoding: `GokartPoseEvent`
Message with approximated gokart pose from lidar localization.
Total length: 28 bytes

<table>
<tr><th>type   <th>name</tr>
<tr><td>double <td>x</tr>
<tr><td>double <td>y</tr>
<tr><td>double <td>angle</tr>
<tr><td>float  <td>quality</tr>
</table>

## joystick.*

Class for decoding: `JoystickDecoder`
Message with commands to rear-wheel motors.
Total length: variable bytes

<table>
<tr><th>type   <th>name</tr>
<tr><td>byte   <td>joystick device id</tr>
<tr><td>byte[] <td>axes</tr>
<tr><td>short  <td>buttons</tr>
<tr><td>byte[] <td>hats</tr>
</table>

The length of the arrays `axes` and `hats` depends on the joystick device id

## davis240c.overview.dvs

Class for decoding:
Class for encoding: `DavisDvsBlockCollector`
Message with events of Davis240C
Total length: variable bytes. at most 1208

<table>
<tr><th>type   <th>name</tr>
<tr><td>short  <td>number of events</tr>
<tr><td>short  <td>packet id</tr>
<tr><td>int    <td>timestamp offset</tr>
<tr><td>`DvsEvent`[]<td>dvs event</tr>
</table>

Message contains array of `DvsEvent`

<table>
<tr><th>type   <th>name</tr>
<tr><td>short  <td>polarity and timestamp increment</tr>
<tr><td>byte   <td>x</tr>
<tr><td>byte   <td>y</tr>
</table>

## davis240c.overview.atg

Class for decoding: `DavisImuFrame`
Message with inertial measurements of Davis240C
Total length: 18 bytes

<table>
<tr><th>type   <th>name</tr>
<tr><td>int    <td>timestamp</tr>
<tr><td>short  <td>accelX</tr>
<tr><td>short  <td>accelY</tr>
<tr><td>short  <td>accelZ</tr>
<tr><td>short  <td>temperature</tr>
<tr><td>short  <td>gyroX</tr>
<tr><td>short  <td>gyroY</tr>
<tr><td>short  <td>gyroZ</tr>
</table>
