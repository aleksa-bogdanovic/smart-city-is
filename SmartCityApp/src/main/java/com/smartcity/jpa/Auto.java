package com.smartcity.jpa;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="auto")
public class Auto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional=false)
    @JoinColumn(name="vlasnik_id")
    private Vlasnik vlasnik;

    @Column(name="energija_trenutna", nullable=false)
    private BigDecimal energijaTrenutna;

    @Column(nullable=false)
    private BigDecimal kapacitet;

    public Integer getId() { return id; }

    public Vlasnik getVlasnik() { return vlasnik; }
    public void setVlasnik(Vlasnik vlasnik) { this.vlasnik = vlasnik; }

    public BigDecimal getEnergijaTrenutna() { return energijaTrenutna; }
    public void setEnergijaTrenutna(BigDecimal e) { this.energijaTrenutna = e; }

    public BigDecimal getKapacitet() { return kapacitet; }
    public void setKapacitet(BigDecimal k) { this.kapacitet = k; }
}
