//
// Created by maximilien on 07.09.19.
//

#include <stdio.h>
#include <lcm/lcm-cpp.hpp>

#include "mpcClient.h"
#include "../../../LCM/idsc/BinaryBlob.hpp"
#include "../../../../src_MATLAB/MPCGokart/shared_dynamic/c/definitions.c"
#include "../ModelMPC/modelDx.cpp"
#include "ModelObject.cpp"
#include "../UnscentedKalmanFilter.h"
#include <functional>

lcm::LCM lcmObj;

struct ControlRequestMsg lastCRMsg;
struct OnlineParam lastOnlineParam;

const int lookback = 10;
std::array<OnlineParam, lookback> arrOnlineParams;

struct PacejkaParameter pacejkaParameter;
idsc::BinaryBlob blob;

double ACCXmod;
double ACCYmod;
double ACCROTZmod;

class Handler {
public:
    ~Handler(){}
    void handleState(const lcm::ReceiveBuffer *rbug, const std::string &chan, const idsc::BinaryBlob *msg){

        printf("Received CNS message on channel \"%s\":\n", chan.c_str());
        memcpy(&lastCRMsg, &msg, msg->data_length);

        printf("time: %f\n", lastCRMsg.state.time);
        printf("X: %f\n", lastCRMsg.state.X);
        printf("Y: %f\n", lastCRMsg.state.Y);
        printf("Psi: %f\n", lastCRMsg.state.Psi);

        printf("Ux: %f\n", lastCRMsg.state.Ux);
        printf("Uy: %f\n", lastCRMsg.state.Uy);
        printf("dotPsi: %f\n", lastCRMsg.state.dotPsi);
    }


    void handleOnline(const lcm::ReceiveBuffer *rbug, const std::string &chan, const idsc::BinaryBlob *msg) {

        printf("Received Online message on channel \"%s\":\n", chan.c_str());
        memcpy(&lastOnlineParam, &msg, msg->data_length);

        printf("time: %f\n", lastOnlineParam.time);
        printf("VX: %f\n", lastOnlineParam.vx);
        printf("VY: %f\n", lastOnlineParam.vy);
        printf("VPsi: %f\n", lastOnlineParam.vpsi);
        printf("Ux: %f\n", lastOnlineParam.beta);
        printf("Uy: %f\n", lastOnlineParam.ab);
        printf("dotPsi: %f\n", lastOnlineParam.tv);

        /* TODO REPLACE COMPUTATION WITH LIST
        arrOnlineParams.push_front(lastOnlineParam);
        int size = arrOnlineParams.size();
        if (size >= lookback) {
            printf("starting to compute acc\n");
            ACCX = 0;
            ACCY = 0;
            OnlineParam prevOp;
            OnlineParam op;
            for (int i = 1; i < size; ++i) {
                prevOp = arrOnlineParams[i-1];
                double deltaT = op.time - op.time;
                double dvx = op.vx - prevOp.vx;
                double dvy = op.vy - prevOp.vy;
                ACCX += dvx / deltaT;
                ACCY += dvy / deltaT;
            }
            ACCX = ACCX/(double)size;
            ACCY = ACCY/(double)size;
        }

        printf("ACCX: %f\n", ACCX);
        printf("ACCY: %f\n", ACCY);
        printf("ACCROTZ: %f\n", ACCROTZ);
        */


        //UKF
        printf("starting ukf\n");

        // inital guess
        double const B1 = 9;
        double C1 = 1;
        double D1 = 10;
        double B2 = 5.2;
        double C2 = 1.1;
        double D2 = 10;
        double Cf = 0.3;
        double param[8] = {B1, C1, D1, B2, C2, D2, Cf};


        // init
        // *******************************************************************
        double q = 0.1; //std of process
        double r = 0.1; //std of measurement
        UKF::ParameterMat processCov = UKF::ParameterMat::Identity() * q; // cov of process
        UKF::MeasurementMat measureCov = UKF::MeasurementMat::Identity() * r; // cov of measurement

        UKF::ParameterVec x; //initial state
        x <<  B1, C1, D1,  B2, C2, D2,  Cf;
        UKF::ParameterMat P = UKF::ParameterMat::Identity(); //inital state cov

        //UKF
        UKF ukf = UKF(x, P);

        //functions
        // *******************************************************************
        std::function<UKF::ParameterVec(UKF::ParameterVec)> predictionFunction
                = [](UKF::ParameterVec parameterVec){

                    // Identity (no info available on dynamics of Factors)

                    return parameterVec;
            };

        // measurement
        std::function<UKF::MeasurementVec(UKF::ParameterVec)> measureFunction
                = [](UKF::ParameterVec param) {
                    UKF::MeasurementVec measurementVec;

                    double velx = lastOnlineParam.vx;
                    double vely = lastOnlineParam.vy;
                    double velrotz = lastOnlineParam.vpsi;

                    printf("Ux: %f\n", lastOnlineParam.beta);
                    printf("Uy: %f\n", lastOnlineParam.ab);
                    printf("dotPsi: %f\n", lastOnlineParam.tv);

                    double ACCXmod;
                    double ACCYmod;
                    double ACCROTZmod;

                    //assume these are constant
                    double BETA = lastOnlineParam.beta;
                    double AB = lastOnlineParam.ab;
                    double TV = lastOnlineParam.tv;
                    const double paramIn[8] = {param(0),
                                         param(1),
                                         param(2),
                                         param(3),
                                         param(4),
                                         param(5),
                                         param(6),
                                         0};

                    modelDx(velx,
                            vely,
                            velrotz,
                            BETA,
                            AB,
                            TV,
                            paramIn,
                            &ACCXmod,
                            &ACCYmod,
                            &ACCROTZmod);

                    measurementVec(0,0) = ACCXmod;
                    measurementVec(1,0) = ACCYmod;
                    measurementVec(2, 0) = ACCROTZmod;
                    return measurementVec;
                };

        std::cout << "pacj param " << std::endl;

        pacejkaParameter = {
                .B1 = (float) 9.0,
                .C1 = (float) 1.0,
                .D1 = (float) 10.0,
                .B2 = (float) 9.0,
                .C2 = (float) 1.0,
                .D2 = (float) 10.0,
        };

        blob.data_length = 6*4;
        blob.data.resize(blob.data_length);
        memcpy(&blob.data[0],&pacejkaParameter,6*4);

        printf("lcmObj addr: %p\n",&lcmObj);
        printf("blob addr: %p\n",&blob);

        lcmObj.publish("mpc.forces.pacj.d", &blob);

    }
};

int main(int argc, char **argv)
{

    if(!lcmObj.good())
        return 1;

    Handler handler;

    printf("about to subscribe\n");
    lcmObj.subscribe("mpc.forces.gs.d", &Handler::handleState, &handler);
    lcmObj.subscribe("online.params.d", &Handler::handleOnline, &handler);
    printf("starting main loop\n");

    while(1) {
        lcmObj.handle();
    }

    return 0;
}