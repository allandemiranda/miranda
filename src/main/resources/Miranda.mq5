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
   
   string cookie=NULL, result_headers;
   string headers="Content-Type: application/json\r\n";
   char   post[],result[];


   string s = "{\n\t\"dateTime\": \"2007-12-03T10:15:30\",\n\t\"bid\": 1.12345,\n\t\"ask\": 2.12345,\n\t\"symbol\": {\n\t\t\"value\": \"EURUSD\",\n\t\t\"margin\": \"EUR\",\n\t\t\"profit\": \"USD\",\n\t\t\"swap\": {\n\t\t\t\"longTax\": -1.2,\n\t\t\t\"shortTax\": 3.3,\n\t\t\t\"rateTriple\": \"WEDNESDAY\"\n\t\t}\n\t}\n}";



   StringToCharArray(s, post);



   ResetLastError();
   int res = WebRequest(Method,Address,headers,500,post,result,result_headers);
   if(res==200) {
      Print(CharArrayToString(result));
   } else {
      PrintFormat("Bad reqeust, error code %d, url %s", res, Address);
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