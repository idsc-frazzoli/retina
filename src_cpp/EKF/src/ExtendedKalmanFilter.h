//
// Created by maximilien on 01.07.19.
// build based on UKF model.
// based on https://en.wikipedia.org/wiki/Extended_Kalman_filter using DT measurements
//

#pragma once

#include <iostream>
#include <Eigen/Dense>
#include <cmath>

using namespace std;

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


    ExtendedKalmanFilter(ParameterVec mean, ParameterMat variance): mean(mean), variance(variance) {
    };

    void update(std::function<MeasurementVec(ParameterVec)> measureFunction,
                std::function<ParameterVec(ParameterVec)> predictionFunction,
                MeasurementMat measurementNoise,
                ParameterMat processNoise ,
                MeasurementVec zMes,
                std::function<JacobiFMat(ParameterVec)> jacobiF,
                std::function<JacobiHMat(ParameterVec)> jacobiH){
        update(mean,
               variance,
               measureFunction,
               predictionFunction,
               measurementNoise,
               processNoise,
               zMes,
               jacobiF,
               jacobiH);
    }


    ParameterVec mean;
    ParameterMat variance;

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
                MeasurementVec zMes,
                std::function<JacobiFMat(ParameterVec)> jacobiF,
                std::function<JacobiHMat(ParameterVec)> jacobiH) {
        // Initialize
        ParameterVec x = mean;
        ParameterMat P = variance;
        MeasurementVec z = measureFunction(mean);

        if (print) {
            cout << "mean" << endl << x << endl;
            cout << "variance" << endl << P << endl;
            cout << "measurement" << endl << z << endl;
        }

        // Predict
        ParameterVec xk = predictionFunction(x);
        JacobiFMat jacobiFMat = jacobiF(x);
        ParameterMat Pk = jacobiFMat * P + P * jacobiFMat + processNoise;

        if (print) {
            cout << "jacobiF" << endl << jacobiFMat << endl;
            cout << "xk" << endl << xk << endl;
            cout << "Pk" << endl << Pk << endl;
        }

        // Update
        JacobiHMat jacobiHMat = jacobiH(x);
        CrossCorellationMat K =
                Pk * jacobiHMat.transpose() * (jacobiHMat * Pk * jacobiHMat.transpose() + measurementNoise).inverse();

        ParameterVec x_pred = xk + K * (z - measureFunction(xk));
        ParameterMat P_pred = (ParameterMat::Identity() - K * jacobiHMat) * P;

        if (print) {
            cout << "jacobiH" << endl << jacobiHMat << endl;
            cout << "K" << endl << K << endl;
            cout << "x_pred" << endl << x_pred << endl;
            cout << "P_pred" << endl << P_pred << endl;
        }

        mean = x_pred;
        variance = P_pred;
    }
};
