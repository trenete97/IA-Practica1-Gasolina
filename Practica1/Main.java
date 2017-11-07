


import ProbGas.*;   //import de all lo de la carpeta ProbGas

//imports de aima

import aima.search.framework.Problem;
import aima.search.framework.GraphSearch;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;

//imports de java

import java.util.List;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class Main {
/* RESUMEN DE LAS DIFERNETES CONFIGURACIONES Y DONDE MODIFICARLAS
    *Modificar en este main*
        -Heuristica: solo tiene en cuenta beneficio de lo atendido
        -Heuristica2: parecida, pero tiene en cuenta las perdidas por peticiones no atendidas (supone que se atienden al dia siguiente)
        -HeuristicaBase3: como la base pero precio km multiplicado por 3
        -HeuristicaBase4: como la base pero precio km multiplicado por 4
        -HeuristicaLessDaysA: minimiza dias de las peticiones atendidas
        -HeuristicaLessDaysAN: minimiza dias de las peticiones NO atendidas
        -HeuristicaMoreDaysA: maximiza dias de las peticiones atendidas
        -HeuristicaMoreDaysAN: maximiza dias de las peticiones NO atendidas


        -SuccesorFunctionHC2: Para estados iniciales NO vacios (generacion por indices y inteligente)
        -SuccesorFuncionHC3: Para estado inicial vacio con puedemover(solo mueve si hay espacio)


    *Modificar en board (comentad o no una linea en la creadora, esta bien marcado cual es)*
        -Generacion del estado inicial: vacio(comentar las dos) o lleno(descomentar llamada a funcion sol_init a escojer)
 */



    public static void main(String[] args) throws Exception{






        //parametros para el problema
        int seed=464;//seed para generar el mapa
        int ngas=100;//numero de gasolineras
        int ncen=10;//numero de centros
        int mult=1;//multiplicidad, camiones por centro

        int heur=1;
        int useSA=1;
        int maxit=2000; //maximo de iteraciones para Simulated Annealing
        int iter=maxit/10; //iteraciones para cada paso
        //parametros de una funcion del SA, Pag 19 del pdf de la practica
        int k=5;
        double l=0.002;



        Scanner reader = new Scanner(System.in);  // Reading from System.in
        System.out.println("Select mode: 1 for auto, 2 for manual");
        int mode = reader.nextInt(); // Scans the next token of the input as an int.
        if (mode==2){
            System.out.println("Parametros generales : seed,ngas,ncen,mult");
            seed=reader.nextInt();
            ngas=reader.nextInt();
            ncen=reader.nextInt();
            mult=reader.nextInt();
            System.out.println("Algoritmo de busqueda:1 HC, 2 SA");
            useSA=reader.nextInt()-1;
            System.out.println("Heuristico: 0 info, 1-8 seleccion de heuristico");
             heur=reader.nextInt();
            if (heur==0){
                System.out.println("1- Heuristica: solo tiene en cuenta beneficio de lo atendido");
                System.out.println("2- Heuristica2: parecida, pero tiene en cuenta las perdidas por peticiones no atendidas (supone que se atienden al dia siguiente)");
                System.out.println("3- HeuristicaBase3: como la base pero precio km multiplicado por 3");
                System.out.println("4- HeuristicaBase4: como la base pero precio km multiplicado por 4");
                System.out.println("5- HeuristicaLessDaysA: minimiza dias de las peticiones atendidas");
                System.out.println("6- HeuristicaLessDaysAN: minimiza dias de las peticiones NO atendidas");
                System.out.println("7- HeuristicaMoreDaysA: maximiza dias de las peticiones atendidas");
                System.out.println("8- HeuristicaMoreDaysAN: maximiza dias de las peticiones NO atendidas");
                System.out.println("\nHeuristico:  1-8 seleccion de heuristico");
                heur=reader.nextInt();
            }

        }


        double tini = System.currentTimeMillis();
        ProbGasBoard board=new ProbGasBoard(seed,ngas,ncen,mult);
        ProbGasHeuristicFunction phi=new ProbGasHeuristicFunction();
        double beneficiIni=phi.getHeuristicValue(board);
        System.out.println("BeneficiInicial:"+ -beneficiIni);
        //Crear problema
        Problem p;
        Search alg;

        if (useSA==1){
            switch(heur) {
                case 1:
                p = new Problem(board, new ProbGasSuccesorFunctionSA(), new ProbGasGoalTest(), new ProbGasHeuristicFunction());
                    break;
                case 2:
                    p = new Problem(board, new ProbGasSuccesorFunctionSA(), new ProbGasGoalTest(), new ProbGasHeuristicFunction2());
                    break;
                case 3:
                    p = new Problem(board, new ProbGasSuccesorFunctionSA(), new ProbGasGoalTest(), new ProbGasHeuristicFunctionBase3());
                    break;

                case 4:
                    p = new Problem(board, new ProbGasSuccesorFunctionSA(), new ProbGasGoalTest(), new ProbGasHeuristicFunctionBase4());
                    break;
                case 5:
                    p = new Problem(board, new ProbGasSuccesorFunctionSA(), new ProbGasGoalTest(), new ProbGasHeuristicFunctionLessDaysA());
                    break;

                case 6:
                    p = new Problem(board, new ProbGasSuccesorFunctionSA(), new ProbGasGoalTest(), new ProbGasHeuristicFunctionLessDaysNA());
                    break;

                case 7:
                    p = new Problem(board, new ProbGasSuccesorFunctionSA(), new ProbGasGoalTest(), new ProbGasHeuristicFunctionMoreDaysA());
                    break;
                default:
                    p = new Problem(board, new ProbGasSuccesorFunctionSA(), new ProbGasGoalTest(), new ProbGasHeuristicFunctionMoreDaysNA());
                    break;

            }
            alg =new SimulatedAnnealingSearch(maxit,iter,k,l);
        }
        else {
            switch(heur) {
                case 1:
                    p = new Problem(board, new ProbGasSuccesorFunctionHC2(), new ProbGasGoalTest(), new ProbGasHeuristicFunction());
                    break;
                case 2:
                    p = new Problem(board, new ProbGasSuccesorFunctionHC2(), new ProbGasGoalTest(), new ProbGasHeuristicFunction2());
                    break;
                case 3:
                    p = new Problem(board, new ProbGasSuccesorFunctionHC2(), new ProbGasGoalTest(), new ProbGasHeuristicFunctionBase3());
                    break;

                case 4:
                    p = new Problem(board, new ProbGasSuccesorFunctionHC2(), new ProbGasGoalTest(), new ProbGasHeuristicFunctionBase4());
                    break;
                case 5:
                    p = new Problem(board, new ProbGasSuccesorFunctionHC2(), new ProbGasGoalTest(), new ProbGasHeuristicFunctionLessDaysA());
                    break;

                case 6:
                    p = new Problem(board, new ProbGasSuccesorFunctionHC2(), new ProbGasGoalTest(), new ProbGasHeuristicFunctionLessDaysNA());
                    break;

                case 7:
                    p = new Problem(board, new ProbGasSuccesorFunctionHC2(), new ProbGasGoalTest(), new ProbGasHeuristicFunctionMoreDaysA());
                    break;
                default:
                    p = new Problem(board, new ProbGasSuccesorFunctionHC2(), new ProbGasGoalTest(), new ProbGasHeuristicFunctionMoreDaysNA());
                    break;

            }
            alg =new HillClimbingSearch();
        }

        //sin parametros

        SearchAgent agent = new SearchAgent(p, alg);

        System.out.println();
        if (useSA==0)  printActions(agent.getActions());
        printInstrumentation(agent.getInstrumentation());
        System.out.println("Total time(s):"+(System.currentTimeMillis()-tini)/1000);

        board = (ProbGasBoard) alg.getGoalState();
        ProbGasHeuristicFunction ph = new ProbGasHeuristicFunction();
        double benefici = ph.getHeuristicValue(board);
        System.out.println("Benefici:"+ -benefici);

        double kmrecorridos = board.kmrecorridos();
        System.out.println("Kms:"+ kmrecorridos);

        int peticionesatendidas = board.getNumPet();
        System.out.println("PeticionesAtendidas:"+ peticionesatendidas);
    }

    private static void printInstrumentation(Properties properties) {
        Iterator keys = properties.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String property = properties.getProperty(key);
            System.out.println(key + " : " + property);
        }

    }

    private static void printActions(List actions) {
        for (int i = 0; i < actions.size(); i++) {
            String action = (String) actions.get(i);
            System.out.println(action);
        }
    }



}
