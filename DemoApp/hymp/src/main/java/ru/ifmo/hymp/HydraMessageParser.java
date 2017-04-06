package ru.ifmo.hymp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.Set;

import okhttp3.Headers;
import retrofit2.adapter.rxjava.Result;
import ru.ifmo.hymp.entities.Link;
import ru.ifmo.hymp.entities.Property;
import ru.ifmo.hymp.entities.Resource;
import ru.ifmo.hymp.net.ApiClient;
import ru.ifmo.hymp.utils.StringUtils;
import ru.ifmo.hymp.utils.rx.NetworkResultTransformer;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

public class HydraMessageParser implements Parser {
    private static final String HEADER_LINK = "Link";
    private static final String HEADER_LINK_API_DOC = "rel=\"http://www.w3.org/ns/hydra/core#apiDocumentation\"";

    public HydraMessageParser(String entryPoint) {
        ApiClient.initApiService(entryPoint);
    }

    /**
     * Load resource from API and parse to {@link Resource} - internal resource representation
     * <p>
     * 1. Load resource from API using url param
     * 2. Extract Link header from response and load ApiDoc
     * 3. Load @context for resource
     * 4. Parse resource to internal representation
     *
     * @param url that point to resource
     * @return internal resource representation
     */
    @Override
    public Observable<Resource> loadAndParseResource(String url) {
        return loadResource(url)
                .flatMap(new Func1<Result<JsonObject>, Observable<Bundle>>() {
                    @Override
                    public Observable<Bundle> call(Result<JsonObject> resResult) {
                        Headers headers = resResult.response().headers();
                        final JsonObject res = resResult.response().body();

                        return Observable.zip(loadContextForResource(res), loadApiDoc(headers),
                                new Func2<Result<JsonObject>, Result<JsonObject>, Bundle>() {
                                    @Override
                                    public Bundle call(Result<JsonObject> contextRes, Result<JsonObject> apiDocRes) {
                                        JsonObject context = contextRes.response().body();
                                        JsonObject apiDoc = apiDocRes.response().body();
                                        return new Bundle(res, context, apiDoc);
                                    }
                                });
                    }
                })
                .map(new Func1<Bundle, Resource>() {
                    @Override
                    public Resource call(Bundle bundle) {
                        return parseToInternalResource(bundle);
                    }
                });
    }

    /**
     * Load resource from api by url
     *
     * @param url that point to resource
     * @return {@link Observable} with resource that represents as {@link JsonObject}
     */
    private Observable<Result<JsonObject>> loadResource(String url) {
        return ApiClient.getApiService().load(url)
                .compose(new NetworkResultTransformer());
    }

    /**
     * Retrieve Link Header from response headers and load Api documentation
     *
     * @param headers of response
     * @return {@link Observable} with Api doc that represents as {@link JsonObject}
     */
    private Observable<Result<JsonObject>> loadApiDoc(Headers headers) {
        String linkHeader = headers.get(HEADER_LINK);

        if (StringUtils.isEmpty(linkHeader)) {
            throw new RuntimeException("Link header is empty");
        }

        if (!linkHeader.contains(HEADER_LINK_API_DOC)) {
            throw new RuntimeException("There is no " + HEADER_LINK_API_DOC);
        }

        String apiDocUrl = linkHeader.substring(1, linkHeader.indexOf(">"));
        return ApiClient.getApiService().load(apiDocUrl)
                .compose(new NetworkResultTransformer());
    }

    /**
     * Retrieve @context link from resource and load @context information for resource
     *
     * @param res for with @context loads
     * @return {@link Observable} with @context that represents as {@link JsonObject}
     */
    private Observable<Result<JsonObject>> loadContextForResource(JsonObject res) {
        String contextUrl = res.get("@context").getAsString();
        if (StringUtils.isEmpty(contextUrl)) {
            throw new RuntimeException("@context link is empty");
        }

        return ApiClient.getApiService().load(contextUrl)
                .compose(new NetworkResultTransformer());
    }

    private Resource parseToInternalResource(Bundle bundle) {
        JsonObject res = bundle.res;
        JsonObject context = bundle.context;
        JsonObject apiDoc = bundle.apiDoc;

        // define resource type and id
        String resId = res.get("@id").getAsString();
        String resType = res.get("@type").getAsString();

        // create internal resource that represent api resource
        Resource internalRes = new Resource(resId, resType);

        JsonObject apiDocClass = getSupportedClassFromApiDoc(apiDoc, resType);
        if (apiDocClass == null) {
            throw new RuntimeException("Class with id: " + resType + " not found");
        }

        boolean resSubClassOfCollection = isResSubClassOfCollection(apiDocClass);

        if (!resSubClassOfCollection) {
            Set<Map.Entry<String, JsonElement>> terms = context.getAsJsonObject("@context").entrySet();
            for (Map.Entry<String, JsonElement> term : terms) {
                String resProperty = term.getKey();
                if (resProperty.startsWith("@") || resProperty.equals("hydra")) {
                    continue;
                }

                JsonElement resPropertyValue = getResourcePropertyValue(res, resProperty);
                if (resPropertyValue == null) {
                    continue;
                }

                String apiDocProperty = getContextTermValue(term.getValue());

                JsonObject apiDocPropertyObject = getSupportedPropertyFromApiDocClass(apiDocClass, apiDocProperty);
                if (apiDocPropertyObject == null) {
                    throw new RuntimeException("Can not find supported property with id: " + apiDocProperty);
                }

                if (apiDocPropertyObject.get("@type").getAsString().equals("rdf:Property")) {
                    Property property = new Property(resPropertyValue.getAsString());
                    internalRes.getPropertyMap().put(apiDocProperty, property);
                } else if (apiDocPropertyObject.get("@type").getAsString().equals("hydra:Link")) {
                    Link link = new Link(resPropertyValue.getAsString());
                    internalRes.getLinks().add(link);
                }
            }
        }

        return internalRes;
    }

    /**
     * Fetch @context term value as simple String
     * <p>
     * Note: @ context define terms as key-value pairs.
     * key - field name in source resource {@link JsonObject} representation,
     * value - field name in Api documentation
     *
     * @param contextTermValue @context term value
     * @return String representation of @context term value
     */
    private String getContextTermValue(JsonElement contextTermValue) {
        if (contextTermValue.isJsonPrimitive()) {
            return contextTermValue.getAsString();
        } else {
            return ((JsonObject) contextTermValue).get("@id").getAsString();
        }
    }

    /**
     * Search resource property and fetch its value.
     * Recursive method that try to find specific property of resource that could be json tree.
     *
     * @param res         json object for search
     * @param resProperty to search
     * @return resource property value that represents as {@link JsonElement}}
     */
    private JsonElement getResourcePropertyValue(JsonElement res, String resProperty) {
        if (res.isJsonObject()) {
            JsonElement value = ((JsonObject) res).get(resProperty);
            if (value != null) {
                return value;
            } else {
                for (Map.Entry<String, JsonElement> node : ((JsonObject) res).entrySet()) {
                    value = getResourcePropertyValue(node.getValue(), resProperty);
                    if (value != null) {
                        return value;
                    }
                }
            }
        } else if (res.isJsonArray()) {
            for (JsonElement element : ((JsonArray) res)) {
                JsonElement value = getResourcePropertyValue(element, resProperty);
                if (value != null) {
                    return value;
                }
            }
        }

        return null;
    }

    /**
     * Fetch api doc class by resource type
     *
     * @param apiDoc  api documentation
     * @param resType resource type for with class will be found
     * @return api doc class for resource or null
     */
    private JsonObject getSupportedClassFromApiDoc(JsonObject apiDoc, String resType) {
        JsonArray supportedClasses = apiDoc.getAsJsonArray("hydra:supportedClass");
        for (JsonElement supportedClass : supportedClasses) {
            if (supportedClass.isJsonObject()) {
                if (((JsonObject) supportedClass).get("@id").getAsString().equals(resType)) {
                    return (JsonObject) supportedClass;
                }
            }
        }

        return null;
    }

    /**
     * Fetch property by property name
     *
     * @param apiDocClass class from api documentation
     * @param property    property name for with property will be found
     * @return property or null
     */
    private JsonObject getSupportedPropertyFromApiDocClass(JsonObject apiDocClass, String property) {
        JsonArray supportedProperties = apiDocClass.getAsJsonArray("hydra:supportedProperty");
        for (JsonElement supportedProperty : supportedProperties) {
            if (supportedProperty.isJsonObject()) {
                if (((JsonObject) supportedProperty).getAsJsonObject("hydra:property").get("@id").getAsString().equals(property)) {
                    return ((JsonObject) supportedProperty).getAsJsonObject("hydra:property");
                }
            }
        }

        return null;
    }

    /**
     * Check is resource sub class of Collection (http://www.w3.org/ns/hydra/core#Collection)
     *
     * @param resClass for check
     * @return true if resource sub class of Collection, false otherwise
     */
    private boolean isResSubClassOfCollection(JsonObject resClass) {
        JsonElement subClassOf = resClass.get("subClassOf");
        return subClassOf != null && subClassOf.getAsString().equals("http://www.w3.org/ns/hydra/core#Collection");
    }

    /**
     * Data class that holds references for resource, @context and apiDocumentation
     */
    private static class Bundle {
        private final JsonObject res;
        private final JsonObject context;
        private final JsonObject apiDoc;

        Bundle(JsonObject res, JsonObject context, JsonObject apiDoc) {
            this.res = res;
            this.context = context;
            this.apiDoc = apiDoc;
        }
    }
}