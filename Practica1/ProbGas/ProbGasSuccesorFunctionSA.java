package ProbGas;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProbGasSuccesorFunctionSA implements SuccessorFunction {

    public List getSuccessors(Object state){
        ArrayList retval = new ArrayList();
        ProbGasBoard PGBoard = (ProbGasBoard) state;
        ProbGasBoard PGBoardaux;
        ArrayList<ArrayList<Integer>> opt=new ArrayList<>();
        int ngasolineras = PGBoard.getNumGas();
        int ncamiones = PGBoard.getNumCam();
        for (int k = 0; k < ncamiones; ++k){ //para cada camion
            for (int l = 0; l <= 9; ++l){ //para cada peticion del camion k
                for (int i = 0; i < ngasolineras; ++i){ //para cada gasolinera
                    int npeticiones = PGBoard.getNumPetGas(i);
                    for (int j = 0; j < npeticiones; ++j){//por cada peticion de la gasolinera i
                        if (PGBoard.getAsocPet(i,j)== -1 && PGBoard.puedemover2(k, l,i)) { // mueve si la peticion no esta atendida
                            ArrayList<Integer> ai=new ArrayList<>(4);
                            ai.add(k); ai.add(l); ai.add(i); ai.add(j);
                            opt.add(ai);


                        }
                    }
                }

            }
        }
        Random r=new Random();
        int f=r.nextInt(opt.size());
        PGBoardaux = new ProbGasBoard(PGBoard);
        ArrayList<Integer> fin=opt.get(f);
        PGBoardaux.mover(fin.get(0),fin.get(1),fin.get(2),fin.get(3));
        retval.add(new Successor("Mover peticion " + fin.get(1) + " del camion " + fin.get(0) +
                " por peticion " +  fin.get(3) + " de la gasolinera " +  fin.get(2) + "\n", PGBoardaux));
        //System.out.println( "tamany = " + retval.size());
        return retval;
    }
}
