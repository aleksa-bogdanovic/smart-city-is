package com.smartcity.web;

import com.smartcity.ejb.AdminService;
import com.smartcity.jpa.Auto;
import com.smartcity.jpa.Stanica;
import com.smartcity.jpa.Vlasnik;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@SuppressWarnings("serial")
@Named
@ViewScoped
public class AdminBean implements Serializable {

    private String ime;
    private String prezime;

    private Integer vlasnikId;          // iz dropdown-a
    private BigDecimal kapacitet;
    private BigDecimal energijaTrenutna;

    private String porukaAdmin;
    private BigDecimal balans;

    private List<Vlasnik> vlasnici;
    private List<Auto> auta;
    
    private BigDecimal maxEnergija;
    private BigDecimal energijaStanice;
    private BigDecimal cenaPoJedinici;
    private BigDecimal cenaDistribucije;

    private List<Stanica> stanice;
    
    private Integer vlasnikDopunaId;
    private BigDecimal iznosDopune;


    @EJB
    private AdminService adminService;

    @PostConstruct
    public void init() {
        osvezi();
    }

    public void dodajVlasnika() {
    	   porukaAdmin = null;
    	   
        if (ime == null || ime.trim().isEmpty() || prezime == null || prezime.trim().isEmpty()) {
            porukaAdmin = "Ime i prezime su obavezni.";
            return;
        }
        
        BigDecimal b = (balans == null) ? BigDecimal.ZERO : balans;
        if (b.compareTo(BigDecimal.ZERO) < 0) {
            porukaAdmin = "Balans ne može biti negativan.";
            return;
        }

        adminService.dodajVlasnika(ime.trim(), prezime.trim(), b);
        porukaAdmin = "Vlasnik dodat.";
        ime = ""; prezime = "";balans=null;
        
        osvezi();
    }

    public void dodajAuto() {
        if (vlasnikId == null) { porukaAdmin = "Izaberi vlasnika."; return; }
        if (kapacitet == null || kapacitet.compareTo(BigDecimal.ZERO) <= 0) { porukaAdmin = "Kapacitet mora biti > 0."; return; }
        if (energijaTrenutna == null || energijaTrenutna.compareTo(BigDecimal.ZERO) < 0) { porukaAdmin = "Energija ne može biti < 0."; return; }
        if (energijaTrenutna.compareTo(kapacitet) > 0) {
            porukaAdmin = "Trenutna energija ne može biti veća od kapaciteta.";
            return;
        }

        Auto a = adminService.dodajAuto(vlasnikId, kapacitet, energijaTrenutna);
        if (a == null) porukaAdmin = "Ne postoji vlasnik sa tim ID.";
        else porukaAdmin = "Auto dodat. ID=" + a.getId();

        kapacitet = null;
        energijaTrenutna = null;
        osvezi();
    }
    
    public void dodajStanicu() {
        porukaAdmin = null;

        if (maxEnergija == null || energijaStanice == null || cenaPoJedinici == null || cenaDistribucije == null) {
            porukaAdmin = "Sva polja su obavezna.";
            return;
        }
        if (energijaStanice.compareTo(maxEnergija) > 0) {
            porukaAdmin = "Trenutna energija ne može biti veća od maksimalne.";
            return;
        }

        Stanica s = adminService.dodajStanicu(
                maxEnergija, energijaStanice, cenaPoJedinici, cenaDistribucije
        );
        if (s == null) porukaAdmin = "Greška pri unosu stanice.";
        else porukaAdmin = "Stanica dodata. ID=" + s.getId();

        maxEnergija = energijaStanice = cenaPoJedinici = cenaDistribucije = null;
        osvezi();
    }
    
    public void dopuniBalans() {
        porukaAdmin = null;

        if (vlasnikDopunaId == null) { porukaAdmin = "Izaberi vlasnika za dopunu."; return; }
        if (iznosDopune == null || iznosDopune.compareTo(BigDecimal.ZERO) <= 0) { porukaAdmin = "Iznos dopune mora biti > 0."; return; }

        Vlasnik v = adminService.dopuniBalans(vlasnikDopunaId, iznosDopune);
        if (v == null) porukaAdmin = "Dopuna nije uspela.";
        else porukaAdmin = "Balans dopunjen. Novi balans: " + v.getBalans();

        iznosDopune = null;
        osvezi();
    }



    private void osvezi() {
        vlasnici = adminService.sviVlasnici();
        auta = adminService.svaAuta();
        stanice = adminService.sveStanice();

    }

    // getters/setters
    public String getIme() { return ime; }
    public void setIme(String ime) { this.ime = ime; }
    public String getPrezime() { return prezime; }
    public void setPrezime(String prezime) { this.prezime = prezime; }

    public Integer getVlasnikId() { return vlasnikId; }
    public void setVlasnikId(Integer vlasnikId) { this.vlasnikId = vlasnikId; }

    public BigDecimal getKapacitet() { return kapacitet; }
    public void setKapacitet(BigDecimal kapacitet) { this.kapacitet = kapacitet; }

    public BigDecimal getEnergijaTrenutna() { return energijaTrenutna; }
    public void setEnergijaTrenutna(BigDecimal energijaTrenutna) { this.energijaTrenutna = energijaTrenutna; }

    public String getPorukaAdmin() { return porukaAdmin; }
    public List<Vlasnik> getVlasnici() { return vlasnici; }
    public List<Auto> getAuta() { return auta; }
    
    public BigDecimal getMaxEnergija() { return maxEnergija; }
    public void setMaxEnergija(BigDecimal x) { this.maxEnergija = x; }

    public BigDecimal getEnergijaStanice() { return energijaStanice; }
    public void setEnergijaStanice(BigDecimal x) { this.energijaStanice = x; }

    public BigDecimal getCenaPoJedinici() { return cenaPoJedinici; }
    public void setCenaPoJedinici(BigDecimal x) { this.cenaPoJedinici = x; }

    public BigDecimal getCenaDistribucije() { return cenaDistribucije; }
    public void setCenaDistribucije(BigDecimal x) { this.cenaDistribucije = x; }

    public List<Stanica> getStanice() { return stanice; }
    
    public BigDecimal getBalans() { return balans; }
    public void setBalans(BigDecimal balans) { this.balans = balans; }
    
    public Integer getVlasnikDopunaId() { return vlasnikDopunaId; }
    public void setVlasnikDopunaId(Integer vlasnikDopunaId) { this.vlasnikDopunaId = vlasnikDopunaId; }

    public BigDecimal getIznosDopune() { return iznosDopune; }
    public void setIznosDopune(BigDecimal iznosDopune) { this.iznosDopune = iznosDopune; }



}
