package com.mycompany.CardGameEngineBlackjack.Domain;

import com.mycompany.CardGameEngineBlackjack.Domain.Enum.*;
import java.util.ArrayList;
import java.util.List;


public class Carta {

   
    private Seme seme;
    private Rango rango;
    private Boolean coperta;
    private List<Integer> valoriPossibili = new ArrayList<>();
    private Mazzo mazzo;

    public Carta() {}

    public Carta(Seme seme, Rango rango, Boolean coperta, List<Integer> valoriPossibili, Mazzo mazzo) {
    
        this.seme = seme;
        this.rango = rango;
        this.coperta = coperta;
        this.valoriPossibili = valoriPossibili;
        this.mazzo = mazzo;
    }

    // --- Getter e Setter ---

    public Seme getSeme() { return seme; }
    public void setSeme(Seme seme) { this.seme = seme; }

    public Rango getRango() { return rango; }
    public void setRango(Rango rango) { this.rango = rango; }

    public Boolean getCoperta() { return coperta; }
    public void setCoperta(Boolean coperta) { this.coperta = coperta; }

    public List<Integer> getValoriPossibili() { return valoriPossibili; }
    public void setValoriPossibili(List<Integer> valoriPossibili) { this.valoriPossibili = valoriPossibili; }

    public Mazzo getMazzo() { return mazzo; }
    public void setMazzo(Mazzo mazzo) { this.mazzo = mazzo; }
    
    @Override
    public String toString() {
        if (this.coperta) {
            return "[CARTA COPERTA]";
        }
        return "[" + this.rango + " di " + this.seme + "]";
    }
}
