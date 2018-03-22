/**
 * Your application code goes here
 */

package userclasses;

import ca.weblite.codename1.net.Socket;
import com.codename1.io.Log;
import com.codename1.io.Util;
import generated.StateMachineBase;
import com.codename1.ui.*; 
import com.codename1.ui.events.*;
import com.codename1.ui.util.Resources;
import java.io.InputStream;

/**
 *
 * @author Your name here
 */
public class StateMachine extends StateMachineBase {
    public StateMachine(String resFile) {
        super(resFile);
        // do not modify, write code in initVars and initialize class members there,
        // the constructor might be invoked too late due to race conditions that might occur
    }
    
    /**
     * this method should be used to initialize variables instead of
     * the constructor/class scope to avoid race conditions
     */
    protected void initVars(Resources res) {
    }


    @Override
    protected void onMain_SendButtonAction(Component c, ActionEvent event) {
        final String[] result = new String[1];
        Display.getInstance().invokeAndBlock(new Runnable(){

            public void run() {
                result[0] = runSocketTests();
            }
            
        });
        
        findResponseArea(c).setText(result[0]);
        findResponseArea(c).repaint();
    
    }
    
    public String runSocketTests(){
        try {
            if ( Socket.isSocketSupported() ){
                System.out.println("About to connect to example.com");
                Socket sock = new Socket("example.com", 8080, 2000);
                sock.getOutputStream().write("GET / HTTP/1.0\r\nHost: example.com\r\n\r\n".getBytes());
                Log.p("Opened connection to example.com");
                InputStream is = sock.getInputStream();
                String result = Util.readToString(is);
                    // NOTE:  Util.readToString() closes the input stream automatically.
                    // Don't use it if you need to keep the socket open.  Use
                    // another mechanism to read the stream.
                return result;
                
            } 
        } catch ( Exception ex){
            System.out.println("An exception occurrred opening socket: "+ex.getMessage());
            return ex.getMessage();

        }
        return "Sockets not supported";
    }
}
