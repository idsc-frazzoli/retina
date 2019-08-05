//
// Created by maximilien on 22.07.19.
//

/*
 * Academic License - for use in teaching, academic research, and meeting
 * course requirements at degree granting institutions only.  Not for
 * government, commercial, or other organizational use.
 *
 * ModelMPC.cpp
 *
 * Code generation for function 'ModelMPC'
 *
 */

/*************************************************************************/
/* This automatically generated example C++ main file shows how to call  */
/* entry-point functions that MATLAB Coder generated. You must customize */
/* this file for your application. Do not modify this file directly.     */
/* Instead, make a copy of this file, modify it, and integrate it into   */
/* your development environment.                                         */
/*                                                                       */
/* This file initializes entry-point function arguments to a default     */
/* size and value before calling the entry-point functions. It does      */
/* not store or use any values returned from the entry-point functions.  */
/* If necessary, it does pre-allocate memory for returned values.        */
/* You can use this file as a starting point for a main function that    */
/* you can deploy in your application.                                   */
/*                                                                       */
/* After you copy the file, and before you deploy it, you must make the  */
/* following changes:                                                    */
/* * For variable-size function arguments, change the example sizes to   */
/* the sizes that your application requires.                             */
/* * Change the example values of function arguments to the values that  */
/* your application requires.                                            */
/* * If the entry-point functions return values, store these values or   */
/* otherwise use them as required by your application.                   */
/*                                                                       */
/*************************************************************************/
/* Include files */
#include "modelDx.h"
#include <iostream>

/* Function Declarations */
static void argInit_1x8_real_T(double result[8]);
static double argInit_real_T();

/* Function Definitions */
static void argInit_1x8_real_T(double result[8])
{
    int idx1;

    /* Loop over the array to initialize each element. */
    for (idx1 = 0; idx1 < 8; idx1++) {
        /* Set the value of the array element.
           Change this value to the value that the application requires. */
        result[idx1] = argInit_real_T();
    }
}

static double argInit_real_T()
{
    return 0.0;
}

void call_modelDx()
{
    double dv0[8];
    double ACCX;
    double ACCY;
    double ACCROTZ;

    /* Initialize function 'modelDx' input arguments. */
    /* Initialize function input argument 'param'. */
    /* Call the entry-point 'modelDx'. */
    argInit_1x8_real_T(dv0);
    modelDx(argInit_real_T(), // VELX
            argInit_real_T(), // VELY
            argInit_real_T(), // VELROTZ
            argInit_real_T(), // BETA
            argInit_real_T(), // AB
            argInit_real_T(), // TV
            dv0, // pacejka param
            &ACCX, // ACCX
            &ACCY, // ACCY
            &ACCROTZ); // ACCROTZ


    std::cout << "****Init tester ****" << std::endl;
    std::cout << dv0[0] << std::endl;
    std::cout << ACCX << std::endl;
    std::cout << ACCY<< std::endl;
    std::cout << ACCROTZ << std::endl;

    /*
     * Should result in this:
     * dv0[0] = 0
     * ACCX = 0
     * ACCY = 0
     * ACCROTZ = -nan
     */


}

/* End of code generation (ModelMPC.cpp) */
