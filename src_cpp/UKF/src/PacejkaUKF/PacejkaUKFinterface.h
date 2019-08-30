//
// Created by maximilien on 26.08.19.
//

#include "../UnscentedKalmanFilter.h"
#include "../InputOutput/ReaderCSV.cpp"

class PacejkaUKFinterface {
public:
    enum {
        NParam = 7,
        NMes = 3,
        NIter = 1000
    };
    typedef UnscentedKalmanFilter<NParam, NMes, NIter> UKF;

    static void runStatic();



private:
    enum {
        print = true,
    };



};

