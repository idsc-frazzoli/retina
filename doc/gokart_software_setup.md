# Gokart[1.0] setup procedure

Confirm: nothing is in front of gokart

Confirm: steering mechanism is not obstructed

* press emergency-off button labeled "turn to start engine", i.e. deactivate power to engine
* connect laptop to go-kart (alienware to ethernet and usb cables)
* flip rubber nob to switch on gokart and check gokart battery level close to 100%
* press button on side of box for power (button should light up in green)

## Configuration of LCM in OS

open a terminal using (Ctrl+Alt+T)

run the command

    ./setup_lcm.sh

# start software in Eclipse

start Eclipse

*Info:* (Ctrl+Shift+R) navigates to files

*Info:* (Ctrl+F11) runs a java file

## DAVIS 240C

run

    JAERViewer.java

* wait for DAVIS-240c camera image to show
* minimize but do not close AEViewer window

## Gokart

run

    QuickStartGui.java

### in the tab "cfg"

activate the modules by enabling the toggle-buttons
* Autobox Introspection
* Autobox Compact

*Info:* the Autobox Introspection module shows which module is commanding the actuators. The window can remain open throughout the operation of the gokart.

press button `Init`, `Reset`, `Calibration`. Alternatively, you can press the yellow `Y` Button on the joystick.

Confirm: the brake makes some noise and the steering wheel moves

turn the emergency-off button to start engine

manually roll the gokart forward and backward a bit and check if the actual speed values change accordingly

confirm that joystick and davis240c measurements are indicated in GUI   

deactivate toggle-button "Autobox Compact"

Confirm: all 4 fields in the Introspection window are gray and display "...PutFallback"

### in the tab "joy" (only if driving by joystick)

activate the module `JoystickGroupModule`

Confirm: all the fields in the Introspection window are green except for "Misc"

The gokart is now controllable by joystick.

## Autonomous mode (pure pursuit trajectory following)
### in the tab "cfg"

activate
* Global View Lcm

Drag and rotate the live-map to almost match the preloaded one 

Once the the maps almost overlap, click in sequence:
* 1 set
* 2 snap
* 3 set
* 4 track

Leave the `Global View Lcm` open during operations

### in the lab "aut"
Activate the `pure pursuit`

Now the go-kart is ready to operate in autonomous mode pressing A on the joystick

Make sure that you are starting from a point close to the trajectory

---

## BASLER ACE acA2500-60uc color camera setup

* Verify that the LED on the back of the Basler camera is green.

Open a terminal (Ctrl+Alt+T) and run the following commands in order to source your catkin workspace, and launch the color camera node that publishes images acquired from the basler ace acA2500-60uc camera run:
    
    source ~/catkin_ws/devel/setup.bash
    roslaunch pylon_camera color_camera_node.launch

Remark: When you are finished with the operation, press Ctrl+C in order to terminate the image acquisition.

In order to record the camera images for post processing, in a new terminal issue
    
    rosbag record color_camera_node/image_raw/compressed

To inspect the camera image for instance for focus and calibration, you can type:

    rosrun image_view image_view image:=/color_camera_node/image_raw

## BASLER ACE acA640-750um grayscale camera setup

* Verify that the LED on the back of the Basler camera is green.

To publish images from the basler ace acA640-750um camera you will have to open another terminal, and execute:

    source ~/catkin_ws/devel/setup.bash
    roslaunch pylon_camera BW_camera_node.launch

Remark: When you are finished with the operation, press Ctrl+C in order to terminate the image acquisition.

The data is logged in the rosbag format by issuing

    rosbag record BW_camera_node/image_raw/compressed

To inspect the camera image for instance for focus and calibration, you can type:

    rosrun image_view image_view image:=/BW_camera_node/image_raw

> Alternative logging method: If you have no idea what rosbags are, you can just run the script which will log stuff automatically for you (in new terminal).

    source ~/catin_ws/src/pylon_camera/scripts/log_camera_topics.bash
    
> This script will log messages from all the cameras that are connected to the USB hub. You can stop it using (Ctrl+C).

## Troubleshooting

### in the tab "lab"

click "Spy" and wait ~10 seconds for the window to show up

Confirm: there are

    8 channels starting with autobox.* 
    4 channels starting with davis.*
    1 channel starting with joystick.*
    2 channels starting with vlp16.*
    1 channel starting with computer.*

deactivate the toggle-button "Spy".
