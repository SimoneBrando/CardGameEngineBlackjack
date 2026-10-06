package com.mycompany.CardGameEngineBlackjack.Domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "giocatori")
public class Giocatore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_giocatore")
    private Long id;

    @Column(name = "nome", nullable = false, length = 50)
    private String nome;

    @Column(name = "bilancio_fiches", nullable = false)
    private Integer bilancioFiches;

    @OneToMany(mappedBy = "giocatore", cascade = CascadeType.ALL)
    private List<Partita> partite = new ArrayList<>();

    @OneToMany(mappedBy = "giocatore", cascade = CascadeType.ALL)
    private List<Mano> mani = new ArrayList<>();

    public Giocatore() {}

    public Giocatore(Long id, String nome, Integer bilancioFiches) {
        this.id = id;
        this.nome = nome;
        this.bilancioFiches = bilancioFiches;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Integer getBilancioFiches() { return bilancioFiches; }
    public void setBilancioFiches(Integer bilancioFiches) { this.bilancioFiches = bilancioFiches; }

    public List<Partita> getPartite() { return partite; }
    public void setPartite(List<Partita> partite) { this.partite = partite; }

    public List<Mano> getMani() { return mani; }
    public void setMani(List<Mano> mani) { this.mani = mani; }
}
