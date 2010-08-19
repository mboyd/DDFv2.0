//USART Libary
//Modified for DDF Firmware
//Original Source:
//Atmel AVR USART Library for GCC
//Version: 1.0
//Jaakko Ala-Paavola 2003/06/28
//http://www.iki.fi/jap email:jap@iki.fi

#include <avr/io.h>
#include <avr/interrupt.h>
#include "usart.h"
#include <string.h>

static unsigned char USART_RxBuf[USART_RX_BUFFER_SIZE];
static volatile unsigned char USART_RxHead;
static volatile unsigned char USART_RxTail;
static volatile unsigned char USART_RxSize;

//Initialize the USART to given baudrate.  U2X is not set.
void usart_init(unsigned char baud_divider) {
	UBRRH = 0x00;
	UBRRL = baud_divider;
	UCSRA = 0x00;
	UCSRC = 0x86;			// Access UCSRC, Asyncronous 8N1
	UCSRB = 0x98;			// Receiver, Transmitter, RX Complete interrupt enabled
	sei();					// Enable interrupts globally
	usart_flushRx();
}

//Transmits a single byte 
void usart_putc(char data) {
    while (!(UCSRA & 0x20));	// Wait untill USART data register is empty
    UDR = data;					// Transmit data
}

void usart_puts(char *data) {
	int len, count;
	len = strlen(data);
	for (count = 0; count < len; count++) 
		usart_putc(*(data+count));
}

//Block on receiving one byte
char usart_getc(void) {
	unsigned char tmptail;
	while ( USART_RxHead == USART_RxTail );  				// Wait for incomming data
	tmptail = ( USART_RxTail + 1 ) & USART_RX_BUFFER_MASK;	// Calculate buffer index	
	USART_RxTail = tmptail;                					// Store new index
	USART_RxSize--;
	return USART_RxBuf[tmptail];           				// Return data
}

//Receive interrupt
SIGNAL(SIG_UART_RECV) {
  	unsigned char data;
	unsigned char tmphead;
	data = UDR;												// Read the received data
	tmphead = ( USART_RxHead + 1 ) & USART_RX_BUFFER_MASK;	// Calculate buffer index
	USART_RxHead = tmphead;      							// Store new index
	if ( tmphead == USART_RxTail )
        usart_flushRx();
	USART_RxSize++;
	USART_RxBuf[tmphead] = data; 							// Store received data in buffer
}

//Flush the USART buffer
void usart_flushRx()
{
	USART_RxHead=0;
	USART_RxTail=0;
	USART_RxSize=0;
}

// Return 0 if the receive buffer is empty
volatile unsigned char data_in_rx_buffer( void )
{
	return ( USART_RxHead != USART_RxTail );
}

//Waits for num_bytes bytes, but times out if they don't arrive
unsigned char usart_wait_for_data(unsigned char num_bytes, unsigned char *buffer)
{
	unsigned int f,timeout;
	for (f=0;f<num_bytes;f++) {
		for (timeout=0;timeout<0xFF;timeout++) {
			if (data_in_rx_buffer()) {
				*buffer=usart_getc();
				buffer++;
				break;
			}
		}
		if (timeout==0xFF)
			return 1;
	}
	return 0;
}
