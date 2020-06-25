package oop.libapp.register;

public class FactoryUser extends AbstractFactoryUser {

    private static FactoryUser factoryUser = new FactoryUser();

    private FactoryUser(){

    }

    @Override
    User getUser() {
        return new User();
    }

    public static FactoryUser getFactoryUser(){
        return factoryUser;
    }
}
