//
// Created by maximilien on 22.05.19.
//

#include "TestPacejkaUKF.h"
#include <iostream>
#include <functional>
#include <stdlib.h>
#include <time.h>
#include "WriterUKF.h"


void TestPacejkaUKF::test() {

    UKF::ParameterVec groundTruth;
    groundTruth<< 9, 1, 10 ;
    UKF::ParameterVec guess;
    guess << 9.24, 0.942, 9.93;

    double r = static_cast <double> (rand()) / static_cast <double> (RAND_MAX); // mesurement noise
    UKF::MeasurementMat measurementNoise = r * UKF::MeasurementMat::Identity();
    double q = .1; //process noise
    UKF::ParameterMat processNoise = q * UKF::ParameterMat::Identity();

    // UKF start
    UKF::ParameterVec mean = groundTruth; //using groundTruth
    UKF::ParameterMat variance = UKF::ParameterMat::Identity() * 0.01;
    UKF ukf = UKF(mean, variance);

    std::function<UKF::ParameterVec(UKF::ParameterVec)> predictionFunction
    = [](UKF::ParameterVec parameterVec){
            return parameterVec;
    };

    //for plotting
    Eigen::Matrix<double, NP + 1, NI+1> params;

    for (int i = 0; i<= NI; i++){
        // print
        if(print){
            std::cout << "iteration-------------------------------------- " << i << std::endl;
        }

        // random parameter s in range [-1;2];
        double s = 3*static_cast <double> (rand()) / static_cast <double> (RAND_MAX) - 1;
        if(true){
            std::cout << "s: " << s << std::endl;
        }

        std::function<UKF::MeasurementVec(UKF::ParameterVec)> measureFunction
                = [s](UKF::ParameterVec parameter){

                    double b = parameter(0);
                    double c = parameter(1);
                    double d = parameter(2);
                    if(false) {
                        std::cout << "b: " << b << std::endl;
                        std::cout << "c: " << c << std::endl;
                        std::cout << "d: " << d << std::endl;
                    }

                    double r = d*sin(c*atan(b*s));

                    UKF::MeasurementVec measurementVec;
                    measurementVec << r   ;
                    return measurementVec;
                };

        UKF::MeasurementVec z = measureFunction(groundTruth);

        if(print){
            std::cout << "zMes: " << z << std::endl;
        }

        ukf.update(measureFunction,predictionFunction,measurementNoise,processNoise,z);

        //for plotting
        Eigen::MatrixXd value(4, 1);
        value << i, ukf.mean(0), ukf.mean(1), ukf.mean(2);
        params.col(i) = value;

    }

    std::cout << "params" << std::endl << params;

    // export for plot
    if(writeCSV) {
        WriterUKF writerUkf;
        writerUkf.writeToCSV("params.csv", params.transpose());
    }



}