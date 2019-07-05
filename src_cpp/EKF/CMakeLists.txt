cmake_minimum_required(VERSION 3.14)
project(EKF)

find_package(Eigen3 3.3 REQUIRED NO_MODULE)

set(CMAKE_CXX_STANDARD 14)

add_executable(
        EKF src/main.cpp
        src/ExtendedKalmanFilter.h
        src/Test/TestPacejkaEKF.cpp
        src/Test/TestPacejkaEKF.h
        src/InputOutput/WriterEKF.cpp
        src/InputOutput/WriterEKF.h
        src/InputOutput/ReaderCSV.cpp src/InputOutput/ReaderCSV.h)

target_link_libraries (EKF Eigen3::Eigen)