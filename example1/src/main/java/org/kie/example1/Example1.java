package org.kie.example1;

import org.kie.builder.KieContainer;
import org.kie.builder.KieServices;
import org.kie.builder.impl.ClasspathKieProject;
import org.kie.builder.impl.KieContainerImpl;
import org.kie.runtime.KieSession;

/**
 * Hello world!
 *
 */
public class Example1 
{
    public static void main( String[] args )
    {
        KieServices ks = KieServices.Factory.get();        
        KieContainer kContainer = ks.getKieClasspathContainer();
        KieSession kSession = kContainer.getKieSession( "ksession1" );
        kSession.insert( new Message("Dave", "Hello, HAL. Do you read me, HAL?") );
        kSession.fireAllRules();
    }
}
