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

    UKF::ParameterVec groundTruth;
    groundTruth<< 10, 1.9, 1 ;
    UKF::ParameterVec guess;
    guess << 10.345, 1.934, 1.363;
    UKF::ParameterMat variance;

    for( int i = 1; i<= 1; i++){
        UKF::ParameterMat § = 0.1 * UKF::ParameterMat::Identity();
        std::cout << "Start Variance : " << std::endl << variance << std::endl;
        TestPacejkaUKF testPacejkaUkf;
        testPacejkaUkf.test(
                groundTruth,
                guess,
                variance
        );
    }



    //TestUKF testUkf;
    //testUkf.test();

}