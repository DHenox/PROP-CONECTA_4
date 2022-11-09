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
    private int myColor;
    
    public ProPlayer()
    {
        nom = "Prop";
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
        int col = minimax(t, 6, true).snd;      //  Obté la milor columna (tauler actual, profunditat, torn)
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
    public int heuristica(Tauler t, int color){
        int value = 0;
        int otherColor = color*-1;
        int N = t.getMida();
        
        for (int i = 0; i < N; i++) {
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
        }
        
        //  heuristica en files
        for (int i = 0; i < N; i++) {
            int myCntRow = 0, myMaxCnt = 0;
            int otherCntRow = 0, otherMaxCnt = 0;
            for (int j = 0; j < N; j++) {
                int currentColor = t.getColor(i, j);
                if(currentColor == 0){
                    myCntRow = 0;
                    otherCntRow = 0;
                }
                else if (j != 0 && currentColor == t.getColor(i, j-1)){
                    if(currentColor == color)
                        ++myCntRow;
                    else if(currentColor == otherColor)
                        ++otherCntRow;
                    
                    if(myCntRow > myMaxCnt)
                        myMaxCnt = myCntRow;
                    if(otherCntRow > otherMaxCnt)
                        otherMaxCnt = otherCntRow;
                }
            }
            //System.out.println("Fila " + i + " MyRowHeuristica: " + myMaxCnt + " EnemyRowHeuristica: " + -otherMaxCnt);
            value += myMaxCnt - otherMaxCnt;
        }
        
        //  heuristica en columnes
        for (int j = 0; j < N; j++) {                   
            int myCntRow = 0, myMaxCnt = 0;
            int otherCntRow = 0, otherMaxCnt = 0;
            for (int i = 0; i < N; i++) {
                int currentColor = t.getColor(i, j);
                if (currentColor != 0 && i != 0 && currentColor == t.getColor(i-1, j)){
                    if(currentColor == color)
                        ++myCntRow;
                    else if(currentColor == otherColor)
                        ++otherCntRow;
                    
                    if(myCntRow > myMaxCnt)
                        myMaxCnt = myCntRow;
                    if(otherCntRow > otherMaxCnt)
                        otherMaxCnt = otherCntRow;
                }
                else{
                    myCntRow = 0;
                    otherCntRow = 0;
                }
            }
            //System.out.println("Columna " + j + " MyColumnHeuristica: " + myMaxCnt + " EnemyColumnHeuristica: " + -otherMaxCnt);
            value += myMaxCnt - otherMaxCnt;
        }
        
        //  TODO heuristica en diagonales positivas
        //  TODO heuristica en diagonales negativas

        //System.out.println("HEURISTICA: " + value);
        return value;
    }
    
    /**
     * Crea tots els fills a partir d'un tauler i els associa
     * amb l'index de la columna modificada
     * @param t tauler a expandir
     * @param color color del jugador a expandir
     * @return llista de parells taulerFill-indexColumnaModificada
     */
    public ArrayList<Pair<Tauler, Integer>> creaFills(Tauler t, int color){
        ArrayList<Pair<Tauler, Integer>> fills = new ArrayList<>();
        int N = t.getMida();
        //  Per cada columna de l'estat actual
        for (int i = 0; i < N; i++) {
            //  Si es poden afegir fitxes a la columna
            if(t.movpossible(i)){
                //  Copiem tot l'estat actual en un nou tauler
                Tauler f = new Tauler(8);
                for (int j = 0; j < N; j++) {
                    for (int k = 0; k < N; k++) {
                        int copyColor = t.getColor(j, k);
                        if(copyColor != 0){
                            f.afegeix(k, copyColor);
                        }
                    }
                }
                //  Afegim una fitxa a una columna del nou tauler
                f.afegeix(i, color);
                //  Emmagatzemem el nou fill i la columna modificada
                fills.add(new Pair<>(f, i));
            }
        }
        return fills;
    }
    
    /**
     * Calcula la heuristica dels nodes a una determinada profunditat, selecciona
     * la columna que arribi a un millor resultat
     * @param t tauler analitzat
     * @param depth profunditat màxima
     * @param maximizingPlayer jugador que maximitza(ProPlayer) o minimitza(Contrincant) l'heuristica
     * @return parell heuristicaTauler-columnaSeleccionada
     */
    public Pair<Integer, Integer> minimax(Tauler t, int depth, boolean maximizingPlayer){
        if(depth == 0 || thereIsWinner(t) || !t.espotmoure()){
            return new Pair<>(heuristica(t, myColor), 0);
        }
        
        int col = 0;
        if(maximizingPlayer){
            //  Crea totes les possibles tirades del jugador que maximitza
            ArrayList<Pair<Tauler, Integer>> fills = creaFills(t, myColor);
            int maxEval = Integer.MIN_VALUE;
            for (int i = 0; i < fills.size(); i++) {
                int eval = minimax(fills.get(i).fst, depth-1, !maximizingPlayer).fst;
                if(maxEval < eval){
                    maxEval = eval;
                    col = fills.get(i).snd;        //  mal
                }
            }
            return new Pair<>(maxEval, col);
        }
        else{
            //  Crea totes les possibles tirades del jugador que minimitza
            ArrayList<Pair<Tauler, Integer>> fills = creaFills(t, myColor*-1);
            int minEval = Integer.MAX_VALUE;
            System.out.println("\n");
            for (int i = 0; i < fills.size(); i++) {
                int eval = minimax(fills.get(i).fst, depth-1, !maximizingPlayer).fst;
                if(minEval > eval){
                    minEval = eval;
                    col = fills.get(i).snd;        //  mal
                }
                //System.out.println("FILL " + i + " " + minEval +  " chosen column = " + col);
                //fills.get(i).fst.pintaTaulerALaConsola();
            }
            return new Pair<>(minEval, col);
        }
    }
    
    /**
     * Comprova si existeix un ganador donat un tauler
     * @param t el tauler
     * @return true si existeix un ganador, false altrament
     */
    private boolean thereIsWinner(Tauler t){
        int N = t.getMida();
        for (int i = 0; i < N; i++) {
            if(t.solucio(i, myColor) || t.solucio(i, myColor*-1))
                return true;
        }
        return false;
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
