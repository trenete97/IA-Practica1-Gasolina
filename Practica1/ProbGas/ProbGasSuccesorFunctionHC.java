
package ProbGas;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.List;

//Succesora para estados iniciales VACIOS y PUEDEMOVER (solo mueve si hay espacio)
public class ProbGasSuccesorFunctionHC implements SuccessorFunction {
    public List getSuccessors(Object state){
        ArrayList retval = new ArrayList();
        ProbGasBoard PGBoard = (ProbGasBoard) state;
        ProbGasBoard PGBoardaux;
        int ngasolineras = PGBoard.getNumGas();
        int ncamiones = PGBoard.getNumCam();
        for (int k = 0; k < ncamiones; ++k){ //para cada camion
            for (int l = 0; l <= 9; ++l){ //para cada peticion del camion k
                for (int i = 0; i < ngasolineras; ++i){ //para cada gasolinera
                    int npeticiones = PGBoard.getNumPetGas(i);
                    for (int j = 0; j < npeticiones; ++j){//por cada peticion de la gasolinera i
                        if (PGBoard.getAsocPet(i,j)== -1 && PGBoard.puedemover(k, l,i)) { // mueve si la peticion no esta atendida
                            PGBoardaux = new ProbGasBoard(PGBoard);
                            PGBoardaux.mover(k, l, i, j); //mover peticion l del camion k por peticion j de la gasolinera i
                            retval.add(new Successor("Mover peticion " + String.valueOf(l) + " del camion " + String.valueOf(k) +
                                    " por peticion " + String.valueOf(j) + " de la gasolinera " + String.valueOf(i) + "\n", PGBoardaux));
                        }
                    }
                }
            }
        }
        //System.out.println( "tamany = " + retval.size());
        return retval;
    }
}
