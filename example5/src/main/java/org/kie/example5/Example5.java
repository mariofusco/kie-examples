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
        
        Resource ex1Res = ks.getResources().newFileSystemResource( getFile("example1") ) ;
        Resource ex2Res = ks.getResources().newFileSystemResource( getFile("example2") ) ;      
        
        KieFactory kf = KieFactory.Factory.get();
        KieFileSystem kfs = kf.newKieFileSystem();
        
        GAV gav = generateKieModule( ks,
                                     ex1Res,
                                     ex2Res,
                                     kf,
                                     kfs );

        KieContainer kContainer = ks.getKieContainer( gav );

        KieSession kSession = kContainer.getKieSession( "ksession5" );
        System.out.println( kSession );
        
        Object msg1 = createMessage(kContainer, "Dave", "Hello, HAL. Do you read me, HAL?");        
        kSession.insert( msg1 );
        kSession.fireAllRules();              

        Object msg2 = createMessage(kContainer, "Dave", "Open the pod bay doors, HAL.");        
        kSession.insert( msg2 );
        kSession.fireAllRules();
        
        Object msg3 = createMessage(kContainer, "Dave", "What's the problem?");        
        kSession.insert( msg3 );
        kSession.fireAllRules();          
    }


    private static GAV generateKieModule(KieServices ks,
                                         Resource ex1Res,
                                         Resource ex2Res,
                                         KieFactory kf,
                                         KieFileSystem kfs) {
        GAV gav = KieFactory.Factory.get().newGav( "org.kie", "kie-example5", "6.0.0-SNAPSHOT" );        
        kfs.generateAndWritePomXML( gav );
        
        KieModuleModel kModuleModel = kf.newKieModuleModel();
        kModuleModel.newKieBaseModel( "org.kie.example5" )
                    .addInclude( "org.kie.example1" )
                    .addInclude( "org.kie.example2") 
                    .newKieSessionModel( "ksession5" );
        
        kfs.writeKModuleXML( kModuleModel.toXML() );        
        kfs.write( "src/main/resources/org/kie/example5/HAL5.drl", getRule() );
        
        KieBuilder kb = ks.newKieBuilder( kfs );
        kb.setDependencies( ex1Res, ex2Res);
        kb.build(); // kieModule is automatically deployed to KieRepository if successfully built.
        if ( kb.hasResults( Level.ERROR ) ) {
            throw new RuntimeException( "Build Errors:\n" + kb.getResults().toString() );
        }
        return gav;
    }
   
    
    private static String getRule() {
        String s = "" +
                "package org.kie.example5 \n\n" +
                "import org.kie.example1.Message \n\n" +
                "rule rule5 when \n" +
                "    Message(text == \"What's the problem?\") \n" +
                "then\n" +
                "    insert( new Message(\"HAL\", \"I think you know what the problem is just as well as I do. \" ) ); \n" +
                "end \n";
        
        return s;
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
