package com.governorapp.config.route;

import android.content.Context;
import android.util.Log;

import com.governorapp.BuildConfig;
import com.governorapp.config.Configuration;
import com.governorapp.config.Route;
import com.governorapp.server.Controller;
import com.governorapp.server.ControllerClassObjectPair;
import com.governorapp.server.ControllerFactory;

import java.lang.reflect.Method;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Dynamic method route.
 */
public class DynamicMethodRoute implements Route {
    /**
     * Controller name.
     */
    protected String controller;

    /**
     * Method name.
     */
    protected String method;

    /**
     * HTTP method.
     */
    protected String verb;

    /**
     * Regular expression for the route.
     */
    protected String routePathRegex;

    /**
     * Method parameters.
     */
    private Map<String, Class<?>> parameters;

    /**
     * Constructor (with parameters).
     *
     * @param controller The name of the controller.
     * @param method     The name of the method.
     * @param verb       The HTTP verb.
     * @param parameters The parameters to pass to the method.
     */
    public DynamicMethodRoute(String controller, String method, String verb, String routePathRegex, Map<String, Class<?>> parameters) {
        this.controller = controller;
        this.method = method;
        this.verb = verb;
        this.routePathRegex = routePathRegex;
        this.parameters = parameters;
    }

    private Object[] getParameterValues(NanoHTTPD.IHTTPSession session, Class[] parameterTypes) {
        Object[] result = new Object[parameterTypes.length];

        String[] uriParts = session.getUri().split("/");

        return result;
    }

    /**
     * Get the controller and execute the method.
     *
     * @param appContext Android application context.
     * @param config     Governor server configuration.
     * @param session    The HTTP session containing the request.
     * @return An HTTP response for NanoHttpd.
     */
    @Override
    public NanoHTTPD.Response getResponse(Context appContext, Configuration config, NanoHTTPD.IHTTPSession session) {
        try {
            ControllerClassObjectPair controllerPair = ControllerFactory.getInstance().getController(controller, appContext, config);

            Class<?> controllerClass = controllerPair.getCls();
            Controller controller = controllerPair.getController();

            Class[] parameterTypes = parameters.values().toArray(new Class[parameters.size()]);
            Object[] parameterValues = getParameterValues(session, parameterTypes);
            Method controllerMethod = controllerClass.getDeclaredMethod(method, parameterTypes);

            return (NanoHTTPD.Response) controllerMethod.invoke(controller, parameterValues);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.e("com.governorapp", "exception when executing method", e);
            }

            return new NanoHTTPD.Response(NanoHTTPD.Response.Status.INTERNAL_ERROR,
                    NanoHTTPD.MIME_PLAINTEXT, "500 Internal Error");
        }
    }
}