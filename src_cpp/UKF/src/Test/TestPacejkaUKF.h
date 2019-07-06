//
// Created by maximilien on 22.05.19.
//

#pragma once
#define NP 3
#define NM 1
#define NI 1000


#include "../UnscentedKalmanFilter.h"


class TestPacejkaUKF {
public:
    void test();
    typedef UnscentedKalmanFilter<NP,NM, NI> UKF;
    double rmse = 0;

private:
    bool print = false;
    bool writeCSV = false;

};



