#ifndef ANDROIDACCESSORY_H
#define ANDROIDACCESSORY_H

#include <stdio.h>
#include <stdint.h>
#include <stdbool.h>
#include <unistd.h>
#include <libusb.h>
#include <ctype.h>

#define GOOGLE_VID    0x18D1

#define ACCESSORY_PID                   0x2D00
#define ACCESSORY_ADB_PID               0x2D01
#define AUDIO_PID                       0x2D02
#define AUDIO_ADB_PID                   0x2D03
#define ACCESSORY_AUDIO_PID             0x2D04
#define ACCESSORY_AUDIO_ADB_PID         0x2D05

#define FIRST_REQUEST_TYPE              0xC0
#define ANDROID_REQUEST_TYPE            0x40


#define ACCESSORY_GET_PROTOCOL          51
#define ACCESSORY_SEND_STRING           52
#define ACCESSORY_START                 53
#define ACCESSORY_SET_AUDIO_MODE        58

typedef struct{
    unsigned char *manufacturer;
    unsigned char *model_name;
    unsigned char *description;
    unsigned char *version;
    unsigned char *URI;
    unsigned char *serialNumber;
} AA_Credentials;

typedef struct {
    uint8_t inAddr;
    uint8_t outAddr;
    struct libusb_device_handle *accessoryHandle;
} Connection;

static Connection connection;

bool connect();
void switchToAAMode(AA_Credentials *credentials);
void readData(unsigned char *buffer, int length);
void sendData(uint8_t *buffer, int length);
void disconnect(void);

#endif // ANDROIDACCESSORY_H
