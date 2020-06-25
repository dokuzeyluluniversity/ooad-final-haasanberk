package oop.libapp.register;


public class FactoryAuthority extends AbstractFactoryAuthority {

    private static FactoryAuthority factoryAuthority = new FactoryAuthority();

    private FactoryAuthority(){

    }

    @Override
    Authority getAuthority() {
        return new Authority();
    }

    @Override
    Authority getAuthority(String authority) {
        return new Authority(authority);
    }

    public static FactoryAuthority getFactoryAuthority(){
        return factoryAuthority;
    }
}
