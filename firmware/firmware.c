//Dropout Design Disco Dance Floor
//Firmware Revision 3.0
//Grant Elliott
//January 2006

#define	VERSION		0x31

//INCLUDES
#include <avr/io.h>
#include <avr/eeprom.h>
#include "i2cmaster.h"
#include "usart.h"

//MASKS
#define RED_MASK	0x00
#define GREEN_MASK	0x01
#define BLUE_MASK	0x08
#define SENSOR_MASK	0x09
const unsigned char ROW_MASKS[]={0x20, 0x22, 0x10, 0x12};

//ERROR CODES
#define ERR_COMMAND	0
#define ERR_ARGS	1
#define ERR_I2C		2

//COMMAND BYTES
#define	COMMAND_MODULE_W			0x10
#define COMMAND_ROW_W				0x11
#define COMMAND_MODULE_R			0x20
#define COMMAND_ROW_R				0x21
#define COMMAND_MODULE_RW			0x30
#define COMMAND_ROW_RW				0x31
#define COMMAND_BLACK				0x40
#define	COMMAND_PING				0x50
#define COMMAND_ENTER_ECHO_MODE		0x51
#define COMMAND_EXIT_ECHO_MODE		0x52
#define COMMAND_USART_TEST			0x53
#define COMMAND_ONLINE_RESET		0x60
#define COMMAND_I2C_STATUS			0x61
#define COMMAND_SEND_VERSION		0x70
#define	COMMAND_ENTER_SELF_TEST_0	0x80
#define COMMAND_EXIT_SELF_TEST		0x8F

//PROTOTYPES
void dispatch(unsigned char);
void pin_config(void);
void finalize_comm(unsigned char, unsigned char*, unsigned char);
static __inline__ void wait(uint16_t);
void calosc(void);
//I2C Status Monitor
void i2c_set_error(unsigned char);
void i2c_set_unusable(unsigned char);
void i2c_set_good(unsigned char);
unsigned char i2c_check_unusable(unsigned char);
unsigned char i2c_lookup_index(unsigned char);
//Dispatch Handlers
unsigned char handle_module_write(void);
unsigned char handle_row_write(void);
unsigned char handle_module_read(unsigned char*, unsigned char*);
unsigned char handle_row_read(unsigned char*, unsigned char*);
unsigned char handle_module_rw(unsigned char*, unsigned char*);
unsigned char handle_row_rw(unsigned char*, unsigned char*);
unsigned char handle_module_black(void);
unsigned char handle_module_reset(void);
unsigned char handle_ping(void);
unsigned char handle_enter_echo_mode(void);
unsigned char handle_usart_test(unsigned char*, unsigned char*);
unsigned char handle_send_version(unsigned char*, unsigned char*);
unsigned char handle_send_i2c_status(unsigned char*, unsigned char*);
unsigned char handle_enter_self_test_0(void);
//Module Level
unsigned char module_init(void);
unsigned char module_write(unsigned char*);
unsigned char module_read(unsigned char*);
//Quadrant Level
unsigned char quadrant_init(unsigned char);
unsigned char quadrant_write(unsigned char, unsigned char*);
unsigned char quadrant_read(unsigned char, unsigned char*);
//Chip Level
unsigned char driver_init(unsigned char);
unsigned char driver_write(unsigned char, unsigned char*);
unsigned char sensor_init(unsigned char);
unsigned char sensor_read(unsigned char, unsigned char*);


//GLOBALS
static volatile unsigned char i2c_unusable[]={0,0};
static volatile unsigned char i2c_error[]={0,0};


//CODE
int main(void) {
	calosc();
	pin_config();
	module_init();
	handle_module_black();
	while(1)
		dispatch(usart_getc());
	return 0;
}

//Calibrate the RC Oscillator using EEPROM address 0.
//Be sure to program this when you flash the chip.
//If you forget, this won't do anything dangerously bad.
void calosc(void) {
	unsigned char temp;
	temp=eeprom_read_byte((unsigned char*)1);
	if (temp>0x80 && temp<0xAF)
		OSCCAL=(unsigned char)(1.036*(float)temp);
}

//Main dispatch loop:
//Calls the appropriate command
//Sends a status byte and the appropriate data
void dispatch(unsigned char command) {
	unsigned char err=0;
	unsigned char data[8];
	unsigned char size=0;
	switch(command)
	{
		case COMMAND_MODULE_W:
			err=handle_module_write();
		break;
		case COMMAND_ROW_W:
			err=handle_row_write();
		break;
		case COMMAND_MODULE_R:
			err=handle_module_read(data,&size);
		break;
		case COMMAND_ROW_R:
			err=handle_row_read(data,&size);
		break;
		case COMMAND_MODULE_RW:
			err=handle_module_rw(data,&size);
		break;
		case COMMAND_ROW_RW:
			err=handle_row_rw(data,&size);
		break;
		case COMMAND_BLACK:
			err=handle_module_black();
		break;
		case COMMAND_PING:
			err=handle_ping();
		break;
		case COMMAND_ENTER_ECHO_MODE:
			err=handle_enter_echo_mode();
		break;
		case COMMAND_USART_TEST:
			err=handle_usart_test(data,&size);
		break;
		case COMMAND_ONLINE_RESET:
			err=handle_module_reset();
		break;
		case COMMAND_I2C_STATUS	:
			err=handle_send_i2c_status(data,&size);
		break;
		case COMMAND_SEND_VERSION:
			err=handle_send_version(data,&size);
		break;
		case COMMAND_ENTER_SELF_TEST_0:
			err=handle_enter_self_test_0();
		break;
		default:
			err=1<<ERR_COMMAND;
		break;
	}
	finalize_comm(err,data,size);
}

//Initializes AVR pins
void pin_config(void) {
	DDRD=0x0A;
	DDRC=0x04;
	PORTD=0xF7;
	PORTC=0x00;
	i2c_init();
	usart_init(8);
}

//Standard delay loop
//wait(2000) waits 1ms
static __inline__ void wait(uint16_t count) {
	__asm__ volatile (
		"1: sbiw %0,1" "\n\t"
		"brne 1b"
		: "=w" (count)
		: "0" (count)
	);
}

//Communication response
//Sends a status byte followed by any data
//Only sends data if status byte is 0
//Also performs the CTS trick to force a USB packet
void finalize_comm(unsigned char status, unsigned char *data, unsigned char size) {
	unsigned char i;
	usart_putc(status);
	while (!(UCSRA & 0x20));
	if (status==0)
		for (i=0;i<size;i++)
			usart_putc(data[i]);
	wait(2000);
	PORTD^=0b00001000;
}


//I2C STATUS MONITOR

//Tag an I2C address as having thrown an error during the last attempt
void i2c_set_error(unsigned char address) {
	unsigned char num;
	num=i2c_lookup_index(address);
	if (num<16)
		i2c_error[num>>3]|=(1<<(num&0b00000111));
}

//Tag an I2C address as having failed to initialize
void i2c_set_unusable(unsigned char address) {	
	unsigned char num;
	num=i2c_lookup_index(address);
	if (num<16) {
		i2c_unusable[num>>3]|=(1<<(num&0b00000111));
		i2c_error[num>>3]|=(1<<(num&0b00000111));
	}
}

//Tag an I2C address as usable and functioning
void i2c_set_good(unsigned char address) {
	unsigned char num;
	num=i2c_lookup_index(address);
	if (num<16) {
		i2c_unusable[num>>3]&=~(1<<(num&0b00000111));
		i2c_error[num>>3]&=~(1<<(num&0b00000111));
	}
}

//Check if an I2C device has been initialized
unsigned char i2c_check_unusable(unsigned char address) {
	unsigned char num;
	num=i2c_lookup_index(address);
	if (num==0xFF || (i2c_unusable[num>>3]&(1<<(num&0b00000111))))
		return 1;
	return 0;
}

//Get an index from an I2C address
//Ordering is from 0 to 15
//R1,G1,B1,S1,R2,G2,B2,S2,R3,G3,B3,S3,R4,G4,B4,S4
unsigned char i2c_lookup_index(unsigned char address) {
	unsigned char i;
	for (i=0;i<4;i++) {
		if (address==(RED_MASK|ROW_MASKS[i]))
			return 0|i<<2;
		if (address==(GREEN_MASK|ROW_MASKS[i]))
			return 1|i<<2;
		if (address==(BLUE_MASK|ROW_MASKS[i]))
			return 2|i<<2;
		if (address==(SENSOR_MASK|ROW_MASKS[i]))
			return 3|i<<2;
	}
	return 0xFF;
}


//DISPATCH CALLS
//All handlers take either no arguments or an output buffer and size
//All handlers return a status byte
//If the status byte is nonzero, the output buffer is never sent

//Write module's LED drivers
//Input: 96 bytes of color data
//Output: None
//Errors: ERR_ARGS, ERR_I2C
unsigned char handle_module_write(void) {
	unsigned char buffer[96];
	if (usart_wait_for_data(96,buffer))
		return 1<<ERR_ARGS;
	return module_write(buffer);
}

//Write quadrant's LED drivers
//Input: 1 byte row index and 24 bytes of color data
//Output: None
//Errors: ERR_ARGS, ERR_I2C
unsigned char handle_row_write(void) {
	unsigned char buffer[25];
	if (usart_wait_for_data(25,buffer))
		return 1<<ERR_ARGS;
	return quadrant_write(buffer[0],buffer+1);
}

//Read module's sensors
//Input: None
//Output: 8 bytes of sensor data
//Errors: ERR_I2C
unsigned char handle_module_read(unsigned char *data, unsigned char *size) {
	*size=8;
	return module_read(data);
}

//Read rows's sensors
//Input: 1 byte row index
//Output: 2 bytes of sensor data
//Errors: ERR_ARGS, ERR_I2C
unsigned char handle_row_read(unsigned char *data, unsigned char *size) {
	unsigned char buffer[1];
	if (usart_wait_for_data(1,buffer))
		return 1<<ERR_ARGS;
	*size=2;
	return quadrant_read(buffer[0],data);
}

//Write module's LED drivers and read modules's sensors
//Input: 96 bytes of color data
//Output: 8 bytes of sensor data
//Errors: ERR_ARGS, ERR_I2C
unsigned char handle_module_rw(unsigned char *data, unsigned char *size) {
	unsigned char buffer[96];
	if (usart_wait_for_data(96,buffer))
		return 1<<ERR_ARGS;
	*size=8;
	return module_write(buffer) | module_read(data);
}

//Write module's LED drivers and read modules's sensors
//Input: 1 byte row index, 24 bytes of color data
//Output: 2 bytes of sensor data
//Errors: ERR_ARGS, ERR_I2C
unsigned char handle_row_rw(unsigned char *data, unsigned char *size) {
	unsigned char buffer[25];
	if (usart_wait_for_data(25,buffer))
		return 1<<ERR_ARGS;
	*size=2;	
	return quadrant_write(buffer[0], buffer+1) | quadrant_read(buffer[0],data);
}

//Write module's LED drivers to all black
//Input: None
//Output: None
//Errors: ERR_I2C
unsigned char handle_module_black(void) {
	unsigned char buffer[96];
	unsigned char i;
	for (i=0;i<96;i++)
		buffer[i]=0xFF;
	return module_write(buffer);
}

//Repeat initiation of drivers and sensors
//Input: None
//Output: None
//Errors: ERR_I2C
unsigned char handle_module_reset(void) {
	return module_init();
}

//Return a status byte
//Input: None
//Output: None
//Errors: None
unsigned char handle_ping(void) {
	return 0;
}

//Ouput all subsequent data until an EXIT_ECHO command is received
//Note that both ENTER_ECHO and EXIT_ECHO return status bytes
//Input: None
//Output: None
//Errors: None
unsigned char handle_enter_echo_mode(void) {
	unsigned char c;
	usart_putc(0);
	c=usart_getc();
	while(c!=COMMAND_EXIT_ECHO_MODE) {
		usart_putc(c);
		c=usart_getc();
	}
	return 0;
}

//Test USART response
//Input: None
//Output: 5 bytes of DISCO
//Errors: None
unsigned char handle_usart_test(unsigned char *data, unsigned char *size) {
	data[0]='D';
	data[1]='I';
	data[2]='S';
	data[3]='C';
	data[4]='O';
	*size=5;
	return 0;
}

//Sends the current version number
//Input: None
//Output: 1 byte of version number
//Errors: None
unsigned char handle_send_version(unsigned char *data, unsigned char *size) {
	data[0]=VERSION;
	*size=1;
	return 0;
}

//Sends the current I2C status
//Input: None
//Output: 2 bytes of i2c status
//Errors: None
unsigned char handle_send_i2c_status(unsigned char *data, unsigned char *size) {
	data[0]=i2c_error[0];
	data[1]=i2c_error[1];
	*size=2;
	return 0;
}

//Run a self test until an EXIT_SELF_TEST command is received
//Note that both ENTER_SELF_TEST and EXIT_SELF_TEST return status bytes
//Input: None
//Output: None
//Errors: None
unsigned char handle_enter_self_test_0(void) {
	unsigned char err=0;
	unsigned char buffer[8];
	unsigned char i;
	usart_putc(0);
	while(!data_in_rx_buffer() || usart_getc()!=COMMAND_EXIT_SELF_TEST) {
		for (i=0;i<8;i++)
			buffer[i]=0x88;
		driver_write(RED_MASK|ROW_MASKS[0],buffer);
	}
	return err;
}


//MODULE LEVEL OPERATIONS

//Initialize all LED drivers and sensors, updating i2c_unusable
unsigned char module_init(void) {
	unsigned char i,err=0;
	for (i=0;i<4;i++)
		err|=quadrant_init(i);
	return err;
}

//Write a buffer of 96 bytes to module's LED drivers, updating i2c_error
unsigned char module_write(unsigned char *buffer) {
	unsigned char err;
	err=quadrant_write(0,buffer);
	err|=quadrant_write(1,buffer+24);
	err|=quadrant_write(2,buffer+48);
	err|=quadrant_write(3,buffer+72);
	return err;
}

//Read module's sensors into an 8 byte buffer, updating i2c_error
unsigned char module_read(unsigned char *data) {
	unsigned char err;
	err=sensor_read(SENSOR_MASK|ROW_MASKS[0],data);
	err|=sensor_read(SENSOR_MASK|ROW_MASKS[1],data+2);
	err|=sensor_read(SENSOR_MASK|ROW_MASKS[2],data+4);
	err|=sensor_read(SENSOR_MASK|ROW_MASKS[3],data+6);
	return err;
}


//QUADRANT LEVEL OPERATIONS
unsigned char quadrant_init(unsigned char row) {
	unsigned char err;
	err=driver_init(RED_MASK|ROW_MASKS[row]);
	err|=driver_init(GREEN_MASK|ROW_MASKS[row]);
	err|=driver_init(BLUE_MASK|ROW_MASKS[row]);
	err|=sensor_init(SENSOR_MASK|ROW_MASKS[row]);
	return err;
}

//Write a buffer of 24 bytes to a quadrant's LED drivers, updating i2c_error
unsigned char quadrant_write(unsigned char row, unsigned char *buffer) {
	unsigned char err;
	err=driver_write(RED_MASK|ROW_MASKS[row],buffer);
	err|=driver_write(GREEN_MASK|ROW_MASKS[row],buffer+8);
	err|=driver_write(BLUE_MASK|ROW_MASKS[row],buffer+16);
	return err;
}

//Read a quadrant's sensors into a 2 byte buffer, updating i2c_error
unsigned char quadrant_read(unsigned char address, unsigned char *data) {
	return sensor_read(address,data);
}


//CHIP LEVEL OPERATIONS

//Initialize a LED driver, updating i2c_unusable
unsigned char driver_init(unsigned char address) {
	unsigned char message[3];
	//config register
	message[0] = 0x0F;
	message[1] = 0x08;
	if (i2c_transmit(address, message, 2 )) {
		i2c_set_unusable(address);
		return 1<<ERR_I2C;
	}
	//port config
	message[0] = 0x06; 
	message[1] = 0x00;
	message[2] = 0x00;
	if (i2c_transmit(address, message, 3 )) {
		i2c_set_unusable(address);
		return 1<<ERR_I2C;
	}
 	//blink phase 0
	message[0] = 0x02; 
	message[1] = 0xFF;
	message[2] = 0xFF;
	if (i2c_transmit(address, message, 3 )) {
		i2c_set_unusable(address);
		return 1<<ERR_I2C;
	}
	//master intensity full on
	message[0] = 0x0E;
	message[1] = 0xFF;
	if (i2c_transmit(address, message, 2 )) {
		i2c_set_unusable(address);
		return 1<<ERR_I2C;
	}
	i2c_set_good(address);
	return 0;
}

//Write a buffer of 8 bytes to a LED driver, updating i2c_error
unsigned char driver_write(unsigned char address, unsigned char *buffer) {
	unsigned char i;
	unsigned char message[9];
	if (i2c_check_unusable(address)) {
		i2c_set_error(address);
		return 1<<ERR_I2C;
	}
	message[0]=0x10;
	for (i=0;i<8;i++)
		message[i+1]=buffer[i];
	if (i2c_transmit(address,message,9)) {
		i2c_set_error(address);
		return 1<<ERR_I2C;
	}
	i2c_set_good(address);
	return 0;
}

//Initialize a sensor, updating i2c_unusable
unsigned char sensor_init(unsigned char address) {
	unsigned char message[3];
	//config register
	message[0] = 0x0F;
	message[1] = 0x00;
	if (i2c_transmit(address, message, 2 )) {
		i2c_set_unusable(address);
		return 1<<ERR_I2C;
	}
	//port config
	message[0] = 0x06;
	message[1] = 0xFF;
	message[2] = 0xFF;
	if (i2c_transmit(address, message, 3 )) {
		i2c_set_unusable(address);
		return 1<<ERR_I2C;
	}
	i2c_set_good(address);
	return 0;
}

//Read a sensor into a 2 byte buffer, updating i2c_error
unsigned char sensor_read(unsigned char address, unsigned char *data) {
	if (i2c_check_unusable(address) || i2c_start((address<<1)) || i2c_write(0x00) || i2c_receive(address,data,2)) {
		i2c_set_error(address);
		return 1<<ERR_I2C;
	}
	i2c_set_good(address);
	return 0;
}
