//
// Created by maximilien on 29.07.19.
//

#include <iostream>
#include "LogTester.h"
#include "modelDx.h"
#include <Eigen/Dense>
#include "../InputOutput/ReaderCSV.cpp"



void call_modelDx_log()
{
    double const B1 = 9;
    double C1 = 1;
    double D1 = 10;
    double B2 = 5.2;
    double C2 = 1.1;
    double D2 = 10;
    double Cf = 0.3;
    double param[8] = {B1, C1, D1, B2, C2, D2, Cf};


    Eigen::MatrixXd data =
            load_csv<Eigen::MatrixXd>("/home/maximilien/Documents/sp/logs/pacejkaFull_20190708T114135_f3f46a8b.lcm.00.csv");







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

    std::cout << "****Log tester****" << std::endl;
    std::cout << param[0] << std::endl;
    std::cout << ACCX << std::endl;
    std::cout << ACCY << std::endl;
    std::cout << ACCROTZ << std::endl;
    std::cout << data;

    /*
     * Results should be:
     * param[0] = 9
     * ACCX = -08835
     * ACCY = -3.9556
     * ACCROTZ = 11.5751
     *
     */

}