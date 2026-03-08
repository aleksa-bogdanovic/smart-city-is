package com.smartcity.ejb;

import com.smartcity.jpa.Auto;
import com.smartcity.jpa.Racun;
import com.smartcity.jpa.Stanica;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Copied from the app (EJB annotations removed for pure unit-test usage).
 * In WildFly, EntityManager is injected via @PersistenceContext.
 * In tests we set it manually via setEntityManager().
 */
public class ChargingService {

    @PersistenceContext(unitName = "SmartCityPU")
    private EntityManager em;

    /** For unit tests (no container). */
    public void setEntityManager(EntityManager em) { this.em = em; }

    public List<Racun> sviRacuni() {
        return em.createQuery("SELECT r FROM Racun r ORDER BY r.id DESC", Racun.class)
                .getResultList();
    }

    public List<Stanica> sveStanice() {
        return em.createQuery("select s from Stanica s order by s.id asc", Stanica.class)
                .getResultList();
    }

    /**
     * Charging with balance check (as in your final implementation).
     * Returns null if charging cannot be executed.
     */
    public Racun napuniAuto(int autoId, int stanicaId) {
        Auto auto = em.find(Auto.class, autoId);
        Stanica stanica = em.find(Stanica.class, stanicaId);
        if (auto == null || stanica == null) return null;

        BigDecimal potreba = auto.getKapacitet().subtract(auto.getEnergijaTrenutna());
        if (potreba.compareTo(BigDecimal.ZERO) <= 0) return null;

        BigDecimal dostupno = stanica.getEnergijaTrenutna();
        if (dostupno.compareTo(BigDecimal.ZERO) <= 0) return null;

        if (auto.getVlasnik() == null) return null;
        BigDecimal balans = auto.getVlasnik().getBalans();
        if (balans == null) return null;

        BigDecimal cenaPoJedinici = stanica.getCenaPoJedinici();
        BigDecimal distribucija = stanica.getCenaDistribucije();
        if (cenaPoJedinici == null || cenaPoJedinici.compareTo(BigDecimal.ZERO) <= 0) return null;
        if (distribucija == null || distribucija.compareTo(BigDecimal.ZERO) < 0) return null;

        if (balans.compareTo(distribucija) < 0) return null;

        BigDecimal maxPoBalansu = balans.subtract(distribucija)
                .divide(cenaPoJedinici, 10, RoundingMode.DOWN);

        if (maxPoBalansu.compareTo(BigDecimal.ZERO) <= 0) return null;

        BigDecimal kolicina = potreba.min(dostupno).min(maxPoBalansu);
        if (kolicina.compareTo(BigDecimal.ZERO) <= 0) return null;

        kolicina = kolicina.setScale(2, RoundingMode.DOWN);

        BigDecimal ukupno = kolicina.multiply(cenaPoJedinici)
                .add(distribucija)
                .setScale(2, RoundingMode.HALF_UP);

        if (balans.compareTo(ukupno) < 0) return null;

        // update energies
        auto.setEnergijaTrenutna(auto.getEnergijaTrenutna().add(kolicina));
        stanica.setEnergijaTrenutna(stanica.getEnergijaTrenutna().subtract(kolicina));

        // charge owner
        auto.getVlasnik().setBalans(balans.subtract(ukupno).setScale(2, RoundingMode.HALF_UP));

        Racun r = new Racun();
        r.setAuto(auto);
        r.setStanica(stanica);
        r.setKolicina(kolicina);
        r.setJedinicnaCena(cenaPoJedinici);
        r.setCenaDistribucije(distribucija);
        r.setUkupno(ukupno);

        em.persist(r);
        return r;
    }
}
