//
// Created by maximilien on 22.05.19.
//


#include <iostream>
#include "TestUKF/TestUKF.h"
#include "TestUKF/TestPacejkaUKF.h"
#include "ModelSimple/Model_first.h"
#include "ModelMPC/ModelMPC.h"
#include "ModelMPC/StaticTester.h"
#include "ModelMPC/LogTester.h"
#include "PacejkaUKF/PacejkaUKFinterface.h"

using namespace std;

int main(int, const char * const [])
{

    // Testing for variance
    //*******************************************************************************
    /*UKF::ParameterVec groundTruth;
    groundTruth<< 10, 1.9, 1 ;
    UKF::ParameterVec guess;
    guess << 10.345, 1.934, 1.363;
    UKF::ParameterMat variance; // = UKF::ParameterMat::Identity();
    //variance << 1, 0, 0,
    //            0, 0.03, 0,
    //            0, 0, 0.37;

    for(int i = 1; i<= 10; i++) {
        UKF::ParameterMat variance = i * 0.1 * UKF::ParameterMat::Identity();
        std::cout << "Start Variance : " << std::endl << variance << std::endl;
        TestPacejkaUKF testPacejkaUkf;
        testPacejkaUkf.test(
                groundTruth,
                guess,
                variance
        );
    }
    */

    // Testing Simple UKF
    //*******************************************************************************
    /*
    TestUKF testUkf;
    testUkf.test();
     */

    // Testing model_first and UKF
    //*******************************************************************************
    //model_first(); // TODO non functioning due to template size

    // Testing ModelMPC from code g en
    //*******************************************************************************
    call_modelDx();

    // Testing ModelMPC from given static data
    //*******************************************************************************
    call_modelDx_static();

    // Testing ModelMPC from log data
    //*******************************************************************************
    call_modelDx_log();

    // UKF for Pacejka
    //*******************************************************************************
    PacejkaUKFinterface ukf;
    ukf.runSta();





}