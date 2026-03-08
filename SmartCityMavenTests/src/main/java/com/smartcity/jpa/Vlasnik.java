package com.smartcity.jpa;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="vlasnik")
public class Vlasnik {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable=false)
    private String ime;

    @Column(nullable=false)
    private String prezime;

    @Column(nullable=false)
    private BigDecimal balans;

    public Integer getId() { return id; }

    public String getIme() { return ime; }
    public void setIme(String ime) { this.ime = ime; }

    public String getPrezime() { return prezime; }
    public void setPrezime(String prezime) { this.prezime = prezime; }

    public BigDecimal getBalans() { return balans; }
    public void setBalans(BigDecimal balans) { this.balans = balans; }
}
