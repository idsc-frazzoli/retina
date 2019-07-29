//
// Created by maximilien on 01.07.19.
//

#include "WriterEKF.h"
#include <iostream>
#include <fstream>
#include <functional>
#include <stdlib.h>
#include <time.h>
#include <Eigen/Dense>

using namespace std;
using namespace Eigen;

const static IOFormat CSVFormat(StreamPrecision, DontAlignCols, ", ", "\n");

void WriterEKF::writeToCSV(string name, Eigen::MatrixXd matrix){
    ofstream outfile (name.c_str());
    if (outfile.is_open()){
        outfile << matrix.format(CSVFormat);
    }
    outfile.close();
}