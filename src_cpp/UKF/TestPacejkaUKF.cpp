//
// Created by maximilien on 22.05.19.
//

#include "TestPacejkaUKF.h"
#include <iostream>
#include <functional>
#include <stdlib.h>
#include <time.h>


void TestPacejkaUKF::test() {
    UKF::ParameterVec groundTruth;
    groundTruth<< 9, 1, 10 ;
    UKF::ParameterVec mean = groundTruth*1;
    UKF::VarienceMat varience = UKF::VarienceMat::Identity()*20;
    UKF ukf = UKF(mean, varience);

    bool print = true;

    std::function<UKF::ParameterVec(UKF::ParameterVec)> predictionFunction
    = [](UKF::ParameterVec parameterVec){
            return parameterVec;
    };


    for (int i = 0; i<= 1000; i++){
        // print
        if(print){
            std::cout << "------------------------------iteration: " << i << std::endl;
        }

        //parameter s;
        double s = 10*static_cast <double> (rand()) / static_cast <double> (RAND_MAX);
        if(true){
            std::cout << "s: " << s << std::endl;
        }

        std::function<UKF::MeasurementVec(UKF::ParameterVec)> measureFunction
                = [s](UKF::ParameterVec parameter){

                    double b = parameter(0);
                    double c = parameter(1);
                    double d = parameter(2);

                    double r = d*sin(c*atan(b*s));

                    if(false){
                        std::cout << "b: " << b << std::endl;
                        std::cout << "c: " << c << std::endl;
                        std::cout << "d: " << d << std::endl;
                    }

                    UKF::MeasurementVec measurementVec;
                    measurementVec << r  ;
                    return measurementVec;
                };

        UKF::MeasurementMat measurementNoise = UKF::MeasurementMat::Identity();
        UKF::VarienceMat processNoise = UKF::VarienceMat::Identity()*0.01;
        UKF::MeasurementVec z = measureFunction(groundTruth);

        if(print){
            std::cout << "est: " << z << std::endl;
        }

        ukf.update(measureFunction,predictionFunction,measurementNoise,processNoise,z);
    }

}