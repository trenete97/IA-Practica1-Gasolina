
Para compilar y ejecutar la practica, ejecutad los siguientes comandos
en una carpeta que contenga el main, los jar de Gasolina y AIMA
y la carpeta ProbGas con el resto de clases del proyecto

La practica tiene una interfaz por consola muy simple, donde elegir
si ejecutar con los parametros definidos en el codigo (modo automatico)
o si dar desde consola parametros,algoritmo y heuristico


javac Main.java -cp Gasolina.jar:AIMA.jar ProbGas/*.java

java -cp  Gasolina.jar:AIMA.jar:ProbGas/*:. Main
