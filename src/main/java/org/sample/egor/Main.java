package org.sample.egor;

class Main {
    public static void main(String[] args) {

        JettyServer jettyServer = new JettyServer();
        jettyServer.start();
        jettyServer.join();

    }

}
