package com.example.myble.ble


/** BleBaseUuid */
const val BASE_BASIC = "0000XXXX00001000800000805F9B34FB"
//const val BASE_CUSTOM_SENSOR = "5301XXXX558A41FABA2784FAED3056BB"
//const val BASE_CUSTOM_CONTROL = "5302XXXX558A41FABA2784FAED3056BB"

/** BleBasicSvcUuid */
const val BASIC_SVC_GENERIC_ACCESS = "1800"
const val BASIC_SVC_GENERIC_ATTRIBUTE = "1801"
const val BASIC_SVC_DEVICE_INFORMATION = "180A"
const val BASIC_SVC_NORDIC_4 = "FE59"

/** BleBasicCharUuid */
const val BASIC_CHAR_DEVICE_NAME = "2A00"
const val BASIC_CHAR_APPEARANCE = "2A01"
const val BASIC_CHAR_PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS = "2A04"
const val BASIC_CHAR_CENTRAL_ADDRESS_RESOLUTION = "2AA6"
const val BASIC_CHAR_MANUFACTURER_NAME_STRING = "2A29"
const val BASIC_CHAR_SOFTWARE_REVISION_STRING = "2A28"
const val BASIC_CHAR_NORDIC_BUTTONLESS_DFU = "2A29"

const val TRANSMIT_STM_IMAGE_UUID = "43825502-1d15-43c7-9b6d-2dde08c11a2c"

// /** BleCustomSvcUuid */
// const val CUSTOM_SVC_SENSOR = "1700"
// const val CUSTOM_SVC_CONTROL = "2700"

// /** BleCustomCharUuid */
// const val CUSTOM_CHAR_SENSOR_EEG = "1701"
// const val CUSTOM_CHAR_SENSOR_PPG_CONN = "1702"
// const val CUSTOM_CHAR_SENSOR_IMP = "1703"
// const val CUSTOM_CHAR_CONTROL_NIR = "2701"