package ProbGas;

import IA.Gasolina.Gasolineras;
import ProbGas.ProbGasBoard.Camiones;

import java.util.ArrayList;

import IA.Gasolina.Gasolinera;
import aima.search.framework.HeuristicFunction;
//HeuristicaBase3: como la base pero precio km multiplicado por 3
public class ProbGasHeuristicFunctionBase3 implements HeuristicFunction {
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
    		 * Ademas, cada dia que pasa desde que la peticion esta en la gasolinera rebaja el precio de la cisterna. Si no han pasado dias se cobra el 102% del precio normal
    		 */
		for(int i = 0;i<gas.size();++i) //obtenemos las peticiones para hacerles el descuentillo
		{
			Gasolinera g = gas.get(i); //obtenemos la gasolinera i
			ArrayList<Integer> p = g.getPeticiones(); //obtenemos sus peticiones
			for(int j = 0;j<pet[i].length;++j)
			{
				if(pet[i][j]!=-1) //si la peticion j de la gasolinera i ha sido atendida
				{
					int dias = p.get(j); //en la peticion j esta guardada la cantidad de dias que han pasado desde que fue atendida
					double porcentaje;
					if(dias!=0) porcentaje = 100 - Math.pow(2, dias); //el porcentaje que se cobra de menos es 100 - 2^dias
					else porcentaje = 102;
					precio += 1000*(porcentaje/100); //como es una peticion atendida, le sumamos lo que vale con la rebaja de porcentaje
				}
			}
		}
		for(int i = 0;i<camiones.length;++i) //obtenemos todos los km recorridos para atender cada peticion
		{
			kmtotales += camiones[i].kmtotal;
		}

		preciot = precio  - kmtotales*3;
		//System.out.println( "pbenefici = " + preciot);
		return -preciot;
	}
}