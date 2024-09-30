//+------------------------------------------------------------------+
//|                                                   MirandaLua.mq5 |
//|                                                 Allan de Miranda |
//|                               https://github.com/allandemiranda/ |
//+------------------------------------------------------------------+
#property copyright "Allan de Miranda"
#property link      "https://github.com/allandemiranda/"
#property version   "1.20"

//+------------------------------------------------------------------+
//| Definição de ENUMs                                               |
//+------------------------------------------------------------------+
enum ENUM_ALLOWED_TIMEFRAMES {
   M15 = PERIOD_M15,
   M30 = PERIOD_M30,
   H1 = PERIOD_H1
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
   NEUTRAL,
   DISABLE
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

// Take Profit e Stop Loss em pontos
input int takeProfit = 100; // TP em pontos
input int stopLoss = 70;    // SL em pontos

// Parâmetros do ADX
input bool adxActivate = true;
input int adxPeriod = 14;
input double adxLevel = 25;
input bool adxReverse = false;

// Parâmetros do RSI
input bool rsiActivate = true;
input int rsiPeriod = 14;
//input double rsiOverbought = 70;
input double rsiOverbought = 50;
//input double rsiOversold = 30;
input double rsiOversold = 50;
input ENUM_APPLIED_PRICE rsiAppliedPrice = PRICE_CLOSE;
input bool rsiReverse = false;

// Parâmetros do AC
input bool acActivate = true;
input bool acReverse = false;

// Parâmetros do MACD
input bool macdActivate = true;
input int macdFastPeriod = 12;
input int macdSlowperiod = 26;
input int macdSignalPeriod = 9;
input ENUM_APPLIED_PRICE macdAppliedPrice = PRICE_CLOSE;
input bool macdReverse = false;

// Parâmetros do Stochastic
input bool stochasticActivate = true;
input int stochasticKperiod = 5;
input int stochasticDperiod = 3;
input int stochasticSlowing = 3;
input ENUM_MA_METHOD stochasticMaMethod = MODE_SMA;
input ENUM_STO_PRICE stochasticPriceField = STO_LOWHIGH;
input double stochasticOverbought = 80;
input double stochasticOversold = 20;
input bool stochasticReverse = false;

// Parâmetros do Williams' Percent Range
input bool percentRangeActivate = true;
input int percentRangePeriod = 14;
input double percentRangeOversold = -80;
input double percentRangeOverbought = -20;
input bool percentRangeReverse = false;

// Parâmetros do Commodity Channel Index
input bool cciActivate = true;
input int cciPeriod = 14;
input ENUM_APPLIED_PRICE cciAppliedPrice = PRICE_TYPICAL;
input double cciOversold = -100;
input double cciOverbought = 100;
input bool cciReverse = false;

// Parâmetros do Relative Vigor Index
input bool rviActivate = true;
input int rviPeriod = 10;
input bool rviReverse = false;

//+------------------------------------------------------------------+
//| Variageis globais                                                |
//+------------------------------------------------------------------+

// Currency pair
string symbolName = Symbol();
double symbolPoint = _Point;

// Variável para armazenar o manipulador dos indicatores
int rsiHandle, adxHandle, acHandle, macdHandle, stochasticHandle, percentRangeHandle, cciHandle, rviHandle;

// Variável para armazenar o timestamp do último candlestick criado
datetime cacheLastCandleTime = 0;

//+------------------------------------------------------------------+
//| Função OnInit                                                    |
//+------------------------------------------------------------------+
int OnInit(void) {
   // ADX init
   if(adxActivate) {
      adxHandle = iADX(symbolName, (ENUM_TIMEFRAMES) allowedTimeframe, adxPeriod);
      if(adxHandle == INVALID_HANDLE) {
         PrintFormat("Falha ao criar o manipulador do indicador iADX para o símbolo %s/%s timeslot %s, código de erro %d", symbolName, EnumToString((ENUM_TIMEFRAMES) allowedTimeframe), EnumToString(timeInterval), GetLastError());
         return(INIT_FAILED);
      }
   }

   // RSI init
   if(rsiActivate) {
      rsiHandle = iRSI(symbolName, (ENUM_TIMEFRAMES) allowedTimeframe, rsiPeriod, rsiAppliedPrice);
      if(rsiHandle == INVALID_HANDLE) {
         PrintFormat("Falha ao criar o manipulador do indicador iRSI para o símbolo %s/%s timeslot %s, código de erro %d", symbolName, EnumToString((ENUM_TIMEFRAMES) allowedTimeframe), EnumToString(timeInterval), GetLastError());
         return(INIT_FAILED);
      }
   }

   // AC init
   if(acActivate) {
      acHandle = iAC(symbolName, (ENUM_TIMEFRAMES) allowedTimeframe);
      if(acHandle == INVALID_HANDLE) {
         PrintFormat("Falha ao criar o manipulador do indicador iAC para o símbolo %s/%s timeslot %s, código de erro %d", symbolName, EnumToString((ENUM_TIMEFRAMES) allowedTimeframe), EnumToString(timeInterval), GetLastError());
         return(INIT_FAILED);
      }
   }

   // Initialize MACD indicator handle with default settings (12, 26, 9 periods)
   if(macdActivate) {
      macdHandle = iMACD(symbolName, (ENUM_TIMEFRAMES) allowedTimeframe, macdFastPeriod, macdSlowperiod, macdSignalPeriod, macdAppliedPrice);
      if(macdHandle == INVALID_HANDLE) {
         PrintFormat("Falha ao criar o manipulador do indicador iMACD para o símbolo %s/%s timeslot %s, código de erro %d", symbolName, EnumToString((ENUM_TIMEFRAMES) allowedTimeframe), EnumToString(timeInterval), GetLastError());
         return(INIT_FAILED);
      }
   }

   // Initialize Stochastic Oscillator handle with default settings (5, 3, 3 periods)
   if(stochasticActivate) {
      stochasticHandle = iStochastic(symbolName, (ENUM_TIMEFRAMES) allowedTimeframe, stochasticKperiod, stochasticDperiod, stochasticSlowing, stochasticMaMethod, stochasticPriceField);
      if(stochasticHandle == INVALID_HANDLE) {
         PrintFormat("Falha ao criar o manipulador do indicador iStochastic para o símbolo %s/%s timeslot %s, código de erro %d", symbolName, EnumToString((ENUM_TIMEFRAMES) allowedTimeframe), EnumToString(timeInterval), GetLastError());
         return(INIT_FAILED);
      }
   }

   // Initialize Williams' Percent Range indicator handle (default 14 periods)
   if(percentRangeActivate) {
      percentRangeHandle = iWPR(symbolName, (ENUM_TIMEFRAMES) allowedTimeframe, percentRangePeriod);
      if(percentRangeHandle == INVALID_HANDLE) {
         PrintFormat("Falha ao criar o manipulador do indicador iWPR para o símbolo %s/%s timeslot %s, código de erro %d", symbolName, EnumToString((ENUM_TIMEFRAMES) allowedTimeframe), EnumToString(timeInterval), GetLastError());
         return(INIT_FAILED);
      }
   }

   // Initialize Commodity Channel Index handle (default 14 periods)
   if(cciActivate) {
      cciHandle = iCCI(symbolName, (ENUM_TIMEFRAMES) allowedTimeframe, cciPeriod, cciAppliedPrice);
      if(cciHandle == INVALID_HANDLE) {
         PrintFormat("Falha ao criar o manipulador do indicador iCCI para o símbolo %s/%s timeslot %s, código de erro %d", symbolName, EnumToString((ENUM_TIMEFRAMES) allowedTimeframe), EnumToString(timeInterval), GetLastError());
         return(INIT_FAILED);
      }
   }

   // Initialize Relative Vigor Index handle (default 10 periods)
   if(rviActivate) {
      rviHandle = iRVI(symbolName, (ENUM_TIMEFRAMES) allowedTimeframe, rviPeriod);
      if(rviHandle == INVALID_HANDLE) {
         PrintFormat("Falha ao criar o manipulador do indicador iRVI para o símbolo %s/%s timeslot %s, código de erro %d", symbolName, EnumToString((ENUM_TIMEFRAMES) allowedTimeframe), EnumToString(timeInterval), GetLastError());
         return(INIT_FAILED);
      }
   }

   if(!adxActivate && !rsiActivate && !acActivate && !macdActivate && !stochasticActivate && !percentRangeActivate && !cciActivate && !rviActivate) {
      Print("Ative pelo menor um indicador!");
      return(INIT_PARAMETERS_INCORRECT);
   }

   return(INIT_SUCCEEDED);
}

//+------------------------------------------------------------------+
//| Função OnTick                                                    |
//+------------------------------------------------------------------+
void OnTick(void) {
   // Obtenha o horário de abertura do último candlestick
   datetime lastCandleTime = iTime(symbolName, (ENUM_TIMEFRAMES) allowedTimeframe, 1);  // 1 significa o último candlestick fechado, 0 é o candlestick atual inacabada

   // Verifique se um novo candlestick foi formado (o tempo mudou)
   if(cacheLastCandleTime != lastCandleTime) {
      cacheLastCandleTime = lastCandleTime;  // Atualizar hora do último candlestick verificada

      if(IsTradingTimeAllowed(lastCandleTime)) {
         ENUM_SIGNAL_INDICATOR signals[] = {getAdxSignal(), getRsiSignal(), getAcSignal(), getMacdSignal(), getStochasticSignal(), getWPRSignal(), getCCISignal(), getRVISignal()};

         int neutralSignal = 0;
         int buySignal = 0;
         int sellSignal = 0;

         for(int i = 0; i < ArraySize(signals); i++) {
            switch(signals[i]) {
            case BUY:
               buySignal++;
               break;
            case SELL:
               sellSignal++;
               break;
            case NEUTRAL:
               neutralSignal++;
               break;
            }
         }

         int totalSignal = neutralSignal + buySignal + sellSignal;

         switch(totalSignal) {
         case 1:
         case 2:
         case 3:
            if(neutralSignal == 0) {
               if(buySignal == 0) {
                  OpenTrade(SELL);
               } else if(sellSignal == 0) {
                  OpenTrade(BUY);
               }
            }
            break;
         case 4:
         case 5:
            if(buySignal == 3) {
               OpenTrade(BUY);
            } else if(sellSignal == 3) {
               OpenTrade(SELL);
            }
            break;
         case 6:
            if(buySignal == 4) {
               OpenTrade(BUY);
            } else if(sellSignal == 4) {
               OpenTrade(SELL);
            }
            break;
         case 7:
         case 8:
            if(buySignal == 5) {
               OpenTrade(BUY);
            } else if(sellSignal == 5) {
               OpenTrade(SELL);
            }
            break;
         }

      }

   }
}

//+------------------------------------------------------------------+
//| Função para obter o sinal do indicador ADX                       |
//+------------------------------------------------------------------+
ENUM_SIGNAL_INDICATOR getAdxSignal() {
   if(!adxActivate) {
      return DISABLE;
   }

   double adxValue[1], plusDIValue[1], minusDIValue[1];
   if(CopyBuffer(adxHandle, 0, 1, 1, adxValue) > 0 &&  // ADX buffer
         CopyBuffer(adxHandle, 1, 1, 1, plusDIValue) > 0 &&  // +DI buffer
         CopyBuffer(adxHandle, 2, 1, 1, minusDIValue) > 0) { // -DI buffer

      if(adxValue[0] >= adxLevel) {
         if(plusDIValue[0] > minusDIValue[0]) {
            // BUY
            return adxReverse ? SELL : BUY;
         } else if(plusDIValue[0] < minusDIValue[0]) {
            // SELL
            return adxReverse ? BUY : SELL;
         }
      }

   } else {
      Print("Failed to retrieve ADX values.");
   }
   return NEUTRAL;
}

//+------------------------------------------------------------------+
//| Função para obter o sinal do indicador RSI                       |
//+------------------------------------------------------------------+
ENUM_SIGNAL_INDICATOR getRsiSignal() {
   if(!rsiActivate) {
      return DISABLE;
   }

   double rsiValue[1];
   if(CopyBuffer(rsiHandle, 0, 1, 1, rsiValue) > 0) {

      if(rsiValue[0] > rsiOverbought) {
         // SELL
         return rsiReverse ? BUY : SELL;
      } else if(rsiValue[0] < rsiOversold) {
         // BUY
         return rsiReverse ? SELL: BUY;
      }

   } else {
      Print("Failed to retrieve RSI value.");
   }
   return NEUTRAL;
}

//+-------------------------------------------------------------------+
//| Função para obter o sinal do indicador Accelerator Oscillator (AC)|
//+-------------------------------------------------------------------+
ENUM_SIGNAL_INDICATOR getAcSignal() {
   if(!acActivate) {
      return DISABLE;
   }

   double acValue[2];
   if(CopyBuffer(acHandle, 0, 1, 2, acValue) > 0) { // Fetch 2 bars (current and previous)

      // Check for AC signal based on the last 2 bars
      if(acValue[0] > 0 && acValue[0] < acValue[1]) {
         // Both bars are positive, indicating a potential BUY signal
         return acReverse ? SELL : BUY;
      } else if(acValue[0] < 0 && acValue[0] > acValue[1]) {
         // Both bars are negative, indicating a potential SELL signal
         return acReverse ? BUY : SELL;
      }
   } else {
      Print("Failed to retrieve AC values.");
   }
   return NEUTRAL; // If bars are mixed or an error occurs, return NEUTRAL
}

//+------------------------------------------------------------------+
//| Function to get MACD signal                                       |
//+------------------------------------------------------------------+
ENUM_SIGNAL_INDICATOR getMacdSignal() {
   if(!macdActivate) {
      return DISABLE;
   }

   double macdLine[1], signalLine[1];  // Arrays to store the current and previous bar values
   // CopyBuffer for MACD line and Signal line to get the last 2 values (current and previous bars)
   if(CopyBuffer(macdHandle, 0, 1, 1, macdLine) > 0 &&   // MACD Line buffer
         CopyBuffer(macdHandle, 1, 1, 1, signalLine) > 0) { // Signal Line buffer

      // Check for MACD crossovers
      if(macdLine[0] > signalLine[0]) {
         //return BUY;  // MACD line crossed above Signal line (bullish crossover)
         return macdReverse ? SELL : BUY;
      } else if(macdLine[0] < signalLine[0]) {
         //return SELL; // MACD line crossed below Signal line (bearish crossover)
         return macdReverse ? BUY : SELL;
      }

   } else {
      Print("Failed to retrieve MACD values.");
   }
   return NEUTRAL; // If no crossover or an error occurs, return NEUTRAL
}

//+------------------------------------------------------------------+
//| Function to get Stochastic signal                                 |
//+------------------------------------------------------------------+
ENUM_SIGNAL_INDICATOR getStochasticSignal() {
   if(!stochasticActivate) {
      return DISABLE;
   }

   double kLine[1], dLine[1];  // Arrays to store the current and previous bar values

   // CopyBuffer for Stochastic %K and %D lines to get the last 2 values (current and previous bars)
   if(CopyBuffer(stochasticHandle, 0, 1, 1, kLine) > 0 &&   // %K Line buffer
         CopyBuffer(stochasticHandle, 1, 1, 1, dLine) > 0) {   // %D Line buffer

      // Check for overbought/oversold conditions and crossovers
      if(kLine[0] < stochasticOversold && dLine[0] < stochasticOversold && kLine[0] >= dLine[0]) {  // Both in oversold region
         //return BUY;  // %K line crosses above %D line, indicating a BUY signal
         return stochasticReverse ? SELL : BUY;
      } else if(kLine[0] > stochasticOverbought && dLine[0] > stochasticOverbought && kLine[0] <= dLine[0]) { // Both in overbought region
         //return SELL; // %K line crosses below %D line, indicating a SELL signal
         return stochasticReverse ? BUY : SELL;
      }
   } else {
      Print("Failed to retrieve Stochastic values.");
   }

   return NEUTRAL;  // If no crossover or error occurs, return NEUTRAL
}

//+------------------------------------------------------------------+
//| Function to get Williams' Percent Range (%R) signal               |
//+------------------------------------------------------------------+
ENUM_SIGNAL_INDICATOR getWPRSignal() {
   if(!percentRangeActivate) {
      return DISABLE;
   }

   double wprValue[1];  // Array to store the current value of the %R

   // CopyBuffer for %R to get the current bar value
   if(CopyBuffer(percentRangeHandle, 0, 1, 1, wprValue) > 0) {   // %R buffer

      // Check for overbought/oversold conditions based on %R value
      if(wprValue[0] < percentRangeOversold) {
         //return BUY;  // %R is below -80, indicating a BUY signal (oversold)
         return percentRangeReverse ? SELL : BUY;
      } else if(wprValue[0] > percentRangeOverbought) {
         //return SELL; // %R is above -20, indicating a SELL signal (overbought)
         return percentRangeReverse ? BUY : SELL;
      }
   } else {
      Print("Failed to retrieve %R values.");
   }

   return NEUTRAL;  // If no signal or an error occurs, return NEUTRAL
}

//+------------------------------------------------------------------+
//| Function to get Commodity Channel Index (CCI) signal              |
//+------------------------------------------------------------------+
ENUM_SIGNAL_INDICATOR getCCISignal() {
   if(!cciActivate) {
      return DISABLE;
   }

   double cciValue[1];  // Array to store the current CCI value

   // CopyBuffer for CCI to get the current bar value
   if(CopyBuffer(cciHandle, 0, 1, 1, cciValue) > 0) {   // CCI buffer

      // Check for overbought/oversold conditions based on CCI value
      if(cciValue[0] < cciOversold) {
         //return BUY;  // CCI is below -100, indicating a BUY signal (oversold)
         return cciReverse ? SELL : BUY;
      } else if(cciValue[0] > cciOverbought) {
         //return SELL; // CCI is above 100, indicating a SELL signal (overbought)
         return cciReverse ? BUY : SELL;
      }
   } else {
      Print("Failed to retrieve CCI values.");
   }

   return NEUTRAL;  // If no signal or an error occurs, return NEUTRAL
}

//+------------------------------------------------------------------+
//| Function to get Relative Vigor Index (RVI) signal                 |
//+------------------------------------------------------------------+
ENUM_SIGNAL_INDICATOR getRVISignal() {
   if(!rviActivate) {
      return DISABLE;
   }

   double rviLine[1], signalLine[1];  // Arrays to store current and previous RVI and Signal values

   // CopyBuffer for RVI line and Signal line to get the last 2 values (current and previous bars)
   if(CopyBuffer(rviHandle, 0, 1, 1, rviLine) > 0 &&   // RVI Line buffer
         CopyBuffer(rviHandle, 1, 1, 1, signalLine) > 0) { // Signal Line buffer

      // Check for RVI crossovers
      if(rviLine[0] < 0 && signalLine[0] < 0 && rviLine[0] > signalLine[0]) {
         //return BUY;  // RVI line crosses above Signal line, indicating a BUY signal
         return rviReverse ? SELL : BUY;
      } else if(rviLine[0] > 0 && signalLine[0] > 0 && rviLine[0] < signalLine[0]) {
         //return SELL; // RVI line crosses below Signal line, indicating a SELL signal
         return rviReverse ? BUY : SELL;
      }
   } else {
      Print("Failed to retrieve RVI values.");
   }

   return NEUTRAL;  // If no crossover or an error occurs, return NEUTRAL
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
      price = SymbolInfoDouble(symbolName, SYMBOL_ASK);
      sl = price - stopLoss * symbolPoint;
      tp = price + takeProfit * symbolPoint;
   } else if (signalIndicator == SELL) {
      orderType = ORDER_TYPE_SELL;
      price = SymbolInfoDouble(symbolName, SYMBOL_BID);
      sl = price + stopLoss * symbolPoint;
      tp = price - takeProfit * symbolPoint;
   } else {
      Print("Sinal inválido para abrir uma operação!");
      return;
   }

   // Preenche a estrutura do pedido de negociação
   ZeroMemory(request);
   request.action = TRADE_ACTION_DEAL;
   request.symbol = symbolName;
   request.volume = lotSize; // Usa o tamanho do lote configurado no input
   request.type = orderType;
   request.price = price;
   request.sl = sl;
   request.tp = tp;
   request.deviation = 2;

   // Envia o pedido de negociação
   if (!OrderSend(request, result)) {
      Print("Error opening order: ", result.retcode);
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

   // Release the AC handle
   if(acHandle != INVALID_HANDLE) {
      IndicatorRelease(acHandle);
   }

   // Release the MACD handle
   if(macdHandle != INVALID_HANDLE) {
      IndicatorRelease(macdHandle);
   }

   // Release the Stochastic handle
   if(stochasticHandle != INVALID_HANDLE) {
      IndicatorRelease(stochasticHandle);
   }

   // Release the %R handle
   if(percentRangeHandle != INVALID_HANDLE) {
      IndicatorRelease(percentRangeHandle);
   }

   // Release the CCI handle
   if(cciHandle != INVALID_HANDLE) {
      IndicatorRelease(cciHandle);
   }

   // Release the RVI handle
   if(rviHandle != INVALID_HANDLE) {
      IndicatorRelease(rviHandle);
   }
}

//+------------------------------------------------------------------+
//| Tester function                                                  |
//+------------------------------------------------------------------+
double OnTester()
  {
   //--- get trade results to the array
   double array[];
   double trades_volume;
   GetTradeResultsToArray(array, trades_volume);
   
   int trades = ArraySize(array);
   int profitable_trades = 0;
   
   //--- if there are no trades, return 0
   if (trades == 0)
      return 0;
   
   //--- count the number of profitable trades
   for (int i = 0; i < trades; i++)
     {
      if (array[i] > 0) // lucro positivo
         profitable_trades++;
     }

   //--- calculate the percentage of profitable trades (win rate)
   double win_rate = (double)profitable_trades / trades * 100.0;

   //--- display the message for the single-test mode
   if (MQLInfoInteger(MQL_TESTER) && !MQLInfoInteger(MQL_OPTIMIZATION))
      PrintFormat("%s: Trades=%d, Profitable trades=%d, Win rate=%.2f%%", __FUNCTION__, trades, profitable_trades, win_rate);
   
   //--- return the win rate as the custom optimization criterion
   return win_rate;
  }
  
//+------------------------------------------------------------------+
//| Get the array of profits/losses from deals                       |
//+------------------------------------------------------------------+
bool GetTradeResultsToArray(double &pl_results[], double &volume)
  {
   //--- request the complete trading history
   if (!HistorySelect(0, TimeCurrent()))
      return (false);
   
   uint total_deals = HistoryDealsTotal();
   volume = 0;
   
   //--- set the initial size of the array with a margin - by the number of deals in history
   ArrayResize(pl_results, total_deals);
   
   //--- counter of deals that fix the trading result - profit or loss
   int counter = 0;
   ulong ticket_history_deal = 0;
   
   //--- go through all deals
   for (uint i = 0; i < total_deals; i++)
     {
      //--- select a deal 
      if ((ticket_history_deal = HistoryDealGetTicket(i)) > 0)
        {
         ENUM_DEAL_ENTRY deal_entry  = (ENUM_DEAL_ENTRY)HistoryDealGetInteger(ticket_history_deal, DEAL_ENTRY);
         long            deal_type   = HistoryDealGetInteger(ticket_history_deal, DEAL_TYPE);
         double          deal_profit = HistoryDealGetDouble(ticket_history_deal, DEAL_PROFIT);
         double          deal_volume = HistoryDealGetDouble(ticket_history_deal, DEAL_VOLUME);
         
         //--- we are only interested in trading operations        
         if ((deal_type != DEAL_TYPE_BUY) && (deal_type != DEAL_TYPE_SELL))
            continue;
         
         //--- only deals that fix profits/losses
         if (deal_entry != DEAL_ENTRY_IN)
           {
            //--- write the trading result to the array and increase the counter of deals
            pl_results[counter] = deal_profit;
            volume += deal_volume;
            counter++;
           }
        }
     }
   
   //--- set the final size of the array
   ArrayResize(pl_results, counter);
   return (true);
  }

