package spark.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.route.HttpMethod;
import spark.route.RouteEntry;
import spark.route.SimpleRouteMatcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DocumentationRoute implements Route {

	private Spark spark;
	
	public DocumentationRoute(Spark spark) {
		this.spark = spark;
	}
	
	@Override
	public Object handle(Request request, Response response) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Map<String, Object> docs = new HashMap<String, Object>();
		fillBasicFields(docs);
		docs.put("paths", getPaths());
		docs.put("definitions", getDefinitions());
		docs.put("info", getInfo());
		return gson.toJson(docs);
	}

	private void fillBasicFields(Map<String, Object> docs) {
		docs.put("swagger", "2.0");
	}

	private Map<String, Object> getPaths() {
		SimpleRouteMatcher routeMatcher = spark.getRouteMatcher();
		List<RouteEntry> routes = routeMatcher.getRoutes();
		Map<String, Object> paths = new HashMap<String, Object>();
		for (RouteEntry route: routes) {
			String path = route.path;
			HttpMethod httpMethod = route.httpMethod;
			Map<String, Object> methods = mapGetOrCreate(paths, path);
			String methodName = httpMethod.toString();
			methods.put(methodName, getEndpoint(route));
		}
		return paths;
	}

	private Map<String, Object> getEndpoint(RouteEntry route) {
		Map<String, Object> endpoint = new HashMap<String, Object>();
		endpoint.put("description", String.format("The %s - %s endpoint", route.httpMethod, route.path));
		return endpoint;
	}

	private Map<String, Object> getDefinitions() {
		Map<String, Object> definitions = new HashMap<String, Object>();
		return definitions;
	}

	private Map<String, Object> getInfo() {
		Map<String, Object> info = new HashMap<String, Object>();
		return info;
	}

	private static Map<String, Object> mapGetOrCreate(Map<String, Object> getFrom, String key) {
		Map<String, Object> map = (Map<String, Object>) getFrom.get(key);
		if (map == null) {
			map = new HashMap<String, Object>();
			getFrom.put(key, map);
		}
		return map;
	}

}
