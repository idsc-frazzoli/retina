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
    // UKF
    cout << "Starting UKF...... " << endl;

    // Preliminaries
    ParameterVector R_e_r_Prev;
    R_e_r_Prev.setOnes();
    R_e_r_Prev = 10 * R_e_r_Prev;


    // Initialize
    cout << "Initialize UKF...... " << endl;
    double gamma = 2;
    ParameterVector w0_plus = ParameterVector::Random();
    ParameterVector P_w0_plus = ParameterVector::Random();

    ParameterVector wpPrev_plus = w0_plus;


    // Prediction
    cout << "Prection UKF...... " << endl;
    ParameterVector wk_min = wpPrev_plus;
    ParameterVector P_wk_min = P_w0_plus + R_e_r_Prev;

    MMatrix Wkkminus;
    ParameterVector sqrtP_wk_min = P_wk_min.cwiseSqrt();
    Wkkminus.col(0) = wk_min;
    Wkkminus.col(1)  = wk_min + gamma * sqrtP_wk_min;
    Wkkminus.col(2)  = wk_min - gamma * sqrtP_wk_min;

    MMatrix Dkkminus;
    /*
     * TODO
     */

    ParameterVector dk_min;
    /*
     * TODO
     */


    //    // Update after measurement of d(k)
    cout << "Update UKF...... " << endl;



    cout << wk_min << endl ;
    cout << "..." << endl;
    cout << P_wk_min << endl ;
    cout << "..." << endl;
    cout << Wkkminus << endl;

    ParameterVector p = P_wk_min.cwiseSqrt();


    cout << "test UKF...... " << endl;
    // TestUKF testUkf;
    // testUkf.test();




}