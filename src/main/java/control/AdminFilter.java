package control;

import model.utente.UtenteBean;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;


public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);
        UtenteBean currentUser = (UtenteBean)session.getAttribute("user");

        boolean isLoggedIn=false;
        boolean isAdmin=false;

        if(currentUser != null) {
            isLoggedIn = true;
            isAdmin = currentUser.isAdmin();
        }

        if (!isLoggedIn || !isAdmin) {
            System.out.println("AdminFilter: Access denied. Redirecting to login.");
            httpResponse.sendRedirect("403.jsp");
        }
        else {
            chain.doFilter(request, response);
        }
    }

}