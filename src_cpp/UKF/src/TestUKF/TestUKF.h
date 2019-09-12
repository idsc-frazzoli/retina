//
// Created by maximilien on 22.05.19.
//

#include "../UnscentedKalmanFilter.h"

#define NP 10
#define NM 1
#define NI 1000

class TestUKF {
public:

    typedef UnscentedKalmanFilter<NP, NM, NI> UKF;

    void test();

    static UKF::MeasurementVec measureFunction(UKF::ParameterVec parameter, double k);

    // debug variables
    double rmse = 0;
    double convergence = 0;
    double weightGroundTruth = 300;

private:
    bool print = false;
    bool writeCSV = false;

};


