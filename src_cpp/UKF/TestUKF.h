//
// Created by maximilien on 22.05.19.
//

#pragma once
#define NP 3
#define NM 1

#include "UnscentedKalmanFilter.h"


class TestUKF {
public:
    void test();
    typedef UnscentedKalmanFilter<NP,NM> UKF;
    static UKF::MeasurementVec measureFunction(UKF::ParameterVec parameter, double k);

};



