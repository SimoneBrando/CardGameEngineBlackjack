package com.mycompany.CardGameEngineBlackjack.Domain;

import com.mycompany.CardGameEngineBlackjack.Domain.Enum.*;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carte")
public class Carta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carta")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "seme", nullable = false)
    private Seme seme;

    @Enumerated(EnumType.STRING)
    @Column(name = "rango", nullable = false)
    private Rango rango;

    @Column(name = "coperta", nullable = false)
    private Boolean coperta;

    @ElementCollection
    @CollectionTable(name = "carta_valori", joinColumns = @JoinColumn(name = "id_carta"))
    @Column(name = "valore")
    private List<Integer> valoriPossibili = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "id_mazzo", nullable = false)
    private Mazzo mazzo;

    public Carta() {}

    public Carta(Long id, Seme seme, Rango rango, Boolean coperta, List<Integer> valoriPossibili, Mazzo mazzo) {
        this.id = id;
        this.seme = seme;
        this.rango = rango;
        this.coperta = coperta;
        this.valoriPossibili = valoriPossibili;
        this.mazzo = mazzo;
    }

    // --- Getter e Setter ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
}
