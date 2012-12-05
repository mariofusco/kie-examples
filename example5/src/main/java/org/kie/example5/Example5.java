package org.kie.example5;

import java.io.File;
import java.util.Arrays;

import org.kie.builder.GAV;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieContainer;
import org.kie.builder.KieFactory;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieModule;
import org.kie.builder.KieModuleModel;
import org.kie.builder.KieRepository;
import org.kie.builder.KieServices;
import org.kie.builder.Results;
import org.kie.builder.Message.Level;
import org.kie.io.Resource;
import org.kie.runtime.KieSession;

/**
 * Hello world!
 *
 */
public class Example5 
{
    public static void main( String[] args )
    {
        KieServices ks = KieServices.Factory.get();  
        
        KieRepository kr = ks.getKieRepository();
      
        
        KieFactory kf = KieFactory.Factory.get();
        KieFileSystem kfs = kf.newKieFileSystem();
        
        kfs.write( "src/main/resources/org/kie/example5/HAL5.drl", getRule() );
 
        KieBuilder kb = ks.newKieBuilder( kfs );
        
        kb.build(); // kieModule is automatically deployed to KieRepository if successfully built.
        if ( kb.hasResults( Level.ERROR ) ) {
            throw new RuntimeException( "Build Errors:\n" + kb.getResults().toString() );
        }

        KieContainer kContainer = ks.getKieContainer( kr.getDefaultGAV() );

        KieSession kSession = kContainer.getKieSession();
                
        kSession.insert( new Message( "Dave", "Hello, HAL. Do you read me, HAL?") );
        kSession.fireAllRules();                       
    }
   
    
    private static String getRule() {
        String s = "" +
                   "package org.kie.example5 \n\n" +
                   "import org.kie.example5.Message \n\n" +
                   "rule \"rule 1\" when \n" +
                   "    m : Message( ) \n" +
                   "then \n" +
                   "    System.out.println( m.getName() + \": \" +  m.getText() ); \n" +
                   "end \n" +
                   "rule \"rule 2\" when \n" +
                   "    Message( text == \"Hello, HAL. Do you read me, HAL?\" ) \n" +
                   "then \n" +
                   "    insert( new Message(\"HAL\", \"Dave. I read you.\" ) ); \n" +
                   "end";

        return s;
    }
  
}
