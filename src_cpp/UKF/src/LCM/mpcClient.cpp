//
// Created by maximilien on 07.09.19.
//

#include <stdio.h>
#include <iostream>
#include <lcm/lcm-cpp.hpp>

#include "mpcClient.h"
#include "../../../LCM/idsc/BinaryBlob.hpp"
#include "../../../../src_MATLAB/MPCGokart/shared_dynamic/c/definitions.c"

struct ControlRequestMsg lastCRMsg;
struct OnlineParam lastOnlineParam;

class Handler {
public:
    ~Handler(){}
    void handleState(const lcm::ReceiveBuffer *rbug, const std::string &chan, const idsc::BinaryBlob *msg){

        printf("Received CNS message on channel \"%s\":\n", chan.c_str());

        memcpy(&lastCRMsg, &msg, msg->data_length);

        printf("time: %f\n", lastCRMsg.state.time);
        printf("X: %f\n", lastCRMsg.state.X);
        printf("Y: %f\n", lastCRMsg.state.Y);
        printf("Psi: %f\n", lastCRMsg.state.Psi);

        printf("Ux: %f\n", lastCRMsg.state.Ux);
        printf("Uy: %f\n", lastCRMsg.state.Uy);
        printf("dotPsi: %f\n", lastCRMsg.state.dotPsi);
    }

    void handleOnline(const lcm::ReceiveBuffer *rbug, const std::string &chan, const idsc::BinaryBlob *msg){

        printf("Received Online message on channel \"%s\":\n", chan.c_str());

        memcpy(&lastOnlineParam, &msg, msg->data_length);
        printf("VX: %f\n", lastOnlineParam.vx);
        printf("VY: %f\n", lastOnlineParam.vy);
        printf("VPsi: %f\n", lastOnlineParam.vpsi);

        printf("Ux: %f\n", lastOnlineParam.beta);
        printf("Uy: %f\n", lastOnlineParam.ab);
        printf("dotPsi: %f\n", lastOnlineParam.tv);

    }
};

int main(int argc, char **argv)
{
    lcm::LCM lcm;

    if(!lcm.good())
        return 1;

    Handler handler;

    printf("about to subscribe\n");
    lcm.subscribe("mpc.forces.gs.d", &Handler::handleState, &handler);
    lcm.subscribe("online.params.d", &Handler::handleOnline, &handler);
    printf("starting main loop\n");

    while(1) {
        lcm.handle();
    }


    return 0;
}