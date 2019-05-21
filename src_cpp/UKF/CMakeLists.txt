cmake_minimum_required(VERSION 3.14)
project(UKF)

find_package (Eigen3 3.3 REQUIRED NO_MODULE)

set(CMAKE_CXX_STANDARD 14)

add_executable(UKF main.cpp)
target_link_libraries (UKF Eigen3::Eigen)
