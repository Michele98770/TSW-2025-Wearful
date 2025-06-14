package control;
import model.ConnectionPool;
import model.EmptyPoolException;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet("/hello-servlet")
public class HelloServlet extends HttpServlet {


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String message= (String) request.getParameter("message");

        response.setContentType("text/html");

        // Hello ciap
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>"+message+"</h1>");
        try {
            System.out.println(ConnectionPool.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        out.println("</body></html>");
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{

    }


}