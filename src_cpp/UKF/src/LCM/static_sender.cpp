//
// Created by maximilien on 30.08.19.
//
// compile with:
//  $ g++ -o static_sender static_sender.cpp -llcm

#include "static_sender.h"
#include <lcm/lcm.h>
#include "/home/maximilien/Documents/sp/retina/src_cpp/LCM/idsc/BinaryBlob.hpp"
#include "definitions.c"

lcm_t * lcm;

int main(int argc, char **argv){

    printf("start lcm static sender\n");

    lcm = lcm_create(NULL);
    if (!lcm)
        return 1;

    printf("about to send\n");

    PacejkaParameter pacejkaParameter = {
            .B1 = 9,
            .C1 = 1,
            .D1 = 10,
            .B2 = 5.2,
            .C2 = 1.1,
            .D2 = 20,
    };

    const PacjMsg my_msg = {
            .messageType = 1,
            .sequenceInt = 1,
            .pacejkaParameter = pacejkaParameter,
    };

    struct idsc_BinaryBlob blob{};
    blob.data_length = sizeof(struct PacjMsg);
    blob.data = (int8_t*)&my_msg;
    printf("lcm addr: %p\n",lcm);
    printf("blob addr: %p\n",&blob);

    _idsc_BinaryBlob(lcm, "mpc.forces.pacj.d", &blob);


    lcm_destroy(lcm);

    return 0;
}
