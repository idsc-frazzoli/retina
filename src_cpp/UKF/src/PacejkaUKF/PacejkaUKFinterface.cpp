//
// Created by maximilien on 26.08.19.
//

#include "PacejkaUKFinterface.h"
#include "../ModelMPC/modelDx.h"
#include <functional>

using namespace std;


// extract data
Eigen::MatrixXd data =
        load_csv<Eigen::MatrixXd>("/home/maximilien/Documents/sp/logs/pacejkaFull_20190708T114135_f3f46a8b.lcmObj.00.csv");

//
long k = 0;



void PacejkaUKFinterface::runStatic() {

    std::cout << "****Static UKF tester****" << std::endl;


    double const B1 = 9;
    double C1 = 1;
    double D1 = 10;
    double B2 = 5.2;
    double C2 = 1.1;
    double D2 = 10;
    double Cf = 0.3;
    double param[8] = {B1, C1, D1, B2, C2, D2, Cf};

    // init
    // *******************************************************************
    double q = 0.1; //std of process
    double r = 0.1; //std of measurement
    UKF::ParameterMat processCov = UKF::ParameterMat::Identity() * q; // cov of process
    UKF::MeasurementMat measureCov = UKF::MeasurementMat::Identity() * r; // cov of measurement

    UKF::ParameterVec x; //initial state
    x <<  B1, C1, D1,  B2, C2, D2,  Cf;
    UKF::ParameterMat P = UKF::ParameterMat::Identity(); //inital state cov
    if(print){
        cout << "initial state" << endl << x << endl;
        cout << "initial cov" << endl << P << endl;
    }

    //UKF
    UKF ukf = UKF(x, P);

    //functions
    // *******************************************************************
    function<UKF::ParameterVec(UKF::ParameterVec)> predictionFunction
            = [](UKF::ParameterVec parameterVec){

        // Identity (no info available on dynamics of Factors)

        return parameterVec;
    };

    //Space allocation for plotting
    // *******************************************************************
    // TODO find new method for writing with more data
    Eigen::Matrix<double, NParam + 1, NIter+1> paramData;
    Eigen::Matrix<double, NParam + 1, NIter+1> mesData;


    // assuming i as time
    for(long i=0; i<data.rows(); i++){
        k = i;

        // measurement
        function<UKF::MeasurementVec(UKF::ParameterVec)> measureFunction
                = [](UKF::ParameterVec param) {
                    UKF::MeasurementVec measurementVec;

                    double velx = data(k, 2);
                    double vely = data(k, 3);
                    double velrotz = data(k, 4);

                    //assume these are constant
                    double BETA = 0.3;
                    double AB = 0.1;
                    double TV = 2;
                    double ACCX;
                    double ACCY;
                    double ACCROTZ = 9.81;

                    double paramIn[8] = {param(0),
                                         param(1),
                                         param(2),
                                         param(3),
                                         param(4),
                                         param(5),
                                         param(6)};

                    modelDx(velx, // VELX
                            vely, // VELY
                            velrotz, // VELROTZ
                            BETA, // BETA
                            AB, // AB
                            TV, // TV
                            paramIn, // pacejka param
                            &ACCX, // ACCX
                            &ACCY, // ACCY
                            &ACCROTZ); // ACCROTZ

                    measurementVec(0) = ACCX;
                    measurementVec(1) = ACCY;
                    measurementVec(2) = ACCROTZ;
                    return measurementVec;
        };

        UKF::MeasurementVec z;
        z << data(k,5), data(k,6), data(k,7);
        if(print) {
            std::cout << "zMes: " << z << std::endl;
        }


        //UKF
        if (print) {
            cout << "update " << i << "..................." << endl;
        }

        //****************************

        ukf.update(
                measureFunction,
                predictionFunction,
                measureCov,
                processCov,
                z);


        /*
        //for plotting
        Eigen::MatrixXd value(NParam+1, 1);
        value << i, ukf.mean(0), ukf.mean(1), ukf.mean(2), ukf.mean(3), ukf.mean(4), ukf.mean(5), ukf.mean(6);
        paramData.col(i) = value;

         */

    }

    if (print){
        std::cout << "paramData" << std::endl << paramData << std::endl;
    }


}