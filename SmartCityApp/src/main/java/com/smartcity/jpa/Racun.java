package com.smartcity.jpa;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name="racun")
public class Racun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional=false)
    @JoinColumn(name="auto_id")
    private Auto auto;

    @ManyToOne(optional=false)
    @JoinColumn(name="stanica_id")
    private Stanica stanica;

    @Column(nullable=false)
    private BigDecimal kolicina;

    @Column(name="jedinicna_cena", nullable=false)
    private BigDecimal jedinicnaCena;

    @Column(name="cena_distribucije", nullable=false)
    private BigDecimal cenaDistribucije;

    @Column(nullable=false)
    private BigDecimal ukupno;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable=false)
    private Date vreme = new Date();

    public Integer getId() { return id; }

    public void setAuto(Auto auto) { this.auto = auto; }
    public void setStanica(Stanica stanica) { this.stanica = stanica; }
    public void setKolicina(BigDecimal kolicina) { this.kolicina = kolicina; }
    public void setJedinicnaCena(BigDecimal jedinicnaCena) { this.jedinicnaCena = jedinicnaCena; }
    public void setCenaDistribucije(BigDecimal cenaDistribucije) { this.cenaDistribucije = cenaDistribucije; }
    public void setUkupno(BigDecimal ukupno) { this.ukupno = ukupno; }
    public Auto getAuto() { return auto; }
    public Stanica getStanica() { return stanica; }
    public BigDecimal getKolicina() { return kolicina; }
    public BigDecimal getJedinicnaCena() { return jedinicnaCena; }
    public BigDecimal getCenaDistribucije() { return cenaDistribucije; }
    public BigDecimal getUkupno() { return ukupno; }
    public Date getVreme() { return vreme; }

}
