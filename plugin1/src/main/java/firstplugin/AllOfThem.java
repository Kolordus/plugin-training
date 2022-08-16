package firstplugin;

import pluginabstract.TestAnnotation;
import pluginabstract.TestInterface;

@TestAnnotation(value = "first plugin")
public class AllOfThem implements TestInterface {

    @Override
    public String screamAndShout(String s) {
        return s + "first";
    }
}
