function [ACCX,ACCY,ACCROTZ] = nomodel_0x6_sparse_poly3_order3(VX,VY,VTHETA,BETA,AB,TV,param)
    ACCX = 0.5648834109306335 * (sin(AB/6.7*pi/2.0) - 0.017419083821411975) / 0.1785014463024927 + 0.4587765634059906 * ((VY) * (VTHETA) * (cos(AB/6.7*pi/2.0)) - -0.0034241363837385448) / 0.4297752996153552 + 0.14884346723556519 * ((AB) * (cos(BETA/0.44*pi/2.0)) * (cos(TV/2.1*pi/2.0)) - 0.06667145440980085) / 0.6568028772547091 + 0.003673602594062686;
    ACCY = 0.40896862745285034 * ((BETA) * (sin(VX/10.0*pi/2.0)) * (cos(TV/2.1*pi/2.0)) - -0.00658344568162781) / 0.06916596921843254 + -0.1548536717891693 * ((VY) * (cos(VX/10.0*pi/2.0)) * (cos(AB/6.7*pi/2.0)) - -0.003290577280095432) / 0.21026023128849894 + -0.5412811636924744 * (((2^VX)) * (cos(VX/10.0*pi/2.0)) * (tan(BETA/0.44*pi/3.0)) - -0.6083722018073039) / 9.481076711683418 + 0.002542872680351138;
    ACCROTZ = 1.2758663892745972 * ((VX) * (sin(BETA/0.44*pi/2.0)) * (cos(AB/6.7*pi/2.0)) - -0.17553810801460182) / 1.772763941713415 + -0.8191916346549988 * ((VTHETA) * (cos(AB/6.7*pi/2.0)) - -0.047068208364958584) / 0.5338412523843211 + -0.4189247786998749 * ((VTHETA) * (cos(BETA/0.44*pi/2.0)) * (cos(AB/6.7*pi/2.0)) - -0.024533058035904626) / 0.3227320389025673 + 0.0010417811572551727;
end

