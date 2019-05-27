#include <iostream>
#include <Eigen/Dense>
#include <unsupported/Eigen/MatrixFunctions> /*sqrt*/
#include "TestUKF.h"

#define NUMPAR 3

typedef Eigen::Matrix<double, NUMPAR, 1> ParameterVector;
typedef Eigen::Matrix<double, NUMPAR, NUMPAR> MMatrix;

using Eigen::MatrixXd;
using std::cout;
using std::endl;

int main()
{
    cout << "test UKF...... " << endl;
    TestUKF testUkf;
    testUkf.test();




}