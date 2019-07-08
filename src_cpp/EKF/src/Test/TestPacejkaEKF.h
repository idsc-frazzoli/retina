//
// Created by maximilien on 22.05.19.
//

#pragma once
#define NP 3
#define NM 1
#define NI 1000


#include "../ExtendedKalmanFilter.h"


class TestPacejkaEKF {
public:
    typedef ExtendedKalmanFilter<NP,NM, NI> EKF;

    void test(
            EKF::ParameterVec groundTruth,
            EKF::ParameterVec guess,
            EKF::ParameterMat variance
            );
    static EKF::MeasurementVec measureFunction(EKF::ParameterVec parameter, double k);
    double rmse = 0;
    double convergence = 0;


private:
    bool print = false;
    bool writeCSV = true;

};



