package org.kie.example7;

import org.kie.builder.KieContainer;
import org.kie.builder.KieFactory;
import org.kie.builder.KieServices;
import org.kie.example1.Message;
import org.kie.runtime.KieSession;

public class Example7 {

    public static void main( String[] args ) {
        KieServices ks = KieServices.Factory.get();
        KieFactory kf = KieFactory.Factory.get();

        // Install example1 in the local maven repo before to do this
        KieContainer kContainer = ks.getKieContainer( kf.newGav("org.kie", "kie-example1", "6.0.0-SNAPSHOT") );

        KieSession kSession = kContainer.getKieSession( "ksession1" );
        kSession.insert( new Message("Dave", "Hello, HAL. Do you read me, HAL?") );
        kSession.fireAllRules();

    }

}
