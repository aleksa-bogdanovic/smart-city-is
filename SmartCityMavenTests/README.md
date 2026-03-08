SmartCity Core (Entities + Services + JUnit Tests)
=================================================

This is a standalone Maven project that contains:
- JPA entities: Vlasnik, Auto, Stanica, Racun
- Services: AdminService, ChargingService (no JSF controllers)
- JUnit tests for 3 required test cases:
  1) Uspešno punjenje vozila
  2) Neuspešno punjenje zbog nedovoljnog balansa
  3) Dopuna balansa vlasnika

How to run tests:
----------------
1) Open terminal in this folder
2) Run:
   mvn test

Notes:
------
- Uses in-memory H2 database (MODE=MySQL) via persistence.xml.
- No WildFly / Docker needed to run tests.
