#Codename Sockets Library

This library was started as an attempt to add sockets support to Codename One.  I have so far created native interfaces for iOS, Android, Blackberry, and J2ME.  So far I have tested on iOS and Android in light examples and it seems to work ok.  I am not using it in any production environments yet.  If you find bugs, please let me know.

Currently this won't work in the emulator (i.e. the JavaSE port) because Codename One doesn't support native interfaces for the JavaSE port.  You can follow [this issue](https://code.google.com/p/codenameone/issues/detail?id=861&q=native) for the status on this support.

##Installation

1. Copy the [CN1Sockets.cn1lib](https://github.com/shannah/CN1Sockets/raw/master/dist/CN1Sockets.cn1lib) library into your application's `lib` directory.
2. Right click on your project's icon in the Netbeans project explorer, and select "Refresh Libs"

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
                sock.getOutputStream().write("GET / HTTP/1.1\r\nHost: example.com\r\n\r\n".getBytes());

                String result = Util.readToString(sock.getInputStream());
                lbl.setText(result);
                sock.close();
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