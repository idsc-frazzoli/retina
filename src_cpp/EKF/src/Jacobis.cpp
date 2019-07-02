//
// Created by maximilien on 02.07.19.
//

#include "Jacobis.h"
#include "TestPacejkaEKF.h"
#include <iostream>


using namespace std;

extern TestPacejkaEKF::EKF::JacobiFMat Jacobis::getJacobiF(TestPacejkaEKF::EKF::ParameterVec parameterVec){

    /*************************************************************************
    ********************* Jacobian of State **********************************
    *************************************************************************/

    TestPacejkaEKF::EKF::JacobiFMat jacobiFMat
            = TestPacejkaEKF::EKF::JacobiFMat::Identity();

    /*************************************************************************
    **************************************************************************
    *************************************************************************/

    return jacobiFMat;
}

extern TestPacejkaEKF::EKF::JacobiHMat Jacobis::getJacobiH(TestPacejkaEKF::EKF::ParameterVec parameterVec){

    /*************************************************************************
    ********************* Jacobian of State **********************************
    *************************************************************************/

    TestPacejkaEKF::EKF::JacobiHMat jacobiHMat;

    //
    // derivatives of Pacejka done according to:
    // (0) https://www.wolframalpha.com/input/?i=derive+wrt+B+B*sin(C*arctan(D*x))
    // (1) https://www.wolframalpha.com/input/?i=derive+wrt+C+B*sin(C*arctan(D*x))
    // (2) https://www.wolframalpha.com/input/?i=derive+wrt+D+B*sin(C*arctan(D*x))
    //
    jacobiHMat(0,1) = parameterVec(1) * parameterVec(2) * /** x **/


    /*************************************************************************
    **************************************************************************
    *************************************************************************/

    return jacobiHMat;




