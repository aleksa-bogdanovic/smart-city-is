package com.smartcity.web;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.smartcity.ejb.ChargingService;
import com.smartcity.jpa.Racun;
import com.smartcity.jpa.Stanica;

@SuppressWarnings("serial")
@Named
@ViewScoped
public class ChargingBean implements Serializable {

    private int autoId;
    private int stanicaId;
    private String poruka;
    private Integer izabranaStanicaId;
    private List<Stanica> stanice;

    private List<Racun> racuni;

    @EJB
    private ChargingService service;

    @PostConstruct
    public void init() {
        osveziRacune();
        stanice = service.sveStanice();

    }

    public void napuni() {
    	 try {
    	        Racun r = service.napuniAuto(autoId, izabranaStanicaId);
    	        poruka = "Punjenje OK. Racun ID=" + r.getId();
    	    } catch (Exception e) {
    	        poruka = e.getMessage();
    	    }
    	    osveziRacune();
    }

    private void osveziRacune() {
        racuni = service.sviRacuni();
    }
    
    public String napuniRedirect() {
        napuni();
        return "home?faces-redirect=true";
    }


    // getters/setters
    public int getAutoId() { return autoId; }
    public void setAutoId(int autoId) { this.autoId = autoId; }
    public int getStanicaId() { return stanicaId; }
    public void setStanicaId(int stanicaId) { this.stanicaId = stanicaId; }
    public String getPoruka() { return poruka; }
    public List<Racun> getRacuni() { return racuni; }
    public Integer getIzabranaStanicaId() { return izabranaStanicaId; }
    public void setIzabranaStanicaId(Integer x) { this.izabranaStanicaId = x; }
    public List<Stanica> getStanice() { return stanice; }

}
