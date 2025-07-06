package control;

import model.utente.UtenteBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoggedFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);
        UtenteBean currentUser = (UtenteBean) (session != null ? session.getAttribute("user") : null);

        boolean isLoggedIn = currentUser != null;

        if (isLoggedIn) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/CatalogoServlet");
        } else {
            chain.doFilter(request, response);
        }
    }
}
