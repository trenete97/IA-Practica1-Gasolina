package ProbGas;

import IA.Gasolina.Gasolineras;
import ProbGas.ProbGasBoard.Camiones;

import java.util.ArrayList;

import IA.Gasolina.Gasolinera;
import aima.search.framework.HeuristicFunction;
//Heuristica que prioriza que se atiendan las peticiones con menos dias
public class ProbGasHeuristicFunctionLessDaysNA implements HeuristicFunction {
	public double getHeuristicValue(Object n)
	{
		ProbGasBoard PGBoard = (ProbGasBoard) n;
		Gasolineras gas = PGBoard.getGas();
		int[][] pet = PGBoard.getPet();
		double days = 0;
		for(int i = 0; i < pet.length; ++i) //por cada gasolinera
		{
			for(int j = 0; j < pet[i].length ; ++j ) //por cada peticion de la gasolinera i
			{
				if(pet[i][j]==-1) //si la peticion no esta atendida
				{
					days += gas.get(i).getPeticiones().get(j); //obtiene los dias que lleva la peticion j de la gasolinera i sin atender
				}
			}
		}
		return days;
	}
}