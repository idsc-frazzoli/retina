//
// Created by maximilien on 01.07.19.
//

#pragma once
#ifndef EKF_WRITERUKF_H
#define EKF_WRITERUKF_H

#endif //EKF_WRITERUKF_H

#include "ExtendedKalmanFilter.h"


class WriterEKF{
public:
    void writeToCSV(std::string, Eigen::MatrixXd);

};