//
// Created by maximilien on 21.05.19.
// based on https://towardsdatascience.com/the-unscented-kalman-filter-anything-ekf-can-do-i-can-do-it-better-ce7c773cf88d
//

#pragma once

#include <iostream>
#include <Eigen/Dense>
#include "functional"
#include <unsupported/Eigen/MatrixFunctions> /*sqrt*/

template <int NParameter, int NMeasurements, int NIterations>
class UnscentedKalmanFilter{
public:
    typedef Eigen::Matrix<double, NParameter, 1> ParameterVec;
    typedef Eigen::Matrix<double, NMeasurements, 1> MeasurementVec;
    typedef Eigen::Matrix<double, NParameter, NParameter> ParameterMat;
    typedef Eigen::Matrix<double, NParameter, NMeasurements> CrossCorellationMat;
    typedef Eigen::Matrix<double, NMeasurements, NMeasurements> MeasurementMat;

    typedef Eigen::Matrix<double, NParameter, NIterations+1> ParameterSafe;
    typedef Eigen::Matrix<double, NMeasurements, NIterations+1> MeasurementSafe;

    UnscentedKalmanFilter(ParameterVec mean, ParameterMat variance): mean(mean), variance(variance){

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
    ParameterVec mean;
    ParameterMat variance;
private:
    // debugUKF param
    bool debugUKF = false;

    // update
    void update(ParameterVec& mean,
                ParameterMat& variance,
                std::function<MeasurementVec(ParameterVec)> measureFunction,
                std::function<ParameterVec(ParameterVec)> predictionFunction,
                MeasurementMat measurementNoise,
                ParameterMat processNoise ,
                MeasurementVec zMes){

        // Parameters
        double L = NParameter;
        double alpha = 1; //spread
        double beta = 2; //incorporation of prior knowledge (here = 2, assuming gaussian)
        double kappa = 0; //secondary usually set to 0
        double lambda = alpha*alpha*(NParameter+kappa) + NParameter;
        if (debugUKF){
            std::cout << "NPARAM " << L << std::endl;
            std::cout << "alpha " << alpha << std::endl;
            std::cout << "beta " << beta << std::endl;
            std::cout << "kappa " << kappa << std::endl;
            std::cout << "lambda " << lambda << std::endl;
        }

        // Sigma points
        ParameterVec chi[2*NParameter+1];
        chi[0] = mean;
        ParameterMat covTermSquared = (L + lambda) * variance;
        if (debugUKF){
            std::cout << "covTermSquared " << std::endl << covTermSquared << std::endl;
        }
        ParameterMat covTerm = covTermSquared.sqrt();
        if (debugUKF){
            std::cout << "covTerm " << std::endl << covTerm << std::endl;
        }

        for (int i = 1; i<=NParameter; i++){
           chi[i] = mean + covTerm.col(i-1);
           chi[i+NParameter] = mean - covTerm.col(i-1);
        }

        if (debugUKF) {
            for (int i = 0; i<= 2*NParameter; i++) {
                std::cout << "Chi" << i << ": " << std::endl << chi[i] << std::endl;
            }
        }

        // Weights
        double w_m[2*NParameter+1];
        double w_c[2*NParameter+1];
         w_m[0] = (double) lambda / ((double) L + (double) lambda);
         for (int i = 1; i<=2*NParameter; i++){
                w_m[i] = 1/ (2*((double) L + (double) lambda));
         }
         for (int i = 0; i<=2*NParameter; i++){
             w_c[i] = w_m[i];
         }

         w_c[0] += (1 - alpha*alpha + beta);

        // debugUKF
        if (debugUKF) {
            for (int i = 0; i <= 2 * NParameter; i++) {
                std::cout << "wm" << i << ": " << w_m[i] << std::endl;
            }
            for (int i = 0; i <= 2 * NParameter; i++) {
                std::cout << "wc" << i << ": " << w_c[i] << std::endl;
            }
        }

        // Prediction: Approximate gaussian
        ParameterVec mu = ParameterVec::Zero();
        for (int i = 0; i<= 2*NParameter; i++){
            ParameterVec predFunChi = predictionFunction(chi[i]);
            mu += w_m[i]*predFunChi;
            if(debugUKF){
                std::cout << "predFunChi:" << i << "\n" << predFunChi << std::endl;
            }
        }

        ParameterMat sigma = ParameterMat::Zero();
        for (int i = 0; i<= 2*NParameter; i++){
            ParameterVec difMu = predictionFunction(chi[i])-mu;
            sigma += w_c[i]*difMu*(difMu.transpose());
        }
        sigma += processNoise;

        // debugUKF
        if (debugUKF) {
            std::cout << "prediction......" << std::endl;
            std::cout << "mu: " << std::endl << mu << std::endl;
            std::cout << "sigma: " << std::endl << sigma << std::endl;
        }

        // Update step, time update
        MeasurementVec zeta[2*NParameter+1];
        for (int i = 0; i< 2*NParameter + 1; i++){
            zeta[i] = measureFunction(chi[i]);
        }
        MeasurementVec zPred = MeasurementVec::Zero();
        for (int i = 0; i< 2*NParameter + 1; i++){
            zPred += w_m[i]*zeta[i];
        }

        // debugUKF
        if (debugUKF){
            for (int i = 0; i<= 2*NParameter+1; i++){
                std::cout << "chi" << i << ":" << std::endl << chi[i] << std::endl;
                std::cout << "zeta" << i << ":" << std::endl << zeta[i] << std::endl;
            }
            std::cout << "zPred: " << std::endl << zPred << std::endl;
            std::cout << "zMes: " << std::endl << zMes << std::endl;
        }

        MeasurementMat sVar = MeasurementMat::Zero();
        for (int i = 0; i<= 2*NParameter + 1; i++){
            MeasurementVec difS = zeta[i] - zPred;
            // MeasurementVec difStran = difS.transpose();
            MeasurementMat SStran = difS*difS.transpose();
            sVar += w_c[i]*SStran;
        }
        sVar += measurementNoise;

        CrossCorellationMat T = CrossCorellationMat::Zero();
        for (int i = 0; i <= 2*NParameter; i++){
            ParameterVec chiMu = chi[i]-mu;
            MeasurementVec zetaZpred = zeta[i]-zPred;
            T += w_c[i]*chiMu*zetaZpred.transpose();
        }
        if (debugUKF) {
            std::cout << "sVar:" << std::endl << sVar << std::endl;
            std::cout << "T:" << std::endl << T << std::endl;
        }

        MeasurementMat sVarInv = sVar.inverse();
        CrossCorellationMat K = T*sVarInv;
        if (debugUKF) {
            std::cout << "K:" << std::endl << K << std::endl;
        }


        // final State
        MeasurementVec zDiff = zMes - zPred;

        // debugUKF
        if (debugUKF) {
            std::cout << "zDiff:" << std::endl << zDiff << std::endl;
        }

        ParameterVec muFinal = mu + K*zDiff;
        ParameterMat sigmaFinal = sigma - K * sVar * (K.transpose());

        mean = muFinal;
        variance = sigmaFinal;

        //debugUKF
        if(debugUKF){
            std::cout << "muFinal" << std::endl << muFinal << std::endl;
            std::cout << "sigmaFinal" << std::endl << sigmaFinal << std::endl;
        }
    }
};
