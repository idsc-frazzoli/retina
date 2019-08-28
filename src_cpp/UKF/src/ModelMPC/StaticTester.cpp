//
// Created by maximilien on 24.07.19.
//


#include <iostream>
#include "StaticTester.h"
#include "modelDx.h"

void call_modelDx_static()
{
    double const B1 = 9;
    double C1 = 1;
    double D1 = 10;
    double B2 = 5.2;
    double C2 = 1.1;
    double D2 = 10;
    double Cf = 0.3;
    double param[8] = {B1, C1, D1, B2, C2, D2, Cf};
    double velx = 7;
    double vely = 3;
    double velrotz = -.5;
    double BETA = 0.3;
    double AB = 0.1;
    double TV = 2;

    double ACCX;
    double ACCY;
    double ACCROTZ;

    modelDx(velx, // VELX
            vely, // VELY
            velrotz, // VELROTZ
            BETA, // BETA
            AB, // AB
            TV, // TV
            param, // pacejka param
            &ACCX, // ACCX
            &ACCY, // ACCY
            &ACCROTZ); // ACCROTZ

    std::cout << "****Static tester****" << std::endl;
    std::cout << param[0] << std::endl;
    std::cout << ACCX << std::endl;
    std::cout << ACCY<< std::endl;
    std::cout << ACCROTZ << std::endl;

    /*
     * Results should be:
     * param[0] = 9
     * ACCX = -08835
     * ACCY = -3.9556
     * ACCROTZ = 11.5751
     *
     */

}


