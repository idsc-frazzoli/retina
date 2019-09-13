//
// Created by maximilien on 12.09.19.
//

#include "../ModelMPC/modelDx.h"

class ModelObject {
private:
    double ACCX;
    double ACCY;
    double ACCROTZ;
public:
    ModelObject(double velx, // VELX
                double vely, // VELY
                double velrotz, // VELROTZ
                double BETA, // BETA
                double AB, // AB
                double TV, // TV
                double paramIn[8]) // pacejka param
    {
        modelDx(velx,
                vely,
                velrotz,
                BETA,
                AB,
                TV,
                paramIn,
                &ACCX,
                &ACCY,
                &ACCROTZ
        );
    }

    double getX(){
        return ACCX;
    }

    double getY(){
        return ACCY;
    }

    double getZ(){
        return ACCROTZ;
    }


};
