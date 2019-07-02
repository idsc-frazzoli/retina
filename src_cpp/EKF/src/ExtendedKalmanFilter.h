//
// Created by maximilien on 01.07.19.
// build based on UKF model.
// based on https://www.mathworks.com/matlabcentral/fileexchange/18189-learning-the-extended-kalman-filter
//

#pragma once

#include <iostream>
#include <Eigen/Dense>
#include <cmath>


template <int NParameter, int NMeasurements, int NIterations>
class ExtendedKalmanFilter{
public:
    typedef Eigen::Matrix<double, NParameter, 1> ParameterVec;
    typedef Eigen::Matrix<double, NMeasurements, 1> MeasurementVec;
    typedef Eigen::Matrix<double, NParameter, NParameter> ParameterMat;
    typedef Eigen::Matrix<double, NParameter, NMeasurements> CrossCorellationMat;
    typedef Eigen::Matrix<double, NMeasurements, NMeasurements> MeasurementMat;

    typedef Eigen::Matrix<double, NParameter, NParameter> JacobiFMat;
    typedef Eigen::Matrix<double, NMeasurements, NParameter> JacobiHMat;


    typedef Eigen::Matrix<double, NParameter, NIterations+1> ParameterSafe;
    typedef Eigen::Matrix<double, NMeasurements, NIterations+1> MeasurementSafe;

    ParameterVec mean;
    ParameterMat variance;

    ExtendedKalmanFilter(ParameterVec mean, ParameterMat variance): mean(mean), variance(variance) {
    };

    void update(std::function<MeasurementVec(ParameterVec)> measureFunction,
                std::function<ParameterVec(ParameterVec)> predictionFunction,
                MeasurementMat measurementNoise,
                ParameterMat processNoise ,
                MeasurementVec zMes){
        update(mean,
               variance,
               measureFunction,
               predictionFunction,
               measurementNoise,
               processNoise,
               zMes);
    }

private:
    // print param
    bool print = true;

    // update
    void update(ParameterVec& mean,
                ParameterMat& variance,
                std::function<MeasurementVec(ParameterVec)> measureFunction,
                std::function<ParameterVec(ParameterVec)> predictionFunction,
                MeasurementMat measurementNoise,
                ParameterMat processNoise ,
                MeasurementVec zMes) {

        // Jacobi 1
        MeasurementVec z = measureFunction(mean);
        JacobiFMat A = JacobiFMat::Zero();
        double h = NParameter;


    }







};
