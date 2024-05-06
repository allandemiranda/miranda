//+------------------------------------------------------------------+
//|                                                      ProjectName |
//|                                      Copyright 2020, CompanyName |
//|                                       http://www.companyname.net |
//+------------------------------------------------------------------+
#property version "1.00"
#property script_show_inputs

input string Address = "localhost";
input int Port = 8080;
input string Method = "POST";
input string Path = "/metatrader/info";
input int Timeout = 1000;
input ENUM_TIMEFRAMES TimeFrame = PERIOD_CURRENT;

//+------------------------------------------------------------------+
//|                                                                  |
//+------------------------------------------------------------------+
bool HTTPSend(const int socket, const string request) {
   char req[];
   const int len = StringToCharArray(request,req) - 1;
   if(len<0) {
      return(false);
   } else {
      return(SocketSend(socket,req,len)==len);
   }
}

//+------------------------------------------------------------------+
//|                                                                  |
//+------------------------------------------------------------------+
string HTTPReceive(const int socket) {
   do {
      const uint len = SocketIsReadable(socket);
      if(len) {
         char rsp[];
         const int rspLen = SocketRead(socket, rsp, len, Timeout);
         if(rspLen>0) {
            const string result = CharArrayToString(rsp,0,rspLen);
            const int headerEnd = StringFind(result,"\r\n\r\n");
            if(headerEnd>0) {
               return(CharArrayToString(rsp,headerEnd+4, rspLen));
            }
         }
      }
   } while(!IsStopped());
   return("");
}

//+------------------------------------------------------------------+
//|                                                                  |
//+------------------------------------------------------------------+
string systemComunication(const string dateTime, const double bid, const double ask, const string symbol, const string symbolMargin, const string symbolProfit, const int digits, const double swapLong, const double swapShort, const ENUM_DAY_OF_WEEK rateTriple, const ENUM_TIMEFRAMES timeFrame) {
   int socket=SocketCreate();
   if(socket!=INVALID_HANDLE) {
      if(SocketConnect(socket, Address, Port, Timeout)) {

         string http, host, body;
         StringConcatenate(host, Address, ":", Port);
         StringConcatenate(body, "{\n\t\"dateTime\": \"", dateTime, "\",\n\t\"bid\": ", bid, ",\n\t\"ask\": ", ask, ",\n\t\"symbol\": {\n\t\t\"value\": \"", symbol, "\",\n\t\t\"margin\": \"", symbolMargin, "\",\n\t\t\"profit\": \"", symbolProfit, "\",\n\t\t\"digits\": ", digits, ",\n\t\t\"swap\": {\n\t\t\t\"longTax\": ", swapLong, ",\n\t\t\t\"shortTax\": ", swapShort, ",\n\t\t\t\"rateTriple\": \"", rateTriple, "\"\n\t\t}\n\t},\n\t\"timeFrame\": \"", StringSubstr(EnumToString(timeFrame), 7), "\"\n}");
         StringConcatenate(http, Method, " ", Path, " HTTP/1.1\r\nContent-Type: application/json\r\nUser-Agent: MT5\r\nHost: ", host, "\r\nContent-Length: ", StringLen(body), "\r\n\r\n", body);

         if(HTTPSend(socket, http)) {
            const string receive = HTTPReceive(socket);
            if(StringLen(receive)!=0) {
               return(receive);
            } else {
               Print("Falha ao obter resposta, erro ",GetLastError());
            }
         } else {
            Print("Falha ao enviar solicitação", Method, ", erro ",GetLastError());
         }

      } else {
         Print("Falhou conexão a ",Address,":",Port,", erro ",GetLastError());
      }
      SocketClose(socket);
   } else {
      Print("Não foi possível criar o soquete, erro ",GetLastError());
   }
   return("ERROR");
}

//+------------------------------------------------------------------+
//|                                                                  |
//+------------------------------------------------------------------+
string GetLocalDateTime(const string currentSymbol) {
   const int symbolTimeInfo = SymbolInfoInteger(currentSymbol, SYMBOL_TIME);
   string dateTime = TimeToString(symbolTimeInfo,TIME_DATE|TIME_SECONDS);
   StringReplace(dateTime, ".", "-");
   StringReplace(dateTime, " ", "T");
   return dateTime;
}

//+------------------------------------------------------------------+
//|                                                                  |
//+------------------------------------------------------------------+
int OnInit() {
   return(INIT_SUCCEEDED);
}

//+------------------------------------------------------------------+
//|                                                                  |
//+------------------------------------------------------------------+
void OnTick() {
   const string dateTime = GetLocalDateTime(Symbol());
   const double bid = SymbolInfoDouble(Symbol(), SYMBOL_BID);
   const double ask = SymbolInfoDouble(Symbol(), SYMBOL_ASK);
   const string symbol = Symbol();
   const string symbolMargin = SymbolInfoString(Symbol(), SYMBOL_CURRENCY_MARGIN);
   const string symbolProfit = SymbolInfoString(Symbol(), SYMBOL_CURRENCY_PROFIT);
   const int digits = SymbolInfoInteger(Symbol(), SYMBOL_DIGITS);
   const double swapLong = SymbolInfoDouble(Symbol(), SYMBOL_SWAP_LONG);
   const double swapShort = SymbolInfoDouble(Symbol(), SYMBOL_SWAP_SHORT);
   const ENUM_DAY_OF_WEEK rateTriple = SymbolInfoInteger(Symbol(), SYMBOL_SWAP_ROLLOVER3DAYS) - 1;
   const ENUM_TIMEFRAMES currentTimeFrame = (TimeFrame==PERIOD_CURRENT ? Period() : TimeFrame);

   const string result = systemComunication(dateTime,bid,ask,symbol,symbolMargin,symbolProfit,digits,swapLong,swapShort,rateTriple,currentTimeFrame);
   Print(result);
   
   CSocket cs;
   
}
//+------------------------------------------------------------------+
