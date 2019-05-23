//
// Created by maximilien on 21.05.19.
// based on https://towardsdatascience.com/the-unscented-kalman-filter-anything-ekf-can-do-i-can-do-it-better-ce7c773cf88d
//

#pragma once

#include <iostream>
#include <Eigen/Dense>
#include "functional"
#include <unsupported/Eigen/MatrixFunctions> /*sqrt*/

template <int NParameter, int NMeasurements>
class UnscentedKalmanFilter{
public:
    typedef Eigen::Matrix<double, NParameter, 1> ParameterVec;
    typedef Eigen::Matrix<double, NMeasurements, 1> MeasurementVec;
    typedef Eigen::Matrix<double, NParameter, NParameter> VarienceMat;
    typedef Eigen::Matrix<double, NParameter, NMeasurements> CrossCorellationMat;
    typedef Eigen::Matrix<double, NMeasurements, NMeasurements> MeasurmentMat;
    UnscentedKalmanFilter(ParameterVec mean, VarienceMat variance):mean(mean),variance(variance){};
    void update(std::function<MeasurementVec(ParameterVec)> measureFunction, std::function<ParameterVec(ParameterVec)> predictionFunction, MeasurmentMat measurementNoise, VarienceMat processNoise , MeasurementVec z){
            update(mean,variance,measureFunction, predictionFunction,measurementNoise,processNoise,z);
    }
private:
    void update(ParameterVec& mean, VarienceMat& variance, std::function<MeasurementVec(ParameterVec)> measureFunction, std::function<ParameterVec(ParameterVec)> predictionFunction, MeasurmentMat measurementNoise, VarienceMat processNoise , MeasurementVec z){

        // Parameters
        int n = NParameter;
        int lambda = 3-n;

        // Sigma points
        ParameterVec chi[2*NParameter+1];
        chi[0] = mean;
        VarienceMat covTermSquared = (n+lambda)*variance;
        std::cout << covTermSquared << std::endl;
        VarienceMat covTerm = covTermSquared.sqrt();
        for (int i = 1; i<=NParameter; i++){
           chi[i] = mean + covTerm.col(i-1);
           chi[i+NParameter] = mean - covTerm.col(i-1);
        }

         // Weights
         double w[NParameter];
         w[0] = lambda / (n + lambda);
         for (int i = 1; i<=2*NParameter; i++){
            w[i] = 1/ (2*(n+lambda));
         }

         // Prediction: Approximate gaussian
        ParameterVec mu = ParameterVec::Zero();
        for (int i = 0; i<= 2*NParameter; i++){
            ParameterVec predFunChi = predictionFunction(chi[i]);
            mu += w[i]*predFunChi ;
        }
        VarienceMat sigma = VarienceMat::Zero();
        for (int i = 0; i<= 2*NParameter; i++){
            ParameterVec difMu = predictionFunction(chi[i])-mu;
            sigma += w[i]*difMu*(difMu.transpose());
        }
        sigma += processNoise;

        // Update step, time update
        MeasurementVec zeta[2*NParameter+1] = MeasurementVec::Zero();
        for (int i = 0; i< 2*NParameter + 1; i++){
            zeta[i] = measureFunction(chi[i]);
        }
        MeasurementVec zPred = MeasurementVec::Zero();
        for (int i = 0; i< 2*NParameter + 1; i++){
            zPred += w[i]*zeta[i];
        }

        MeasurmentMat sVar = MeasurmentMat::Zero();
        for (int i = 0; i<= 2*NParameter + 1; i++){
            MeasurementVec difS = zeta[i] - zPred;
            MeasurementVec difStran = difS.transpose();
            sVar += w[i]*difS*difStran;
        }
        sVar += measurementNoise;

        CrossCorellationMat T = CrossCorellationMat::Zero();
        for (int i = 0; i <= 2*NParameter; i++){
            ParameterVec chiMu = chi[i]-mu;
            MeasurementVec zetaZpred = zeta[i]-zPred;
            MeasurementVec zetaZpredTran =zetaZpred.transpose();
            T += w[i]*chiMu*zetaZpred;
        }

        MeasurmentMat sVarInv = sVar.inverse();
        CrossCorellationMat K = T*sVarInv;


        // final State
        MeasurementVec zDiff = z - zPred;
        ParameterVec muFinal = mu + K*zDiff;

        VarienceMat sigmaFinal = sigma - K*sVar*(K.transpose());

        mean = muFinal;
        std::cout << muFinal << std::endl;
        variance = sigmaFinal;
        std::cout << sigmaFinal << std::endl;
    }
    ParameterVec mean;
    VarienceMat variance;
};
