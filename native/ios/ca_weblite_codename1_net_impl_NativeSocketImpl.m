#import "ca_weblite_codename1_net_impl_NativeSocketImpl.h"

@implementation ca_weblite_codename1_net_impl_NativeSocketImpl

static void _yield() {
#ifdef NEW_CODENAME_ONE_VM
    CN1_YIELD_THREAD;
#endif
}

static void _resume() {
#ifdef NEW_CODENAME_ONE_VM
    CN1_RESUME_THREAD;
#endif
}

-(void)dealloc {
    if ( inputStream != NULL ){
        [inputStream release];
        inputStream = NULL;
    }
    if ( outputStream != NULL ){
        [outputStream release];
        outputStream = NULL;
    }
}

-(int)getBufferId{
    return bufferId;
}

-(void)setBufferId:(int)id{
    isFinished = NO;
    bufferId = id;
}

-(int)read{
    if ( isFinished ){
        isFinished = NO;
        return -1;
    }
    errorMessage = NULL;
    uint8_t buf[1];
    _yield(); // Tell the GC that it is OK to collect on us while we're blocked
    while ([self available] <= 0) {
        // Since iOS doesn't block infinitely, we need to wait until there is
        // data to read.
        usleep(100000);
    }
    int bytesRead = [inputStream read:buf maxLength:1];
    _resume(); // tell the GC that we're back
    if ( bytesRead == -1 ){
        errorMessage = [[inputStream streamError] localizedDescription];
        return -2;
    } else if ( bytesRead == 0 ){
        return -1;
    } else {
        return buf[0];
    }
    
}

-(long long)skip:(long long)param{
    errorMessage = NULL;
    int bufSize = 32768;
    uint8_t buf[bufSize];
    long long bytesSkipped = 0;
    int bytesRead = 0;
    int bytesToRead = bufSize;
    while ( bytesSkipped < param ){
        if ( param - bytesSkipped < bufSize ){
            bytesToRead = param-bytesRead;
        } else {
            bytesToRead = bufSize;
        }
        _yield(); // Tell GC that it is ok to collect our garbage while we're blocked
        while ([self available] <= 0) {
            // Since iOS doesn't block infinitely, we need to wait until there is
            // data to read.
            usleep(100000);
        }
        bytesRead = [inputStream read:buf maxLength:bytesToRead];
        _resume(); // Tell the GC that we're back
        if ( bytesRead == -1 ){
            errorMessage = [[inputStream streamError] localizedDescription];
            return -2;
        } else if ( bytesRead == 0 ){
            return bytesSkipped;
        } else {
            bytesSkipped += bytesRead;
        }
    }
    return  bytesSkipped;
}

-(int)available{
    errorMessage = NULL;
    BOOL available = [inputStream hasBytesAvailable];
    if (available)
        return 1;
    else
        return 0;
}

-(BOOL)markSupported{
    errorMessage = NULL;
    return NO;
}

-(BOOL)setReceiveBufferSize:(int)param{
    errorMessage = NULL;
    // Not sure what to do here
    return YES;
}

-(BOOL)write:(int)param{
    isFinished = NO;
    errorMessage = NULL;
    uint8_t buf[1];
    buf[0] = (uint8_t)param;
    _yield();
    NSInteger res = [outputStream write:buf maxLength:1];
    _resume();
    if ( res <= 0 ){
        errorMessage = [[outputStream streamError] localizedDescription];
        return NO;
    } else {
        return res;
    }
}

-(BOOL)setSendBufferSize:(int)param{
    errorMessage = NULL;
    // Not sure what to do here
    return YES;
}

-(BOOL)setKeepAlive:(BOOL)param{
    errorMessage = NULL;
    // Not sure what to do here
    return YES;
}

-(BOOL)isInputShutdown{
    errorMessage = NULL;
    NSStreamStatus status = [inputStream streamStatus];
    return (status == NSStreamStatusOpening || status == NSStreamStatusNotOpen || NSStreamStatusClosed == status );
}

-(BOOL)isOutputShutdown{
    errorMessage = NULL;
    NSStreamStatus status = [outputStream streamStatus];
    return (status == NSStreamStatusOpening ||  status == NSStreamStatusNotOpen || NSStreamStatusClosed == status );
}

-(NSString*)getErrorMessage{
    errorMessage = NULL;
    return errorMessage;
}

-(BOOL)closeOutputStream{
    errorMessage = NULL;
    [outputStream close];
    return YES;
}

-(int)readBuf:(int)len{
    if ( isFinished == YES ){
        isFinished = NO;
        return -1;
    }
    errorMessage = NULL;
#ifndef NEW_CODENAME_ONE_VM
    org_xmlvm_runtime_XMLVMArray* byteArray = ca_weblite_codename1_net_Socket_getBuffer___int(bufferId);
    JAVA_ARRAY_BYTE* buffer = (JAVA_ARRAY_BYTE*)byteArray->fields.org_xmlvm_runtime_XMLVMArray.array_;
    if ( len > byteArray->fields.org_xmlvm_runtime_XMLVMArray.length_){
#else
        JAVA_ARRAY byteArray = (JAVA_ARRAY)ca_weblite_codename1_net_Socket_getBuffer___int_R_byte_1ARRAY(CN1_THREAD_GET_STATE_PASS_ARG bufferId);
        JAVA_ARRAY_BYTE* buffer = (JAVA_ARRAY_BYTE*)byteArray->data;
        if ( len > byteArray->length){
#endif
            errorMessage = @"Attempt to read byte array longer than the buffer.";
            return -2;
        }
        
        
        _yield();  // tell the GC that it is OK to do GC while this thread is blocked
        while ([self available] <= 0) {
            // Since iOS doesn't block infinitely, we need to wait until there is
            // data to read.
            usleep(100000);
        }
        int bytesRead = [inputStream read:buffer maxLength:len];
        _resume(); // Tell the GC that we're back
        if ( bytesRead == -1 ){
            errorMessage = [[inputStream streamError] localizedDescription];
            return -2;
        } else if ( bytesRead == 0 ){
            return -1;
        } else {
            if ( bytesRead < len ){
                isFinished = YES;
            }
            return bytesRead;
        }
    }
    
    -(BOOL)closeInputStream{
        errorMessage = NULL;
        [inputStream close];
        return YES;
    }
    
    -(int)getErrorCode{
        if ( errorMessage == NULL ){
            return 0;
        } else {
            return 500;
        }
    }
    
    -(BOOL)createSocket:(NSString*)host param1:(int)port{
        isFinished = NO;
        CFReadStreamRef readStream;
        CFWriteStreamRef writeStream;
        
        BOOL isSSL = NO;
        if ([host hasPrefix:@"SSL@"]) {
            isSSL = YES;
            host = [host stringByReplacingOccurrencesOfString:@"SSL@" withString:@""];
            NSLog(@"Connecting to SSL socket %@:%d", host, port);
        }
        else {
            NSLog(@"Connecting to plain socket %@:%d", host, port);
        }
        
        _yield();
        CFStreamCreatePairWithSocketToHost(NULL, (__bridge CFStringRef)host, port, &readStream, &writeStream);
        _resume();
        
        inputStream = (__bridge NSInputStream *)readStream;
        outputStream = (__bridge NSOutputStream *)writeStream;
        
        [inputStream retain];
        [outputStream retain];
        
        if (isSSL) {
            NSDictionary *settings = [ [NSDictionary alloc ]
                                      initWithObjectsAndKeys:
                                      [NSNumber numberWithBool:NO], kCFStreamSSLValidatesCertificateChain,
                                      [NSNull null], kCFStreamSSLPeerName,
                                      kCFStreamSocketSecurityLevelNegotiatedSSL, kCFStreamSSLLevel,
                                      nil ];
            CFReadStreamSetProperty((CFReadStreamRef)inputStream, kCFStreamPropertySSLSettings, (CFTypeRef)settings);
            CFWriteStreamSetProperty((CFWriteStreamRef)outputStream, kCFStreamPropertySSLSettings, (CFTypeRef)settings);
        }
        
        return YES;
    }
    
    -(BOOL)closeSocket{
        errorMessage = NULL;
        [inputStream close];
        [outputStream close];
        return YES;
    }
    
    -(BOOL)writeBuf:(NSData*)buffer{
        isFinished = NO;
        errorMessage = NULL;
        _yield();
        int bytesWritten = [outputStream write:(const uint8_t*)[buffer bytes] maxLength:[buffer length]];
        _resume();
        if ( bytesWritten == -1 ){
            errorMessage = [[outputStream streamError] localizedDescription];
            return NO;
        } else if ( bytesWritten != [buffer length]){
            errorMessage = [NSString stringWithFormat:@"Not all bytes in buffer were written.  Buffer length was %d but only wrote %d bytes.", [buffer length], bytesWritten];
            return NO;
        }
        return YES;
    }
    
    -(BOOL)flushOutputStream{
        
        return YES;
    }
    
    -(BOOL)markInputStream:(int)param{
        errorMessage = @"Mark not supported";
        return NO;
    }
    
    -(BOOL)connectSocket:(int)timeout{
        isFinished = NO;
        errorMessage = NULL;
        [inputStream open];
        [outputStream open];
        while ([outputStream streamStatus] == NSStreamStatusOpening) {
            _yield();
            usleep(100000);
            _resume();
        }
        while ([inputStream streamStatus] == NSStreamStatusOpening) {
            _yield();
            usleep(100000);
            _resume();
        }
        if ([self isInputShutdown] || [self isOutputShutdown]) {
            return NO;
        }
        return YES;
    }
    
    
    -(BOOL)isSocketConnected{
        return ![self isInputShutdown] && ![self isOutputShutdown];
    }
    
    -(BOOL)isSocketClosed{
        return ![self isSocketConnected];
    }
    
    -(BOOL)writeBuffOffsetLength:(NSData*)buffer param1:(int)param1 param2:(int)param2{
        isFinished = NO;
        errorMessage = NULL;
        _yield();
        int bytesWritten = [outputStream write:((const uint8_t*)[buffer bytes]+param1) maxLength:param2];
        _resume();
        if ( bytesWritten == -1 ){
            errorMessage = [[outputStream streamError] localizedDescription];
            return NO;
        } else if ( bytesWritten != param2){
            errorMessage = [NSString stringWithFormat:@"Not all bytes in buffer were written.  Buffer length was %d but only wrote %d bytes.", [buffer length], bytesWritten];
            return NO;
        }
        return YES;
    }
    
    -(BOOL)resetInputStream{
        isFinished = NO;
        return NO;
    }
    
    -(BOOL)isSupported{
        return YES;
    }
    
    @end
