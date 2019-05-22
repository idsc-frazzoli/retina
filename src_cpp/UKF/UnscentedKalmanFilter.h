//
// Created by maximilien on 21.05.19.
// based on https://towardsdatascience.com/the-unscented-kalman-filter-anything-ekf-can-do-i-can-do-it-better-ce7c773cf88d
//

#pragma once

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
    typedef Eigen::Matrix<double, NMeasurements, NMeasurements> MesurmentMat;
    UnscentedKalmanFilter(ParameterVec mean, VarienceMat variance):mean(mean),variance(variance){};
    void update(std::function<MeasurementVec(ParameterVec)> measureFunction, std::function<ParameterVec(ParameterVec)> predictionFunction, MesurmentMat measurementNoise, VarienceMat processNoise , MeasurementVec z){
            update(mean,variance,measureFunction, predictionFunction,measurementNoise,processNoise,z);
    }
private:
    void update(ParameterVec priorMean, VarienceMat priorVariance, std::function<MeasurementVec(ParameterVec)> measureFunction, std::function<ParameterVec(ParameterVec)> predictionFunction, MesurmentMat measurementNoise, VarienceMat processNoise , MeasurementVec z){

        // Sigma points
         ParameterVec chi[2*NParameter+1];
         chi[0] = priorMean;
         int n = NParameter;
         int lambda = 3-n;
         VarienceMat covTermSquard = ((n+lambda)*priorVariance);
         VarienceMat covTerm = covTermSquard.sqrt();
         for (int i = 1; i<=NParameter; i++){
            chi[i] = priorMean + covTerm.col(i-1);
            chi[i+NParameter] = priorMean - covTerm.col(i-1);
         }

         // Weights
         ParameterVec w[NParameter];
         w[0] << lambda / (n + lambda);
         for (int i = 1; i<=2*NParameter; i++){
            w[i] << 1/ (2*(n+lambda));
         }

         // Prediction: Approximate gaussian
        ParameterVec mu = ParameterVec::Zero();
        for (int i = 0; i<= 2*NParameter; i++){
            ParameterVec predFunChi = predictionFunction(chi[i]);
            mu += w[i](0)*predFunChi ;
        }
        VarienceMat sigma = VarienceMat::Zero();
        for (int i = 0; i<= 2*NParameter; i++){
            ParameterVec difChi = predictionFunction(chi[i])-mu;
            sigma += w[i](0)*difChi*(difChi.transpose());
        }
        sigma += processNoise;

        // Update step
        ParameterVec zeta[2*NParameter+1] = ParameterVec::Zero();
        for (int i = 0; i< 2*NParameter + 1; i++){
            zeta[i] = measureFunction(chi[i]);
        }
        MeasurementVec zPred = MeasurementVec::Zero();
        for (int i = 0; i<= 2*NParameter; i++){
            zPred += w[i](0)*zeta[i];
        }
        VarienceMat sVar = VarienceMat::Zero();
        for (int i = 0; i<= 2*NParameter; i+1){
            ParameterVec difS = zeta[i] - zPred;
            sVar += w[i](0)*difS*(difS.transpose());
        }
        sVar += measurementNoise;

        CrossCorellationMat T = CrossCorellationMat::Zero();
        for (int i = 0; i <= 2*NParameter; i++){
            ParameterVec chiMu = chi[i]-mu;
            MeasurementVec zetaZpred = zeta[i]-zPred;
            T += w[i](0)*(chiMu)*((zetaZpred).transpose());
        }

        CrossCorellationMat K = CrossCorellationMat::Zero();
        VarienceMat sVarInv = sVar.inverse();
        K = T*sVarInv;

        // final state
        ParameterVec muFinal = ParameterVec::Zero();
        muFinal = mu + K*(z - zPred);

        VarienceMat sigmaFinal = VarienceMat::Zero();
        sigmaFinal = (VarienceMat::Identity() - K*T)*sigma;
    }
    ParameterVec mean;
    VarienceMat variance;
};
