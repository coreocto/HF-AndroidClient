package org.coreocto.dev.hf.perfmon.aspect;

import android.util.Log;
import com.google.gson.Gson;
import okhttp3.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.coreocto.dev.hf.perfmon.internal.DebugLog;
import org.coreocto.dev.hf.perfmon.internal.StopWatch;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

/**
 * Aspect representing the cross cutting-concern: Method and Constructor Tracing.
 */
@Aspect
public class TraceAspect {

    public static final OkHttpClient httpClient = new OkHttpClient();
    public static final String REQ_STAT_URL = "stat";
    public static final String PREF_SERVER_HOSTNAME = "http://coreocto.ddns.net:8080/";
    public static final Gson GSON = new Gson();
    private static final String POINTCUT_METHOD =
            "execution(@org.coreocto.dev.hf.perfmon.annotation.DebugTrace * *(..))";
    private static final String POINTCUT_CONSTRUCTOR =
            "execution(@org.coreocto.dev.hf.perfmon.annotation.DebugTrace *.new(..))";
    private static final String TAG = "TraceAspect";
    private static final String SSE_CLIENT_SUISE = "SuiseClient";
    private static final String SSE_CLIENT_VASST = "VasstClient";

    private static volatile boolean enabled = true;

    public TraceAspect() {
        Log.d(TAG, "init of " + TAG);
    }

    public static void setEnabled(boolean enabled) {
        TraceAspect.enabled = enabled;
    }

    /**
     * Create a log message.
     *
     * @param methodName     A string with the method name.
     * @param methodDuration Duration of the method in milliseconds.
     * @return A string representing message.
     */
    private static String buildLogMessage(String methodName, long methodDuration) {
        StringBuilder message = new StringBuilder();
        message.append("PerfMon --> ");
        message.append(methodName);
        message.append(" --> ");
        message.append("[");
        message.append(methodDuration);
        message.append("ms");
        message.append("]");
        return message.toString();
    }

    @Pointcut(POINTCUT_METHOD)
    public void methodAnnotatedWithDebugTrace() {
    }

    @Pointcut(POINTCUT_CONSTRUCTOR)
    public void constructorAnnotatedDebugTrace() {
    }

    @Around("methodAnnotatedWithDebugTrace() || constructorAnnotatedDebugTrace()")
    public Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();

        //find the parameter index of the docId
        String[] paramNames = methodSignature.getParameterNames();
        Object[] paramVals = joinPoint.getArgs();

        int addInfoIdx = -1;
        for (int i = 0; i < paramNames.length; i++) {
            if (paramNames[i].equalsIgnoreCase("addInfo")) {
                addInfoIdx = i;
                break;
            }
        }

        Map<String, String> addInfo = (Map<String, String>) paramVals[addInfoIdx];

        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = joinPoint.proceed();
        stopWatch.stop();

        if (enabled) {

            final String statUrl = PREF_SERVER_HOSTNAME + REQ_STAT_URL;

            String type = className + "." + methodName;

            JSONObject statJson = new JSONObject();
            for (Map.Entry<String, String> entry : addInfo.entrySet()) {
                statJson.put(entry.getKey(), entry.getValue());
            }
            statJson.put("startTime", stopWatch.getStartTime());
            statJson.put("endTime", stopWatch.getEndTime());
            if (!statJson.has("name")) {
                statJson.put("name", "N.A.");
            }

            RequestBody requestBody = new FormBody.Builder()
                    .add("data", statJson.toString())
                    .add("type", type).build();

            Request request = new Request.Builder()
                    .url(statUrl)
                    .post(requestBody)
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "error when pushing statistics to server", e);
                    call.cancel();
                }

                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        // Handle the error
                        Log.e(TAG, "response.isSuccessful() = false");
                    }

                    response.close();
                }
            });

            DebugLog.log(className, buildLogMessage(methodName, stopWatch.getTotalTimeMillis()));
        }

        return result;
    }
}

