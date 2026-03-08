# Smart City IS — EV Charging Station System

> University project for the **Information Systems** course  
> Faculty of Electronic Engineering, University of Niš — 2024/2025

A Java EE web application that simulates a smart city information system for managing autonomous electric vehicle (EV) charging stations and their owners.

---

## 📋 About the Project

The system tracks:
- **Charging stations** — max energy capacity, current energy level, price per unit, distribution cost, identifier
- **Vehicles & owners** — owner name, current energy, energy capacity, account balance

Key feature: **Charging simulation** — the system simulates charging a vehicle at a station and automatically generates a bill using the formula:

```
Total = quantity_of_energy × unit_price + distribution_cost
```

---

## 🏗️ Architecture

```
SmartCityApp/
├── src/
│   └── com/smartcity/
│       ├── jpa/          # JPA Entities
│       │   ├── Stanica.java     (Charging Station)
│       │   ├── Auto.java        (Vehicle)
│       │   ├── Vlasnik.java     (Owner)
│       │   └── Racun.java       (Bill)
│       ├── ejb/          # Business Logic
│       │   ├── ChargingService.java
│       │   └── AdminService.java
│       └── web/          # JSF Backing Beans
│           ├── ChargingBean.java
│           └── AdminBean.java
└── WebContent/           # JSF Pages (.xhtml)
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java EE 7 |
| Presentation | JSF (JavaServer Faces) |
| Business Logic | EJB (Enterprise JavaBeans) |
| Persistence | JPA (Java Persistence API) |
| Database | SQL (via JPA) |
| IDE | Eclipse |
| Server | GlassFish / WildFly |

---

## ⚙️ How to Run

### Prerequisites
- Java JDK 8+
- Eclipse IDE for Enterprise Java
- GlassFish or WildFly application server

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/aleksa-bogdanovic/smart-city-is.git
   cd smart-city-is
   ```

2. **Import into Eclipse**
   - File → Import → Existing Projects into Workspace
   - Select the `SmartCityApp` folder

3. **Configure the server**
   - Add GlassFish/WildFly to Eclipse servers
   - Set up a datasource in `persistence.xml`

4. **Deploy & Run**
   - Right-click project → Run As → Run on Server

---

## 📸 Features

- ✅ View and manage charging stations
- ✅ View and manage vehicle owners
- ✅ Simulate charging process
- ✅ Automatic bill generation
- ✅ Admin panel for system management

---

## 👤 Author

**Aleksa Bogdanović**  
[LinkedIn](https://www.linkedin.com/in/aleksa-bogdanovic-b00423210/) · [GitHub](https://github.com/aleksa-bogdanovic)
