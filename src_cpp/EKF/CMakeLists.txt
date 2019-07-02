cmake_minimum_required(VERSION 3.14)
project(EKF)

find_package(Eigen3 3.3 REQUIRED NO_MODULE)

set(CMAKE_CXX_STANDARD 14)

add_executable(
        EKF main.cpp
        src/ExtendedKalmanFilter.h
        src/TestPacejkaEKF.cpp
        src/TestPacejkaEKF.h
        src/WriterEKF.cpp
        src/WriterEKF.h
)

target_link_libraries (EKF Eigen3::Eigen)