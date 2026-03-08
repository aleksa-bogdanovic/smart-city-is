package com.smartcity.ejb;

import com.smartcity.jpa.Auto;
import com.smartcity.jpa.Stanica;
import com.smartcity.jpa.Vlasnik;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.List;

/**
 * Copied from the app (EJB annotations removed for pure unit-test usage).
 * In WildFly, EntityManager is injected via @PersistenceContext.
 * In tests we set it manually via setEntityManager().
 */
public class AdminService {

    @PersistenceContext
    private EntityManager em;

    /** For unit tests (no container). */
    public void setEntityManager(EntityManager em) { this.em = em; }

    public Vlasnik dodajVlasnika(String ime, String prezime, BigDecimal balans) {
        Vlasnik v = new Vlasnik();
        v.setIme(ime);
        v.setPrezime(prezime);
        v.setBalans(balans == null ? BigDecimal.ZERO : balans);
        em.persist(v);
        return v;
    }

    public Vlasnik dopuniBalans(Integer vlasnikId, BigDecimal iznos) {
        Vlasnik v = em.find(Vlasnik.class, vlasnikId);
        if (v == null) return null;
        if (iznos == null || iznos.compareTo(BigDecimal.ZERO) <= 0) return null;

        v.setBalans(v.getBalans().add(iznos));
        return v;
    }

    public Auto dodajAuto(Integer vlasnikId, BigDecimal kapacitet, BigDecimal energijaTrenutna) {
        Vlasnik v = em.find(Vlasnik.class, vlasnikId);
        if (v == null) return null;

        Auto a = new Auto();
        a.setVlasnik(v);
        a.setKapacitet(kapacitet);
        a.setEnergijaTrenutna(energijaTrenutna);
        em.persist(a);
        return a;
    }

    public Stanica dodajStanicu(BigDecimal max, BigDecimal tren, BigDecimal cena, BigDecimal distrib) {
        if (max == null || tren == null || cena == null || distrib == null) return null;
        if (tren.compareTo(max) > 0) return null;

        Stanica s = new Stanica();
        s.setMaxEnergija(max);
        s.setEnergijaTrenutna(tren);
        s.setCenaPoJedinici(cena);
        s.setCenaDistribucije(distrib);
        em.persist(s);
        return s;
    }

    public List<Vlasnik> sviVlasnici() {
        return em.createQuery("select v from Vlasnik v order by v.id asc", Vlasnik.class)
                .getResultList();
    }

    public List<Auto> svaAuta() {
        return em.createQuery("select a from Auto a order by a.id asc", Auto.class)
                .getResultList();
    }

    public List<Stanica> sveStanice() {
        return em.createQuery("select s from Stanica s order by s.id asc", Stanica.class)
                .getResultList();
    }
}
