//
// Created by maximilien on 04.07.19.
// https://stackoverflow.com/questions/34247057/how-to-read-csv-file-and-assign-to-eigen-matrix
//

#include <string>
#include <vector>
#include <fstream>
#include <Eigen/Dense>

using namespace Eigen;

template <typename M>
M load_csv (const std::string & path) {

    std::ifstream indata;
    indata.open(path);
    if (indata.fail()){
        std::cout << "File non existing, manually create it" << std::endl;
    }
    std::string line;
    std::vector<double> values;
    uint rows = 0;
    while (std::getline(indata,line)){
        std::stringstream lineStream(line);
        std::string cell;
        while(std::getline(lineStream,cell, ',')){
            values.push_back(std::stod(cell));
        }
        ++rows;
    }
    return Map<const Matrix<typename
            M::Scalar,
            M::RowsAtCompileTime,
            M::ColsAtCompileTime,
            RowMajor>>(values.data(),rows, values.size()/rows);}

