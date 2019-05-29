//
// Created by maximilien on 22.05.19.
//

#include "TestUKF.h"
#include <iostream>
#include <functional>
#include <stdlib.h>
#include <time.h>


void TestUKF::test() {
    UKF::ParameterVec groundTruth;
    groundTruth<< 9, 1, 10 ;
    UKF::ParameterVec mean = groundTruth*1;
    UKF::VarienceMat varience = UKF::VarienceMat::Identity()*20;
    UKF ukf = UKF(mean, varience);




    std::function<UKF::ParameterVec(UKF::ParameterVec)> predictionFunction
    = [](UKF::ParameterVec parameter){
            return parameter;
    };

    for (int i = 0; i<= 1000; i++){
        std::cout << "------------------------------iteration: " << i << std::endl;
        //parameter;
        double k = 10*static_cast <double> (rand()) / static_cast <double> (RAND_MAX);
        //double k = 1;
        std::cout << "k: " << k << std::endl;

        std::function<UKF::MeasurementVec(UKF::ParameterVec)> measureFunction
                = [k](UKF::ParameterVec parameter){
                    double b = parameter(0);
                    double c = parameter(1);
                    double d = parameter(2);

                    double r = d*sin(c*atan(b*k));
                    //std::cout << "b: " << b << std::endl;
                    //std::cout << "c: " << c << std::endl;
                    //std::cout << "d: " << d << std::endl;
                    //std::cout << "r: " << r << std::endl;
                    UKF::MeasurementVec measurementVec;
                    measurementVec << r ;
                    return measurementVec;
                };

        UKF::MeasurmentMat measurementNoise = UKF::MeasurmentMat::Identity();
        UKF::VarienceMat processNoise = UKF::VarienceMat::Identity()*0.01;
        UKF::MeasurementVec z = measureFunction(groundTruth);

        ukf.update(measureFunction,predictionFunction,measurementNoise,processNoise,z);
    }

}