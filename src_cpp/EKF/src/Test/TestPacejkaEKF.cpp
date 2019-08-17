//
// Created by maximilien on 22.05.19.
//

#include "TestPacejkaEKF.h"
#include <iostream>
#include <functional>
#include <stdlib.h>
#include <time.h>
#include "../InputOutput/WriterEKF.h"
#include "../InputOutput/ReaderCSV.cpp"


void TestPacejkaEKF::test(
        EKF::ParameterVec groundTruth,
        EKF::ParameterVec guess,
        EKF::ParameterMat variance
        ) {



    double r = 0.1; // measurement noise
    //double r = static_cast <double> (rand()) / static_cast <double> (RAND_MAX); // measurement noise
    EKF::MeasurementMat measurementNoise = r * r * EKF::MeasurementMat::Identity();
    double q = 0.1; //process noise
    EKF::ParameterMat processNoise = q * q * EKF::ParameterMat::Identity();

    // UKF start
    EKF::ParameterVec mean = guess;
    EKF ekf = EKF(mean, variance);

    std::function<EKF::ParameterVec(EKF::ParameterVec)> predictionFunction
    = [](EKF::ParameterVec parameterVec) {
                return parameterVec;
    };

    // extract slip
    Eigen::MatrixXd slip =
            load_csv<Eigen::MatrixXd>("/home/maximilien/Documents/sp/logs/slip_20190708T114135_f3f46a8b.lcm.00.csv");

    // slip filter
    /*    double size = 40;
        for (int i = size; i < slip.rows(); i++){
            slip(i,2) = 1/size * slip(i,2);
            for (int j = 0;  j < size; j++){
                    slip(i,2) += 1/size * slip(i-j,2);
            }
        }
    */



    //for plotting
    // TODO find new method for writing with more data
    Eigen::Matrix<double, NP + 1, NI+1> params;

    for (int i = 0; i<= NI; i++){
        // print
        if(print){
            std::cout << "iteration--------------------------------------- " << i << std::endl;
        }

        // side slip s
        //constant slip
        //double s = .391;

        // random parameter s in range [-1;2];
        //double s = 3*static_cast <double> (rand()) / static_cast <double> (RAND_MAX) - 1;

        // sinusoid around -1 and 2
        //double s = 1.5*sin(0.01*i)+0.5;

        // sinusoid around 0 and 2
        //double s = 0.5*sin(0.05*i) + 0.3*sin(3*i) + 0.2*sin(10*i) + 1 ;

        // using slip from gokart log
        double s = slip(i,2);

        if(print){
            std::cout << "s: " << s << std::endl;
        }

        // measurement function
        std::function<EKF::MeasurementVec(EKF::ParameterVec)> measureFunction
                = [s](EKF::ParameterVec parameter){
            double b = parameter(0);
            double c = parameter(1);
            double d = parameter(2);

            double r = d*sin(c*atan(b*s));

            EKF::MeasurementVec measurementVec;
            measurementVec << r   ;
           return measurementVec;
        };

        EKF::MeasurementVec zMes = measureFunction(groundTruth);

        if(print){
            std::cout << "zMes: " << zMes << std::endl;
        }

        // Jacobis
        //****************************
        // F
        // derivatives if x(k+1) = x(k) (->Identity)
        std::function<EKF::JacobiFMat(EKF::ParameterVec)> jacobiF
                = [s](EKF::ParameterVec p){

            EKF::JacobiFMat jacobiFMat = EKF::JacobiFMat::Identity();

            return jacobiFMat;
        };
        // H
        // derivatives of Pacejka done according to:
        // (0) https://www.wolframalpha.com/input/?i=derive+wrt+B+B*sin(C*arctan(D*x))
        // (1) https://www.wolframalpha.com/input/?i=derive+wrt+C+B*sin(C*arctan(D*x))
        // (2) https://www.wolframalpha.com/input/?i=derive+wrt+D+B*sin(C*arctan(D*x))
        //
        std::function<EKF::JacobiHMat(EKF::ParameterVec)> jacobiH
                = [s](EKF::ParameterVec p){
            EKF::JacobiHMat jacobiHMat;

            jacobiHMat(0,0) = (p(1)*p(2)*s*cos(p(1)*atan(p(1)*s)))/(p(0)*p(0)*s*s + 1);
            jacobiHMat(0,1) = p(2)*atan(p(0)*s)*cos(p(1)*atan(p(0)*s));
            jacobiHMat(0,2) = sin(p(1)*atan(p(0)*s));

            return jacobiHMat;
        };

        // EKF Update
        //****************************
        ekf.update(
                measureFunction,
                predictionFunction,
                measurementNoise,
                processNoise,
                zMes,
                jacobiF,
                jacobiH);

        //for plotting
        Eigen::MatrixXd value(4, 1);
        value << i, ekf.mean(0), ekf.mean(1), ekf.mean(2);
        params.col(i) = value;
    }

    if(print){
        std::cout << "params" << std::endl << params << std::endl;
    }

    // compute rmse
    for (int i = 0; i < NI; i++){
        rmse += std::sqrt(pow(params(1,i) - groundTruth(0),2)
                          +pow(params(2,i) - groundTruth(1),2)
                          +pow(params(3,i) - groundTruth(2),2));
    }
    rmse = rmse/sqrt(NI);

    // compute convergence
    convergence = std::sqrt(pow(params(1,NI-1) - groundTruth(0),2)
                      +pow(params(2,NI-1) - groundTruth(1),2)
                      +pow(params(3,NI-1) - groundTruth(2),2));


    std::cout << "Mean: \t" << std::endl << ekf.mean << std::endl;
    std::cout << "Variance: \t" << std::endl << ekf.variance << std::endl;
    std::cout << "RMSE: \t " << rmse << std::endl;
    std::cout << "Convergence: \t " << convergence << std::endl;



    // export for plot
    if(writeCSV) {
        WriterEKF writerEkf;
        writerEkf.writeToCSV("paramsEKF.csv", params.transpose());
    }

}