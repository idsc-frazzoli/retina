//
// Created by maximilien on 07.09.19.
//

#ifndef UKF_MPCCLIENT_H
#define UKF_MPCCLIENT_H

#include "../UnscentedKalmanFilter.h"


enum {
    NParam = 7,
    NMes = 3,
    NIter = 500
};
typedef UnscentedKalmanFilter<NParam, NMes, NIter> UKF;

enum {
    print = false
};



#endif //UKF_MPCCLIENT_H
