//
// Created by maximilien on 22.07.19.
//

/*
 * Academic License - for use in teaching, academic research, and meeting
 * course requirements at degree granting institutions only.  Not for
 * government, commercial, or other organizational use.
 *
 * modelDx.cpp
 *
 * Code generation for function 'modelDx'
 *
 */

/* Include files */
#include <cmath>
#include "modelDx.h"

/* Type Definitions */
    typedef struct {
    double tunableEnvironment[1];
} c_coder_internal_anonymous_func;

/* Function Declarations */
static double __anon_fcn(const double capfactor_tunableEnvironment[1], const
c_coder_internal_anonymous_func c_simplediraccy_tunableEnvironm[1], double
                         d_simplediraccy_tunableEnvironm, double e_simplediraccy_tunableEnvironm,
                         double f_simplediraccy_tunableEnvironm, double VELY, double VELX, double taccx);
static double b___anon_fcn(const double capfactor_tunableEnvironment[1], double
reg, double VELY, double VELX, double taccx);

/* Function Definitions */
static double __anon_fcn(const double capfactor_tunableEnvironment[1], const
c_coder_internal_anonymous_func c_simplediraccy_tunableEnvironm[1], double
                         d_simplediraccy_tunableEnvironm, double e_simplediraccy_tunableEnvironm,
                         double f_simplediraccy_tunableEnvironm, double VELY, double VELX, double taccx)
{
    double a;
    a = taccx / capfactor_tunableEnvironment[0];
    a *= a;

    /* 'satfun:2' l = 0.8; */
    /* 'satfun:3' r = 1-l; */
    /* 'satfun:4' if isa(x, 'double') */
    /* 'satfun:5' if(x<l) */
    if (!(a < 0.8)) {
        if (a < 1.2) {
            /* 'satfun:7' elseif(x<1+r) */
            /* 'satfun:8' d = (1+r-x)/r; */
            a = (1.2 - a) / 0.19999999999999996;

            /* 'satfun:9' y = 1-1/4*r*d^2; */
            a = 1.0 - 0.049999999999999989 * (a * a);
        } else {
            /* 'satfun:10' else */
            /* 'satfun:11' y = 1; */
            a = 1.0;
        }
    } else {
        /* 'satfun:6' y=x; */
    }

    /* 'satfun:13' y=0.95*y; */
    a *= 0.95;
    return std::sqrt(1.0 - a) * (f_simplediraccy_tunableEnvironm * std::sin
            (e_simplediraccy_tunableEnvironm * std::atan(d_simplediraccy_tunableEnvironm
                                                         * b___anon_fcn(c_simplediraccy_tunableEnvironm[0].tunableEnvironment, 0.5,
                                                                        VELY, VELX, taccx))));
}

static double b___anon_fcn(const double capfactor_tunableEnvironment[1], double
reg, double VELY, double VELX, double taccx)
{
    double a;
    a = taccx / capfactor_tunableEnvironment[0];
    a *= a;

    /* 'satfun:2' l = 0.8; */
    /* 'satfun:3' r = 1-l; */
    /* 'satfun:4' if isa(x, 'double') */
    /* 'satfun:5' if(x<l) */
    if (!(a < 0.8)) {
        if (a < 1.2) {
            /* 'satfun:7' elseif(x<1+r) */
            /* 'satfun:8' d = (1+r-x)/r; */
            a = (1.2 - a) / 0.19999999999999996;

            /* 'satfun:9' y = 1-1/4*r*d^2; */
            a = 1.0 - 0.049999999999999989 * (a * a);
        } else {
            /* 'satfun:10' else */
            /* 'satfun:11' y = 1; */
            a = 1.0;
        }
    } else {
        /* 'satfun:6' y=x; */
    }

    /* 'satfun:13' y=0.95*y; */
    a *= 0.95;
    return -(1.0 / std::sqrt(1.0 - a)) * VELY / (VELX + reg);
}

/*
 * function [ACCX,ACCY,ACCROTZ] = modelDx(VELX,VELY,VELROTZ,BETA,AB,TV, param)
 */
void modelDx(double VELX,
            double VELY,
            double VELROTZ,
            double BETA,
            double AB,
            double TV,
            const double param[8],
            double *ACCX,
            double *ACCY,
            double *ACCROTZ)
{
    double capfactor_tunableEnvironment[1];
    c_coder_internal_anonymous_func simpleslip_tunableEnvironment[1];
    double vel1_idx_1;
    double ACCROTZ_tmp;
    double VELX_idx_1;
    double d0;
    double d1;

    /*  BETA : Lenk winkel (control) */
    /*  AB : acceleration of hinterachse (control) */
    /*  TV : torque vectoring */
    /*  AB-TV rechte achse */
    /*  AB+TV linke achse */
    /* param = [B1,C1,D1,B2,C2,D2,Ic]; */
    /* 'modelDx:10' B1 = param(1); */
    /* 'modelDx:11' C1 = param(2); */
    /* 'modelDx:12' D1 = param(3); */
    /* 'modelDx:13' B2 = param(4); */
    /* 'modelDx:14' C2 = param(5); */
    /* 'modelDx:15' D2 = param(6); */
    /* 'modelDx:16' Ic = param(7); */
    /* Moment of inertia */
    /* maxA = param(8); */
    /* 'modelDx:18' magic = @(s,B,C,D)D.*sin(C.*atan(B.*s)); */
    /* 'modelDx:19' reg = 0.5; */
    /* 'modelDx:20' capfactor = @(taccx)(1-satfun((taccx/D2)^2))^(1/2); */
    capfactor_tunableEnvironment[0] = param[5];

    /* 'modelDx:21' simpleslip = @(VELY,VELX,taccx)-(1/capfactor(taccx))*VELY/(VELX+reg); */
    simpleslip_tunableEnvironment[0].tunableEnvironment[0] = param[5];

    /* simpleslip = @(VELY,VELX,taccx)-VELY/(VELX+reg); */
    /* 'modelDx:23' simplediraccy = @(VELY,VELX,taccx)magic(simpleslip(VELY,VELX,taccx),B2,C2,D2); */
    /* 'modelDx:24' simpleaccy = @(VELY,VELX,taccx)capfactor(taccx)*simplediraccy(VELY,VELX,taccx); */
    /* acclim = @(VELY,VELX, taccx)(VELX^2+VELY^2)*taccx^2-VELX^2*maxA^2; */
    /* 'modelDx:26' simplefaccy = @(VELY,VELX)magic(-VELY/(VELX+reg),B1,C1,D1); */
    /* simpleaccy = @(VELY,VELX,taccx)magic(-VELY/(VELX+reg),B2,C2,D2); */
    /* 'modelDx:31' l = 1.19; */
    /* 'modelDx:32' l1 = 0.73; */
    /* 'modelDx:33' l2 = l-l1; */
    /* 'modelDx:34' f1n = l2/l; */
    /* 'modelDx:35' f2n = l1/l; */
    /* 'modelDx:36' w = 1; */
    /* 'modelDx:37' rotmat = @(beta)[cos(beta),sin(beta);-sin(beta),cos(beta)]; */
    /* 'modelDx:38' vel1 = rotmat(BETA)*[VELX;VELY+l1*VELROTZ]; */
    vel1_idx_1 = std::cos(BETA);
    ACCROTZ_tmp = std::sin(BETA);
    VELX_idx_1 = VELY + 0.73 * VELROTZ;

    /* 'modelDx:39' f1y = simplefaccy(vel1(2),vel1(1)); */
    /* 'modelDx:40' F1 = rotmat(-BETA)*[0;f1y]*f1n; */
    d0 = std::cos(-BETA);
    d1 = std::sin(-BETA);
    VELX_idx_1 = param[2] * std::sin(param[1] * std::atan(param[0] *
                                                          (-(-ACCROTZ_tmp * VELX + vel1_idx_1 * VELX_idx_1) / ((vel1_idx_1 * VELX +
                                                                                                                ACCROTZ_tmp * VELX_idx_1) + 0.5))));
    vel1_idx_1 = (-d1 * 0.0 + d0 * VELX_idx_1) * 0.38655462184873951;

    /* 'modelDx:41' F1x = F1(1); */
    /* 'modelDx:42' F1y = F1(2); */
    /* 'modelDx:43' frontabcorr = F1x; */
    /* 'modelDx:44' F2x = AB; */
    /* 'modelDx:45' F2y1 = simpleaccy(VELY-l2*VELROTZ,VELX,(AB+TV/2)/f2n)*f2n/2; */
    /* 'modelDx:46' F2y2 = simpleaccy(VELY-l2*VELROTZ,VELX,(AB-TV/2)/f2n)*f2n/2; */
    /* 'modelDx:47' F2y = simpleaccy(VELY-l2*VELROTZ,VELX,AB/f2n)*f2n; */
    /* 'modelDx:48' TVTrq = TV*w; */
    /* 'modelDx:51' ACCROTZ = (TVTrq + F1y*l1 -F2y*l2)/Ic; */
    ACCROTZ_tmp = VELY - 0.45999999999999996 * VELROTZ;
    *ACCROTZ = ((TV + vel1_idx_1 * 0.73) - __anon_fcn(capfactor_tunableEnvironment,
                                                      simpleslip_tunableEnvironment, param[3], param[4], param[5], ACCROTZ_tmp,
                                                      VELX, AB / 0.61344537815126055) * 0.61344537815126055 * 0.45999999999999996)
               / param[6];

    /* ACCROTZ = TVTrq + F1y*l1; */
    /* 'modelDx:53' ACCX = F1x+F2x+VELROTZ*VELY; */
    *ACCX = ((d0 * 0.0 + d1 * VELX_idx_1) * 0.38655462184873951 + AB) + VELROTZ *
                                                                        VELY;

    /* 'modelDx:54' ACCY = F1y+F2y1+F2y2-VELROTZ*VELX; */
    *ACCY = ((vel1_idx_1 + __anon_fcn(capfactor_tunableEnvironment,
                                      simpleslip_tunableEnvironment, param[3], param[4], param[5],
                                      ACCROTZ_tmp, VELX, (AB + TV / 2.0) / 0.61344537815126055) *
                           0.61344537815126055 / 2.0) + __anon_fcn(capfactor_tunableEnvironment,
                                                                   simpleslip_tunableEnvironment, param[3], param[4], param[5],
                                                                   ACCROTZ_tmp, VELX, (AB - TV / 2.0) / 0.61344537815126055) *
                                                        0.61344537815126055 / 2.0) - VELROTZ * VELX;
}

/* End of code generation (modelDx.cpp) */
