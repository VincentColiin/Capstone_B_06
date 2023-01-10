#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <ArduinoJson.h>
#include <SoftwareSerial.h>

#define REPORTING_PERIOD_MS 1000
uint32_t tsLastReport = 0;
SoftwareSerial uno(D1,D2);
ESP8266WebServer server;
char* ssid = "VC";
char* password = "ctyj7080";

char data;
String dataReceived;
int8_t BPM_index, SpO2_index;
String BPM, SpO2;

void setup()
{
  WiFi.begin(ssid,password);
  Serial.begin(9600);
  uno.begin(9600);
  while(WiFi.status()!=WL_CONNECTED)
  {
    Serial.print(".");
    delay(500);
  }
  Serial.println("");
  Serial.print(F("IP Address: "));
  Serial.println(WiFi.localIP());

  server.on(F("/data"),HTTP_GET,sendData);
  server.begin();
}

void loop()
{
  server.handleClient();
  if (millis() - tsLastReport > REPORTING_PERIOD_MS) {
    while(uno.available()>0){
      data= uno.read();
      if(data=='\n') {break;}
      else        {dataReceived+=data;}
    }

      ParseData();

    if(data=='\n'){
      data=0;
      dataReceived="";
    }

    // Serial.print("BPM : ");
    // Serial.println(BPM.toFloat());
    // Serial.print("SpO2 : ");
    // Serial.println(SpO2.toFloat());
    tsLastReport = millis();
  }
}

void ParseData(){
  BPM_index = dataReceived.indexOf(F("A"));
  SpO2_index = dataReceived.indexOf(F("B"));

  BPM = dataReceived.substring  (0,BPM_index);
  SpO2 = dataReceived.substring (BPM_index+1, SpO2_index);
}

void sendData(){
  DynamicJsonDocument doc(1024);
  doc["BPM"] = BPM;
  doc["SpO2"] = SpO2;
  String data;
  serializeJson(doc,data);
  server.send(200,F("application/json"), data);

}