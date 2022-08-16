package secondplugin;

import pluginabstract.TestInterface;

public class SecondPlugin implements TestInterface {


    @Override
    public String screamAndShout(String s) {
        return s + " second";
    }
}
