package com.mycompany.CardGameEngineBlackjack.Application.Strategy;

public class dealerStrategyFactory {

    public dealerStrategy getDealerStrategy(String tipo) {
        if ("Standard17".equalsIgnoreCase(tipo)) {
            return (punteggioAttuale, haAssoSoft) -> {
                // Il Dealer deve pescare se ha meno di 17. 
                // Nelle regole standard, si ferma su un 17 soft o hard.
                return punteggioAttuale < 17;
            };
        }
        
        // Predisposizione per varianti di regole (es. Dealer Hit on Soft 17)
        if ("HitSoft17".equalsIgnoreCase(tipo)) {
            return (punteggioAttuale, haAssoSoft) -> {
                if (punteggioAttuale < 17) return true;
                return punteggioAttuale == 17 && haAssoSoft;
            };
        }

        throw new IllegalArgumentException("Strategia Dealer non supportata: " + tipo);
    }
}

