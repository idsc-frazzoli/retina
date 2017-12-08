# Gokart setup procedure

Confirm: nothing is in front of gokart

Confirm: steering mechanism is not obstructed

Confirm: DVS128 is disconnected

* press emergency-off button labeled "turn to start engine", i.e. deactivate power to engine
* flip rubber nob to switch on gokart and check gokart battery level close to 100%
* press button on side of box for power (button should light up in green)

## Configuration of LCM in OS

open a terminal using (Ctrl+Alt+T)

run the command

    ./setup_lcm.sh

# start software in Eclipse

start Eclipse

Info: (Ctrl+Shift+R) navigates to files

Info: (Ctrl+F11) runs a java file

## DAVIS 240C

run

    JAERViewer.java

* wait for DAVIS-240c camera image to show
* minimize but do not close AEViewer window

## Joystick

run

    RunTaskGui.java

enable toggle-button "Generic Xbox Pad Lcm Server"

Confirm: console printed

    found joystick GENERIC XBOX PAD

## Gokart

run

    RunTabbedGui.java

### in the tab "dev"

activate all 3 toggle-buttons:
* Autobox Socket
* Vlp16Lcm Server
* Urg04lx Lcm Server

Confirm: when "Urg04lx Lcm Server" is started the console printed

    urg_alive1=true
    urg_alive2=true

### in the tab "lcm"

activate all 3 toggle-buttons:
* Autobox Lcm Server
* Gokart Status Lcm
* Logger

### in the tab "lab"

click "Spy" and wait ~10 seconds for the window to show up

Confirm: there are

    8 channels starting with autobox.* 
    4 channels starting with davis.*
    1 channel starting with joystick
    1 channel starting with urg04lx
    2 channels starting with vlp16

deactivating the toggle-button "Spy".  

activate 2 more modules by enabling the toggle-buttons
* Autobox Introspection
* Autobox Testing

Info: the Autobox Introspection module shows which module is commanding the actuators. The window can remain open throughout the operation of the gokart.

### In the tab Testing::Linmot

press button "Init" and wait for 3 sec for the brake to calibrate.

Confirm: the brake makes some noise

Confirm: the status word field should be green

### In the tab Testing::Misc

If the Emergency field is "red" then press button "Reset".

Confirm: the Emergency field is "white"

Confirm: the steering battery voltage is above 11.7 V

### In the tab Testing::Steer

press button "Calibration"

Confirm: the steering moves left and right

Confirm: the RangePos field is green

enable button "controller"

Confirm: steering goes to center

disable button "controller"

### In the tab Testing::Rimo

turn the emergency-off button to start engine

manually roll the gokart forward and backward a bit and check if the actual speed values change accordingly   

---

(back in the "lab" tab)

deactivate toggle-button "Autobox Testing"

Confirm: all 4 fields in the Introspection window are gray and display "...PutFallback"

activate 2 modules by enabling the toggle-buttons:
* Local View Lcm
* Davis Detail

Confirm: sensors are running without delay by moving something in front of the sensors

stop the 2 modules "Local View Lcm" and "Davis Detail" by disabling the toggle-buttons

### in the tab "fuse"

activate all 6 modules from top to bottom

Confirm: the console did not print anything in red

Confirm: all 4 fields in the Introspection window are gray and display "...PutFallback"

### in the tab "track"

activate all 3 modules from top to bottom

Confirm: all the fields in the Introspection window are green except for "Misc"

---

The gokart is now controllable by joystick.
