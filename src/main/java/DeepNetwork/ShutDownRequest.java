package DeepNetwork;

public class ShutDownRequest extends Request {
    private static final String TYPE = "ShutDownRequest";

    @Override
    public String type() { return TYPE; }
}
