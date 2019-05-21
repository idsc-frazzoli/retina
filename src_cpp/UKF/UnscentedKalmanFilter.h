//
// Created by maximilien on 21.05.19.
//

#pragma once

#include <Eigen/Dense>
template <int NParameter, int NMeasurements>
class UnscentedKalmanFilter{
public:
    typedef Eigen::Matrix<double, NParameter, 1> ParameterVec;
    typedef Eigen::Matrix<double, NMeasurements, 1> MeasurementVec;
    typedef Eigen::Matrix<double, NParameter, NParameter> VarienceMat;
private:
    void update(ParameterVec priorMean, VarienceMat priorVariance, MeasurementVec(*measureFunction)(ParameterVec parameter), ParameterVec(*updateFunction)(ParameterVec parameter)){
        




    }
    ParameterVec mean;
    VarienceMat variance;
};
