/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.epsevg.prop.lab.c4;

import com.sun.tools.javac.util.Pair;
import java.util.ArrayList;

/**
 * Pro player
 * @author HENOK
 */
public class ProPlayer implements Jugador, IAuto {
    private String nom;
    private int profunditat;
    private int myColor;
    
    /*  
        Taula que associa a cada posició del tauler
        el nº de possibles 4 en ratlla donat un color
    */
    public int[][] tablaPuntuacio = {
        {3, 4, 5, 7, 7, 5, 4, 3},
        {4, 6, 8,10,10, 8, 6, 4},
        {5, 8,11,13,13,11, 8, 5},
        {7,10,13,16,16,13,10, 7},
        {7,10,13,16,16,13,10, 7},
        {5, 8,11,13,13,11, 8, 5},
        {4, 6, 8,10,10, 8, 6, 4},
        {3, 4, 5, 7, 7, 5, 4, 3}
    };
    
    public ProPlayer(int prof)
    {
        nom = "ProPlayer";
        profunditat = prof;
        myColor = 0;
    }

    /**
     * Cerca la columna en la que realitzar el següent moviment
     * a partir del tauler
     * @param t el tauler actualment
     * @param color el color del ProPlayer
     * @return l'index de la columna
     */
    public int moviment(Tauler t, int color)
    {
        myColor = color;
        //  Obté la milor columna (tauler actual, profunditat, torn)
        int col = minimax(t, profunditat, true, 0).snd;
        return col;
    }
    
    /*
    La función heurística es proporcional a la potencia del número de fichas
    que son continuas en una fila/columna/diagonal. Esto se suma para cada
    fila, columna y diagonal. Nota: El valor calculado para cada fila/columna/diagonal
    tiene un signo positivo o negativo dependiendo de qué fichas de colores estén
    presentes consecutivamente en esa fila o columna.
    */
    /**
     * La funció heuristica es proporcional a la suma del nombre de fitxes
     * consecutives fila/columna/diagonal per a un determinat jugador, les
     * connexions consecutives entre fitxes del ProPlayer sumen 1 i les
     * connexions consecutives entre fitxes del contrincant resten 1
     * @param t el tauler a analitzar
     * @param color color del ProPlayer
     * @return valor heuristic del node actual
     */
    public int heuristica(Tauler t){
        int value = 0;
        int N = t.getMida();
        for (int i = 0; i < 8; i++) {
            if(isEmpty(t, i))
                break;
            for (int j = 0; j < 8; j++) {
                if (t.getColor(i, j) == myColor) {
                    value += tablaPuntuacio[i][j];
                } else if (t.getColor(i, j) == myColor*-1) {
                    value -= tablaPuntuacio[i][j];
                }
            }
        }
        return value;
        /*int value = 0;
        int otherColor = color*-1;
        int N = t.getMida();
        
        for (int i = 0; i < N; i++) {
            //  heuristica en files
            if(t.solucio(i, color)){
                //System.out.println("I WIN HERE");
                //t.pintaTaulerALaConsola();
                value += 1000;
                return value;
            }
            if(t.solucio(i, otherColor)){
                //System.out.println("YOU WIN HERE");
                //t.pintaTaulerALaConsola();
                value -= 1000;
                return value;
            }
            int myRowCnt = 0, myMaxCnt = 0;
            int otherRowCnt = 0, otherMaxCnt = 0;
            for (int j = 0; j < N; j++) {
                int currentColor = t.getColor(i, j);
                if(currentColor == 0){
                    myRowCnt = 0;
                    otherRowCnt = 0;
                }
                else if (j != 0 && currentColor == t.getColor(i, j-1)){
                    if(currentColor == color)
                        ++myRowCnt;
                    else if(currentColor == otherColor)
                        ++otherRowCnt;
                    
                    if(myRowCnt > myMaxCnt)
                        myMaxCnt = myRowCnt;
                    if(otherRowCnt > otherMaxCnt)
                        otherMaxCnt = otherRowCnt;
                }
            }
            //System.out.println("Fila " + i + " MyRowHeuristica: " + myMaxCnt + " EnemyRowHeuristica: " + -otherMaxCnt);
            value += myMaxCnt - otherMaxCnt;
        }
        
        //  heuristica en columnes
        for (int j = 0; j < N; j++) {
            int myColumnCnt = 0, myMaxCnt = 0;
            int otherColumnCnt = 0, otherMaxCnt = 0;
            for (int i = 0; i < N; i++) {
                int currentColor = t.getColor(i, j);
                if (currentColor != 0 && i != 0 && currentColor == t.getColor(i-1, j)){
                    if(currentColor == color)
                        ++myColumnCnt;
                    else if(currentColor == otherColor)
                        ++otherColumnCnt;
                    
                    if(myColumnCnt > myMaxCnt)
                        myMaxCnt = myColumnCnt;
                    if(otherColumnCnt > otherMaxCnt)
                        otherMaxCnt = otherColumnCnt;
                }
                else{
                    myColumnCnt = 0;
                    otherColumnCnt = 0;
                }
            }
            //System.out.println("Columna " + j + " MyColumnHeuristica: " + myMaxCnt + " EnemyColumnHeuristica: " + -otherMaxCnt);
            value += myMaxCnt - otherMaxCnt;
        }
        
        //  TODO revisar si files i columnes OK     //  si que va xd, for invertido(i,j) en columnas
        //  TODO heuristica en diagonales positivas
        //  TODO heuristica en diagonales negativas

        //System.out.println("HEURISTICA: " + value);
        return value;*/
    }
    
    /**
     * Funció que comprova si la fila indicada està totalment buida
     * @param t el tauler a analitzar
     * @param i la fila del tauler
     * @return true si la fila i-éssima es buida, false altrament
     */
    public boolean isEmpty(Tauler t, int i){
        return (t.getColor(i, 0)==0 && t.getColor(i,1)==0  &&
                t.getColor(i, 2)==0 && t.getColor(i,3)==0 &&
                t.getColor(i, 4)==0 && t.getColor(i,5)==0 &&
                t.getColor(i, 6)==0 && t.getColor(i,7)==0);
    }
    
    /**
     * Calcula la heuristica dels nodes a una determinada profunditat, selecciona
     * la columna que arribi a un millor resultat
     * @param t tauler analitzat
     * @param depth profunditat màxima
     * @param maximizingPlayer jugador que maximitza(ProPlayer) o minimitza(Contrincant) l'heuristica
     * @return parell heuristicaTauler-columnaSeleccionada
     */
    public Pair<Integer, Integer> minimax(Tauler t, int depth, boolean maximizingPlayer, int ultCol){
        if(t.solucio(ultCol, myColor))
            return new Pair<>(10000, ultCol);
        else if(t.solucio(ultCol, myColor*-1))
            return new Pair<>(-10000, ultCol);
        else if(depth == 0 || !t.espotmoure()){
            return new Pair<>(heuristica(t), 0);
        }
        
        int col = 0;
        if(maximizingPlayer){
            //  Crea totes les possibles tirades del jugador que maximitza
            //ArrayList<Pair<Tauler, Integer>> fills = creaFills(t, myColor);
            int maxEval = Integer.MIN_VALUE;
            for (int i = 0; i < 8; i++) {
                //  Si es poden afegir fitxes a la columna
                if(t.movpossible(i)){
                    //  Copiem tot l'estat actual en un nou tauler
                    Tauler f = new Tauler(t);
                    //  Afegim una fitxa a una columna del nou tauler
                    f.afegeix(i, myColor);
                    int eval = minimax(f, depth-1, !maximizingPlayer, i).fst;
                    if(maxEval < eval){
                        maxEval = eval;
                        col = i;
                    }
                }
            }
            return new Pair<>(maxEval, col);
        }
        else{
            //  Crea totes les possibles tirades del jugador que minimitza
            //ArrayList<Pair<Tauler, Integer>> fills = creaFills(t, myColor*-1);
            int minEval = Integer.MAX_VALUE;
            for (int i = 0; i < 8; i++) {
                //  Si es poden afegir fitxes a la columna
                if(t.movpossible(i)){
                    //  Copiem tot l'estat actual en un nou tauler
                    Tauler f = new Tauler(t);
                    //  Afegim una fitxa a una columna del nou tauler
                    f.afegeix(i, myColor*-1);
                    int eval = minimax(f, depth-1, !maximizingPlayer, i).fst;
                    if(minEval > eval){
                        minEval = eval;
                        col = i;
                    }
                }
            }
            return new Pair<>(minEval, col);
        }
    }
    
    /**
     * retorna el nom que identifica al ProPlayer
     * @return 
     */
    public String nom()
    {
      return nom;
    }
}
