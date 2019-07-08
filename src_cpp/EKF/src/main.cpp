//
// Created by maximilien on 02.07.19.
//
#include <iostream>
#include "Test/TestPacejkaEKF.h"
#include <fstream>
#include <string>
#include "InputOutput/ReaderCSV.cpp"
#include "InputOutput/WriterEKF.h"
#include "ExtendedKalmanFilter.h"

typedef ExtendedKalmanFilter<NP,NM, NI> EKF;


int main() {



    EKF::ParameterVec groundTruth;
    groundTruth<< 10, 1.9, 1 ;
    EKF::ParameterVec guess;
    guess << 10.345, 1.934, 1.363;
    EKF::ParameterMat variance = 0.2 * EKF::ParameterMat::Identity();

    for (int i = 1; i<=100; i++){
        EKF::ParameterMat variance = i * 0.02 * EKF::ParameterMat::Ones();
        std::cout << "Start Variance : " << std::endl << variance << std::endl;
        TestPacejkaEKF testPacejkaEkf;
        testPacejkaEkf.test(
                groundTruth,
                guess,
                variance
        );
    }





}

