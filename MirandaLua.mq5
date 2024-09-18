//+------------------------------------------------------------------+
//|                                                   MirandaLua.mq5 |
//|                                                 Allan de Miranda |
//|                               https://github.com/allandemiranda/ |
//+------------------------------------------------------------------+
#property copyright "Allan de Miranda"
#property link      "https://github.com/allandemiranda/"
#property version   "1.10"

//+------------------------------------------------------------------+
//| Definição de ENUMs                                               |
//+------------------------------------------------------------------+
enum ENUM_ALLOWED_TIMEFRAMES {
   M1 = PERIOD_M1,
   M15 = PERIOD_M15,
   M30 = PERIOD_M30
};

enum ENUM_TIME_INTERVALS {
   Interval_00_04,
   Interval_04_08,
   Interval_08_12,
   Interval_12_16,
   Interval_16_20,
   Interval_20_24
};

enum ENUM_SIGNAL_INDICATOR {
   BUY,
   SELL,
   NEUTRAL
};

//+------------------------------------------------------------------+
//| Inputs do robô                                                   |
//+------------------------------------------------------------------+

// Lista de seleção dos timeframes permitidos
input ENUM_ALLOWED_TIMEFRAMES allowedTimeframe = M15; // Seleção de timeframes permitidos

// Definição do dia da semana e intervalo de horas permitidos
input ENUM_DAY_OF_WEEK tradingDay = MONDAY;   // Dia da semana para negociação

// Lista de intervalos de horas permitidos
input ENUM_TIME_INTERVALS timeInterval = Interval_00_04; // Intervalo de horas permitidos

// Tamanho do lote
input double lotSize = 0.01; // Tamanho do lote para negociação

// Parâmetros do ADX
input int adxPeriod = 14;
input double adxLevel = 25;

// Parâmetros do RSI
input int rsiPeriod = 14;
input double rsiOverbought = 70.0;
input double rsiOversold = 30.0;

// Take Profit e Stop Loss em pontos
input int takeProfit = 100; // TP em pontos
input int stopLoss = 70;    // SL em pontos

// Símbolo
string symbol = Symbol();

// Variável para armazenar o manipulador dos indicatores
int rsiHandle, adxHandle;

// Variável para armazenar o timestamp do último candlestick criado
datetime lastCheckedCandleTime = 0;

//+------------------------------------------------------------------+
//| Função OnInit                                                    |
//+------------------------------------------------------------------+
int OnInit(void) {
   // ADX init
   adxHandle = iADX(symbol, (ENUM_TIMEFRAMES) allowedTimeframe, adxPeriod);
   if(adxHandle == INVALID_HANDLE) {
      PrintFormat("Falha ao criar o manipulador do indicador iADX para o símbolo %s/%s timeslot %s, código de erro %d", symbol, EnumToString((ENUM_TIMEFRAMES) allowedTimeframe), EnumToString(timeInterval), GetLastError());
      return(INIT_FAILED);
   }

   // RSI init
   rsiHandle = iRSI(symbol, (ENUM_TIMEFRAMES) allowedTimeframe, rsiPeriod, PRICE_CLOSE);
   if(rsiHandle == INVALID_HANDLE) {
      PrintFormat("Falha ao criar o manipulador do indicador iRSI para o símbolo %s/%s timeslot %s, código de erro %d", symbol, EnumToString((ENUM_TIMEFRAMES) allowedTimeframe), EnumToString(timeInterval), GetLastError());
      return(INIT_FAILED);
   }

   return(INIT_SUCCEEDED);
}

//+------------------------------------------------------------------+
//| Função OnTick                                                    |
//+------------------------------------------------------------------+
void OnTick(void) {
   // Obtenha o horário de abertura do último candlestick
   datetime currentCandleTime = iTime(symbol, (ENUM_TIMEFRAMES) allowedTimeframe, 1);  // 1 significa o último candlestick fechado, 0 é o candlestick atual inacabada

   // Verifique se um novo candlestick foi formado (o tempo mudou)
   if(currentCandleTime != lastCheckedCandleTime) {
      lastCheckedCandleTime = currentCandleTime;  // Atualizar hora do último candlestick verificada

      if(IsTradingTimeAllowed(lastCheckedCandleTime)) {
         // Copie os valores ADX do buffer do indicador para o último candlestick fechado
         ENUM_SIGNAL_INDICATOR adxSignal = NEUTRAL;
         double adxValue[], plusDIValue[], minusDIValue[];
         if(CopyBuffer(adxHandle, 0, 1, 1, adxValue) > 0 &&  // ADX buffer
               CopyBuffer(adxHandle, 1, 1, 1, plusDIValue) > 0 &&  // +DI buffer
               CopyBuffer(adxHandle, 2, 1, 1, minusDIValue) > 0) { // -DI buffer

            if(adxValue[0] >= adxLevel) {
               if(plusDIValue[0] > minusDIValue[0]) {
                  adxSignal = BUY;
               } else if(plusDIValue[0] < minusDIValue[0]) {
                  adxSignal = SELL;
               }
            }

         } else {
            Print("Failed to retrieve ADX values.");
         }

         // Copie os valores RSI do buffer do indicador para o último candlestick fechado
         ENUM_SIGNAL_INDICATOR rsiSignal = NEUTRAL;
         double rsiValue[];
         if(CopyBuffer(rsiHandle, 0, 1, 1, rsiValue) > 0) {

            if(rsiValue[0] >= rsiOverbought) {
               rsiSignal = SELL;
            } else if(rsiValue[0] <= rsiOversold) {
               rsiSignal = BUY;
            }

         } else {
            Print("Failed to retrieve RSI value.");
         }

         if(adxSignal == rsiSignal && adxSignal != NEUTRAL) {
            OpenTrade(adxSignal);
         }
      }

   }
}

//+------------------------------------------------------------------+
//| Função para abrir uma operação                                   |
//+------------------------------------------------------------------+
void OpenTrade(const ENUM_SIGNAL_INDICATOR signalIndicator) {
   MqlTradeRequest request;
   MqlTradeResult result;

   double price, sl, tp;
   ENUM_ORDER_TYPE orderType;

   // Defina preços com base no tipo de pedido
   if (signalIndicator == BUY) {
      orderType = ORDER_TYPE_BUY;
      price = SymbolInfoDouble(symbol, SYMBOL_ASK);
      sl = price - stopLoss * _Point;
      tp = price + takeProfit * _Point;
   } else if (signalIndicator == SELL) {
      orderType = ORDER_TYPE_SELL;
      price = SymbolInfoDouble(symbol, SYMBOL_BID);
      sl = price + stopLoss * _Point;
      tp = price - takeProfit * _Point;
   } else {
      return;
   }

   // Preenche a estrutura do pedido de negociação
   ZeroMemory(request);
   request.action = TRADE_ACTION_DEAL;
   request.symbol = symbol;
   request.volume = lotSize; // Usa o tamanho do lote configurado no input
   request.type = orderType;
   request.price = price;
   request.sl = sl;
   request.tp = tp;
   request.deviation = 2;

   // Envia o pedido de negociação
   if (!OrderSend(request, result)) {
      Print("Error opening order: ", result.retcode);
   } else {
      // Converte o intervalo de tempo para uma string legível
      string timeRange = "";
      switch (timeInterval) {
      case Interval_00_04:
         timeRange = "00:00h-03:59h";
         break;
      case Interval_04_08:
         timeRange = "04:00h-07:59h";
         break;
      case Interval_08_12:
         timeRange = "08:00h-11:59h";
         break;
      case Interval_12_16:
         timeRange = "12:00h-15:59h";
         break;
      case Interval_16_20:
         timeRange = "16:00h-19:59h";
         break;
      case Interval_20_24:
         timeRange = "20:00h-23:59h";
         break;
      }

      // Mensagem de sucesso ao abrir a ordem
      Print("Order opened successfully. Ticket: ", result.order, ", Symbol: ", symbol, ", LotSize: ", DoubleToString(lotSize, 2), ", TimeFrame: ", EnumToString((ENUM_TIMEFRAMES) allowedTimeframe), ", Day: ", EnumToString(tradingDay), ", Time Range: ", timeRange);
   }
}

//+------------------------------------------------------------------+
//| Função para obter o dia da semana                                |
//+------------------------------------------------------------------+
ENUM_DAY_OF_WEEK TimeDayOfWeek(const datetime time) {
   MqlDateTime mt;
   TimeToStruct(time, mt);
   return (ENUM_DAY_OF_WEEK) mt.day_of_week;
}

//+------------------------------------------------------------------+
//| Função para obter a hora do dia                                  |
//+------------------------------------------------------------------+
int TimeHour(const datetime time) {
   MqlDateTime mt;
   TimeToStruct(time, mt);
   return mt.hour;
}

//+------------------------------------------------------------------+
//| Função para verificar se o horário é permitido                   |
//+------------------------------------------------------------------+
bool IsTradingTimeAllowed(const datetime lastTime) {
   ENUM_DAY_OF_WEEK currentWeek = TimeDayOfWeek(lastTime);
   if(currentWeek != tradingDay) {
      return false;
   } else {
      int currentHour = TimeHour(lastTime);
      switch (timeInterval) {
      case Interval_00_04:
         return (currentHour >= 0 && currentHour < 4);
      case Interval_04_08:
         return (currentHour >= 4 && currentHour < 8);
      case Interval_08_12:
         return (currentHour >= 8 && currentHour < 12);
      case Interval_12_16:
         return (currentHour >= 12 && currentHour < 16);
      case Interval_16_20:
         return (currentHour >= 16 && currentHour < 20);
      case Interval_20_24:
         return (currentHour >= 20 && currentHour < 24);
      default:
         return false;
      }
   }
}

//+------------------------------------------------------------------+
//| Função OnDeinit                                                  |
//+------------------------------------------------------------------+
void OnDeinit(const int reason) {
   // Release the ADX indicator handle
   if(adxHandle != INVALID_HANDLE) {
      IndicatorRelease(adxHandle);
   }

   // Release the RSI indicator handle
   if(rsiHandle != INVALID_HANDLE) {
      IndicatorRelease(rsiHandle);
   }
}
//+------------------------------------------------------------------+
