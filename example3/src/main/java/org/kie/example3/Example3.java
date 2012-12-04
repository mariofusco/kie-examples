package org.kie.example3;

import java.io.File;

import org.kie.builder.KieContainer;
import org.kie.builder.KieModule;
import org.kie.builder.KieRepository;
import org.kie.builder.KieServices;
import org.kie.builder.impl.ClasspathKieProject;
import org.kie.builder.impl.KieContainerImpl;
import org.kie.io.KieResources;
import org.kie.runtime.KieSession;

/**
 * Hello world!
 *
 */
public class Example3 
{
    public static void main( String[] args )
    {
        KieServices ks = KieServices.Factory.get();  
        
        KieRepository kr = ks.getKieRepository();
        
        KieModule kModule = kr.addKieModule( ks.getResources().newFileSystemResource( getFile("example1") ) );
                
        KieContainer kContainer = ks.getKieContainer( kModule.getGAV() );
        
        KieSession kSession = kContainer.getKieSession( "ksession1" );
        Object msg1 = createMessage( kContainer,"Dave", "Hello, HAL. Do you read me, HAL?" );        
        kSession.insert( msg1 );
        kSession.fireAllRules();
              
    }
    
    private static Object createMessage(KieContainer kContainer, String name, String text) {
        Object o = null;
        try {
            Class cl = kContainer.getClassLoader().loadClass( "org.kie.example1.Message" );
            o =  cl.getConstructor( new Class[] { String.class, String.class } ).newInstance( name, text );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return o;
    }
    
    public static File getFile(String exampleName) {
        File folder = new File( "." ).getAbsoluteFile();
        File exampleFolder = null;
        while ( folder != null ) {
            exampleFolder = new File( folder,
                                      exampleName );
            if ( exampleFolder.exists() ) {
                break;
            }
            exampleFolder = null;
            folder = folder.getParentFile();
        }        

        if ( exampleFolder != null ) {
            
            File targetFolder = new File( exampleFolder,
                                          "target" );
            if ( !targetFolder.exists() ) {
                throw new RuntimeException("The target folder does not exist, please build project " + exampleName + "first");
            }
            
            for ( String str : targetFolder.list() ) {
                if ( str.startsWith( "kie-" + exampleName ) ) {
                    return new File( targetFolder, str );
                }
            }
        }
        
        throw new RuntimeException("The target jar does not exist, please build project " + exampleName + "first");
    }    
}
