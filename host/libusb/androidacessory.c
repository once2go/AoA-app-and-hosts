#include "androidaccessory.h"

void switchToAAMode(AA_Credentials *credentials) {
    printf("\nWhich device should be activated?");
    struct libusb_device **list;
    struct libusb_device_descriptor desc = {0};
    struct libusb_device_handle *handle;
    unsigned char buffer[128];

    libusb_init(NULL);
    ssize_t size = libusb_get_device_list (NULL, &list);
    for (int i = 0; i < size; i++)
    {
        libusb_get_device_descriptor(list[i], &desc);
        libusb_get_device_descriptor(list[i], &desc);
        if (desc.bDeviceClass == 0) {
            buffer[0] = 0;
            libusb_open(list[i], &handle);
            printf("\n[%d] \t%04x:%04x -> ", i, desc.idVendor, desc.idProduct);
            libusb_get_string_descriptor_ascii(handle, desc.iProduct, buffer, sizeof(buffer));
            if (buffer[0] == '\0') {
                libusb_get_string_descriptor_ascii(handle, desc.iManufacturer, buffer, sizeof(buffer));
            }
            printf("%s", buffer);
        }
    }

    char word[16];
    printf("\n:");
    int input;
user_input:
    if(fgets(word, sizeof(word), stdin)) {
        sscanf(word, "%d", &input);
        if (input < 0 || input >= size) {
            printf("\nWrong value.Try again\n:");
            goto user_input;
        }
    }
    libusb_open(list[input], &handle);
    unsigned char buf[2];
    int response;
    response = libusb_control_transfer(handle, FIRST_REQUEST_TYPE,  ACCESSORY_GET_PROTOCOL, 0, 0, buf, 2, 0);
    if (response < 0 || (buf[1] << 8 | buf[0] < 2)) {
        printf("\nDevice does not support accessoty protocol. Try another one\n:");
        goto user_input;
    }
    printf("Protocol supported \n");
    unsigned char* creds[6];
    creds[0] = credentials->manufacturer;
    creds[1] = credentials->model_name;
    creds[2] = credentials->description;
    creds[3] = credentials->version;
    creds[4] = credentials->URI;
    creds[5] = credentials->serialNumber;
    for(uint16_t i=0; i < 6; i++) {
        response = libusb_control_transfer(handle, ANDROID_REQUEST_TYPE,
                                           ACCESSORY_SEND_STRING,
                                           0,
                                           i,
                                           creds[i],
                                           strlen((char*) creds[i]) + 1,
                                           0);
    }
    if (response < 0) return;
    printf("\nDo you want audio forwarding support?(Y/N):");
    bool audio_support = false;
    if(fgets(word, sizeof(word), stdin)) {
        if (tolower(word[0]) == 'y') {
            audio_support = true;
        }
    }
    if (audio_support) {
        response = libusb_control_transfer(handle, ANDROID_REQUEST_TYPE,  ACCESSORY_SET_AUDIO_MODE, 1, 0, NULL, 0, 0);
        if (response < 0) return;
        printf("\nSet audio forwarding ");
    }
    response = libusb_control_transfer(handle, ANDROID_REQUEST_TYPE,ACCESSORY_START, 0, 0, NULL, 0, 0);
    if (response < 0) return ;
    if ( handle != NULL) {
        libusb_release_interface (handle, 0);
    }
    libusb_free_device_list(list, 1);
    libusb_exit(NULL);
    printf("\nStart Accessory Mode");
    printf("\n");
}


bool connect() {
    struct libusb_device **list;
    struct libusb_config_descriptor *config;
    struct libusb_device_descriptor desc = {0};
    struct libusb_device_handle *handle;
    libusb_init(NULL);
    ssize_t size = libusb_get_device_list (NULL, &list);
    for (int i = 0; i < size; i++)
    {
        libusb_get_device_descriptor(list[i], &desc);
        if (desc.idVendor == GOOGLE_VID && (
                    desc.idProduct == ACCESSORY_PID             ||
                    desc.idProduct == ACCESSORY_ADB_PID         ||
                    desc.idProduct == AUDIO_PID                 ||
                    desc.idProduct == AUDIO_ADB_PID             ||
                    desc.idProduct == ACCESSORY_AUDIO_PID       ||
                    desc.idProduct == ACCESSORY_AUDIO_ADB_PID   )) {

            int speed = libusb_get_device_speed(list[i]);
            printf("\nSpeed: %d", speed); // 1 Low(1.5mb/s), 2 Full(12mb/s), 3 High(480mb/s)
            printf("\nUSB specification: %hx", desc.bcdUSB);
            printf("\nDevice class: %d", desc.bDeviceClass);
            printf("\nDevice subclass: %d", desc.bDeviceSubClass);
            printf("\nDevice protocol: %d", desc.bDeviceProtocol);
            printf("\nPacket size: %d", desc.bMaxPacketSize0);
            printf("\nNumber of configuratons: %d", desc.bNumConfigurations);
            printf("\nOpen device");

            libusb_get_active_config_descriptor (list[i], &config);
            printf("\nNumber of interfaces: %d", config->bNumInterfaces);
            printf("\nLength of extra: %d", config->extra_length);
            for (int i=0; i < config->bNumInterfaces; i++){
                printf("\n  Interface %d with alt. settings num %d",i, config->interface[i].num_altsetting);
                printf("\n  Interface number: %d", config->interface[i].altsetting->iInterface);
                printf("\n  Interface endpoints number: %d", config->interface[i].altsetting->bNumEndpoints);
                printf("\n  Interface class: %d", config->interface[i].altsetting->bInterfaceClass);
                printf("\n  Interface subclass: %d", config->interface[i].altsetting->bInterfaceSubClass);
                for (int j=0; j<config->interface[i].altsetting->bNumEndpoints; j++) {
                    printf("\n      Endpoint addr: %02x", config->interface[i].altsetting->endpoint[j].bEndpointAddress);
                    printf("\n      Endpoint attributes: %d", config->interface[i].altsetting->endpoint[j].bmAttributes);
                    printf("\n      Endpoint max packet size: %d", config->interface[i].altsetting->endpoint[j].wMaxPacketSize);
                    printf("\n      Endpoint polling interval: %d", config->interface[i].altsetting->endpoint[j].bInterval);
                    printf("\n      Endpoint audio refresh: %d", config->interface[i].altsetting->endpoint[j].bRefresh);
                    printf("\n      Endpoint audio sync address: %d", config->interface[i].altsetting->endpoint[j].bSynchAddress);
                    printf("\n      Endpoint extra length: %d", config->interface[i].altsetting->endpoint[j].extra_length);
                    printf("\n");
                }
                printf("\n  *Extra length: %d", config->interface[i].altsetting->extra_length);
                printf("\n ");
                printf("\n ");
            }
            int error = libusb_open(list[i], &handle);
            if (error) return false;
            printf("\nClaim interface 0");
            error = libusb_claim_interface(handle, 0);
            connection.accessoryHandle = handle;
            connection.inAddr = config->interface[0].altsetting->endpoint[0].bEndpointAddress;
            connection.outAddr = config->interface[0].altsetting->endpoint[1].bEndpointAddress;
            printf("\n in  %02x out %02x", connection.inAddr, connection.outAddr);
            if (error) return false;
            return true;
        }
    }
    libusb_free_device_list(list, 1);
    return false;
}

void readData(unsigned char *buffer, int length) {
    if (connection.accessoryHandle == NULL) return;
    int response = libusb_bulk_transfer(connection.accessoryHandle, connection.inAddr, buffer,
                                        length, &length, 0);
    if (response < 0) {
        printf("\nCant read data; Read %d bytes; Error: %d", length, response);
    }
}
void sendData(uint8_t *buffer, int length) {
    if (connection.accessoryHandle == NULL) return;
    int response = libusb_bulk_transfer(connection.accessoryHandle, connection.outAddr, buffer,
                                        length, &length, 0);
    if (response < 0) {
        printf("\nCant write data");
    }
}

void disconnect(void) {
    libusb_exit(NULL);
}


