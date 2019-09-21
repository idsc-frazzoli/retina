//
// Created by maximilien on 07.09.19.
//

#include <stdio.h>
#include <lcm/lcm-cpp.hpp>
#include <functional>

#include "mpcClient.h"
#include "../../../LCM/idsc/BinaryBlob.hpp"
#include "../../../../src_MATLAB/MPCGokart/shared_dynamic/c/definitions.c"
#include "../ModelMPC/modelDx.cpp"
#include "ModelObject.cpp"
#include "../UnscentedKalmanFilter.h"

using namespace std;

lcm::LCM lcmObj;

struct ControlRequestMsg lastCRMsg;
struct OnlineParam lastOnlineParam;

const int lookback = 10;
OnlineParam oParam[lookback];
int counter = 0;

struct PacejkaParameter pacejkaParameter;
idsc::BinaryBlob blob;

double ACCXmod;
double ACCYmod;
double ACCROTZmod;
double ACCXtrue = 0;
double ACCYtrue = 0;
double ACCROTZtrue = 9.81;

class Handler {
public:
    ~Handler(){}

    void handleState(const lcm::ReceiveBuffer *rbug, const std::string &chan, const idsc::BinaryBlob *msg){

        //printf("---Received CNS message on channel \"%s\"[%i]:\n", chan.c_str(), msg->data_length);

        memcpy(&lastCRMsg, msg->data.data(), msg->data_length);

        /*
        printf("time: %f\n", lastCRMsg.state.time);
        printf("X: %f\n", lastCRMsg.state.X);
        printf("Y: %f\n", lastCRMsg.state.Y);
        printf("Psi: %f\n", lastCRMsg.state.Psi);
        printf("Ux: %f\n", lastCRMsg.state.Ux);
        printf("Uy: %f\n", lastCRMsg.state.Uy);
        printf("dotPsi: %f\n", lastCRMsg.state.dotPsi);
        */
    }


    void handleOnline(const lcm::ReceiveBuffer *rbug, const std::string &chan, const idsc::BinaryBlob *msg) {

        printf("Received Online message on channel \"%s\":\n", chan.c_str());

        memcpy(&lastOnlineParam, msg->data.data(), msg->data_length);

        printf("time: %f\n", lastOnlineParam.time);
        printf("VX: %f\n", lastOnlineParam.vx);
        printf("VY: %f\n", lastOnlineParam.vy);
        printf("VROTZ: %f\n", lastOnlineParam.vrotz);
        printf("Beta: %f\n", lastOnlineParam.beta);
        printf("AB: %f\n", lastOnlineParam.ab);
        printf("TV: %f\n", lastOnlineParam.tv);

        /* //TODO MCP CHECK THIS CODE
        oParam[counter] = lastOnlineParam;
        ++counter;
        int size = (sizeof(oParam)/sizeof(*oParam));
        if (size >= lookback) {
            printf("starting to compute acc\n");
            ACCXtrue = 0;
            ACCYtrue = 0;
            for (int i = 0; i<size-1; ++i ) {
                OnlineParam prevOp = oParam[i];
                OnlineParam op = oParam[i+1];
                double deltaT = op.time - prevOp.time;
                double dvx = op.vx - prevOp.vx;
                double dvy = op.vy - prevOp.vy;
                ACCXtrue += dvx / deltaT;
                ACCYtrue += dvy / deltaT;
            }
            ACCXtrue = ACCXtrue/(double)size;
            ACCYtrue = ACCYtrue/(double)size;
        } else {
            ACCXtrue = 0;
            ACCYtrue = 0;
        }
        */

        ACCXtrue = 0.13;
        ACCYtrue = 0.4;

        printf("ACCX: %f\n", ACCXtrue);
        printf("ACCY: %f\n", ACCYtrue);
        printf("ACCROTZ: %f\n", ACCROTZtrue);

        UKF::MeasurementVec z;
        z << ACCXtrue, ACCYtrue, ACCROTZtrue;

        // inital guess
        double B1 = 9;
        double C1 = 1;
        double D1 = 10;
        double B2 = 5.2;
        double C2 = 1.1;
        double D2 = 10;
        double Cf = 0.3;
        double param[7] = {B1, C1, D1, B2, C2, D2, Cf};

        //UKF
        printf("starting ukf............\n");

        // init
        // *******************************************************************
        double q = 0.1; //std of process
        double r = 0.1; //std of measurement
        UKF::ParameterMat processCov = UKF::ParameterMat::Identity(); // cov of process
        UKF::MeasurementMat measureCov = UKF::MeasurementMat::Identity(); // cov of measurement

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
                = [](UKF::ParameterVec paramVec) {
                    UKF::MeasurementVec measurementVec;

                    double velx = lastOnlineParam.vx;
                    double vely = lastOnlineParam.vy;
                    double velrotz = lastOnlineParam.vrotz;
                    double BETA = lastOnlineParam.beta;
                    double AB = lastOnlineParam.ab;
                    double TV = lastOnlineParam.tv;

                    double ACCXmod;
                    double ACCYmod;
                    double ACCROTZmod;


                    const double paramIn[8] = {
                                         paramVec(0),
                                         paramVec(1),
                                         paramVec(2),
                                         paramVec(3),
                                         paramVec(4),
                                         paramVec(5),
                                         paramVec(6)};
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
                    measurementVec(0) = ACCXmod;
                    measurementVec(1) = ACCYmod;
                    measurementVec(2) = ACCROTZmod;

                    return measurementVec;
        };

        ukf.update(
                measureFunction,
                predictionFunction,
                measureCov,
                processCov,
                z);

        pacejkaParameter = {
                .B1 = (float) ukf.mean(0),
                .C1 = (float) ukf.mean(1),
                .D1 = (float) ukf.mean(2),
                .B2 = (float) ukf.mean(3),
                .C2 = (float) ukf.mean(4),
                .D2 = (float) ukf.mean(5),
        };


        printf("pacj param \n");
        printf("B1: %f\n", pacejkaParameter.B1);
        printf("C1: %f\n", pacejkaParameter.C1);
        printf("D1: %f\n", pacejkaParameter.D1);
        printf("B2: %f\n", pacejkaParameter.B2);
        printf("C2: %f\n", pacejkaParameter.C2);
        printf("D2: %f\n", pacejkaParameter.D2);

        blob.data_length = sizeof(pacejkaParameter);
        blob.data.resize(blob.data_length);
        memcpy(blob.data.data(),&pacejkaParameter, blob.data_length);

        //printf("lcmObj addr: %p\n",&lcmObj);
        //printf("blob addr: %p\n",&blob);

        lcmObj.publish("mpc.online.pacj.d", &blob);

    }
};

int main(int argc, char **argv)
{

    if(!lcmObj.good())
        return 1;

    Handler handler;

    printf("about to subscribe\n");
    lcmObj.subscribe("mpc.forces.gs.d", &Handler::handleState, &handler);
    lcmObj.subscribe("mpc.online.params.d", &Handler::handleOnline, &handler);
    printf("starting main loop\n");

    while(1) {
        lcmObj.handle();
    }

    return 0;
}