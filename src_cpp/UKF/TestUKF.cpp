//
// Created by maximilien on 22.05.19.
//

#include "TestUKF.h"
#include <iostream>
#include <functional>
#include <stdlib.h>
#include <time.h>


void TestUKF::test() {
    UKF::ParameterVec mean = UKF::ParameterVec::Zero();
    UKF::VarienceMat varience = UKF::VarienceMat::Identity()*1000;
    UKF ukf = UKF(mean, varience);
    UKF::MeasurementVec groundTruth;
    groundTruth<< 9, 1, 10 ;


    std::function<UKF::ParameterVec(UKF::ParameterVec)> predictionFunction
    = [](UKF::ParameterVec parameter){
            return parameter;
    };

    for (int i = 0; i<= 10000; i++){
        std::cout << "iteration: " << i << std::endl;
        //parameter;
        double k = rand();

        std::function<UKF::MeasurementVec(UKF::ParameterVec)> measureFunction
                = [k](UKF::ParameterVec parameter){
                    double b = parameter(0);
                    double c = parameter(1);
                    double d = parameter(2);

                    double r = d*sin(c*atan(b*k));

                    UKF::MeasurementVec measurementVec;
                    measurementVec << r ;
                    return measurementVec;
                };

        UKF::MesurmentMat measurementNoise = UKF::MesurmentMat::Identity();
        UKF::VarienceMat processNoise = UKF::VarienceMat::Zero();
        UKF::MeasurementVec z = measureFunction(groundTruth);

        ukf.update(measureFunction,predictionFunction,measurementNoise,processNoise,z);
    }

}