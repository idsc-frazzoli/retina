/* 
 * CasADi to FORCES Template - missing information to be filled in by createCasadi.m 
 * (C) embotech AG, Zurich, Switzerland, 2013-19. All rights reserved.
 *
 * This file is part of the FORCES client, and carries the same license.
 */ 

#ifdef __cplusplus
extern "C" {
#endif
    
#include "MPCPathFollowing_stop/include/MPCPathFollowing_stop.h"    
    
/* prototyes for models */
extern void MPCPathFollowing_stop_model_1(const MPCPathFollowing_stop_float **arg, MPCPathFollowing_stop_float **res);
extern void MPCPathFollowing_stop_model_1_sparsity(solver_int32_default i, solver_int32_default *nrow, solver_int32_default *ncol, const solver_int32_default **colind, const solver_int32_default **row);
extern void MPCPathFollowing_stop_model_31(const MPCPathFollowing_stop_float **arg, MPCPathFollowing_stop_float **res);
extern void MPCPathFollowing_stop_model_31_sparsity(solver_int32_default i, solver_int32_default *nrow, solver_int32_default *ncol, const solver_int32_default **colind, const solver_int32_default **row);
    

/* copies data from sparse matrix into a dense one */
static void sparse2fullcopy(solver_int32_default nrow, solver_int32_default ncol, const solver_int32_default *colidx, const solver_int32_default *row, MPCPathFollowing_stop_float *data, MPCPathFollowing_stop_float *out)
{
    solver_int32_default i, j;
    
    /* copy data into dense matrix */
    for(i=0; i<ncol; i++)
    {
        for( j=colidx[i]; j < colidx[i+1]; j++ )
        {
            out[i*nrow + row[j]] = data[j];
        }
    }
}

/* CasADi - FORCES interface */
extern void MPCPathFollowing_stop_casadi2forces(MPCPathFollowing_stop_float *x,        /* primal vars                                         */
                                 MPCPathFollowing_stop_float *y,        /* eq. constraint multiplers                           */
                                 MPCPathFollowing_stop_float *l,        /* ineq. constraint multipliers                        */
                                 MPCPathFollowing_stop_float *p,        /* parameters                                          */
                                 MPCPathFollowing_stop_float *f,        /* objective function (scalar)                         */
                                 MPCPathFollowing_stop_float *nabla_f,  /* gradient of objective function                      */
                                 MPCPathFollowing_stop_float *c,        /* dynamics                                            */
                                 MPCPathFollowing_stop_float *nabla_c,  /* Jacobian of the dynamics (column major)             */
                                 MPCPathFollowing_stop_float *h,        /* inequality constraints                              */
                                 MPCPathFollowing_stop_float *nabla_h,  /* Jacobian of inequality constraints (column major)   */
                                 MPCPathFollowing_stop_float *hess,     /* Hessian (column major)                              */
                                 solver_int32_default stage,     /* stage number (0 indexed)                            */
								 solver_int32_default iteration /* iteration number of solver                          */)
{
    /* CasADi input and output arrays */
    const MPCPathFollowing_stop_float *in[4];
    MPCPathFollowing_stop_float *out[7];
    
    /* temporary storage for casadi sparse output */
    MPCPathFollowing_stop_float this_f;
    MPCPathFollowing_stop_float nabla_f_sparse[9];
    MPCPathFollowing_stop_float h_sparse[5];
    MPCPathFollowing_stop_float nabla_h_sparse[18];
    MPCPathFollowing_stop_float c_sparse[9];
    MPCPathFollowing_stop_float nabla_c_sparse[59];
            
    
    /* pointers to row and column info for 
     * column compressed format used by CasADi */
    solver_int32_default nrow, ncol;
    const solver_int32_default *colind, *row;
    
    /* set inputs for CasADi */
    in[0] = x;
    in[1] = p; /* maybe should be made conditional */
    in[2] = l; /* maybe should be made conditional */     
    in[3] = y; /* maybe should be made conditional */
    
    /* set outputs for CasADi */
    out[0] = &this_f;
    out[1] = nabla_f_sparse;
                
	 if ((stage >= 0 && stage < 30))
	 {
		 /* set inputs */
		 out[2] = h_sparse;
		 out[3] = nabla_h_sparse;
		 out[4] = c_sparse;
		 out[5] = nabla_c_sparse;
		 

		 /* call CasADi */
		 MPCPathFollowing_stop_model_1(in, out);

		 /* copy to dense */
		 if( nabla_f )
		 {
			 MPCPathFollowing_stop_model_1_sparsity(3, &nrow, &ncol, &colind, &row);
			 sparse2fullcopy(nrow, ncol, colind, row, nabla_f_sparse, nabla_f);
		 }
		 if( c )
		 {
			 MPCPathFollowing_stop_model_1_sparsity(6, &nrow, &ncol, &colind, &row);
			 sparse2fullcopy(nrow, ncol, colind, row, c_sparse, c);
		 }
		 if( nabla_c )
		 {
			 MPCPathFollowing_stop_model_1_sparsity(7, &nrow, &ncol, &colind, &row);
			 sparse2fullcopy(nrow, ncol, colind, row, nabla_c_sparse, nabla_c);
		 }
		 if( h )
		 {
			 MPCPathFollowing_stop_model_1_sparsity(4, &nrow, &ncol, &colind, &row);
			 sparse2fullcopy(nrow, ncol, colind, row, h_sparse, h);
		 }
		 if( nabla_h )
		 {
			 MPCPathFollowing_stop_model_1_sparsity(5, &nrow, &ncol, &colind, &row);
			 sparse2fullcopy(nrow, ncol, colind, row, nabla_h_sparse, nabla_h);
		 }
		 
	 }

	 if ((stage >= 30 && stage < 31))
	 {
		 /* set inputs */
		 out[2] = h_sparse;
		 out[3] = nabla_h_sparse;
		 /* call CasADi */
		 MPCPathFollowing_stop_model_31(in, out);

		 /* copy to dense */
		 if( nabla_f )
		 {
			 MPCPathFollowing_stop_model_31_sparsity(3, &nrow, &ncol, &colind, &row);
			 sparse2fullcopy(nrow, ncol, colind, row, nabla_f_sparse, nabla_f);
		 }
		 if( h )
		 {
			 MPCPathFollowing_stop_model_31_sparsity(4, &nrow, &ncol, &colind, &row);
			 sparse2fullcopy(nrow, ncol, colind, row, h_sparse, h);
		 }
		 if( nabla_h )
		 {
			 MPCPathFollowing_stop_model_31_sparsity(5, &nrow, &ncol, &colind, &row);
			 sparse2fullcopy(nrow, ncol, colind, row, nabla_h_sparse, nabla_h);
		 }
		 
	 }

         
    
    /* add to objective */
    if( f )
    {
        *f += this_f;
    }
}

#ifdef __cplusplus
} /* extern "C" */
#endif