cmake_minimum_required (VERSION 2.6)
project (urg_provider)

# here define the install folder if you have installed in a non default location
#set(URG_INSTALL_FOLDER /home/jelavice/Programs/urg)

if (URG_INSTALL_FOLDER)
else()
	set(URG_INSTALL_FOLDER /usr/local)
endif()

set(URG_HEADERS ${URG_INSTALL_FOLDER}/include/urg_c)
include_directories( include
		     ${URG_HEADERS})

message(STATUS "urg isntall folder is ${URG_INSTALL_FOLDER}")

#compiler flags
set( CMAKE_C_FLAGS  "${CMAKE_C_FLAGS} -fPIC -Wall" )
#set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} -L/usr/lib32 -lm")

# will link against static libraries
SET(CMAKE_FIND_LIBRARY_SUFFIXES ".a")
SET(BUILD_SHARED_LIBRARIES OFF)
SET(CMAKE_EXE_LINKER_FLAGS "-static")


find_library(URG_LIBRARIES NAMES liburg_c.a
			   PATHS ${URG_INSTALL_FOLDER}/lib)

add_library(open_urg_sensor src/open_urg_sensor.c)
target_link_libraries(open_urg_sensor ${URG_LIBRARIES})

add_executable(urg_provider src/urg_provider.c)
target_link_libraries(urg_provider ${URG_LIBRARIES} open_urg_sensor m)	#m is linking against math library

