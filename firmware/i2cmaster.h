//I2C Library

//Initialize I2C for operation at 220kHz
void i2c_init(void);

//Send a start signal followed by address transmission
unsigned char i2c_start(unsigned char);
//Send a stop signal
void i2c_stop(void);

//writes one byte to I2C buffer
unsigned char i2c_write(unsigned char);
//returns data and handles ACK or NAK
unsigned char i2c_read(unsigned char);

//Start, Send data, Stop
unsigned char i2c_transmit(unsigned char, unsigned char[], unsigned char);
//Start, Receive data, Stop (if you need to init a memory address first, do so)
unsigned char i2c_receive(unsigned char,unsigned char*, unsigned char);
