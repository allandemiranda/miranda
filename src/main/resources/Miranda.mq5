#property version "1.00"
#property script_show_inputs

input string Address = "http://localhost:8080/metatrader/info";
input string Method = "POST";
input ENUM_TIMEFRAMES TimeFrame = PERIOD_CURRENT;

int OnInit() {
   return(INIT_SUCCEEDED);
} 

void OnTick() {  

   const string currentSymbol = Symbol();
   const int digits = SymbolInfoInteger(currentSymbol, SYMBOL_DIGITS);
   
   const string dateTime = GetLocalDateTime(currentSymbol);
   const string pair = Symbol();
   const string ask = DoubleToString(SymbolInfoDouble(currentSymbol,SYMBOL_ASK), digits);
   const string bid = DoubleToString(SymbolInfoDouble(currentSymbol,SYMBOL_BID), digits);
   const string timeFrame = TimeFrameDescription(TimeFrame);
   
   const string url = StringFormat("%s?dataTime=%s&pair=%s&ask=%s&bid=%s&timeFrame=%s&digits=%d", Address, dateTime, pair, ask, bid, timeFrame, digits);
   
   string cookie=NULL,headers;
   char   post[],result[];   
   ResetLastError();
   int res = WebRequest(Method,url,cookie,NULL,500,post,0,result,headers);
   if(res==200) {
      Print(CharArrayToString(result));
   } else {      
      PrintFormat("Bad reqeust, error code %d, url %s", res, url);
   }

}

string GetLocalDateTime(const string currentSymbol) {
   const int symbolTimeInfo = SymbolInfoInteger(currentSymbol, SYMBOL_TIME);
   string dateTime = TimeToString(symbolTimeInfo,TIME_DATE|TIME_SECONDS);
   StringReplace(dateTime, ".", "-");
   StringReplace(dateTime, " ", "T");
   return dateTime; 
}

string TimeFrameDescription(const ENUM_TIMEFRAMES period) {
   const ENUM_TIMEFRAMES currentPeriod = (period==PERIOD_CURRENT ? Period() : period);
   return(StringSubstr(EnumToString(currentPeriod), 7));
}