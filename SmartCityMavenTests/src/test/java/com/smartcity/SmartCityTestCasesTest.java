package com.smartcity;

import com.smartcity.ejb.AdminService;
import com.smartcity.ejb.ChargingService;
import com.smartcity.jpa.Auto;
import com.smartcity.jpa.Racun;
import com.smartcity.jpa.Stanica;
import com.smartcity.jpa.Vlasnik;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for required test cases:
 * 1) Uspešno punjenje vozila
 * 2) Neuspešno punjenje zbog nedovoljnog balansa
 * 3) Dopuna balansa vlasnika
 *
 * NOTE:
 * We do NOT rely on fixed autoId/stanicaId values because IDENTITY counters may not reset
 * consistently across DBs. Instead we seed known entities and keep their generated IDs.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SmartCityTestCasesTest {

    private static EntityManagerFactory emf;

    private EntityManager em;
    private AdminService adminService;
    private ChargingService chargingService;

    // IDs of seeded entities (set in resetAndSeed)
    private int vlasnikId;
    private int autoId;
    private int stanicaId;

    @BeforeAll
    static void beforeAll() {
        emf = Persistence.createEntityManagerFactory("SmartCityPU");
    }

    @AfterAll
    static void afterAll() {
        if (emf != null) emf.close();
    }

    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        adminService = new AdminService();
        chargingService = new ChargingService();
        adminService.setEntityManager(em);
        chargingService.setEntityManager(em);

        resetAndSeed();
    }

    @AfterEach
    void tearDown() {
        if (em != null) em.close();
    }

    private void resetAndSeed() {
        em.getTransaction().begin();

        // delete in FK-safe order (JPA-friendly, no native SQL keywords like AUTO)
        em.createQuery("delete from Racun").executeUpdate();
        em.createQuery("delete from Auto").executeUpdate();
        em.createQuery("delete from Stanica").executeUpdate();
        em.createQuery("delete from Vlasnik").executeUpdate();

        // Seed Vlasnik (balance 5000.00)
        Vlasnik v = new Vlasnik();
        v.setIme("Test");
        v.setPrezime("Vlasnik");
        v.setBalans(new BigDecimal("5000.00"));
        em.persist(v);

        // Seed Stanica: energy 500.00 (also price/distribution needed for calculation)
        Stanica s = new Stanica();
        s.setMaxEnergija(new BigDecimal("1000.00"));
        s.setEnergijaTrenutna(new BigDecimal("500.00"));
        s.setCenaPoJedinici(new BigDecimal("50.00"));
        s.setCenaDistribucije(new BigDecimal("100.00"));
        em.persist(s);

        // Seed Auto: capacity 60, current 10 (needs 50)
        Auto a = new Auto();
        a.setVlasnik(v);
        a.setKapacitet(new BigDecimal("60.00"));
        a.setEnergijaTrenutna(new BigDecimal("10.00"));
        em.persist(a);

        em.getTransaction().commit();

        // save IDs
        vlasnikId = v.getId();
        stanicaId = s.getId();
        autoId = a.getId();

        assertTrue(vlasnikId > 0);
        assertTrue(stanicaId > 0);
        assertTrue(autoId > 0);
    }

    /**
     * Test case 1: Uspešno punjenje vozila
     */
    @Test
    @Order(1)
    void testCase1_uspesnoPunjenje() {
        em.getTransaction().begin();

        Racun r = chargingService.napuniAuto(autoId, stanicaId);
        assertNotNull(r, "Racun mora biti kreiran");

        em.flush();
        assertNotNull(r.getId(), "Racun mora imati ID");

        Auto a = em.find(Auto.class, autoId);
        Stanica s = em.find(Stanica.class, stanicaId);
        Vlasnik v = em.find(Vlasnik.class, vlasnikId);

        // potreba = 60-10 = 50, station has 500 => kolicina=50
        assertEquals(new BigDecimal("60.00"), a.getEnergijaTrenutna().setScale(2));
        assertEquals(new BigDecimal("450.00"), s.getEnergijaTrenutna().setScale(2));

        // ukupno = 50*50 + 100 = 2600 => balance: 5000-2600 = 2400
        assertEquals(new BigDecimal("2400.00"), v.getBalans().setScale(2));

        // record exists
        List<Racun> racuni = chargingService.sviRacuni();
        assertEquals(1, racuni.size());

        em.getTransaction().commit();
    }

    /**
     * Test case 2: Neuspešno punjenje zbog nedovoljnog balansa
     */
    @Test
    @Order(2)
    void testCase2_nedovoljanBalans() {
        em.getTransaction().begin();

        // set balance to 0.00
        Vlasnik v = em.find(Vlasnik.class, vlasnikId);
        v.setBalans(new BigDecimal("0.00"));

        Auto aBefore = em.find(Auto.class, autoId);
        Stanica sBefore = em.find(Stanica.class, stanicaId);
        BigDecimal autoEnergyBefore = aBefore.getEnergijaTrenutna();
        BigDecimal stationEnergyBefore = sBefore.getEnergijaTrenutna();

        Racun r = chargingService.napuniAuto(autoId, stanicaId);
        assertNull(r, "Punjenje ne sme da prodje bez balansa");

        // ensure no changes
        Auto a = em.find(Auto.class, autoId);
        Stanica s = em.find(Stanica.class, stanicaId);
        assertEquals(autoEnergyBefore, a.getEnergijaTrenutna());
        assertEquals(stationEnergyBefore, s.getEnergijaTrenutna());

        // no new invoice
        assertEquals(0, chargingService.sviRacuni().size());

        em.getTransaction().commit();
    }

    /**
     * Test case 3: Dopuna balansa vlasnika
     */
    @Test
    @Order(3)
    void testCase3_dopunaBalansa() {
        em.getTransaction().begin();

        Vlasnik v = em.find(Vlasnik.class, vlasnikId);
        assertEquals(new BigDecimal("5000.00"), v.getBalans().setScale(2));

        Vlasnik updated = adminService.dopuniBalans(vlasnikId, new BigDecimal("2000.00"));
        assertNotNull(updated);

        em.flush();
        Vlasnik reloaded = em.find(Vlasnik.class, vlasnikId);
        assertEquals(new BigDecimal("7000.00"), reloaded.getBalans().setScale(2));

        em.getTransaction().commit();
    }
}
