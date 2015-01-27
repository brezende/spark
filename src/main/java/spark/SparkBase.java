package spark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.route.RouteMatcherFactory;
import spark.route.SimpleRouteMatcher;
import spark.webserver.SparkServer;

/**
 * Spark base class
 */
public abstract class SparkBase {
    protected static boolean initialized = false;

    protected static final String DEFAULT_ACCEPT_TYPE = "*/*";

    protected static SparkServer server;
    protected static SimpleRouteMatcher routeMatcher = RouteMatcherFactory.get();
    private static boolean runFromServlet;

    /**
     * Stops the Spark server and clears all routes
     */
    public static synchronized void stop() {
        if (server != null) {
            routeMatcher.clearRoutes();
            server.stop();
        }
        initialized = false;
    }

    static synchronized void runFromServlet() {
        runFromServlet = true;
        if (!initialized) {
            routeMatcher = RouteMatcherFactory.get();
            initialized = true;
        }
    }

    /**
     * Wraps the route in RouteImpl
     *
     * @param path  the path
     * @param route the route
     * @return the wrapped route
     */
    protected static RouteImpl wrap(final String path, final Route route) {
        return wrap(path, DEFAULT_ACCEPT_TYPE, route);
    }

    /**
     * Wraps the route in RouteImpl
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param route      the route
     * @return the wrapped route
     */
    protected static RouteImpl wrap(final String path, String acceptType, final Route route) {
        if (acceptType == null) {
            acceptType = DEFAULT_ACCEPT_TYPE;
        }
        RouteImpl impl = new RouteImpl(path, acceptType) {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                return route.handle(request, response);
            }
        };
        return impl;
    }

    /**
     * Wraps the filter in FilterImpl
     *
     * @param path   the path
     * @param filter the filter
     * @return the wrapped route
     */
    protected static FilterImpl wrap(final String path, final Filter filter) {
        return wrap(path, DEFAULT_ACCEPT_TYPE, filter);
    }

    /**
     * Wraps the filter in FilterImpl
     *
     * @param path       the path
     * @param acceptType the accept type
     * @param filter     the filter
     * @return the wrapped route
     */
    protected static FilterImpl wrap(final String path, String acceptType, final Filter filter) {
        if (acceptType == null) {
            acceptType = DEFAULT_ACCEPT_TYPE;
        }
        FilterImpl impl = new FilterImpl(path, acceptType) {
            @Override
            public void handle(Request request, Response response) throws Exception {
                filter.handle(request, response);
            }
        };
        return impl;
    }

    protected static void addRoute(String httpMethod, RouteImpl route) {
        routeMatcher.parseValidateAddRoute(httpMethod + " '" + route.getPath()
                                                   + "'", route.getAcceptType(), route);
    }

    protected static void addFilter(String httpMethod, FilterImpl filter) {
        routeMatcher.parseValidateAddRoute(httpMethod + " '" + filter.getPath()
                                                   + "'", filter.getAcceptType(), filter);
    }

    public static synchronized void init() {
        if (!initialized) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    server = new SparkServer();
                    server.ignite();
                }
            }).start();
            initialized = true;
        }
    }

}
