import numpy as np
import matplotlib.pyplot as plt
import lcm
from idsc import DavisImu
from idsc import BinaryBlob
import binascii
from math import copysign

IMAGE = np.zeros((180, 240))


def hexint(b):
    return int(binascii.hexlify(b), 16)


def IMU_handler(channel, data):
    msg = DavisImu.decode(data)
    plt.ion()
    t = msg.clock_usec
    plt.scatter(t, msg.accel[0], c='r')
    plt.scatter(t, msg.accel[1], c='g')
    plt.scatter(t, msg.accel[2], c='b')
    # print("Data:", msg.accel)
    # print("Received message on channel \"%s\"" % channel)
    plt.pause(0.02)


def IMG_handler(channel, data):
    """
    Read out image data from DAVIS
    :param channel:
    :param data:
    :return:
    """
    msg = BinaryBlob.decode(data)
    all_data = msg.data
    tmp = [d + 256*(d < 0) for d in reversed(all_data[0:2])]
    column_offset = hexint(bytearray(tmp))
    size = 184
    for i in xrange(8):
        # col = all_data[2+i*size:2+(i+1)*size]
        IMAGE[:, column_offset+i] = all_data[6+i*size:2+(i+1)*size]
    # print("column_offset:", column_offset)
    # print("Image:", IMAGE[:5, :5])
    if column_offset == 232:
        IMAGE[IMAGE < 0] += 256
        plt.ion()
        plt.imshow(IMAGE, cmap='gray')
        plt.pause(0.0001)


def DVS_handler(channel, data):
    msg = BinaryBlob.decode(data)
    all_data = msg.data
    n_events = hexint(bytearray([d + 256*(d < 0) for d in reversed(all_data[0:2])]))
    package_id = hexint(bytearray([d + 256*(d < 0) for d in reversed(all_data[2:4])]))
    time_offset = hexint(bytearray([d + 256*(d < 0) for d in reversed(all_data[4:8])]))

    # print("n_events:", n_events)
    for i in range(n_events):
        polarity = int(copysign(1, all_data[9 + 4 * i]))
        timeHi = all_data[9 + 4 * i] & 0x7f
        timeLo = all_data[8 + 4 * i] & 0xff
        time = hexint(bytearray([timeHi, timeLo])) + time_offset
        x = all_data[10 + 4 * i] & 0xff
        y = all_data[11 + 4 * i] & 0xff
        # print("TimeHigh:", timeHi)
        # print("time:", time)
        # print("Polarity:", polarity)
        # print("x, y:", x, y)
        assert 0 <= x < 240
        assert 0 <= y < 180
        # polarity = copysign(1, all_data[4 + 4 * i])
        # t = hexint(bytearray(all_data[4 + 4 * i:6 + 4 * i] + 256 * (all_data[4 + 4 * i:6 + 4 * i] < 0)))
        # x = all_data[6 + 4 * i] + 256 * (all_data[6 + i*4] < 0)
        # y = all_data[7 + 4 * i] + 256 * (all_data[7 + i*4] < 0)


    # plt.ion()
    # t = msg.clock_usec
    # events = msg.encode


lc = lcm.LCM()
# subscription = lc.subscribe("davis.jzilly.imu", IMU_handler)
# subscription_img = lc.subscribe("davis.jzilly.sig", IMG_handler)
subscription_dvs = lc.subscribe("davis.jzilly.dvs", DVS_handler)


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