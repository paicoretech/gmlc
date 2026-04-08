package org.mobicents.gmlc.slee;

/**
 * HTTP Request Types (REST or MLP)
 */
public enum HttpRequestType {
    REST("rest"),
    MLP("mlp"),
    UNSUPPORTED("404");

    private final String path;

    HttpRequestType(String path) {
        this.path = path;
    }

    public String getPath(String operation) {
        if (path.equals("mlp"))
            return String.format("/gmlc/%s", path);
        else
            return String.format("/gmlc/%s/%s", path, operation);
    }

    public static String getRequestOperation(String pathInfo) {
        if (pathInfo != null && !pathInfo.isEmpty()) {
            String[] pathSegments = pathInfo.split("/");
            if (pathSegments.length > 3)
                return pathSegments[3];
        }
        return "";
    }

    public static HttpRequestType fromPath(String path) {
        String requestOperation = getRequestOperation(path);
        for (HttpRequestType type : values()) {
            if (path.equals(type.getPath(requestOperation))) {
                return type;
            }
        }
        return UNSUPPORTED;
    }
}