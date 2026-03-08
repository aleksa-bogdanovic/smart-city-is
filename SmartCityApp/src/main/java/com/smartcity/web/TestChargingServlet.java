package com.smartcity.web;


import com.smartcity.ejb.ChargingService;
import com.smartcity.jpa.Racun;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@SuppressWarnings("serial")
@WebServlet("/testCharge")
public class TestChargingServlet extends HttpServlet {

    @EJB
    private ChargingService service;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Racun r = service.napuniAuto(1, 1); // autoId=1, stanicaId=1

        resp.setContentType("text/plain; charset=UTF-8");
        if (r == null) {
            resp.getWriter().println("Punjenje NIJE izvrseno (nema potrebe / nema energije / ID ne postoji).");
        } else {
            resp.getWriter().println("Punjenje OK. Racun upisan. ID=" + r.getId());
        }
    }
}
