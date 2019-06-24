//
// Created by maximilien on 22.05.19.
//

#pragma once
#define NP 3
#define NM 1
#define NI 30

#include "UnscentedKalmanFilter.h"


class TestPacejkaUKF {
public:
    void test();
    typedef UnscentedKalmanFilter<NP,NM, NI> UKF;
    static UKF::MeasurementVec measureFunction(UKF::ParameterVec parameter, double k);

};



