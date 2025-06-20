package control;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import java.io.IOException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public LoginServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String emailFromCookie = null;
        String passwordFromCookie = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("rememberGuestEmail".equals(cookie.getName())) {
                    emailFromCookie = cookie.getValue();
                }
                if ("rememberGuestPassword".equals(cookie.getName())) {
                    passwordFromCookie = cookie.getValue();
                }
            }
        }

        if (emailFromCookie != null) {
            request.setAttribute("prefilledEmail", emailFromCookie);
        }
        if (passwordFromCookie != null) {
            request.setAttribute("prefilledPassword", passwordFromCookie);
        }

        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Email e password non possono essere vuoti.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        if ("on".equals(rememberMe)) {
            Cookie emailCookie = new Cookie("rememberGuestEmail", email);
            emailCookie.setMaxAge(60 * 60 * 24 * 7);
            emailCookie.setPath("/");
            response.addCookie(emailCookie);

            Cookie passwordCookie = new Cookie("rememberGuestPassword", password);
            passwordCookie.setMaxAge(60 * 60 * 24 * 7);
            passwordCookie.setPath("/");
            response.addCookie(passwordCookie);
        } else {
            Cookie emailCookie = new Cookie("rememberGuestEmail", "");
            emailCookie.setMaxAge(0);
            emailCookie.setPath("/");
            response.addCookie(emailCookie);

            Cookie passwordCookie = new Cookie("rememberGuestPassword", "");
            passwordCookie.setMaxAge(0);
            passwordCookie.setPath("/");
            response.addCookie(passwordCookie);
        }

        response.sendRedirect(request.getContextPath() + "/loginSuccess.jsp");
    }
}