//
// Created by maximilien on 22.05.19.
//

#pragma once
#define NP 10
#define NM 1
#define NI 500


#include "../UnscentedKalmanFilter.h"


class TestUKF {
public:
    void test();
    typedef UnscentedKalmanFilter<NP, NM, NI> UKF;
    static UKF::MeasurementVec measureFunction(UKF::ParameterVec parameter, double k);

    double rmse = 0;
    double convergence = 0;

    double weightGroundTruth = 300;

private:
    bool print = true;
    bool writeCSV = false;

};


