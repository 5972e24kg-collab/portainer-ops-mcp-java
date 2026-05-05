package vr46.portaineropsmcppublic.portainer;

public class PortainerApiCallException extends Exception {

    private final PortainerMcpApiCore.ResponseParam responseParam;

    public PortainerApiCallException(String message, PortainerMcpApiCore.ResponseParam responseParam) {
        super(message);
        this.responseParam = responseParam;
    }

    public PortainerMcpApiCore.ResponseParam getResponseParam() {
        return responseParam;
    }
}
