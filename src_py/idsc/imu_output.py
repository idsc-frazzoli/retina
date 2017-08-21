import numpy as np
import matplotlib.pyplot as plt
import lcm
from idsc import DavisImu


def my_handler(channel, data):
    msg = DavisImu.decode(data)
    plt.ion()
    t = msg.clock_usec
    plt.scatter(t, msg.accel[0], c='r')
    plt.scatter(t, msg.accel[1], c='g')
    plt.scatter(t, msg.accel[2], c='b')
    # print("Data:", msg.accel)
    # print("Received message on channel \"%s\"" % channel)
    plt.pause(0.02)

lc = lcm.LCM()
subscription = lc.subscribe("davis.FX2_02460045.imu", my_handler)

try:
    while True:
        lc.handle()
except KeyboardInterrupt:
    pass



# plt.axis([0, 10, 0, 1])
# plt.ion()
#
# for i in range(10):
#     y = np.random.random()
#     plt.scatter(i, y)
#     plt.pause(0.05)
#
# while True:
#     plt.pause(0.05)