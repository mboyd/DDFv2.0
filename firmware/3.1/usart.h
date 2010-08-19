//USART Libary
//Modified for DDF Firmware
//Original Source:
//Atmel AVR USART Library for GCC
//Version: 1.0
//Jaakko Ala-Paavola 2003/06/28
//http://www.iki.fi/jap email:jap@iki.fi

#define USART_RX_BUFFER_SIZE 128     /* 2,4,8,16,32,64,128 or 256 bytes */
#define USART_RX_BUFFER_MASK ( USART_RX_BUFFER_SIZE - 1 )
#if ( USART_RX_BUFFER_SIZE & USART_RX_BUFFER_MASK )
	#error RX buffer size is not a power of 2
#endif

//Initializes USART device.
//Consult datasheet for baud_divider.
void usart_init(unsigned char baud_divider);

//Transmit one character. No buffering.
void usart_putc(char);

//Transmit a zero terminated string.
void usart_puts(char*);

//Receive one character. Blocking operation, if no new data in buffer.
char usart_getc(void);

//Returns number of unread character in ring buffer.
unsigned char usart_unread_data(void);

//Flush receive buffer
void usart_flushRx(void);

//Checks if data is in the receive buffer
volatile unsigned char data_in_rx_buffer(void);

//Receive blocking with timeout
unsigned char usart_wait_for_data(unsigned char num_bytes, unsigned char *buffer);
