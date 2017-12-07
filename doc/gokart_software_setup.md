# Gokart setup procedure

Confirm: location of emergency-off button

Confirm: nothing is in front of gokart

Confirm: steering is not obstructed

Confirm: DAVIS 128 is disconnected

* prepare hardware
* connect of all devices
* power on

## Configuration of LCM in OS

in home directory run
    ./setup_lcm.sh

# start software in Eclipse

start Eclipse

Info: (Ctrl+Shift+R) navigates to files
Info: (Ctrl+F11) runs a java file

## DAVIS 240C

run

    JAERViewer.java

* wait for DAVIS-240c camera image to show
* minimize (not close!) AEViewer window

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

Info: the next one will stall for 5-10 seconds. Don't take any action during this time.

* click "Spy" and wait for the window to show up

Confirm: there are

    8 channels starting with autobox.* 
    4 channels starting with davis.*
    1 channel starting with joystick
    1 channel starting with urg04lx
    2 channels starting with vlp16

activate 2 more modules by enabling the toggle-buttons
* Autobox Introspection
* Autobox Testing

### In the tab Testing::Linmot

press button "Init" and wait for 3 sec for the brake to calibrate.
Confirm: the brake makes some noise
Confirm: the status word field should be green

### In the tab Testing::Steer

press button "Calibration"

Confirm: the steering moves left and right

Confirm: the RangePos field is green

enable button "controller"

Confirm: steering goes to center

disable button "controller"

### In the tab Testing::Misc

If the Emergency field is "red" then press button "Reset".

Confirm: the Emergency field is "white"

Confirm: the steering battery voltage is above 11.7 V

### In the tab Testing::Rimo

Important: Do nothing here! The tab is only for testing the rear wheels when suspended in the air.

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

Confirm: all the fields in the Introspection window except for "Misc" are gray and display "...PutFallback"

---

The gokart is now controllable by joystick.

