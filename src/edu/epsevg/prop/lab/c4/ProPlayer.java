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
    
    /**
     * TODO
     * @param t el tauler a analitzar
     * @return valor heuristic del node actual
     */
    public int heuristica(Tauler t){
        int value = 0;
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
