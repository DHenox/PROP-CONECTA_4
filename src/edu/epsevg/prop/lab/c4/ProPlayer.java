/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.epsevg.prop.lab.c4;

import com.sun.tools.javac.util.Pair;
import java.util.ArrayList;

/**
 * Pro player
 * @author Henok Argudo i Cinta Gonzalez
 */
public class ProPlayer implements Jugador, IAuto {
    private String nom;
    private int profunditat;
    private int myColor;
    private int cntNodes;
    
    /*  
        Taula que associa a cada posició del tauler
        el nº de possibles 4 en ratlla donat un color
    */
    public int[][] predictTable = {
        {3, 4, 5, 7, 7, 5, 4, 3},
        {4, 6, 8,10,10, 8, 6, 4},
        {5, 8,11,13,13,11, 8, 5},
        {7,10,13,16,16,13,10, 7},
        {7,10,13,16,16,13,10, 7},
        {5, 8,11,13,13,11, 8, 5},
        {4, 6, 8,10,10, 8, 6, 4},
        {3, 4, 5, 7, 7, 5, 4, 3}
    };
    
    /**
     * Constructor de ProPlayer parametritzat amb la profunditat maxima
     * @param depth profunditat maxima
     */
    public ProPlayer(int depth)
    {
        nom = "ProPlayer";
        profunditat = depth;
        myColor = 0;
        cntNodes = 0;
    }

    /**
     * Cerca la columna en la que realitzar el següent moviment
     * a partir de la situacio actual del tauler
     * @param t el tauler actualment
     * @param color el color del ProPlayer
     * @return l'index de la columna
     */
    public int moviment(Tauler t, int color)
    {
        myColor = color;
        //  Tots els parametres d'aquesta funció estan explicats a la docu
        int col = minimax(t, profunditat, true, 0, Integer.MIN_VALUE, Integer.MAX_VALUE).snd;
        System.out.println("Nombre de jugades finals explorades: " + cntNodes);
        return col;
    }
    
    /**
     * Calcula l'heuristica a partir de sumar/restar les prediccions
     * de cada casella del tauler en funció del color que ocupa la casella
     * @param t el tauler a analitzar
     * @return valor heuristic del node actual
     */
    public int heuristica(Tauler t){
        ++cntNodes;
        int value = 0;
        for (int i = 0; i < 8; i++) {
            if(isRowEmpty(t, i))
                break;
            for (int j = 0; j < 8; j++) {
                if (t.getColor(i, j) == myColor) {
                    value += predictTable[i][j];
                } else if (t.getColor(i, j) == myColor*-1) {
                    value -= predictTable[i][j];
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
    public boolean isRowEmpty(Tauler t, int i){
        return (t.getColor(i, 0)==0 && t.getColor(i,1)==0 &&
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
     * @param ultCol l'index de la ultima columna a la qual s'ha fet el darrer moviment
     * @param alpha valor maxim que s'aplica per realitzar la poda alpha-beta
     * @param beta valor minim que s'aplica per realitzar la poda alpha-beta
     * @return parell [heuristica del tauler] - [columna seleccionada per fer seguent moviment]
     */
    public Pair<Integer, Integer> minimax(Tauler t, int depth, boolean maximizingPlayer, int ultCol, int alpha, int beta){
        
        if(t.solucio(ultCol, myColor))              //  Guanyem nosaltres
            return new Pair<>(10000, ultCol);
        else if(t.solucio(ultCol, myColor*-1))      //  Guanya el contrincant
            return new Pair<>(-10000, ultCol);
        else if(depth == 0 || !t.espotmoure()){     //  Empat o profunditat maxima
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
                    int eval = minimax(f, depth-1, !maximizingPlayer, i, alpha, beta).fst;
                    alpha = Math.max(maxEval, alpha);
                    if(maxEval < eval){
                        maxEval = eval;
                        col = i;
                    }
                    if(alpha >= beta)
                        break;
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
                    int eval = minimax(f, depth-1, !maximizingPlayer, i, alpha, beta).fst;
                    beta = Math.min(minEval, beta);
                    if(minEval > eval){
                        minEval = eval;
                        col = i;
                    }
                    if(alpha >= beta)
                        break;
                }
            }
            return new Pair<>(minEval, col);
        }
    }
    
    /**
     * retorna el nom que identifica al ProPlayer
     * @return el nom del ProPlayer
     */
    public String nom()
    {
      return nom;
    }
}
