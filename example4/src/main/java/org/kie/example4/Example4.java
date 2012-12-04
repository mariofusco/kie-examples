package org.kie.example4;

import java.io.File;

import org.kie.builder.KieContainer;
import org.kie.builder.KieModule;
import org.kie.builder.KieRepository;
import org.kie.builder.KieServices;
import org.kie.io.Resource;
import org.kie.runtime.KieSession;

/**
 * Hello world!
 *
 */
public class Example4 
{
    public static void main( String[] args )
    {
        KieServices ks = KieServices.Factory.get();  
        
        KieRepository kr = ks.getKieRepository();
        
        Resource ex1Res = ks.getResources().newFileSystemResource( getFile("example1") ) ;
        Resource ex2Res = ks.getResources().newFileSystemResource( getFile("example2") ) ;
        
        KieModule kModule = kr.addKieModule(ex1Res,  ex2Res);                
        KieContainer kContainer = ks.getKieContainer( kModule.getGAV() );

        KieSession kSession = kContainer.getKieSession( "ksession2" );
        
        Object msg1 = createMessage(kContainer, "Dave", "Hello, HAL. Do you read me, HAL?");        
        kSession.insert( msg1 );
        kSession.fireAllRules();              

        Object msg2 = createMessage(kContainer, "Dave", "Open the pod bay doors, HAL");        
        kSession.insert( msg2 );
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
