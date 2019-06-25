//
// Created by maximilien on 22.05.19.
// based on https://www.mathworks.com/matlabcentral/fileexchange/18217-learning-the-unscented-kalman-filter
//

#include "TestUKF.h"
#include <iostream>
#include <fstream>
#include <functional>
#include <stdlib.h>
#include <time.h>

using namespace std;
using namespace Eigen;

void TestUKF::test() {

    bool print = true;

    double q = 0.1; //std of process
    double r = 0.1; //std of measurement
    UKF::ParameterMat processCov = UKF::ParameterMat::Identity() * q; // cov of process
    UKF::MeasurementMat measureCov = UKF::MeasurementMat::Identity() * r; // cov of measurement

    UKF::ParameterVec s; //initial state
    s << 0, 0, 1;
    UKF::ParameterVec x =
            s + q * UKF::ParameterVec::Random(); //initial state with noise
    UKF::ParameterMat P = UKF::ParameterMat::Identity(); //inital state cov
    if(print){
        cout << "initial state" << endl << x << endl;
        cout << "initial cov" << endl << P << endl;
    }

    //UKF
    UKF ukf = UKF(x, P);

    //functions
    std::function<UKF::MeasurementVec(UKF::ParameterVec)> measureFunction
            = [x](UKF::ParameterVec parameter){
        UKF::MeasurementVec measurementVec;
        measurementVec(0) = x(0);
        return measurementVec;
    };
    std::function<UKF::ParameterVec(UKF::ParameterVec)> predictionFunction
            = [](UKF::ParameterVec parameterVec){
        UKF::ParameterVec results;
        results(0) = parameterVec(1);
        results(1) = parameterVec(2);
        results(2) = 0.05*parameterVec(0)*(parameterVec(1)+parameterVec(2));
        return results;
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
        cout << "update " << i << "..................." << endl;
        ukf.update(measureFunction,predictionFunction,measureCov,processCov,z);

        // save actual estimate
        xV.col(i) = ukf.mean;
        ukf.variance;
        // update process
        s = predictionFunction(s) + q * UKF::ParameterVec::Random();
    }

    // export for plot
    UKF::ParameterVec vec = UKF::ParameterVec::Ones();
    writeToCSV("vec", vec);
}

const static IOFormat CSVFormat(StreamPrecision, DontAlignCols, ", ", "\n");

void TestUKF::writeToCSV(string name, Eigen::MatrixXd matrix){
    ofstream file(name.c_str());
    if (file.is_open()){
        file << matrix.format(CSVFormat);
    }
}