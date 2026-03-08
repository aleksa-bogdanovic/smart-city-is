package com.smartcity.ejb;

import com.smartcity.jpa.Auto;
import com.smartcity.jpa.Racun;
import com.smartcity.jpa.Stanica;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Stateless
public class ChargingService {
	
	 @PersistenceContext(unitName = "SmartCityPU") // mora isto ime kao u persistence.xml
	    private EntityManager em;

	
	public List<Racun> sviRacuni() {
	    return em.createQuery("SELECT r FROM Racun r ORDER BY r.id DESC", Racun.class)
	             .getResultList();
	}
	public List<Stanica> sveStanice() {
	    return em.createQuery("select s from Stanica s", Stanica.class)
	            .getResultList();
	}



	public Racun napuniAuto(int autoId, int stanicaId) {
	    Auto auto = em.find(Auto.class, autoId);
	    Stanica stanica = em.find(Stanica.class, stanicaId);
	    if (auto == null || stanica == null) return null;

	    BigDecimal potreba = auto.getKapacitet().subtract(auto.getEnergijaTrenutna());
	    if (potreba.compareTo(BigDecimal.ZERO) <= 0) return null;

	    BigDecimal dostupno = stanica.getEnergijaTrenutna();
	    if (dostupno.compareTo(BigDecimal.ZERO) <= 0) return null;

	    // vlasnik i balans
	    if (auto.getVlasnik() == null) return null;
	    BigDecimal balans = auto.getVlasnik().getBalans();
	    if (balans == null) return null;

	    BigDecimal cenaPoJedinici = stanica.getCenaPoJedinici();
	    BigDecimal distribucija = stanica.getCenaDistribucije();

	    // mora da ima bar za distribuciju
	    if (balans.compareTo(distribucija) < 0)
	    	  throw new RuntimeException("Nema dovoljno balansa za cenu distribucije (" + distribucija + ").");
	    
	    if (cenaPoJedinici == null || cenaPoJedinici.compareTo(BigDecimal.ZERO) <= 0) return null;
	    if (distribucija == null || distribucija.compareTo(BigDecimal.ZERO) < 0) return null;

	    // maksimalno kolicine po balansu: (balans - distribucija) / cenaPoJedinici
	    BigDecimal maxPoBalansu = balans.subtract(distribucija)
	            .divide(cenaPoJedinici, 10, RoundingMode.DOWN);

	    if (maxPoBalansu.compareTo(BigDecimal.ZERO) <= 0) return null;

	    // stvarna kolicina je minimum: potreba, dostupno na stanici, i limit po balansu
	    BigDecimal kolicina = potreba.min(dostupno).min(maxPoBalansu);
	    if (kolicina.compareTo(BigDecimal.ZERO) <= 0) return null;
	    
	    kolicina = kolicina.setScale(2, RoundingMode.DOWN);
	    // cena
	    BigDecimal ukupno = kolicina.multiply(cenaPoJedinici)
	            .add(distribucija)
	            .setScale(2, RoundingMode.HALF_UP);
	    
	    if (balans.compareTo(ukupno) < 0) return null;

	    // update energije
	    auto.setEnergijaTrenutna(auto.getEnergijaTrenutna().add(kolicina));
	    stanica.setEnergijaTrenutna(stanica.getEnergijaTrenutna().subtract(kolicina));

	    // naplata vlasniku
	    auto.getVlasnik().setBalans(balans.subtract(ukupno).setScale(2, RoundingMode.HALF_UP));

	    // racun
	    Racun r = new Racun();
	    r.setAuto(auto);
	    r.setStanica(stanica);
	    r.setKolicina(kolicina.setScale(2, RoundingMode.HALF_UP));
	    r.setJedinicnaCena(cenaPoJedinici);
	    r.setCenaDistribucije(distribucija);
	    r.setUkupno(ukupno);

	    em.persist(r);
	    return r;
	}

}

