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
    UKF::ParameterMat variance; // = UKF::ParameterMat::Identity();
    //variance << 1, 0, 0,
    //            0, 0.03, 0,
    //            0, 0, 0.37;

    /* for( int i = 1; i<= 100; i++){
        UKF::ParameterMat variance = i * 0.01 *UKF::ParameterMat::Identity();
        std::cout << "Start Variance : " << std::endl << variance << std::endl;
        TestPacejkaUKF testPacejkaUkf;
        testPacejkaUkf.test(
                groundTruth,
                guess,a
                variance
        );
    }*/

    variance = 0.01 *UKF::ParameterMat::Identity();
    TestPacejkaUKF testPacejkaUkfFinal;
    testPacejkaUkfFinal.test(
            groundTruth,
            guess,
            variance
    );


    //TestUKF testUkf;
    //testUkf.test();

}