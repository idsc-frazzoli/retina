//
// Created by maximilien on 22.05.19.
// based on https://www.mathworks.com/matlabcentral/fileexchange/18217-learning-the-unscented-kalman-filter
//

#include "../Test/TestUKF.h"
#include "../InputOutput/WriterUKF.h"
#include "../InputOutput/ReaderCSV.cpp"
#include <iostream>
#include <fstream>
#include <functional>
#include <stdlib.h>
#include <time.h>

using namespace std;
using namespace Eigen;

void TestUKF::test() {

    // init
    // *******************************************************************
    double q = 0.1; //std of process
    double r = 0.1; //std of measurement
    UKF::ParameterMat processCov = UKF::ParameterMat::Identity() * q; // cov of process
    UKF::MeasurementMat measureCov = UKF::MeasurementMat::Identity() * r; // cov of measurement

    UKF::ParameterVec x; //initial state
    x <<  10, 1.9, 1,  10, 1.9, 1,  10, 1.9, 1 , 300;
    UKF::ParameterMat P = UKF::ParameterMat::Identity(); //inital state cov
    if(print){
        cout << "initial state" << endl << x << endl;
        cout << "initial cov" << endl << P << endl;
    }

    // extract slip
    Eigen::MatrixXd data =
            load_csv<Eigen::MatrixXd>("/home/maximilien/Documents/sp/logs/pacejkaFull_20190708T114135_f3f46a8b.lcm.00.csv");

    //UKF
    UKF ukf = UKF(x, P);

    //functions
    // *******************************************************************
    function<UKF::ParameterVec(UKF::ParameterVec)> predictionFunction
            = [](UKF::ParameterVec parameterVec){

        // Identity (no info available on dynamics of Factors

        return parameterVec;
    };

    //Space allocation for plotting
    // *******************************************************************
    // TODO find new method for writing with more data
    Eigen::Matrix<double, NP + 1, NI+1> params;
    Eigen::Matrix<double, NP + 1, NI+1> mes;


    for(int i = 0; i<= NI; i++) {

        // slipValues
        double s_RLF[3];
        s_RLF[0] = data(NI,3);
        s_RLF[1] = data(NI,2);
        s_RLF[2] = 0;
        if(print){
            std::cout << "sR: " << s_RLF[0] << std::endl;
            std::cout << "sL: " << s_RLF[1] << std::endl;
            std::cout << "sF: " << s_RLF[2] << std::endl;
        }


        //measurements save actual state and measurement
        function<UKF::MeasurementVec(UKF::ParameterVec)> measureFunction
                = [s_RLF](UKF::ParameterVec param){
                    UKF::MeasurementVec measurementVec;

                    // pacejkas
                    double mu_R = param(2)*sin(param(1)*atan(param(0)*s_RLF[0]));
                    double mu_L = param(5)*sin(param(4)*atan(param(3)*s_RLF[1]));
                    double mu_F = param(8)*sin(param(7)*atan(param(6)*s_RLF[2]));

                    // distances
                    double x_COG = 0.55;
                    double d_BtoF = 1.60;

                    // forcesa
                    double g = 9.81;

                    double F_xF = 1/mu_F * x_COG/d_BtoF * param(9) * g;
                    double F_xR = 1/(mu_R*mu_L -1) * ((mu_L-1)*param(9)*g - (mu_L -1)*F_xF);
                    double F_xL = 1/(mu_R*mu_L -1) * ((mu_R-1)*param(9)*g - (mu_R -1)*F_xF);

                    measurementVec(0) = 1/param(9)*(F_xR + F_xL + F_xF);

                    return measurementVec;
        };

        // extract measurement from csv
        UKF::MeasurementVec z = UKF::MeasurementVec::Ones();
        if(print) {
            std::cout << "zMes: " << z << std::endl;
        }


        //UKF
        if (print) {
            cout << "update " << i << "..................." << endl;
        }

        // UKF Update
        //****************************
        ukf.update(
                measureFunction,
                predictionFunction,
                measureCov,
                processCov,
                z);

        //for plotting
        Eigen::MatrixXd value(NP+1, 1);
        value << i, ukf.mean(0), ukf.mean(1), ukf.mean(2), ukf.mean(3), ukf.mean(4), ukf.mean(5), ukf.mean(6), ukf.mean(7), ukf.mean(8), ukf.mean(9);
        params.col(i) = value;
    }

    if (print){
        std::cout << "params" << std::endl << params << std::endl;
    }

    // compute rmse Weight
    for (int i = 0; i < NI; i++){
        rmse += std::sqrt(pow(params(10,i) - weightGroundTruth,2));
    }

    // compute convergence
    convergence = std::sqrt(pow(params(10,NI-1) - weightGroundTruth,2));



    std::cout << "Mean: \t" << std::endl << ukf.mean << std::endl;
    std::cout << "Variance: \t" << std::endl << ukf.variance << std::endl;
    std::cout << "RMSE: \t " << rmse << std::endl;
    std::cout << "Convergence: \t " << convergence << std::endl;



    // export for plot
    // *******************************************************************
    if(writeCSV) {
        WriterUKF writerUkf;
        writerUkf.writeToCSV("mes.csv", mes.transpose());
        writerUkf.writeToCSV("paramsFull.csv", params.transpose());
    }
}
