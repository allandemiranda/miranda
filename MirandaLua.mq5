//+------------------------------------------------------------------+
//|                                                   MirandaLua.mq5 |
//|                                                 Allan de Miranda |
//|                               https://github.com/allandemiranda/ |
//+------------------------------------------------------------------+
#property copyright "Allan de Miranda"
#property link      "https://github.com/allandemiranda/"
#property version   "1.00"

//+------------------------------------------------------------------+
//| Expert initialization function                                   |
//+------------------------------------------------------------------+
input string symbol = "EURUSD";    // Símbolo
input ENUM_TIMEFRAMES timeframe = PERIOD_H1; // Timeframe
input double lotSize = 0.01; // Tamanho do lote

// Dias da semana permitidos
input bool Monday = true;
input bool Tuesday = true;
input bool Wednesday = true;
input bool Thursday = true;
input bool Friday = true;

// Intervalos de horas permitidos
input bool Hours_00_04 = true;
input bool Hours_04_08 = true;
input bool Hours_08_12 = true;
input bool Hours_12_16 = true;
input bool Hours_16_20 = true;
input bool Hours_20_00 = true;

// Parâmetros do ADX
input int adxPeriod = 14;
input double adxLevel = 40;

// Parâmetros do RSI
input int rsiPeriod = 14;
input double rsiOverbought = 70;
input double rsiOversold = 30;

// Take Profit e Stop Loss em pontos
input int takeProfit = 100; // TP em pontos
input int stopLoss = 70;    // SL em pontos

// Variável global para armazenar o número da última barra verificada
int lastBar = 0;

//+------------------------------------------------------------------+
//| Expert tick function                                             |
//+------------------------------------------------------------------+
void OnTick()
  {
   // Verificar se o símbolo e o timeframe são válidos
   if (Symbol() != symbol || Period() != timeframe)
      return;
      
   // Verificar se o robô está dentro do horário permitido para operar
   if (!IsTradingTimeAllowed())
      return;
   
   // Obter o número da barra atual
   int currentBar = iBars(symbol, timeframe);
   
   // Se a barra atual é diferente da última barra verificada, significa que um novo candlestick foi formado
   if (currentBar > lastBar)
     {
      // Atualiza o número da última barra
      lastBar = currentBar;

      // Agora verifica os indicadores no fechamento do candlestick anterior
      CheckIndicatorsAndOpenTrade();
     }
  }
//+------------------------------------------------------------------+
//| Verificar indicadores ADX e RSI no fechamento do candlestick      |
//+------------------------------------------------------------------+
void CheckIndicatorsAndOpenTrade()
  {
   // Obter valores do ADX e RSI no fechamento da barra anterior
   double adxValue = iADX(symbol, timeframe, adxPeriod);
   double rsiValue = iRSI(symbol, timeframe, rsiPeriod, PRICE_CLOSE);
   
   // Verificar se ADX está acima do nível definido
   if (adxValue < adxLevel)
      return;

   // Verificar sinal do ADX e RSI (compra/venda)
   bool buySignal = (rsiValue < rsiOversold); // RSI em sobrevenda
   bool sellSignal = (rsiValue > rsiOverbought); // RSI em sobrecompra
   
   if (buySignal && adxValue > adxLevel) // Condição de compra
     {
      if (OrdersTotal() == 0) // Se não houver ordens abertas
        {
         OpenTrade(ORDER_TYPE_BUY);
        }
     }
   else if (sellSignal && adxValue > adxLevel) // Condição de venda
     {
      if (OrdersTotal() == 0)
        {
         OpenTrade(ORDER_TYPE_SELL);
        }
     }
  }
//+------------------------------------------------------------------+
//| Verificar se está em horário e dia permitido                     |
//+------------------------------------------------------------------+
bool IsTradingTimeAllowed()
  {
   datetime currentTime = TimeCurrent();  // Obter o tempo atual do servidor
   
   int currentDay = TimeDayOfWeek(currentTime);  // Obter o dia da semana a partir de currentTime
   int currentHour = TimeHour(currentTime); // Obter a hora a partir de currentTime
   
   // Verificar dias da semana
   if ((currentDay == 1 && !Monday) ||
       (currentDay == 2 && !Tuesday) ||
       (currentDay == 3 && !Wednesday) ||
       (currentDay == 4 && !Thursday) ||
       (currentDay == 5 && !Friday))
      return false;

   // Verificar intervalos de horário
   if (Hours_00_04 && (currentHour >= 0 && currentHour < 4)) return true;
   if (Hours_04_08 && (currentHour >= 4 && currentHour < 8)) return true;
   if (Hours_08_12 && (currentHour >= 8 && currentHour < 12)) return true;
   if (Hours_12_16 && (currentHour >= 12 && currentHour < 16)) return true;
   if (Hours_16_20 && (currentHour >= 16 && currentHour < 20)) return true;
   if (Hours_20_00 && (currentHour >= 20 && currentHour < 24)) return true;

   return false;
}
//+------------------------------------------------------------------+
//| Função para obter o dia da semana                                |
//+------------------------------------------------------------------+
int TimeDayOfWeek(datetime time) {
   MqlDateTime mt;
   bool turn=TimeToStruct(time,mt);
   return(mt.day_of_week);
}
//+------------------------------------------------------------------+
//| Função para obter a hora do dia                                  |
//+------------------------------------------------------------------+
int TimeHour(datetime time) {
   MqlDateTime mt;
   bool turn=TimeToStruct(time,mt);
   return(mt.hour);
}

//+------------------------------------------------------------------+
//| Função para abrir uma ordem de mercado                           |
//+------------------------------------------------------------------+
void OpenTrade(int orderType)
  {
   MqlTradeRequest request;
   MqlTradeResult result;
   MqlTradeCheckResult checkResult;

   double price, sl, tp;

   // Definir os preços para a operação
   if (orderType == ORDER_TYPE_BUY)
     {
      price = SymbolInfoDouble(symbol, SYMBOL_ASK);  // Preço de compra
      sl = price - stopLoss * _Point;  // Definir Stop Loss
      tp = price + takeProfit * _Point; // Definir Take Profit
     }
   else if (orderType == ORDER_TYPE_SELL)
     {
      price = SymbolInfoDouble(symbol, SYMBOL_BID);  // Preço de venda
      sl = price + stopLoss * _Point;  // Definir Stop Loss
      tp = price - takeProfit * _Point; // Definir Take Profit
     }

   // Preencher a estrutura MqlTradeRequest
   ZeroMemory(request);
   request.action = TRADE_ACTION_DEAL;           // Ação: abrir uma posição
   request.symbol = symbol;                      // Símbolo para operar
   request.volume = lotSize;                     // Tamanho do lote
   request.type = orderType;                     // Tipo da ordem (compra ou venda)
   request.price = price;                        // Preço da operação
   request.sl = sl;                              // Stop Loss
   request.tp = tp;                              // Take Profit
   request.deviation = 2;                        // Desvio permitido
   request.magic = 123456;                       // Número mágico (identificador único)
   request.comment = (orderType == ORDER_TYPE_BUY) ? "Buy Order" : "Sell Order"; // Comentário

   // Enviar a ordem
   if (!OrderSend(request, result))
     {
      Print("Erro ao abrir ordem: ", result.retcode);
     }
   else
     {
      Print("Ordem aberta com sucesso. Ticket: ", result.order);
     }
  }
