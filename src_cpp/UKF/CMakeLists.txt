cmake_minimum_required(VERSION 3.14)
project(UKF)

find_package(Eigen3 3.3 REQUIRED NO_MODULE)

set(CMAKE_CXX_STANDARD 14)

add_executable(
        UKF src/main.cpp
        src/UnscentedKalmanFilter.h
        src/Test/TestPacejkaUKF.cpp
        src/Test/TestPacejkaUKF.h
        src/Test/TestUKF.cpp
        src/InputOutput/WriterUKF.cpp
        src/InputOutput/WriterUKF.h
)

target_link_libraries (UKF Eigen3::Eigen)