//
// Created by maximilien on 25.06.19.
//

#pragma once
#ifndef UKF_WRITERUKF_H
#define UKF_WRITERUKF_H

#endif //UKF_WRITERUKF_H

#include "../UnscentedKalmanFilter.h"


class WriterUKF{
public:
    void writeToCSV(std::string, Eigen::MatrixXd);

};