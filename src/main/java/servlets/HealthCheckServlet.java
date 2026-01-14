package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bittercode.util.DBUtil;

public class HealthCheckServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        PrintWriter pw = res.getWriter();

        try {
            Connection conn = DBUtil.getConnection();
            if (conn != null && !conn.isClosed()) {
                res.setStatus(HttpServletResponse.SC_OK);
                pw.println("{\"status\":\"UP\",\"database\":\"CONNECTED\"}");
            } else {
                res.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                pw.println("{\"status\":\"DOWN\",\"database\":\"DISCONNECTED\"}");
            }
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            pw.println("{\"status\":\"DOWN\",\"database\":\"ERROR\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }
}
