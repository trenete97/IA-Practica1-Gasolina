package ProbGas;

import IA.Gasolina.Gasolineras;
import ProbGas.ProbGasBoard.Camiones;

import java.util.ArrayList;

import IA.Gasolina.Gasolinera;
import aima.search.framework.HeuristicFunction;

//modificacion de la primera heuristica para que descuente lo perdido por las peticiones no atendidas
//como dice el enunciado, la heuristica supone que una peticion no atendida se atenderá al dia siguiente

public class ProbGasHeuristicFunction2 implements HeuristicFunction {
    public double getHeuristicValue(Object n){
        ProbGasBoard board = (ProbGasBoard) n;
        int[][] pet = board.getPet(); //obtenemos las peticionesv
        Camiones[] camiones = board.getCam(); //obtenemos los camiones
        double preciot = 0; //precio total
        double precio = 0; //precio por peticion;
        Gasolineras gas = board.getGas(); //obtenemos las gasolineras
        double kmtotales = 0;
    		/*
    		 * A recordar: cada cisterna que acude a una gasolinera y atiende una peticion nos otorga un beneficio de 1000 y cada km recorrido cuesta 2
    		 * Además, cada dia que pasa desde que la peticion está en la gasolinera rebaja el precio de la cisterna. Si no han pasado dias se cobra el 102% del precio normal
    		 */
        for(int i = 0;i<gas.size();++i) //obtenemos las peticiones para hacerles el descuentillo
        {
            Gasolinera g = gas.get(i); //obtenemos la gasolinera i
            ArrayList<Integer> p = g.getPeticiones(); //obtenemos sus peticiones
            for(int j = 0;j<pet[i].length;++j)
            {
                int dias = p.get(j); //en la peticion j está guardada la cantidad de dias que han pasado desde que fue atendida
                double porcentaje;
                if(dias!=0) porcentaje = 100 - Math.pow(2, dias); //el porcentaje que se cobra de menos es 100 - 2^dias
                else porcentaje = 102;

                if(pet[i][j]!=-1) //si la peticion j de la gasolinera i ha sido atendida
                {
                    precio += 1000*(porcentaje/100); //como es una peticion atendida, le sumamos lo que vale con la rebaja de porcentaje
                }
                else{
                    precio -= 1000*(porcentaje/100);
                    //ahora calculo el porcentaje para el dia siguiente
                    porcentaje = 100 - Math.pow(2, (dias+1)); //dias+1 >=1
                    //el resultado es restar lo que no he ganado hoy,y sumarle lo que ganaria mañana, es decir
                    //he restado lo que pierdo por no atender la peticion hoy
                    precio += 1000*(porcentaje/100);

                }
            }
        }
        for(int i = 0;i<camiones.length;++i) //obtenemos todos los km recorridos para atender cada petición
        {
            kmtotales += camiones[i].kmtotal;
        }

        preciot = precio  - kmtotales*2;
        //System.out.println( "pbenefici = " + preciot);
        return -preciot;
    }
}
