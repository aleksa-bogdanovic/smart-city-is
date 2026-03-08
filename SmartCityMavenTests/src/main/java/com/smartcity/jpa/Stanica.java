package com.smartcity.jpa;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="stanica")
public class Stanica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="max_energija", nullable=false)
    private BigDecimal maxEnergija;

    @Column(name="energija_trenutna", nullable=false)
    private BigDecimal energijaTrenutna;

    @Column(name="cena_po_jedinici", nullable=false)
    private BigDecimal cenaPoJedinici;

    @Column(name="cena_distribucije", nullable=false)
    private BigDecimal cenaDistribucije;

    public Integer getId() { return id; }

    public BigDecimal getMaxEnergija() { return maxEnergija; }
    public void setMaxEnergija(BigDecimal x) { this.maxEnergija = x; }

    public BigDecimal getEnergijaTrenutna() { return energijaTrenutna; }
    public void setEnergijaTrenutna(BigDecimal x) { this.energijaTrenutna = x; }

    public BigDecimal getCenaPoJedinici() { return cenaPoJedinici; }
    public void setCenaPoJedinici(BigDecimal x) { this.cenaPoJedinici = x; }

    public BigDecimal getCenaDistribucije() { return cenaDistribucije; }
    public void setCenaDistribucije(BigDecimal x) { this.cenaDistribucije = x; }
}
