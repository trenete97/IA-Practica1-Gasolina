package ProbGas;

import IA.Gasolina.Gasolineras;
import IA.Gasolina.Gasolinera;

import IA.Gasolina.CentrosDistribucion;
import IA.Gasolina.Distribucion;
//import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORB;

import java.lang.reflect.Array;
import java.util.ArrayList;
import static java.lang.Math.abs;
//import java.lang.Cloneable;
public class ProbGasBoard {

    //nombres por similitud con c++
    private class Pair{
        public int first,second; //en un camion: first y second representan respectivamente ngasolinera y npeticion de Gasolineras
        public Pair (int f,int s){first=f;second=s;}
        public boolean  equals(Pair p){
            return this.first==p.first && this.second==p.second ;
        }
        public Pair(){}
    }

    public class Camiones{
        public double kmtotal;
        public double[] kmviaje; // cada i se corresponde con su correspondiente i (gasolineras) en peticiones
        public Pair [] peticiones; //first = gasolinera, second = peticion de esa gasolinera

    }
    /* atributos estaticos
       no hay forma de modificar las clases que ellos te dan, asi que van a ser estaticas
     */
    private static Gasolineras gasolineras;
    private static CentrosDistribucion centros;
    private Camiones[] camiones;

    private int[][] peticiones; //peticiones de todas las gasolineras en vector:
    // i = gasolinera
    //j = peticion de la gasolinera
    // peticionesv[i][j] = camion

    private static double kmax=640.0; //pagina 5
    private static int vmax=5;

    /*
    recordar que cada centro es un camion, puede haber varios centros con mismas coordenadas
     */

    // seria muy pesado hacer la creadora pasandole objetos y teniendo que copiarlo
    // public ProbGasBoard(){}




    //creadora de copia
    public ProbGasBoard(ProbGasBoard another)
    {
        int petaux[][] = another.getPeticiones();
        peticiones=new int[gasolineras.size()][];
        for (int i=0;i<gasolineras.size();i++){
            peticiones[i]=new int[(gasolineras.get(i).getPeticiones()).size()];
            for (int j = 0; j < (gasolineras.get(i).getPeticiones()).size();j++){

                peticiones[i][j]=petaux[i][j];

            }
        }
        Camiones camaux[] = another.getCamiones();
        camiones=new Camiones[centros.size()];
        for (int i=0;i<camiones.length;i++){
            camiones[i]=new Camiones();
            camiones[i].kmtotal=camaux[i].kmtotal;
            camiones[i].kmviaje=new double[vmax];
            camiones[i].peticiones=new Pair[2*vmax];
            for (int j=0;j<2*vmax;j++){
                camiones[i].peticiones[j]=new Pair();
                camiones[i].peticiones[j].first= camaux[i].peticiones[j].first;
                camiones[i].peticiones[j].second = camaux[i].peticiones[j].second;
                if (j<vmax) camiones[i].kmviaje[j]=camaux[i].kmviaje[j];
            }

        }
    }

    public ProbGasBoard(int seed,int ngas,int ncen,int mult){
        gasolineras=new Gasolineras(ngas,seed);
        centros=new CentrosDistribucion(ncen,mult,seed);

        //Generacion del estado inicial vacio de las peticiones

        //ara con vector para comparar
        peticiones=new int[gasolineras.size()][];
        for (int i=0;i<gasolineras.size();i++){
            peticiones[i]=new int[(gasolineras.get(i).getPeticiones()).size()];
            // System.out.println((gasolineras.get(i).getPeticiones()).size());
            for (int j = 0; j < (gasolineras.get(i).getPeticiones()).size();j++){
                peticiones[i][j]=-1;
            }
        }
        //generacion del estado inicial vacio de los camiones

        camiones=new Camiones[centros.size()];
       // System.out.println("camiones totales: "+camiones.length);
        for (int i=0;i<camiones.length;i++){
            camiones[i]=new Camiones();
            camiones[i].kmtotal=0.0;
            camiones[i].kmviaje=new double[vmax];
            camiones[i].peticiones=new Pair[2*vmax];
            for (int j=0;j<2*vmax;j++){
                camiones[i].peticiones[j]=new Pair();
                camiones[i].peticiones[j].first=camiones[i].peticiones[j].second=-1;
                if (j<vmax) camiones[i].kmviaje[j]=0.0;
            }

        }
        //PARTE PARA GENERAR SOL NO VACIA
        //genera_sol_ini();   //Por indices
        genera_sol_ini_opt(); //Inteligente

    }
    //creadora con estado inicial no vacio
    //parto de all a -1
    //Se que se puede hacer mas eficiente, pero paso de optimizar xD
    public void genera_sol_ini(){
        for (int i=0;i<peticiones.length;i++){//por cada gasolinera
            for (int j=0;j<peticiones[i].length;j++){//por cada peticion de la gasolinera i
                boolean asignado=false;
                for (int k=0;k<camiones.length && !asignado;k++){ //por cada camion
                    for (int l=0;l<vmax*2 && !asignado;l++){//la meto en el menor viaje posible
                        if (puedemover(k,l,i)){
                            mover(k,l,i,j);
                            asignado=true;
                        }
                    }
                }
            }
        }
    }

    //generadora solucion inicial a partir de kms
    public void genera_sol_ini_opt()
    {
        for(int i = 0;i<peticiones.length;++i) //para cada gasolinera
        {
            for(int j = 0;j<peticiones[i].length;++j) //para cada peticion de la gasolinera
            {

                int camion = camionCercano(i);
                boolean asignado = false;
                for(int l = 0;l<vmax*2 && !asignado;++l)
                {
                    if (puedemover(camion,l,i))
                    {
                        mover(camion,l,i,j);
                        asignado=true;

                    }
                }

            }
        }
    }
    public int camionCercano (int i)
    {
        int d = 100000; //int para guardar distancia mas pequeña
        int cercano = 0; //int para guardar el camion mas cercano
        for(int k = 0; k<camiones.length;++k) //calculamos la distancia de cada camion a la gasolinera y nos quedamos con la mejor
        { //nos quedamos con el camion mas cercano
            boolean espacio = false;
            for(int l = 0;l<vmax*2 && !espacio;++l){
                if (puedemover(k,l,i)) espacio = true;
            }
            int distancia = calculaDistancia(k,i);
            if (d>distancia && espacio)
            {
                d = distancia; //nos quedamos con el minimo de distancia
                cercano = k;
            }
        }
        //en este punto cercano = al camion mas cercano a la gasolinera i
        return cercano;
    }

    public int calculaDistancia(int k, int i) //calcula la distancia de la gasolinera i al camion k
    {
        Distribucion c = centros.get(k);
        int xc = c.getCoordX(), yc = c.getCoordY();
        Gasolinera g = gasolineras.get(i);
        int xg = g.getCoordX(), yg = g.getCoordY();
        return dist(xc,yc,xg,yg);
    }
    public int getNumGas() { //devuelve el numero de gasolineras
        return gasolineras.size();
    }

    public int getNumCam() { //devuelve el numero de camiones
        return centros.size();
    }

    public int getNumPet() //Retorna el numero de peticiones atendidas
    {
        int numpet = 0;
        for(int i = 0;i<peticiones.length;++i)
        {
            for(int j = 0;j<peticiones[i].length;++j)
            {
                if(peticiones[i][j]!= -1) ++numpet;
            }
        }
        return numpet;
    }
    public Camiones[] getCamiones(){return camiones; }

    public int[][] getPeticiones() {
        return peticiones;
    }

    public Gasolineras getGas() //retorna las gasolineras
    {
        return gasolineras;
    }

    public int[][] getPet() //retorna las peticiones atendidas en total
    {
        return peticiones;
    }
    public Camiones[] getCam() //retorna los camiones
    {
        return camiones;
    }
    public int getNumPetGas(int i) { //devuelve el numero de peticiones de la gasolinera i
        return (gasolineras.get(i)).getPeticiones().size();
    }


    public int getAsocPet(int gas, int pet) { //devuelve a que camion esta asociada la peticion numero pet de la gasolinera gas
        return peticiones[gas][pet]; //aixo si retorna camions i el raul no plora
    }

    public int getCamPet(int cam, int pet) { //devuelve el numero de gasolinera a la que esta asociada la peticion pet del camion cam
        return camiones[cam].peticiones[pet].first;
    }



    private int dist(int x1, int y1, int x2,int y2 ){
        return abs(x1-x2)+abs(y1-y2);
    }

    private void UpdateKm(int k,int v){ //recalcula los km que hace el camion k en el viaje v,machaca los km del viaje v
        camiones[k].kmtotal-=camiones[k].kmviaje[v];
        Distribucion c = centros.get(k);
        int xc = c.getCoordX(), yc = c.getCoordY();
        int d;
        if (camiones[k].peticiones[2*v].first == -1 && camiones[k].peticiones[2*v+1].first == -1){
            //caso en el que un camion se queda sin peticiones al haber intercambiado o movido
            d = 0;
        }

        else if (camiones[k].peticiones[2*v].first == -1){ //caso solo tiene peticion 2
            Gasolinera g2 = gasolineras.get(camiones[k].peticiones[2 * v + 1].first);
            int xg2 = g2.getCoordX(),yg2 = g2.getCoordY();
            d =  2*dist(xg2, yg2, xc, yc);
        }
        else if (camiones[k].peticiones[2*v+1].first == -1){//caso solo tiene peticion 1
            Gasolinera g1 = gasolineras.get(camiones[k].peticiones[2 * v].first);
            int xg1 = g1.getCoordX(),yg1 = g1.getCoordY();
            d = 2*dist(xc, yc, xg1, yg1);
        }
        else { //caso tiene 2 peticiones
            Gasolinera g1 = gasolineras.get(camiones[k].peticiones[2 * v].first);
            Gasolinera g2 = gasolineras.get(camiones[k].peticiones[2 * v + 1].first);
            int xg1 = g1.getCoordX(), xg2 = g2.getCoordX(), yg1 = g1.getCoordY(), yg2 = g2.getCoordY();
            d = dist(xc, yc, xg1, yg1) + dist(xg1, yg1, xg2, yg2) + dist(xg2, yg2, xc, yc);
        }
        camiones[k].kmtotal += d;
        if (camiones[k].kmtotal > 640) System.out.println("kmtotal: "+camiones[k].kmtotal);
        camiones[k].kmviaje[v] = d;

    }
    //hago test de camion k peticion l, que quiero cambiar por una peticion en la gasolinera i
    //devuelve true si los km con el cambio estan dentro de lo aceptable
    public boolean testkm(int k,int l,int i){
        Distribucion c = centros.get(k);
        int xc = c.getCoordX(), yc = c.getCoordY();
        int xg =gasolineras.get(i).getCoordX(); int yg=gasolineras.get(i).getCoordY();
        int d;

        if (l%2==0){
            if (camiones[k].peticiones[l+1].first == -1){ //caso solo tiene peticion 1
                d =  2*dist(xg, yg, xc, yc);
            }
            else{ //caso tiene 2 peticiones
                Gasolinera g2 = gasolineras.get(camiones[k].peticiones[l+1].first);
                int  xg2 = g2.getCoordX(), yg2 = g2.getCoordY();
                d = dist(xc, yc, xg, yg) + dist(xg, yg, xg2, yg2) + dist(xg2, yg2, xc, yc);
            }
            return camiones[k].kmtotal-camiones[k].kmviaje[l/2]+d <kmax;
        }
        else {

            Gasolinera g2 = gasolineras.get(camiones[k].peticiones[l-1].first);
            int  xg2 = g2.getCoordX(), yg2 = g2.getCoordY();
            d = dist(xc, yc, xg, yg) + dist(xg, yg, xg2, yg2) + dist(xg2, yg2, xc, yc);
            return camiones[k].kmtotal - camiones[k].kmviaje[(l-1) / 2] + d < kmax;
        }
    }

    //mueve la petición l del camión k por la petición j de la gasolinera i
    /*
    PRECONDICION: La peticion identificada por gasolinera i, peticion j no es atendida por ningun otro camion en el momento
    la peticion que atiende el camion k en la posicion l es: o una peticion de una gasolinera o la peticion fantasma
    con INDICE MINIMO
     */
    public void mover(int k,int l,int i,int j)
    {
        Pair p=camiones[k].peticiones[l];//p es el pair que vamos a substituir
        if (p.first!=-1)peticiones [p.first][p.second]=-1; //quitamos la associacion al camion k
        if (i != -1) peticiones[i][j]=k;// si existe, la peticion i,j ahora la atiende el camion k
        camiones[k].peticiones[l]=new Pair(i,j); //el camion k ahora atiende la peticion i,j o nada (-1) en posicion l
        if (l%2==0){ // posicion 0 del viaje

            UpdateKm(k,l/2);

        }
        else { // posicion 1 del viaje

            UpdateKm(k, (l-1) / 2);
        }
    }
    public boolean puedemover (int k, int l,int i)
    { //solo permite para peticiones del camion vacias

        if (l%2==1){ // optimizacion, evita mover a una posicion impar si la par esta vacia
            if (camiones[k].peticiones[l-1].first==-1 ) return false;
        }
        if (! testkm(k, l, i))return false;
        return (camiones[k].peticiones[l].second==-1);
    }
    public boolean puedemover2(int k,int l,int i){
        //permite tanto si esta vacio como si no
        if (l%2==1){ // optimizacion, evita mover a una posicion impar si la par esta vacia
            if (camiones[k].peticiones[l-1].first==-1 ) return false;
        }
        if (! testkm(k, l, i))return false;
        return true;
    }

    public double kmrecorridos(){ //km recorrdiso de solucion (para hacer experimetos)
        double total = 0;
        for(int i = 0;i<camiones.length;++i) //obtenemos todos los km recorridos para atender cada petición
        {
            total+= camiones[i].kmtotal;
        }
        return total;
    }
}
