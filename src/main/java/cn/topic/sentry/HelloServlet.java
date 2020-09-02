package cn.topic.sentry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static com.google.common.base.Strings.isNullOrEmpty;

public class HelloServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String a = request.getParameter("a");

        if (!isNullOrEmpty(a)) {
            throw new IOException() {
                @Override
                public String getMessage() {
                    return a;
                }
            };
        }

        String b = request.getParameter("b");
        if (!isNullOrEmpty(b)) {
            throw new ServletException(b);
        }

        String c = request.getParameter("c");

        if (!isNullOrEmpty(c)) {
            throw new RuntimeException(c);
        }

        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        out.println("<h1>Success</h1>");
    }
}
