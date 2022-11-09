/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.epsevg.prop.lab.c4;

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
     * Retornar columna on realitzar el nou moviment per al color indicat
     * @param t
     * @param color
     * @return 
     */
    public int moviment(Tauler t, int color)
    {
        myColor = color;
        int col = minimax(t, 4, true);      //  Obté la milor columna (tauler actual, profunditat, torn)
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
     * @param t
     * @param color
     * @return 
     */
    public int heuristica(Tauler t, int color){
        int value = 0;
        int otherColor = color*-1;
        int N = t.getMida();
        for (int i = 0; i < N; i++) {                   //  heuristica en filas
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
            //System.out.println("Fila " + i +" MyRowHeuristica: " + myMaxCnt + " EnemyRowHeuristica: " + -otherMaxCnt);
            value += myMaxCnt - otherMaxCnt;
        }
        
        for (int j = 0; j < N; j++) {                   //  heuristica en columnas
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
            //System.out.println("Columna " + j +" MyColumnHeuristica: " + myMaxCnt + " EnemyColumnHeuristica: " + -otherMaxCnt);
            value += myMaxCnt - otherMaxCnt;
        }
        
        //  TODO heuristica en diagonales positivas
        //  TODO heuristica en diagonales negativas

        //System.out.println("HEURISTICA: " + value);
        return value;
    }
    
    /**
     * Creació de tots els fills a partir de l'estat t
     * @param t
     * @return 
     */
    public ArrayList<Tauler> creaFills(Tauler t){
        ArrayList<Tauler> fills = new ArrayList<>();
        int N = t.getMida();
        //  Per cada columna de l'estat actual
        for (int i = 0; i < N; i++) {
            //  Si es poden afegir fitxes a la columna
            if(t.movpossible(i)){
                //  Copiem tot l'estat actual en un now tauler
                Tauler f = new Tauler(8);
                for (int j = 0; j < N; j++) {
                    for (int k = 0; k < N; k++) {
                        int copyColor = t.getColor(j, k);
                        if(copyColor != 0){
                            f.afegeix(j, copyColor);
                        }
                    }
                }
                //  Al nou tauler l'hi afegim un nou moviiment
                f.afegeix(i, myColor);
                //  Desem el nou fill
                fills.add(f);
            }
        }
        return fills;
    }
    
    /**
     * A partir de l'estat t fa ús de minimax fins a una determinada profunditat,
     * pren decisions tenint en compte l'heuristica dels nodes finals(depth=0)
     * @param t
     * @param depth
     * @param maximizingPlayer
     * @return 
     */
    public int minimax(Tauler t, int depth, boolean maximizingPlayer){
        if(depth == 0 || !t.espotmoure()){
            return heuristica(t, myColor);
        }
        
        int col = -1;
        ArrayList<Tauler> fills = creaFills(t);         //  Crea totes les possibles tirades
        if(maximizingPlayer){
            int maxEval = Integer.MIN_VALUE;
            for (int i = 0; i < fills.size(); i++) {
                int eval = minimax(fills.get(i), depth-1, false);
                if(maxEval < eval){
                    maxEval = eval;
                    col = i;
                }
            }
        }
        else{
            int minEval = Integer.MAX_VALUE;
            for (int i = 0; i < fills.size(); i++) {
                int eval = minimax(fills.get(i), depth-1, true);
                if(minEval > eval){
                    minEval = eval;
                    col = i;
                }
            }
        }
        return col;
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
