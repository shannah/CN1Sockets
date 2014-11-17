#import <Foundation/Foundation.h>
#import "xmlvm.h"
#import "ca_weblite_codename1_net_Socket.h"

@interface ca_weblite_codename1_net_impl_NativeSocketImpl : NSObject {
    NSInputStream* inputStream;
    NSOutputStream* outputStream;
    NSString* errorMessage;
    int bufferId;
    BOOL isFinished;
}

-(int)read;
-(long long)skip:(long long)param;
-(int)available;
-(BOOL)markSupported;
-(BOOL)setReceiveBufferSize:(int)param;
-(BOOL)write:(int)param;
-(void)setBufferId:(int)param;
-(int)getBufferId;
-(BOOL)setSendBufferSize:(int)param;
-(BOOL)setKeepAlive:(BOOL)param;
-(BOOL)isInputShutdown;
-(BOOL)isOutputShutdown;
-(NSString*)getErrorMessage;
-(BOOL)closeOutputStream;
-(int)readBuf:(int)len;
-(BOOL)closeInputStream;
-(int)getErrorCode;
-(BOOL)createSocket:(NSString*)param param1:(int)param1;
-(BOOL)closeSocket;
-(BOOL)writeBuf:(NSData*)param;
-(BOOL)flushOutputStream;
-(BOOL)markInputStream:(int)param;
-(BOOL)connectSocket:(int)param;
-(BOOL)isSocketConnected;
-(BOOL)isSocketClosed;
-(BOOL)writeBuffOffsetLength:(NSData*)param param1:(int)param1 param2:(int)param2;
-(BOOL)resetInputStream;
-(BOOL)isSupported;
@end
