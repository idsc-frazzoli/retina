//
// Created by maximilien on 30.08.19.
//
// compile with:
//  $ g++ -o static_sender static_sender.cpp -llcm

#include <lcm/lcm-cpp.hpp>
#include <iostream>
#include <thread>
#include <mutex>
#include <condition_variable>


#include "../../../LCM/idsc/BinaryBlob.hpp"
#include "../../../../src_MATLAB/MPCGokart/shared_dynamic/c/definitions.c"

struct PacejkaParameter pacejkaParameter;
idsc::BinaryBlob blob;

int main(int argc, char **argv){

    printf("start lcm static sender\n");
    lcm::LCM lcm;
    if(!lcm.good())
        return 1;
    printf("about to send\n");

    pacejkaParameter = {
            .B1 = 9,
            .C1 = 1,
            .D1 = 10,
            .B2 = 5.2,
            .C2 = 1.1,
            .D2 = 20,
    };

    blob.data_length = 6*4;
    blob.data.resize(blob.data_length);
    memcpy(&blob.data[0],&pacejkaParameter,6*4);

    printf("lcm addr: %p\n",&lcm);
    printf("blob addr: %p\n",&blob);

    lcm.publish("mpc.forces.pacj.d", &blob);
    return 0;
}
