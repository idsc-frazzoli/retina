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
    UnscentedKalmanFilter(ParameterVec mean, VarienceMat variance):mean(mean),variance(variance){

    };

    void update(std::function<MeasurementVec(ParameterVec)> measureFunction,
            std::function<ParameterVec(ParameterVec)> predictionFunction,
            MeasurmentMat measurementNoise,
            VarienceMat processNoise ,
            MeasurementVec z){
            update(mean,
                    variance,
                    measureFunction,
                    predictionFunction,
                    measurementNoise,
                    processNoise,
                    z);
    }
private:
    void update(ParameterVec& mean,
            VarienceMat& variance,
            std::function<MeasurementVec(ParameterVec)> measureFunction,
            std::function<ParameterVec(ParameterVec)> predictionFunction,
            MeasurmentMat measurementNoise,
            VarienceMat processNoise ,
            MeasurementVec z){

        // print param
        bool print = false;

        // Parameters
        int n = NParameter;
        double alpha = 1;
        double beta = 0;
        double kappa = 1;
        double lambda = alpha*alpha*((double) NParameter+kappa) + (double) NParameter;
        if (print){
            std::cout << "NPARAM " << n << std::endl;
            std::cout << "alpha " << alpha << std::endl;
            std::cout << "beta " << beta << std::endl;
            std::cout << "kappa " << kappa << std::endl;
            std::cout << "lambda " << lambda << std::endl;
        }

        // Sigma points
        ParameterVec chi[2*NParameter+1];
        chi[0] = mean;
        VarienceMat covTermSquared = (n+lambda)*variance;
        VarienceMat covTerm = covTermSquared.sqrt();
        for (int i = 1; i<=NParameter; i++){
           chi[i] = mean + covTerm.col(i-1);
           chi[i+NParameter] = mean - covTerm.col(i-1);
        }

        // print
        //std::cout << "CovSqrd: " << std::endl << covTermSquared << std::endl;
        //std::cout << "Cov: " << std::endl << covTerm << std::endl;
        //for (int i = 0; i<= 2*NParameter; i++){
        //    std::cout << "Chi" <<i <<": " << std::endl << chi[i] << std::endl;
        //}

        // Weights
        double w_m[2*NParameter+1];
        double w_c[2*NParameter+1];
         w_m[0] = (double) lambda / ((double) n + (double) lambda);
         for (int i = 1; i<=2*NParameter; i++){
                w_m[i] = 1/ (2*((double) n + (double) lambda));
         }
         for (int i = 0; i<=2*NParameter; i++){
             w_c[i] = w_m[i];
         }

         w_c[0] += (1 - alpha*alpha + beta);

        // print
        /*
        for (int i = 0; i<= 2*NParameter; i++){
            std::cout << "wm" <<i <<": " << w_m[i] << std::endl;
            std::cout << "wc" <<i <<": " << w_c[i] << std::endl;
        }*/

        // Prediction: Approximate gaussian
        ParameterVec mu = ParameterVec::Zero();
        for (int i = 0; i<= 2*NParameter; i++){
            ParameterVec predFunChi = predictionFunction(chi[i]);
            mu += w_m[i]*predFunChi;
            //std::cout << "funchi:\n" << predFunChi << std::endl;
        }
        VarienceMat sigma = VarienceMat::Zero();
        for (int i = 0; i<= 2*NParameter; i++){
            ParameterVec difMu = predictionFunction(chi[i])-mu;
            sigma += w_c[i]*difMu*(difMu.transpose());
            //std::cout << "difmu:\n" << difMu << std::endl;
        }
        sigma += processNoise;

        // print
        std::cout << "predict" << std::endl;
        std::cout << "mu: " << std::endl << mu << std::endl;
        std::cout << "sigma: " << std::endl << sigma << std::endl;


        // Update step, time update
        MeasurementVec zeta[2*NParameter+1];// = MeasurementVec::Zero();
        for (int i = 0; i< 2*NParameter + 1; i++){
            zeta[i] = measureFunction(chi[i]);
        }
        MeasurementVec zPred = MeasurementVec::Zero();
        for (int i = 0; i< 2*NParameter + 1; i++){
            zPred += w_m[i]*zeta[i];
        }

        /*
        // print
        for (int i = 0; i<= 2*NParameter+1; i++){
            std::cout << "chi" <<i <<": " << std::endl << chi[i] << std::endl;
            std::cout << "zeta" <<i <<": " << std::endl << zeta[i] << std::endl;
        }
        std::cout << "zPred" <<": " << std::endl << zPred << std::endl;
         */

        MeasurmentMat sVar = MeasurmentMat::Zero();
        for (int i = 0; i<= 2*NParameter + 1; i++){
            MeasurementVec difS = zeta[i] - zPred;
            //std::cout << "difS" <<i<<": " << std::endl << difS << std::endl;
            MeasurementVec difStran = difS.transpose();
            sVar += w_c[i]*difS*difStran;
        }
        sVar += measurementNoise;

        CrossCorellationMat T = CrossCorellationMat::Zero();
        for (int i = 0; i <= 2*NParameter; i++){
            ParameterVec chiMu = chi[i]-mu;
            MeasurementVec zetaZpred = zeta[i]-zPred;
            MeasurementVec zetaZpredTran =zetaZpred.transpose();
            T += w_c[i]*chiMu*zetaZpred;
        }

        MeasurmentMat sVarInv = sVar.inverse();
        CrossCorellationMat K = T*sVarInv;


        // final State
        MeasurementVec zDiff = z - zPred;
        ParameterVec muFinal = mu + K*zDiff;

        VarienceMat sigmaFinal = sigma - K*sVar*(K.transpose());

        mean = muFinal;
        std::cout << "correct" << std::endl;
        std::cout << "muFinal" << std::endl << muFinal << std::endl;
        variance = sigmaFinal;
        std::cout << "sigma final" << std::endl << sigmaFinal << std::endl;
    }
    ParameterVec mean;
    VarienceMat variance;
};
