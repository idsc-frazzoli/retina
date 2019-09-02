cmake_minimum_required(VERSION 3.14)
project(UKF)

find_package(Eigen3 3.3 REQUIRED NO_MODULE)

set(CMAKE_CXX_STANDARD 14)

add_executable(
        UKF src/main.cpp
        src/UnscentedKalmanFilter.h
        src/TestUKF/TestPacejkaUKF.cpp
        src/TestUKF/TestPacejkaUKF.h
        src/TestUKF/TestUKF.cpp
        src/InputOutput/WriterUKF.cpp
        src/InputOutput/WriterUKF.h
        src/ModelSimple/Model_first.cpp
        src/ModelSimple/Model_first.h
        src/main.h
        src/ModelMPC/ModelMPC.cpp
        src/ModelMPC/ModelMPC.h
        src/ModelMPC/modelDx.cpp
        src/ModelMPC/modelDx.h
        src/ModelMPC/StaticTester.cpp
        src/ModelMPC/StaticTester.h
        src/ModelMPC/LogTester.cpp
        src/ModelMPC/LogTester.h
        src/PacejkaUKF/PacejkaUKFinterface.cpp
        src/PacejkaUKF/PacejkaUKFinterface.h
        src/LCM/static_sender.cpp
        src/LCM/static_sender.h
        src/LCM/BinaryBlob.hpp)

target_link_libraries (UKF Eigen3::Eigen)