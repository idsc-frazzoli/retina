//
// Created by maximilien on 22.05.19.
//

#pragma once
#define NP 3
#define NM 1
#define NI 10


#include "ExtendedKalmanFilter.h"


class TestPacejkaEKF {
public:
    typedef ExtendedKalmanFilter<NP,NM, NI> EKF;

    void test();
    static EKF::MeasurementVec measureFunction(EKF::ParameterVec parameter, double k);


private:
    bool print = true;
    bool writeCSV = false;

};



