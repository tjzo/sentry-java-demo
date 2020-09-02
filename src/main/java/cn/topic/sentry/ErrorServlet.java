package cn.topic.sentry;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import io.sentry.Sentry;
import io.sentry.SentryClient;
import io.sentry.context.Context;
import io.sentry.event.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ErrorServlet extends HttpServlet {
    private static final AtomicInteger counter = new AtomicInteger(1);
    ExecutorService executorService = Executors.newFixedThreadPool(4);

    public void init() throws ServletException {
        String dsn = "http://e45e27b11b6448d2b2a7b428d85a1065@127.0.0.1:9000/2";
        Sentry.init(dsn);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processError(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processError(request, response);
    }

    public void doHead(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processError(request, response);
    }

    public void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processError(request, response);
    }

    public void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processError(request, response);
    }

    public void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processError(request, response);
    }

    public void doTrace(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processError(request, response);
    }

    public static String convertException(Throwable t, Integer statusCode) {
        return new Gson().toJson(new ImmutableMap.Builder<String, Object>()
                .put("msg", t.getMessage())
                .put("code", statusCode)
                .build());
    }

    private void processError(HttpServletRequest request,
                              HttpServletResponse response) throws IOException {
        // 从request中获取错误信息
        Throwable throwable = (Throwable) request
                .getAttribute("javax.servlet.error.exception");
        Integer statusCode = (Integer) request
                .getAttribute("javax.servlet.error.status_code");

        // 设置一些额外的上下文，这段逻辑应该放到auth之后的某个filter/interceptor
        int uid = counter.getAndIncrement();
        // 用户信息，id，username，ip，email
        Sentry.getContext().setUser(new User("id" + uid, "name" + uid, "192.168.0." + uid, "email" + uid));
        // 请求id
        Sentry.getContext().setLastEventId(UUID.randomUUID());
        Sentry.getContext().addExtra("extra1", "extra_v1");
        Sentry.getContext().addExtra("extra2", "extra_v2");
        Sentry.getContext().addTag("tag1", "tag_v1");
        Sentry.getContext().addTag("tag2", "tag_v2");

        // 发送错误信息，级别为error
        Sentry.capture(throwable);

        // 级别为info
        Sentry.capture("1");

        // 获取当前上下文的方法
        SentryClient client = Sentry.getStoredClient();
        Context context = Sentry.getContext();

        // 在异步线程池中发送信息，会丢失上下文
        executorService.submit(() -> client.sendMessage("3"));

        // 报错信息以json格式返回
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.write(convertException(throwable, statusCode));
    }
}
