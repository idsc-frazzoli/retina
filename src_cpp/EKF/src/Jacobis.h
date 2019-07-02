//
// Created by maximilien on 02.07.19.
//

#pragma once

#include "Jacobis.h"
#include "TestPacejkaEKF.h"
#include "ExtendedKalmanFilter.h"
#include <iostream>



class Jacobis {

    TestPacejkaEKF::EKF::JacobiFMat getJacobiF(TestPacejkaEKF::EKF::ParameterVec parameterVec);
    TestPacejkaEKF::EKF::JacobiHMat getJacobiH(TestPacejkaEKF::EKF::ParameterVec parameterVec);

};