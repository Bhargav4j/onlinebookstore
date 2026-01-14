package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bittercode.util.DBUtil;

public class HealthCheckServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        boolean isDatabaseHealthy = checkDatabaseConnection();

        if (isDatabaseHealthy) {
            response.setStatus(HttpServletResponse.SC_OK);
            out.println("{\"status\":\"UP\",\"database\":\"connected\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            out.println("{\"status\":\"DOWN\",\"database\":\"disconnected\"}");
        }

        out.flush();
    }

    private boolean checkDatabaseConnection() {
        Connection conn = null;
        try {
            conn = DBUtil.provideConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        } finally {
            if (conn != null) {
                try {
                    DBUtil.closeConnection(conn);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
