//
// Created by maximilien on 22.05.19.
//

#include "../UnscentedKalmanFilter.h"


class TestPacejkaUKF {
public:

    typedef UnscentedKalmanFilter<NP, NM, NI> UKF;

    void test(
            UKF::ParameterVec groundTruth,
            UKF::ParameterVec guess,
            UKF::ParameterMat variance
            );

    // debug variables
    double rmse = 0;
    double convergence = 0;

private:
    bool print = false;
    bool writeCSV = true;

};



