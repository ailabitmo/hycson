package ru.ifmo.hymp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Headers;
import retrofit2.adapter.rxjava.Result;
import ru.ifmo.hymp.entities.Link;
import ru.ifmo.hymp.entities.Operation;
import ru.ifmo.hymp.entities.Resource;
import ru.ifmo.hymp.net.ApiClient;
import ru.ifmo.hymp.utils.JsonUtils;
import ru.ifmo.hymp.utils.StringUtils;
import ru.ifmo.hymp.utils.rx.NetworkResultTransformer;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * This class responsible for loading and parsing messages from API that supports:
 * <p>
 * Hydra core vocabulary:
 *
 * @see <a href="http://www.hydra-cg.com/</a>
 * and JSON-LD:
 * @see <a href="https://www.w3.org/TR/json-ld/</a>
 */
public class HypermediaMessageParser implements Parser {
    private static final String HEADER_LINK = "Link";
    private static final String HEADER_LINK_API_DOC = "rel=\"http://www.w3.org/ns/hydra/core#apiDocumentation\"";

    // simple cache for context json objects
    private static Map<String, JsonObject> contextCache = new HashMap<>(2);

    public HypermediaMessageParser(String entryPoint) {
        ApiClient.initApiService(entryPoint);
    }

    /**
     * Load resource from API and parse to {@link Resource} - internal resource representation
     * <p>
     * 1. Load resource from API using url param
     * 2. Extract Link header from response
     * 3. Load @context for resource and api documentation
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
                                new Func2<JsonObject, Result<JsonObject>, Bundle>() {
                                    @Override
                                    public Bundle call(JsonObject context, Result<JsonObject> apiDocRes) {
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
    private Observable<JsonObject> loadContextForResource(JsonObject res) {
        final String contextUrl = res.get("@context").getAsString();
        if (StringUtils.isEmpty(contextUrl)) {
            throw new RuntimeException("@context link is empty");
        }

        if (contextCache.containsKey(contextUrl)) {
            return Observable.just(contextCache.get(contextUrl));
        }

        return ApiClient.getApiService().load(contextUrl)
                .compose(new NetworkResultTransformer())
                .map(new Func1<Result<JsonObject>, JsonObject>() {
                    @Override
                    public JsonObject call(Result<JsonObject> result) {
                        return result.response().body();
                    }
                })
                .doOnNext(new Action1<JsonObject>() {
                    @Override
                    public void call(JsonObject context) {
                        contextCache.put(contextUrl, context);
                    }
                });
    }

    private Resource parseToInternalResource(Bundle bundle) {
        JsonObject res = bundle.res;
        JsonObject apiDoc = bundle.apiDoc;
        JsonObject context = bundle.context;
        if (context == null) {
            context = loadContextForResource(res)
                    .toBlocking()
                    .single();
        }

        String resId = res.get("@id").getAsString();
        String resType = res.get("@type").getAsString();
        JsonObject resClass = getResClassFromApiDoc(apiDoc, resType);
        if (resClass == null) {
            throw new RuntimeException("Class with id: " + resType + " not found");
        }

        // create internal resource representation
        Resource internalRes = new Resource(resId, resType);

        Set<Map.Entry<String, JsonElement>> terms = context.getAsJsonObject("@context").entrySet();
        for (Map.Entry<String, JsonElement> term : terms) {
            String termPropertyKey = term.getKey();
            String termPropertyValue = getContextTermValue(term.getValue());

            if (termPropertyKey.startsWith("@") || termPropertyKey.equals("hydra")) {
                continue;
            }

            JsonElement resPropertyValue = getResourcePropertyValue(res, termPropertyKey);
            if (resPropertyValue == null) {
                continue;
            }

            if (resPropertyValue.isJsonArray() && isResSubClassOfCollection(resClass)) {
                JsonArray members = resPropertyValue.getAsJsonArray();
                List<Resource> memberResources = new ArrayList<>();
                for (JsonElement member : members) {
                    Resource memberRes = parseToInternalResource(new Bundle(member.getAsJsonObject(), null, apiDoc));
                    memberResources.add(memberRes);
                }

                internalRes.getPropertyMap().put(termPropertyValue, memberResources);
                continue;
            }

            JsonObject classProperty = getPropertyFromClass(resClass, termPropertyValue);
            if (classProperty == null) {
                throw new RuntimeException("Not found supported property with termPropertyValue: " + termPropertyValue);
            }

            if (classProperty.get("@type").getAsString().equals("rdf:Property")) {
                internalRes.getPropertyMap().put(termPropertyValue, resPropertyValue.getAsString());
            } else if (classProperty.get("@type").getAsString().equals("hydra:Link")) {
                List<Operation> linkOperations = getOperationsFromClassPropery(classProperty);
                Link link = new Link(resPropertyValue.getAsString(), linkOperations);
                internalRes.getLinks().add(link);
            }
        }

        return internalRes;
    }

    /**
     * Extract all available operations  for specific property from api doc
     *
     * @param apiDocPropertyObject Api doc property object
     * @return all available operations  for specific property
     */
    private List<Operation> getOperationsFromClassPropery(JsonObject apiDocPropertyObject) {
        List<Operation> operations = new ArrayList<>();

        JsonArray supportedOperations = apiDocPropertyObject.getAsJsonArray("hydra:supportedOperation");
        for (JsonElement supportedOperation : supportedOperations) {
            JsonObject operationObject = (JsonObject) supportedOperation;
            String method = operationObject.get("hydra:method").getAsString();
            String expects = JsonUtils.getMemberAsStringNullSafety(operationObject, "expects");
            String returns = JsonUtils.getMemberAsStringNullSafety(operationObject, "returns");

            Operation.Type type = Operation.Type.valueOf(method);
            Operation operation;
            switch (type) {
                case GET:
                    operation = new Operation(type, returns);
                    break;
                case POST:
                    operation = new Operation(type, expects, returns);
                    break;
                default:
                    operation = new Operation(Operation.Type.UNKNOWN);
            }

            operations.add(operation);
        }

        return operations;
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
    private JsonObject getResClassFromApiDoc(JsonObject apiDoc, String resType) {
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
    private JsonObject getPropertyFromClass(JsonObject apiDocClass, String property) {
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