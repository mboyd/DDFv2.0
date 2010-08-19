//I2C Library

#include <compat/twi.h>
#include <avr/io.h>
#include "i2cmaster.h"

//Initialize I2C interface for 220KHz operation
void i2c_init(void) {
  TWSR = 0;
  TWBR = 0x0A;
}

//Send a start signal followed by transmitting the address
unsigned char i2c_start(unsigned char address) {
	TWCR = (1<<TWEN) | (1<<TWSTA) | (1<<TWINT);
	while(!(TWCR & (1<<TWINT)));
	if ( ((TW_STATUS & 0xF8) != TW_START) && ((TW_STATUS & 0xF8) != TW_REP_START)) return 1;
	TWDR = address;
	TWCR = (1<<TWINT) | (1<<TWEN);
	while(!(TWCR & (1<<TWINT)));
	if(((TW_STATUS & 0xF8) != TW_MT_SLA_ACK) && ((TW_STATUS & 0xF8) != TW_MR_SLA_ACK)) return 1;
	return 0;
}

//Send a stop signal
void i2c_stop(void) {
	TWCR = (1<<TWINT) | (1<<TWEN) | (1<<TWSTO);
	while(TWCR & (1<<TWSTO));
}

//Transmit a byte
//Must be preceded by an i2c_start
unsigned char i2c_write( unsigned char data ) {
	TWDR = data;
	TWCR = (1<<TWINT) | (1<<TWEN);
	while(!(TWCR & (1<<TWINT)));
	if( (TW_STATUS & 0xF8) != TW_MT_DATA_ACK) return 1;
	return 0;
}

//Receive a byte
//Must be preceded by an i2c_start
unsigned char i2c_read(unsigned char ack){
	TWCR = (1<<TWINT) | (1<<TWEN) | (ack<<TWEA);
	while(!(TWCR & (1<<TWINT)));    
    return TWDR;
}

//Transmits a series of bytes
//Performs a start, transmits data, and performs a stop
unsigned char i2c_transmit(unsigned char address, unsigned char msg[], unsigned char size) {
	unsigned char i,err;
	err=i2c_start(address<<1);
	for (i=0;i<size && !err;i++)
		err|=i2c_write(msg[i]);
    i2c_stop();
	return err;
}

//Receive data
//Performs a start, receives bytes into the buffer, and performs a stop
//If a location to read from must be specified, this must be done prior to calling i2c_receive
unsigned char i2c_receive(unsigned char address,unsigned char *buffer, unsigned char size){
	unsigned char i,err;
	err=i2c_start((address<<1)|1);
	for (i=size;i>1;i--) {
		*buffer=i2c_read(1);
		buffer++;
	}
	*buffer=i2c_read(0);
	i2c_stop();
	return err;
}
