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


#include "/home/maximilien/Documents/sp/retina/src_cpp/LCM/idsc/BinaryBlob.hpp"
#include "definitions.c"


struct PacejkaParameter pacejkaParameter;

idsc::BinaryBlob blob;


int main(int argc, char **argv){

    printf("start lcm static sender\n");
    lcm::LCM lcm;
    printf("about to send\n");

    pacejkaParameter = {
            .B1 = 9,
            .C1 = 1,
            .D1 = 10,
            .B2 = 5.2,
            .C2 = 1.1,
            .D2 = 20,
    };

    blob.data_length = sizeof(struct PacejkaParameter);
    blob.data[0] = pacejkaParameter.B1;
    blob.data[3] = pacejkaParameter.C1;
    blob.data[7] = pacejkaParameter.D2;
    blob.data[11] = pacejkaParameter.B2;
    blob.data[15] = pacejkaParameter.C2;
    blob.data[19] = pacejkaParameter.D2;



    /*idsc::BinaryBlob blob {
        .data_length = sizeof(struct PacjMsg),
        .data = {1,2,3} // TODO MAX
    };*/



    int size = sizeof(struct PacjMsg);

    std::cout << "size of PacjMsg: " <<  size << std::endl;
    printf("lcm addr: %p\n",&lcm);
    printf("lcm addr: %p\n","HELLO");
    printf("blob addr: %p\n",&blob);

    lcm.publish("mpc.forces.pacj,d", &blob);


    return 0;
}
