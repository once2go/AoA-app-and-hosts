#include <stdio.h>
#include "androidaccessory.h"

 
#define BUFFER_SIZE 64

 
int main()
{
    AA_Credentials credentials;
    credentials.manufacturer = "once2go";
    credentials.model_name = "ProjectionApp";
    credentials.description = "ProjectionApp";
    credentials.version = "1.0";
    credentials.URI = "uri";
    credentials.serialNumber = "1234567890ABCDEF";


connection:
    if (!connect(&connection)){
        printf("\nNo active devices");
        switchToAAMode(&credentials);
        usleep(1000000);
        goto connection;
    }
 
   while (1) {
        uint8_t buffer[BUFFER_SIZE];
        readData(buffer, BUFFER_SIZE);
        fwrite(buffer, 1, BUFFER_SIZE, stdout);
    }
    return 0;
}


 

