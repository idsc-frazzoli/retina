//
// Created by maximilien on 22.05.19.
//
#include <iostream>
#include "Test/TestUKF.h"
#include "Test/TestPacejkaUKF.h"

typedef UnscentedKalmanFilter<NP, NM, NI> UKF;

using namespace std;
int main()
{

    /*

    UKF::ParameterVec groundTruth;
    groundTruth<< 10, 1.9, 1 ;
    UKF::ParameterVec guess;
    guess << 10.345, 1.934, 1.363;
    UKF::ParameterMat variance; // = UKF::ParameterMat::Identity();
    //variance << 1, 0, 0,
    //            0, 0.03, 0,
    //            0, 0, 0.37;

    for( int i = 1; i<= 100; i++){
        UKF::ParameterMat variance = i * 0.01 *UKF::ParameterMat::Identity();
        std::cout << "Start Variance : " << std::endl << variance << std::endl;
        TestPacejkaUKF testPacejkaUkf;
        testPacejkaUkf.test(
                groundTruth,
                guess,a
                variance
        );


    variance = 0.01 *UKF::ParameterMat::Identity();
    TestPacejkaUKF testPacejkaUkfFinal;
    testPacejkaUkfFinal.test(
            groundTruth,
            guess,
            variance
    );

    */


    /*
    TestUKF testUkf;
    testUkf.test();
    */

    // slipValues
    double s_RLF[3];
    s_RLF[0] = 0.1;
    s_RLF[1] = 0.3;
    s_RLF[2] = 0.3;
    std::cout << s_RLF << std::endl;


    UKF::ParameterVec x; //initial state
    std::cout << x.size() << std::endl;

    x << 10,
    1.9,
    1,
    10,
    1.9,
    1,
    10,
    1.9,
    1,
    300;
    std::cout << x << std::endl;



    //measurements save actual state and measurement
    function<UKF::MeasurementVec(UKF::ParameterVec)> measureFunction
            = [s_RLF](UKF::ParameterVec param){

                // pacejkas
                double mu_R = param(2)*sin(param(1)*atan(param(0)*s_RLF[0]));
                double mu_L = param(5)*sin(param(4)*atan(param(3)*s_RLF[1]));
                double mu_F = param(8)*sin(param(7)*atan(param(6)*s_RLF[2]));
                std::cout << mu_R << std::endl;
                std::cout << mu_L << std::endl;
                std::cout << mu_F << std::endl;


                // distances
                double x_COG = 0.55;
                double d_BtoF = 1.60;

                // Rorces
                double g = 9.81;

                double F_xF = 1/mu_F * x_COG/d_BtoF * param(9) * g;
                double F_xR = 1/(mu_R*mu_L -1) * ((mu_L-1)*param(9)*g - (mu_L -1)*F_xF);
                double F_xL = 1/(mu_R*mu_L -1) * ((mu_R-1)*param(9)*g - (mu_R -1)*F_xF);
                std::cout << F_xF << std::endl;
                std::cout << F_xR << std::endl;
                std::cout << F_xL << std::endl;

                UKF::MeasurementVec measurementVec;
                measurementVec << 1/param(9)*(F_xR + F_xL + F_xF);

                return measurementVec;
            };

    UKF::MeasurementVec zeta = measureFunction(x);
    std::cout << zeta << std::endl;

}