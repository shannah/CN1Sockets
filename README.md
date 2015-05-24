#Codename Sockets Library

This library was started as an attempt to add sockets support to Codename One.  I have so far created native interfaces for iOS, Android, Blackberry, JavaSE and J2ME.

##Installation

1. Copy the [CN1Sockets.cn1lib](https://github.com/shannah/CN1Sockets/raw/master/dist/CN1Sockets.cn1lib) library into your application's `lib` directory.
2. Right click on your project's icon in the Netbeans project explorer, and select "Refresh Libs"
3. Add build hint `ios.add_libs=CFNetwork.framework` (if deploying to iOS)

Note:  In some versions of Codename One, you will need to perform an explicit "Build" before it will work in the simulator.  I.e. Right click on your project in the project explorer, and select "Build".  After that you should be able to run it normally in the simulator.

##Workaround for CN1 Build Script Bug

Currently (as of Aug. 25, 2014) there is a [bug](https://code.google.com/p/codenameone/issues/detail?id=1150&q=socket) in the Codename One build scripts that prevents cn1libs that include native components from running on the simulator.  The workaround is essentially to perform a "Clean & Build" on your project before trying to run it in the simulator.  This workaround is described step by step in [this issue](https://code.google.com/p/codenameone/issues/detail?id=1150&q=socket) and this [screencast](http://youtu.be/3rtobVgAcQQ).

##Usage Example

~~~~

import ca.weblite.codename1.net.Socket;
// .. rest of imports


// … your application's main class

    public void start(){
        lbl = new Label("Result here");
        Form hi = new Form("Socket Tester");
        Button btn = new Button("Run Test");
        btn.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent evt) {
                Display.getInstance().callSerially(new Runnable(){

                    public void run() {
                        runSocketTests();
                    }
                    
                });
            }
            
        });
        
        hi.addComponent(btn);
        hi.addComponent(lbl);
        hi.show();
    }
    
    
    public void runSocketTests(){
        try {
            if ( Socket.isSocketSupported() ){
                Socket sock = new Socket("example.com", 80);
                sock.getOutputStream().write("GET / HTTP/1.0\r\nHost: example.com\r\n\r\n".getBytes());

                String result = Util.readToString(sock.getInputStream());
                    // NOTE:  Util.readToString() closes the input stream automatically.
                    // Don't use it if you need to keep the socket open.  Use
                    // another mechanism to read the stream.
                lbl.setText(result);
                //sock.close();
                Log.p(result);
            } else {
                lbl.setText("Sockets not supported");
                Log.p("Sockets not supported");
            }
        } catch ( Exception ex){
            Log.p("We have an error");
            Log.e(ex);
            
        }
    }

// …

~~~~


## To Do

1. Try out the J2ME and Blackberry ports.  They are there but I have never tested them so they may need some tweaking.
2. Stress test.  
