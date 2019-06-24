//
// Created by maximilien on 22.05.19.
//

#include "TestUKF.h"
#include <iostream>
#include <fstream>
#include <functional>
#include <stdlib.h>
#include <time.h>

using namespace std;
using namespace Eigen;

const static IOFormat CSVFormat(StreamPrecision, DontAlignCols, ", ", "\n");

void TestUKF::test() {
    int q = 0.1; //std of process
    int r = 0.1; //std of measurement
    UKF::VarienceMat processCov = UKF::VarienceMat::Identity()*q; // cov of process
    UKF::MeasurementMat measureCov = UKF::MeasurementMat::Ones()*r; // cov of measurement

    UKF::ParameterVec s; //initial state
    s << 0, 0, 1;
    UKF::ParameterVec x =
            s + q * UKF::ParameterVec::Random(); //initial state with noise
    UKF::VarienceMat P = UKF::VarienceMat::Identity(); //inital state cov

    //UKF
    UKF ukf = UKF(s, P);

    //functions
    std::function<UKF::MeasurementVec(UKF::ParameterVec)> measureFunction
            = [s](UKF::ParameterVec parameter){
        UKF::MeasurementVec measurementVec;
        measurementVec << s(1);
        return measurementVec;
    };
    std::function<UKF::ParameterVec(UKF::ParameterVec)> predictionFunction
            = [](UKF::ParameterVec parameterVec){
        return parameterVec;
    };

    //Space allocation for plotting
    UKF::ParameterSafe xV;
    UKF::ParameterSafe sV;
    UKF::MeasurementSafe zV;
    xV = UKF::ParameterSafe::Zero();               //estimate
    sV = UKF::ParameterSafe::Zero();               //actual
    zV = UKF::MeasurementSafe::Zero();

    for(int i = 0; i<= xV.size(); i++){
        //measurements save actual state and measurement
        UKF::MeasurementVec z =
                measureFunction(s) + UKF::MeasurementVec::Random() * r;
        sV.col(i) = s;
        zV.col(i) = z;

        //UKF
        ukf.update(measureFunction,predictionFunction,measureCov,processCov,z);

        // save actual estimate
        xV.col(i) = ukf.mean;
        // update process
        s = predictionFunction(s) + q * UKF::ParameterVec::Random();
    }

    // export for plot
    UKF::ParameterVec vec = UKF::ParameterVec::Ones();
    writeToCSV("vec", vec);




}

void TestUKF::writeToCSV(string name, Eigen::MatrixXd matrix){
    ofstream file(name.c_str());
    if (file.is_open()){
        file << matrix.format(CSVFormat);
    }

}